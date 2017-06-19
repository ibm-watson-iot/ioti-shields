package application.stores;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.views.Key;
import com.cloudant.client.api.views.Key.ComplexKey;
import com.cloudant.client.api.views.ViewRequest;
import com.cloudant.client.api.views.ViewRequestBuilder;
import com.cloudant.client.api.views.ViewResponse;
import com.ibm.json.java.JSONObject;

public class BaseStore {

	private String docType;
	private final String designName = "iot4i";
	private Database db;

	protected BaseStore(String dbName, String dbHost, String userId, String password, String docType) {
		CloudantClient client = ClientBuilder.account(dbHost.replaceAll("\\s*\\b.cloudant.com\\b\\s*", "")).username(userId).password(password).build();
		this.db = client.database(dbName, false);
		this.docType = docType;
	}

	protected ArrayList<JSONObject> queryView(String viewName, String property) {
		ArrayList<JSONObject> results = new ArrayList<JSONObject>();
		try {
			Logger.getLogger(this.getClass()).log(Level.WARN,"Query started for viewName: " + viewName + " and property: " + property);
			
			ComplexKey startKey = (property != null) ? Key.complex(property) : null;
			ComplexKey endKey = (property != null) ? Key.complex(property).add("") : null;
			viewName = (viewName == null) ? this.docType + "s" : this.docType + "s_by_" + viewName;

			ViewRequestBuilder viewBuilder = db.getViewRequestBuilder(this.designName, viewName);
			ViewRequest<ComplexKey, String> request;

			if (property != null) {
				request = viewBuilder.newRequest(Key.Type.COMPLEX, String.class).startKey(startKey).endKey(endKey)
						.build();
			} else {
				request = viewBuilder.newRequest(Key.Type.COMPLEX, String.class).build();
			}

			ViewResponse<ComplexKey, String> response = null;
			try {
				response = request.getResponse();
			} catch (IOException e) {
				Logger.getLogger(this.getClass()).log(Level.WARN,"no data found, error:" + e.getLocalizedMessage());
			}

			if (response != null) {
				for (ViewResponse.Row<ComplexKey, String> row : response.getRows()) {
					JSONObject doc = new JSONObject();
					doc.put("key", row.getKey().toJson());
					doc.put("value", row.getValue());
					results.add(doc);
				}
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).log(Level.WARN,"query view failed, error: " + e.getLocalizedMessage());
		}

		return results;
	}

}
