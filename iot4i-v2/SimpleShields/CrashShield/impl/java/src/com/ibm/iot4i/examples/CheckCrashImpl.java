package com.ibm.iot4i.examples;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ibm.json.java.JSONObject;
import com.ibm.streams.function.model.Function;

public class CheckCrashImpl {

	static Logger logger = Logger.getLogger(CheckCrashImpl.class);

	@Function(namespace = "com.ibm.iot4i.examples", name = "checkCrash", description = "", stateful = false)
	public static boolean checkCrash(String message) {
		JSONObject jsonMessage = null;
		try {
			jsonMessage = JSONObject.parse(message);
			logger.log(Level.WARN, "Message to check: " + jsonMessage.toString());
			JSONObject rawEvent = JSONObject.parse(jsonMessage.get("event").toString());
			logger.log(Level.WARN, "Event to check: " + rawEvent.toString());
			return Boolean.valueOf(rawEvent.get("isCrash").toString());
		} catch (Exception e) {
			logger.log(Level.WARN, "check shield pattern failed, error: " + e.getLocalizedMessage());
			return false;
		}
	}

}
