package com.ibm.iot4i.events.MH;

import java.util.logging.Logger;
import com.ibm.streams.operator.logging.*;

import com.ibm.json.java.JSONObject;
import com.ibm.streams.function.model.Function;

public class GetUserIdImpl {

	private static final Logger trace = Logger.getLogger(GetUserIdImpl.class.getName());
	private static final Logger log = Logger.getLogger("com.ibm.streams.operator.log");

	@Function(namespace = "com.ibm.iot4i.events.MH", name = "getUserId", description = "", stateful = false)
	public static String getUserId(String message) {
		JSONObject jsonMessage = null;
		try {
			jsonMessage = JSONObject.parse(message);
			String userId = jsonMessage.get("userId").toString();
			if (userId == null)
				return "";
			return userId;
		} catch (Exception e) {
			log.log(LogLevel.ERROR, "get user id from message failed, error: " + e.getLocalizedMessage());
			return "";
		}
	}

}
