package com.ibm.iot4i.events.common;

import java.util.logging.Logger;
import com.ibm.streams.operator.logging.*;
import com.ibm.iot4i.services.ShieldActivationCacheService;
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

@Libraries("impl/lib/*")
@PrimitiveOperator(name = "CheckActivationOp", namespace = "com.ibm.iot4i.events.common", description = "Java Operator CheckActivationOp")
@InputPorts({
		@InputPortSet(description = "Port that ingests tuples", cardinality = 1, optional = false, windowingMode = WindowMode.NonWindowed, windowPunctuationInputMode = WindowPunctuationInputMode.Oblivious),
		@InputPortSet(description = "Optional input ports", optional = true, windowingMode = WindowMode.NonWindowed, windowPunctuationInputMode = WindowPunctuationInputMode.Oblivious) })
@OutputPorts({
		@OutputPortSet(description = "Port that produces tuples", cardinality = 1, optional = false, windowPunctuationOutputMode = WindowPunctuationOutputMode.Generating),
		@OutputPortSet(description = "Optional output ports", optional = true, windowPunctuationOutputMode = WindowPunctuationOutputMode.Generating) })
public class CheckActivationOp extends AbstractOperator {

	private static final Logger trace = Logger.getLogger(CheckActivationOp.class.getName());
	private static final Logger log = Logger.getLogger(CheckActivationOp.class.getName());

	private final static String DEVICE_MAPPING_NOTIFICATION_TOPIC = "IoT4i_deviceMappingChange";
	private final static String ACTIVATION_MAPPING_NOTIFICATION_TOPIC = "IoT4i_userActivationChange";

	private ShieldActivationCacheService shieldActivationCacheService;

	private String apiToken;
	private String apiURL;
	private String shieldId = null;
	private String tenantId = null;
	private JSONObject key = null;

	@Parameter(name = "apiToken", optional = false)
	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}

	@Parameter(name = "apiURL", optional = false)
	public void setApiURL(String apiURL) {
		this.apiURL = apiURL;
	}

	@Parameter(name = "shieldId", optional = false)
	public void setShieldId(String shieldId) {
		this.shieldId = shieldId;
	}

	@Parameter(name = "tenantId", optional = false)
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	@Override
	public synchronized void initialize(OperatorContext context) throws Exception {
		super.initialize(context);
		log.log(LogLevel.WARN, "Operator " + context.getName() + " initializing in PE: " + context.getPE().getPEId()
				+ " in Job: " + context.getPE().getJobId());

		shieldActivationCacheService = new ShieldActivationCacheService(this.apiToken, this.apiURL, this.tenantId);
		shieldActivationCacheService.setCache();
	}

	private void setKey(String topic) {
		try {
			this.key = JSONObject.parse(topic);
		} catch (Exception e) {
			log.log(LogLevel.ERROR, "parsing topic failed, error: " + e.getLocalizedMessage());
		}
	}

	private void submitTuple(String userId, String key, String message) {
		try {
			// Create a new tuple for output port 0
			StreamingOutput<OutputTuple> outStream = getOutput(0);
			OutputTuple outTuple = outStream.newTuple();
			if (shieldActivationCacheService.isShieldActive(userId, this.shieldId)) {
				outTuple.setString("userId", userId);
				outTuple.setString("key", key);
				outTuple.setString("message", message);

				log.log(LogLevel.WARN, "submitted tuple: " + outTuple.toString());
				outStream.submit(outTuple);
			} else {
				log.log(LogLevel.WARN, "Shield not active for user, ignore event");
			}
		} catch (Exception e) {
			log.log(LogLevel.WARN, "Submitting tuple failed, error: " + e.getLocalizedMessage());
		}
	}

	private boolean isNotification(String topicDeviceType) {
		return (topicDeviceType.equals(DEVICE_MAPPING_NOTIFICATION_TOPIC)
				|| topicDeviceType.equals(ACTIVATION_MAPPING_NOTIFICATION_TOPIC));
	}

	private boolean isActivationMappingNotification(String topicDeviceType) {
		return topicDeviceType.equals(ACTIVATION_MAPPING_NOTIFICATION_TOPIC);
	}

	@Override
	public final void process(StreamingInput<Tuple> inputStream, Tuple tuple) throws Exception {

		String message = tuple.getString("message");
		String stringKey = tuple.getString("key");
		String userId = tuple.getString("userId");

		setKey(stringKey);
		if (this.key != null) {
			String topicDeviceType = this.key.get("deviceType").toString();
			String topicDeviceId = this.key.get("deviceId").toString();

			if (!isNotification(topicDeviceType)) {
				log.log(LogLevel.WARN, "Incoming tuple:" + tuple.toString());
				submitTuple(userId, stringKey, message);
			} else {
				// notification to update cache
				if (isActivationMappingNotification(topicDeviceType))
					shieldActivationCacheService.updateUserShieldMappingCache(topicDeviceId, message);
			}
		}
	}
}
