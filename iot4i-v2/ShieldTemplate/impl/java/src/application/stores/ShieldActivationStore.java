package application.stores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class ShieldActivationStore extends BaseStore {

	private final static String docType = "shield-activation";

	public ShieldActivationStore(String dbName, String dbHost, String userId, String password) {
		super(dbName, dbHost, userId, password, docType);
	}

	public HashMap<String, ArrayList<String>> getAllUserShieldMapping() {
		HashMap<String, ArrayList<String>> usersShieldsMap = new HashMap<String, ArrayList<String>>();
		for (JSONObject doc : this.queryView("userId", null)) {
			try {
				JSONArray keys = JSONArray.parse(doc.get("key").toString());
				ArrayList<String> shieldIds = usersShieldsMap.get(keys.get(0).toString());
				if (shieldIds == null) {
					shieldIds = new ArrayList<String>();
					shieldIds.add(doc.get("value").toString());
				} else {
					shieldIds.add(doc.get("value").toString());
				}
				usersShieldsMap.put(keys.get(0).toString(), shieldIds);
			} catch (IOException e) {
				Logger.getLogger(this.getClass()).log(Level.WARN,"convert to JSONArray failed: " + e.getLocalizedMessage());
			}
		}
		return usersShieldsMap;
	}

	public ArrayList<String> getUserShieldMapping(String userId) {
		ArrayList<String> results = new ArrayList<String>();
		for (JSONObject doc : this.queryView("userId", userId)) {
			results.add(doc.get("value").toString());
		}
		return results;
	}
}
