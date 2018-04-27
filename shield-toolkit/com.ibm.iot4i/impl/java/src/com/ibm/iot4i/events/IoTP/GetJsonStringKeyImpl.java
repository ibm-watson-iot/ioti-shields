package com.ibm.iot4i.events.IoTP;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.ibm.streams.function.model.Function;

public class GetJsonStringKeyImpl {

	@Function(namespace = "com.ibm.iot4i.events.IoTP", name = "getJsonStringKey", description = "get a kafka key from topic", stateful = false)
	public static String getJsonStringKey(String orgId, String topic) {
		// iot-2/type/DeviceType/id/DeviceId/evt/status/fmt/json
		String[] topicContent = topic.split("/");
		String deviceType = topicContent[2];
		String deviceId = topicContent[4];
		String eventType = topicContent[6];
		String format = topicContent[8];
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
				.format(new Timestamp(System.currentTimeMillis()));

		return "{\"orgId\": \"" + orgId + "\",\"deviceType\": \"" + deviceType + "\",\"deviceId\": \" " + deviceId
				+ "\",\"eventType\": \"" + eventType + "\",\"format\": \"" + format + "\",\"timestamp\": \"" + timeStamp
				+ "\"}";
	}

}
