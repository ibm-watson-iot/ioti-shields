package com.ibm.iot4i.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.JsonObject;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.ibm.iot4i.models.ListResponse;

import retrofit2.Call;
import retrofit2.Response;

public class ShieldActivationCacheService {

	private String apiURL;
	private String apiToken;
	private String tenantId;
	private ShieldActivationService shieldActivationService;

	private Map<String, ArrayList<String>> userShieldMapCache;

	public ShieldActivationCacheService(String apiToken, String apiURL, String tenantId) {
		this.apiToken = apiToken;
		this.apiURL = apiURL;
		this.tenantId = tenantId;

		this.userShieldMapCache = new HashMap<>();

		shieldActivationService = ServiceGenerator.createService(this.apiURL, this.apiToken,
				ShieldActivationService.class);
	}

	private void setUserShieldActivationsMapping() throws IOException {
		// TODO do it in batches, now assuming max is 50k mappings
		Call<ListResponse> activationsListCall = this.shieldActivationService.listShieldActivations(this.tenantId, 0,
				50000, false, false, "all", null);
		Response<ListResponse> activationsResponse = activationsListCall.execute();
		if (activationsResponse.body() != null) {
			for (JsonObject item : activationsResponse.body().getItems()) {
				String key = item.get("key").getAsJsonArray().get(0).getAsString();
				ArrayList<String> shieldActivations = userShieldMapCache.get(key);
				if (shieldActivations == null) {
					shieldActivations = new ArrayList<>();
				}

				shieldActivations.add(item.get("value").getAsString());
				userShieldMapCache.put(key, shieldActivations);
			}
			Logger.getLogger(this.getClass()).log(Level.WARN, "User shield mapping was set successfully ");
		} else {
			Logger.getLogger(this.getClass()).log(Level.WARN,
					"Not able to retrieve user-to-shield-activation mappings, error: "
							+ activationsResponse.errorBody().toString());
		}
	}

	private ArrayList<String> getUserShieldMapping(String userId) {
		return this.userShieldMapCache.get(userId);
	}

	public boolean isShieldActive(String userId, String shieldId) {
		ArrayList<String> shieldIds = this.getUserShieldMapping(userId);
		if (shieldIds != null)
			for (String id : shieldIds) {
				if (id.equals(shieldId)) {
					return true;
				}
			}
		return false;
	}

	public void setCache() throws IOException {
		// set shield activation map
		setUserShieldActivationsMapping();
	}

	public void updateUserShieldMappingCache(String userId, String payload) {
		Logger.getLogger(this.getClass()).log(Level.WARN,
				"User to Shield Mapping before notification: " + this.userShieldMapCache.get(userId));
		JSONObject jsonObject;
		try {
			jsonObject = JSONObject.parse(payload);
			ArrayList<String> shieldActivations = new ArrayList<>();
			@SuppressWarnings("unchecked")
			ArrayList<JSONObject> items = JSONArray.parse(jsonObject.get("items").toString());
			for (JSONObject item : items) {
				shieldActivations.add(item.get("value").toString());
			}
			this.userShieldMapCache.put(userId, shieldActivations);
			Logger.getLogger(this.getClass()).log(Level.WARN,
					"User to Shield Mapping after notification: " + this.userShieldMapCache.get(userId));
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).log(Level.WARN, "Notification has not json payload, payload: " + payload);
		}
	}
}