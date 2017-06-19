package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;

import com.ibm.json.java.JSONObject;
import com.ibm.streams.operator.AbstractOperator;
import com.ibm.streams.operator.OperatorContext;
import com.ibm.streams.operator.OutputTuple;
import com.ibm.streams.operator.StreamingInput;
import com.ibm.streams.operator.StreamingOutput;
import com.ibm.streams.operator.Tuple;
import com.ibm.streams.operator.model.InputPortSet;
import com.ibm.streams.operator.model.InputPortSet.WindowMode;
import com.ibm.streams.operator.model.InputPortSet.WindowPunctuationInputMode;
import com.ibm.streams.operator.model.InputPorts;
import com.ibm.streams.operator.model.Libraries;
import com.ibm.streams.operator.model.OutputPortSet;
import com.ibm.streams.operator.model.OutputPortSet.WindowPunctuationOutputMode;
import com.ibm.streams.operator.model.OutputPorts;
import com.ibm.streams.operator.model.Parameter;
import com.ibm.streams.operator.model.PrimitiveOperator;

import application.stores.DeviceStore;
import application.stores.ShieldActivationStore;

@Libraries("impl/lib/*")
@PrimitiveOperator(name = "Transformer", namespace = "application", description = "Java Operator Transformer")
@InputPorts({
		@InputPortSet(description = "Port that ingests tuples", cardinality = 1, optional = false, windowingMode = WindowMode.NonWindowed, windowPunctuationInputMode = WindowPunctuationInputMode.Oblivious) })
@OutputPorts({
		@OutputPortSet(description = "Port that produces tuples", cardinality = 1, optional = false, windowPunctuationOutputMode = WindowPunctuationOutputMode.Generating) })
public class Transformer extends AbstractOperator {

	// enumeration type
	public enum ProviderTypes {
		digital_concepts, wibutler, bosch
	}

	private CacheManager cacheManager;
	// set providers to deviceId attribute name mapping
	// topicId: the device id is inside the topic
	private static final Map<String, String> providerDeviceIdMap;
	static {
		Map<String, String> aMap = new HashMap<String, String>();
		aMap.put("digital_concepts", "gatewayId");
		aMap.put("wibutler", "topicId");
		aMap.put("bosch", "sensorId");
		providerDeviceIdMap = Collections.unmodifiableMap(aMap);
	}
	// example topic for deviceId change notification
	// iot-2/type/IoT4i_deviceMappingChange/id/vendorId/evt/change/fmt/json
	private static final String deviceMappingChangeTopicType = "IoT4i_deviceMappingChange";
	// example topic for deviceId change notification
	// iot-2/type/IoT4i_userActivationChange/id/userId/evt/change/fmt/json
	private static final String userActivationChangeTopicType = "IoT4i_userActivationChange";
	private Cache<String, String> deviceUserMapCache;
	@SuppressWarnings("rawtypes")
	private Cache<String, ArrayList> userShieldMapCache;
	private String deviceId;
	private String deviceIdAttributeName;
	private ProviderTypes gatewayType;
	private String dbName;
	private String dbHost;
	private String dbUser;
	private String dbPassword;
	private boolean checkShieldActivationRule;
	private String shieldId = null;
	private DeviceStore deviceStore;
	private ShieldActivationStore shieldActivationStore;

	@Parameter(name = "dbName", optional = true)
	public void setDBName(String dbName) {
		this.dbName = dbName;
	}

	@Parameter(name = "dbHost", optional = true)
	public void setDBHost(String dbHost) {
		this.dbHost = dbHost;
	}

	@Parameter(name = "dbUser", optional = true)
	public void setDBUser(String dbUser) {
		this.dbUser = dbUser;
	}

	@Parameter(name = "dbPassword", optional = true)
	public void setDBPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	@Parameter(name = "shieldId", optional = true)
	public void setShieldId(String shieldId) {
		this.shieldId = shieldId;
	}

	@Parameter(name = "checkShieldActivationRule", optional = true)
	public void setCheckShieldActivationRulee(boolean checkShieldActivationRule) {
		this.checkShieldActivationRule = checkShieldActivationRule;
	}

	@Parameter(name = "deviceIdAttributeName", optional = true)
	public void setDeviceIdAttributeName(String deviceIdAttributeName) {
		this.deviceIdAttributeName = deviceIdAttributeName;
	}

	@Parameter(name = "gatewayType", optional = true)
	public void setProviderType(ProviderTypes gatewayType) {
		this.gatewayType = gatewayType;
	}

	private void setCache() {
		// set the deviceId to users mapping
		HashMap<String, String> usersDevices = this.deviceStore.getAllUserDeviceMapping();
		for (String deviceId : usersDevices.keySet()) {
			this.deviceUserMapCache.put(deviceId, usersDevices.get(deviceId));
		}
		Logger.getLogger(this.getClass()).log(Level.WARN, "Device User mapping was set successfully ");

		// set shield activation map
		if (this.checkShieldActivationRule) {
			HashMap<String, ArrayList<String>> usersShieldsMap = this.shieldActivationStore.getAllUserShieldMapping();
			for (String userId : usersShieldsMap.keySet()) {
				this.userShieldMapCache.put(userId, usersShieldsMap.get(userId));
			}
			Logger.getLogger(this.getClass()).log(Level.WARN, "User shield mapping was set successfully ");
		}
	}

	private String getDeviceUserMapping(String deviceId) {
		String userId = this.deviceUserMapCache.get(deviceId);
		if (userId == null) {
			userId = this.deviceStore.getUserDeviceMapping(deviceId);
			this.deviceUserMapCache.put(deviceId, userId);
		}
		return userId;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> getUserShieldMapping(String userId) {
		ArrayList<String> activeShieldIds = this.userShieldMapCache.get(userId);
		if (activeShieldIds == null) {
			activeShieldIds = this.shieldActivationStore.getUserShieldMapping(userId);
			this.userShieldMapCache.put(userId, activeShieldIds);
		}
		return activeShieldIds;
	}

	@Override
	public synchronized void initialize(OperatorContext context) throws Exception {
		super.initialize(context);

		Logger.getLogger(this.getClass()).log(Level.WARN, "Operator " + context.getName() + " initializing in PE: "
				+ context.getPE().getPEId() + " in Job: " + context.getPE().getJobId());

		/*
		 * Map<String, String> env = System.getenv(); this.dbName =
		 * env.get("DB_NAME"); this.dbHost = env.get("DB_HOST"); this.dbUser =
		 * env.get("DB_USER"); this.dbPassword = env.get("DB_PASSWORD");
		 */

		cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
		cacheManager.init();
		this.deviceUserMapCache = cacheManager.createCache("deviceUserMapCache",
				CacheConfigurationBuilder
						.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(10))
						.withExpiry(Expirations.timeToLiveExpiration(Duration.of(30, TimeUnit.MINUTES))));

		this.userShieldMapCache = cacheManager.createCache("userShieldMapCache",
				CacheConfigurationBuilder
						.newCacheConfigurationBuilder(String.class, ArrayList.class, ResourcePoolsBuilder.heap(10))
						.withExpiry(Expirations.timeToLiveExpiration(Duration.of(30, TimeUnit.MINUTES))));

		deviceStore = new DeviceStore(this.dbName, this.dbHost, this.dbUser, this.dbPassword);
		shieldActivationStore = new ShieldActivationStore(this.dbName, this.dbHost, this.dbUser, this.dbPassword);

		setCache();
	}

	private void setDeviceId(String payload, String attributeName) {
		JSONObject jsonObject;
		try {
			jsonObject = JSONObject.parse(payload);
			@SuppressWarnings("unchecked")
			Set<String> keys = jsonObject.keySet();
			Iterator<String> iterator = keys.iterator();
			String key = null;
			while (iterator.hasNext()) {
				key = iterator.next();
				if (key.equals(attributeName)) {
					this.deviceId = jsonObject.get(key).toString();
				} else {
					setDeviceId(jsonObject.get(key).toString(), attributeName);
				}
			}
		} catch (Exception e) {
		}
	}

	private boolean isShieldActive(String userId) {
		ArrayList<String> shieldIds = this.getUserShieldMapping(userId);
		for (String shieldId : shieldIds) {
			if (shieldId.equals(this.shieldId)) {
				return true;
			}
		}
		return false;
	}

	private void findAndSetDeviceId(String payload, String topicDeviceId) {
		if (this.gatewayType != null) {
			if (providerDeviceIdMap.get(gatewayType.toString()).equals("topicId")) {
				this.deviceId = topicDeviceId;
			} else {
				setDeviceId(payload, providerDeviceIdMap.get(this.gatewayType.toString()));
			}
		} else if (this.deviceIdAttributeName != null) {
			setDeviceId(payload, this.deviceIdAttributeName);
		} else {
			Logger.getLogger(this.getClass()).log(Level.WARN,
					"deviceIdAttributeName and gatewayType weren't set correctly ");
		}
	}

	private void submitTuple(String userId, String payload) {
		try {
			// Create a new tuple for output port 0
			StreamingOutput<OutputTuple> outStream = getOutput(0);
			OutputTuple outTuple = outStream.newTuple();
			if (userId != null) {
				JSONObject jsonObject = JSONObject.parse(payload);
				jsonObject.put("userId", userId);
				outTuple.setString("data", jsonObject.toString());

				if ((this.checkShieldActivationRule && isShieldActive(userId)) || !this.checkShieldActivationRule) {
					Logger.getLogger(this.getClass()).log(Level.WARN, "submitted tuple: " + outTuple.toString());
					outStream.submit(outTuple);
				} else {
					Logger.getLogger(this.getClass()).log(Level.WARN, "Shield not active for user, ignore event");
				}
			} else {
				Logger.getLogger(this.getClass()).log(Level.WARN, "No match for deviceId: " + this.deviceId);
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).log(Level.WARN,
					"Submitting tuple failed, error: " + e.getLocalizedMessage());
		}
	}

	@Override
	public final void process(StreamingInput<Tuple> inputStream, Tuple tuple) throws Exception {

		String payload = tuple.getString("payload");
		String topic = tuple.getString("topic"); // example:
													// iot-2/type/DeviceType/id/DeviceId/evt/status/fmt/json
		String[] topicContent = topic.split("/");
		String topicDeviceType = topicContent[2];
		String topicDeviceId = topicContent[4];
		
		Logger.getLogger(this.getClass()).log(Level.WARN, "topic" + topic);
		Logger.getLogger(this.getClass()).log(Level.WARN, "topicDeviceType" + topicDeviceType);

		if (!(deviceMappingChangeTopicType.equals(topicDeviceType)
				|| userActivationChangeTopicType.equals(topicDeviceType))) {
			Logger.getLogger(this.getClass()).log(Level.WARN, "Incoming tuple:" + tuple.toString());

			// check the provider type attribute
			findAndSetDeviceId(payload, topicDeviceId);

			if (this.deviceId != null) {
				submitTuple(getDeviceUserMapping(this.deviceId), payload);
			} else {
				Logger.getLogger(this.getClass()).log(Level.WARN, "deviceId not found");
			}
		} else {
			// notification to update cache
			if (deviceMappingChangeTopicType.equals(topicDeviceType)) {
				updateDeviceUserMappingCache(topicDeviceId);
			} else if (userActivationChangeTopicType.equals(topicDeviceType)) {
				updateUserShieldMappingCache(topicDeviceId);
			}
		}
	}

	private void updateDeviceUserMappingCache(String deviceId) {
		Logger.getLogger(this.getClass()).log(Level.WARN,
				"Device to User Mapping before notification: " + this.deviceUserMapCache.get(deviceId));
		this.deviceUserMapCache.put(deviceId, this.deviceStore.getUserDeviceMapping(deviceId));
		Logger.getLogger(this.getClass()).log(Level.WARN,
				"Device to User Mapping after notification: " + this.deviceUserMapCache.get(deviceId));
	}

	private void updateUserShieldMappingCache(String userId) {
		Logger.getLogger(this.getClass()).log(Level.WARN,
				"User to Shield Mapping before notification: " + this.userShieldMapCache.get(userId));
		ArrayList<String> activeShieldIds = this.shieldActivationStore.getUserShieldMapping(userId);
		this.userShieldMapCache.put(userId, activeShieldIds);
		Logger.getLogger(this.getClass()).log(Level.WARN,
				"User to Shield Mapping after notification: " + this.userShieldMapCache.get(userId));
	}

	public synchronized void shutdown() throws Exception {
		OperatorContext context = getOperatorContext();
		Logger.getLogger(this.getClass()).log(Level.WARN, "Operator " + context.getName() + " shutting down in PE: "
				+ context.getPE().getPEId() + " in Job: " + context.getPE().getJobId());

		cacheManager.removeCache("deviceUserMapCache");
		cacheManager.removeCache("userShieldMapCache");
		cacheManager.close();

		super.shutdown();
	}
}
