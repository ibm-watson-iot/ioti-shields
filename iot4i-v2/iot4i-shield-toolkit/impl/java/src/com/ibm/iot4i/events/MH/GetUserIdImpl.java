package com.ibm.iot4i.events.MH;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ibm.json.java.JSONObject;
import com.ibm.streams.function.model.Function;

public class GetUserIdImpl {

	static Logger logger = Logger.getLogger(GetUserIdImpl.class);

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
			logger.log(Level.WARN, "get user id from message failed, error: " + e.getLocalizedMessage());
			return "";
		}
	}

}
