package com.ibm.iot4i.examples;

import java.util.Base64;
import java.util.List;

import java.util.logging.Logger;
import com.ibm.streams.operator.logging.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.streams.function.model.Function;
import com.ibm.streams.toolkit.model.ToolkitLibraries;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

@ToolkitLibraries("impl/lib/*")
public class ExecuteJsonQueryImpl {

	static Configuration conf = Configuration.defaultConfiguration().addOptions(Option.AS_PATH_LIST)
			.addOptions(Option.ALWAYS_RETURN_LIST).addOptions(Option.SUPPRESS_EXCEPTIONS);

	private static final Logger trace = Logger.getLogger(ExecuteJsonQueryImpl.class.getName());
	private static final Logger log = Logger.getLogger("com.ibm.streams.operator.log");
	
	@Function(namespace = "com.ibm.iot4i.examples", name = "executeJsonQuery", description = "", stateful = false)
	public static boolean executeJsonQuery(String message, String[] jsonQueries, String operatorName) {
		try {
			JsonObject messageJson = new JsonParser().parse(message).getAsJsonObject();
			String rawEvent = messageJson.getAsJsonObject("event").toString();
			log.log(LogLevel.WARN, operatorName + ": Incoming event: " + rawEvent);
			Object parsedRawEvent = conf.jsonProvider().parse(rawEvent);

			for (String jsonQuery : jsonQueries) {
				// check if the query is encoded
				if (!jsonQuery.startsWith("$")) {
					try {
						byte[] decodedQueryBytes = Base64.getDecoder().decode(jsonQuery);
						jsonQuery = new String(decodedQueryBytes);
					} catch (Exception e) {
						e.printStackTrace();
						log.log(LogLevel.ERROR,
								operatorName + ": Decoding base 64 string failed: " + e.getLocalizedMessage());
						return false;
					}
				}
				log.log(LogLevel.WARN, operatorName + ": Json query: " + jsonQuery);
				List<Object> results = JsonPath.read(parsedRawEvent, jsonQuery);
				if (results.size() != 0) {
					log.log(LogLevel.WARN, operatorName + ": Event passed json query, results: " + results);
					return true;
				}
			}

			log.log(LogLevel.WARN, operatorName + ": Event failed json query");
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			log.log(LogLevel.WARN, operatorName + ": Executing json query failed, error: " + e.getMessage());
			return false;
		}
	}

}
