package com.ibm.iot4i.transformerjob;

import java.util.UUID;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ibm.json.java.JSONObject;
import com.ibm.streams.function.model.Function;

public class PrepareAnnotatedEventImpl {

	static Logger logger = Logger.getLogger(PrepareAnnotatedEventImpl.class);

	@Function(namespace = "com.ibm.iot4i.transformerjob", name = "prepareAnnotatedEvent", description = "", stateful = false)
	public static String prepareAnnotatedEvent(String userId, String key, String message) {
		try {
			JSONObject jsonObject = new JSONObject();
			JSONObject evt = JSONObject.parse(message);
			JSONObject metadata = JSONObject.parse(key);
			jsonObject.put("userId", userId);
			jsonObject.put("event", evt);
			jsonObject.put("metadata", metadata);
			jsonObject.put("arrivedAtMH", metadata.get("timestamp"));
			jsonObject.put("eventId", userId + "_" + UUID.randomUUID().toString());
			logger.log(Level.WARN, "Prepare annotated event is: " + jsonObject.toString());
			return jsonObject.toString();
		} catch (Exception e) {
			logger.log(Level.WARN, "Prepare annotated event failed, error: " + e.getLocalizedMessage());
			return "";
		}
	}

}
