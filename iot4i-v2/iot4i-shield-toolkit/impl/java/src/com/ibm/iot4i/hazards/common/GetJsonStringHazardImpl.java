package com.ibm.iot4i.hazards.common;

import java.util.Arrays;

import com.ibm.streams.function.model.Function;

public class GetJsonStringHazardImpl {

	@Function(namespace = "com.ibm.iot4i.hazards.common", name = "getJsonStringHazard", description = "", stateful = false)
	public static String getJsonStringHazard(String shieldId, String userId, String[] rawEvents, String actionParams) {
		return "{\"userId\": \"" + userId + "\",\"shieldId\": \"" + shieldId + "\",\"rawEvents\": "
				+ Arrays.toString(rawEvents) + ",\"actionParams\": " + actionParams + "}";
	}

}
