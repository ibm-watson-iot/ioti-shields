package com.ibm.iot4i.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.JsonObject;
import com.ibm.json.java.JSONObject;
import com.ibm.iot4i.models.ListResponse;

import retrofit2.Call;
import retrofit2.Response;

public class DeviceCacheService {

	private String apiURL;
	private String apiToken;
	private String tenantId;
	private DeviceService deviceService;

	private Map<String, String> deviceUserMapCache;

	public DeviceCacheService(String apiToken, String apiURL, String tenantId) {
		this.apiToken = apiToken;
		this.apiURL = apiURL;
		this.tenantId = tenantId;

		this.deviceUserMapCache = new HashMap<>();

		deviceService = ServiceGenerator.createService(this.apiURL, this.apiToken, DeviceService.class);
	}

	private void setDeviceUserMappings() throws IOException {
		// TODO do it in batches, now assuming max is 50k mappings
		Call<ListResponse> deviceListCall = this.deviceService.listDevices(this.tenantId, 0, 50000, false, false, null,
				"all");
		Response<ListResponse> deviceListResponse = deviceListCall.execute();
		if (deviceListResponse.body() != null) {
			for (JsonObject item : deviceListResponse.body().getItems()) {
				String key = item.get("key").getAsJsonArray().get(0).getAsString();
				String value = item.get("value").getAsString();
				deviceUserMapCache.put(key, value);
			}
			Logger.getLogger(this.getClass()).log(Level.WARN, "Device User mapping was set successfully ");
		} else {
			Logger.getLogger(this.getClass()).log(Level.WARN,
					"Not able to retrieve device-to-user mapping, Error: " + " error code: " + deviceListResponse.code()
							+ " error message: " + deviceListResponse.message() + " all: "
							+ deviceListResponse.toString());
		}
	}

	public void setCache() throws IOException {
		// set the deviceId to users mapping
		setDeviceUserMappings();
	}

	public String getDeviceUserMapping(String deviceId) {
		return this.deviceUserMapCache.get(deviceId);
	}

	public void updateDeviceUserMappingCache(String deviceId, String payload) {
		Logger.getLogger(this.getClass()).log(Level.WARN,
				"Device to User Mapping before notification: " + this.deviceUserMapCache.get(deviceId));

		JSONObject jsonObject;
		try {
			jsonObject = JSONObject.parse(payload);
			String value = jsonObject.get("value").toString();
			if (value.equals("deleted")) {
				this.deviceUserMapCache.remove(deviceId);
			} else {
				this.deviceUserMapCache.put(deviceId, value);
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).log(Level.WARN, "Notification has not json payload, payload: " + payload);
		}
		Logger.getLogger(this.getClass()).log(Level.WARN,
				"Device to User Mapping after notification: " + this.deviceUserMapCache.get(deviceId));
	}
}