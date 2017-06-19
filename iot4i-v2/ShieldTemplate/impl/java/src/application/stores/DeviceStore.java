package application.stores;

import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class DeviceStore extends BaseStore {

	private final static String docType = "device";

	public DeviceStore(String dbName, String dbHost, String userId, String password) {
		super(dbName, dbHost, userId, password, docType);
	}

	public HashMap<String, String> getAllUserDeviceMapping() {
		HashMap<String, String> usersDevices = new HashMap<String, String>();
		for (JSONObject doc : this.queryView("vendorId", null)) {
			try {
				JSONArray keys = JSONArray.parse(doc.get("key").toString());
				usersDevices.put(keys.get(0).toString(), doc.get("value").toString());
			} catch (IOException e) {
				Logger.getLogger(this.getClass()).log(Level.WARN,"convert to JSONArray failed: " + e.getLocalizedMessage());
			}
		}
		return usersDevices;
	}

	public String getUserDeviceMapping(String deviceId) {
		return this.queryView("vendorId", deviceId).get(0).get("value").toString();
	}

}
