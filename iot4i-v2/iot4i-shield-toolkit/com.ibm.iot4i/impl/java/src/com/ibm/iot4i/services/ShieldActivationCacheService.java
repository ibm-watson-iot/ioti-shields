package com.ibm.iot4i.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.util.logging.Logger;
import com.ibm.streams.operator.logging.*;

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
	private int skip = 0;
	private int limit = 50000;

	private Map<String, ArrayList<String>> userShieldMapCache;
	
	private static final Logger trace = Logger.getLogger(ShieldActivationCacheService.class.getName());
	private static final Logger log = Logger.getLogger(ShieldActivationCacheService.class.getName());

	public ShieldActivationCacheService(String apiToken, String apiURL, String tenantId) {
		this.apiToken = apiToken;
		this.apiURL = apiURL;
		this.tenantId = tenantId;

		this.userShieldMapCache = new HashMap<>();

		shieldActivationService = ServiceGenerator.createService(this.apiURL, this.apiToken,
				ShieldActivationService.class);
	}

	private void setUserShieldActivationsMapping() throws IOException {
		// TODO: Keeping it simple for now. Later a jetty for mockup
		if (this.apiURL.startsWith("https://dummyURL")) { //$NON-NLS-1$
			// dummy shield activations
			ArrayList<String> dummyShieldActivations = new ArrayList<>();
			String dummyShieldId = "dummyShieldId";
			dummyShieldActivations.add(dummyShieldId);
			this.userShieldMapCache.put("dummyUserWithShieldActivation", dummyShieldActivations); //$NON-NLS-1$
			return;
		}

		Call<ListResponse> activationsListCall = this.shieldActivationService.listShieldActivations(this.tenantId,
				this.skip, this.limit, false, false, null, null);
		Response<ListResponse> activationsResponse = activationsListCall.execute();
		if (activationsResponse.isSuccessful()) {
			for (JsonObject item : activationsResponse.body().getItems()) {
				String key = item.get("key").getAsJsonArray().get(0).getAsString();
				ArrayList<String> shieldActivations = userShieldMapCache.get(key);
				if (shieldActivations == null) {
					shieldActivations = new ArrayList<>();
				}

				shieldActivations.add(item.get("value").getAsString());
				userShieldMapCache.put(key, shieldActivations);
			}
			if (activationsResponse.body().getItems().size() >= this.limit) {
				this.skip += this.limit;
				setUserShieldActivationsMapping();
			} else {
				log.log(LogLevel.WARN, "User shield mapping was set successfully ");
			}
		} else {
			log.log(LogLevel.WARN,
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
		log.log(LogLevel.WARN,
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
			log.log(LogLevel.WARN,
					"User to Shield Mapping after notification: " + this.userShieldMapCache.get(userId));
		} catch (Exception e) {
			log.log(LogLevel.ERROR, "Notification has not json payload, payload: " + payload);
		}
	}
}