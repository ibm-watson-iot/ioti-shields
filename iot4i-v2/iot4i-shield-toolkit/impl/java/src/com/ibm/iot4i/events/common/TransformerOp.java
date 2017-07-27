package com.ibm.iot4i.events.common;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.iot4i.services.*;

@Libraries("impl/lib/*")
@PrimitiveOperator(name = "TransformerOp", namespace = "com.ibm.iot4i.events.common", description = "Java Operator Transformer")
@InputPorts({
		@InputPortSet(description = "Port that ingests tuples", cardinality = 1, optional = false, windowingMode = WindowMode.NonWindowed, windowPunctuationInputMode = WindowPunctuationInputMode.Oblivious) })
@OutputPorts({
		@OutputPortSet(description = "Port that produces tuples", cardinality = 1, optional = false, windowPunctuationOutputMode = WindowPunctuationOutputMode.Generating) })
public class TransformerOp extends AbstractOperator {

	private final static String DEVICE_MAPPING_NOTIFICATION_TOPIC = "IoT4i_deviceMappingChange";
	private final static String ACTIVATION_MAPPING_NOTIFICATION_TOPIC = "IoT4i_userActivationChange";

	private JSONObject key = null;
	private String deviceIdAttributeNames;
	private String apiToken;
	private String apiURL;
	private String tenantId = null;
	private DeviceCacheService deviceCacheService;
	private String deviceId;

	@Parameter(name = "apiToken", optional = false)
	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}

	@Parameter(name = "apiURL", optional = false)
	public void setApiURL(String apiURL) {
		this.apiURL = apiURL;
	}

	@Parameter(name = "tenantId", optional = false)
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	@Parameter(name = "deviceIdAttributeNames", optional = false)
	public void setDeviceIdAttributeName(String deviceIdAttributeNames) {
		this.deviceIdAttributeNames = deviceIdAttributeNames;
	}

	@Override
	public synchronized void initialize(OperatorContext context) throws Exception {
		super.initialize(context);

		Logger.getLogger(this.getClass()).log(Level.WARN, "Operator " + context.getName() + " initializing in PE: "
				+ context.getPE().getPEId() + " in Job: " + context.getPE().getJobId());

		deviceCacheService = new DeviceCacheService(this.apiToken, this.apiURL, this.tenantId);
		deviceCacheService.setCache();
	}

	private void setKey(String topic) {
		try {
			this.key = JSONObject.parse(topic);
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).log(Level.ERROR,
					"parsing topic failed, error: " + e.getLocalizedMessage());
		}
	}

	private boolean isNotification(String topicDeviceType) {
		return (topicDeviceType.equals(DEVICE_MAPPING_NOTIFICATION_TOPIC)
				|| topicDeviceType.equals(ACTIVATION_MAPPING_NOTIFICATION_TOPIC));
	}

	private boolean isDeviceMappingNotification(String topicDeviceType) {
		return topicDeviceType.equals(DEVICE_MAPPING_NOTIFICATION_TOPIC);
	}

	@Override
	public final void process(StreamingInput<Tuple> inputStream, Tuple tuple) throws Exception {
		String message = tuple.getString("message");
		String stringKey = tuple.getString("key");
		// example key:
		// "{\"orgId\":\"ceybag\",\"deviceType\":\"crashEvent\",\"deviceId\":\"deviceId\",\"eventType\":\"status\",\"format\":\"json\",\"timestamp\":\"2017-07-07T14:52:49.710Z\"}"
		setKey(stringKey);

		if (this.key != null) {
			String topicDeviceType = this.key.get("deviceType").toString();
			String topicDeviceId = this.key.get("deviceId").toString();

			Logger.getLogger(this.getClass()).log(Level.WARN, "key: " + key);
			Logger.getLogger(this.getClass()).log(Level.WARN, "deviceType: " + topicDeviceType);

			if (!isNotification(topicDeviceType)) {
				Logger.getLogger(this.getClass()).log(Level.WARN, "Incoming tuple:" + tuple.toString());

				// first check if the userDeviceMapping exists for topicDeviceId
				String userId = deviceCacheService.getDeviceUserMapping(topicDeviceId);
				if (userId == null) {
					// search for the deviceId in the message based on
					// deviceIdAttributeNames
					String deviceId = findDeviceId(message);
					Logger.getLogger(this.getClass()).log(Level.WARN, "deviceId is: " + deviceId);
					if (deviceId != null)
						userId = this.deviceCacheService.getDeviceUserMapping(deviceId);
				}

				submitTuple(userId, stringKey, message);
			} else {
				// notification to update cache
				if (isDeviceMappingNotification(topicDeviceType))
					deviceCacheService.updateDeviceUserMappingCache(topicDeviceId, message);
			}
		}
	}

	private void setDeviceId(JsonElement element, String attributeName) {
		if (element.isJsonObject()) {
			JsonObject jsonObject = element.getAsJsonObject();
			Set<String> keys = jsonObject.keySet();
			Iterator<String> iterator = keys.iterator();
			String key = null;
			while (iterator.hasNext()) {
				key = iterator.next();
				if (key.equals(attributeName))
					this.deviceId = jsonObject.get(attributeName).getAsString();
				setDeviceId(jsonObject.get(key), attributeName);
			}
		} else if (element.isJsonArray()) {
			JsonArray jsonArray = element.getAsJsonArray();
			for (JsonElement childElement : jsonArray) {
				setDeviceId(childElement, attributeName);
			}
		}
	}

	private String findDeviceId(String message) {
		this.deviceId = null;
		if (this.deviceIdAttributeNames != null) {
			String[] attributeNames = this.deviceIdAttributeNames.split(",");
			for (String deviceIdAttributeName : attributeNames) {
				setDeviceId(new JsonParser().parse(message), deviceIdAttributeName.trim());
				if (this.deviceId != null) {
					break;
				}
			}
			return deviceId;
		} else {
			Logger.getLogger(this.getClass()).log(Level.WARN, "deviceIdAttributeNames was not set");
		}
		return null;
	}

	private void submitTuple(String userId, String key, String message) {
		try {
			// Create a new tuple for output port 0
			StreamingOutput<OutputTuple> outStream = getOutput(0);
			OutputTuple outTuple = outStream.newTuple();
			if (userId == null) {
				Logger.getLogger(this.getClass()).log(Level.WARN, "User Id wasn't found");
				userId = "";
			}

			outTuple.setString("userId", userId);
			outTuple.setString("key", key);
			outTuple.setString("message", message);

			Logger.getLogger(this.getClass()).log(Level.WARN, "submitted tuple: " + outTuple.toString());
			outStream.submit(outTuple);
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).log(Level.WARN,
					"Submitting tuple failed, error: " + e.getLocalizedMessage());
		}
	}
}
