package com.ibm.iot4i.examples;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ibm.json.java.JSON;
import com.ibm.streams.operator.AbstractOperator;
import com.ibm.streams.operator.OperatorContext;
import com.ibm.streams.operator.OutputTuple;
import com.ibm.streams.operator.StreamingInput;
import com.ibm.streams.operator.StreamingOutput;
import com.ibm.streams.operator.Tuple;
import com.ibm.streams.operator.model.InputPortSet;
import com.ibm.streams.operator.model.InputPortSet.WindowMode;
import com.ibm.streams.operator.model.InputPortSet.WindowPunctuationInputMode;
import com.ibm.streams.operator.model.InputPorts;
import com.ibm.streams.operator.model.Libraries;
import com.ibm.streams.operator.model.OutputPortSet;
import com.ibm.streams.operator.model.OutputPortSet.WindowPunctuationOutputMode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.ibm.streams.operator.model.OutputPorts;
import com.ibm.streams.operator.model.Parameter;
import com.ibm.streams.operator.model.PrimitiveOperator;
import com.ibm.streams.operator.state.Checkpoint;
import com.ibm.streams.operator.state.StateHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Libraries("impl/lib/*")
@PrimitiveOperator(name = "JsonQueryStateTimeFilterOP", namespace = "com.ibm.iot4i.examples", description = "Java Operator JsonQueryStateTimeFilterOP")
@InputPorts({
		@InputPortSet(description = "Port that ingests tuples", cardinality = 1, optional = false, windowingMode = WindowMode.NonWindowed, windowPunctuationInputMode = WindowPunctuationInputMode.Oblivious) })
@OutputPorts({
		@OutputPortSet(description = "Port that produces tuples", cardinality = 1, optional = false, windowPunctuationOutputMode = WindowPunctuationOutputMode.Generating) })
public class JsonQueryStateTimeFilterOP extends AbstractOperator implements StateHandler {

	static Configuration conf = Configuration.defaultConfiguration()
			.addOptions(Option.ALWAYS_RETURN_LIST).addOptions(Option.SUPPRESS_EXCEPTIONS);
	static Logger logger = Logger.getLogger(JsonQueryStateTimeFilterOP.class);

	private Map<String, Long> timePeriodCache;
	private Map<String, Boolean> stateCache;

	private List<String> jsonQueries = new ArrayList<String>();
	private Long timePeriod = (long) 0;
	private Boolean needResetState = false;
	private String eventTimeKey = "";

	@Override
	public synchronized void initialize(OperatorContext context) throws Exception {
		super.initialize(context);

		Logger.getLogger(this.getClass()).log(Level.WARN, "Operator " + context.getName() + " initializing in PE: "
				+ context.getPE().getPEId() + " in Job: " + context.getPE().getJobId());

		this.stateCache = new HashMap<>();
		this.timePeriodCache = new HashMap<>();
	}

	@Parameter(name="jsonQueries", optional=false)
	public void setJsonQueries(String jsonQueries) {
	   	this.jsonQueries.clear();
	   	for(JsonElement elem: new JsonParser().parse(jsonQueries).getAsJsonArray()) {
	   		this.jsonQueries.add(elem.getAsString());
	   	}
	}

	@Parameter(name="timePeriod", optional=true)
	public void setTimePeriod(String timePeriod) {
	   	this.timePeriod = Long.parseLong(timePeriod);
	}

	@Parameter(name="needResetState", optional=true)
	public void setNeedResetState(String needResetState) {
		this.needResetState = Boolean.parseBoolean(needResetState);
	}

	@Parameter(name="eventTimeKey", optional=true)
	public void setEventTimeKey(String eventTimeKey) {
		this.eventTimeKey = eventTimeKey;
	}

	@Override
	public final void process(StreamingInput<Tuple> inputStream, Tuple tuple) throws Exception {
		boolean pass = executeJsonQuery(tuple.getString("message"));
		if (pass) {
			StreamingOutput<OutputTuple> outStream = getOutput(0);
			OutputTuple outTuple = outStream.newTuple();
			outTuple.assign(tuple);
			JsonObject messageObj = new JsonParser().parse(tuple.getString("message")).getAsJsonObject();
			String vendorId = messageObj.get("vendorId").getAsString();
			outTuple.setString("vendorId", vendorId);
			outStream.submit(outTuple);
		}
	}

	public boolean executeJsonQuery(String message) {
		String operatorName = this.getOperatorContext().getLogicalName();
		try {
			JsonObject messageJson = new JsonParser().parse(message).getAsJsonObject();
			String rawEvent = messageJson.getAsJsonObject("event").toString();
			logger.log(Level.WARN, operatorName + ": Incoming event: " + rawEvent);
			DocumentContext parsedRawEvent = JsonPath.using(conf).parse(rawEvent);

			for (String jsonQuery : jsonQueries) {
				// check if the query is encoded

				String vendorId = messageJson.get("vendorId").getAsString();
				logger.log(Level.WARN, operatorName + ": Json query: " + jsonQuery);
				List<Object> results = parsedRawEvent.read(jsonQuery);
				logger.log(Level.WARN, operatorName + ": Json query results: " + results);
				boolean pastJsonQuery = !results.isEmpty();

				Boolean timeDidPass = null;
				Long eventTime = (long) -1;
				if (timePeriod > 0) {
					List<Object> timestamps = parsedRawEvent.read(eventTimeKey);
					for(Object eventTimeElement: timestamps) {
						boolean isNumber = eventTimeElement.toString().chars().allMatch(Character::isDigit);
						Long otherTime;
						if (isNumber) {
							otherTime = Long.parseLong(eventTimeElement.toString());
						} else {
							otherTime = Instant.parse(eventTimeElement.toString()).getEpochSecond();
						}
						eventTime = Math.max(otherTime, eventTime);
					}
					if (eventTime == -1) {
						eventTime = Instant.parse(messageJson.get("arrivedAtMH").getAsString()).getEpochSecond();
					}
					timeDidPass = eventTime - timePeriodCache.getOrDefault(vendorId, (long) 0) > timePeriod;
				}

				Boolean stateChanged = null;
				if (needResetState) {
					stateChanged = false;
					if (!stateCache.containsKey(vendorId) && pastJsonQuery) {
						stateChanged = true;
					}
				}
				logger.log(Level.WARN, operatorName + ": needResetState: " + needResetState);
				logger.log(Level.WARN, operatorName + ": stateChanged: " + stateChanged);
				logger.log(Level.WARN, operatorName + ": timeDidPass: " + timeDidPass);
				logger.log(Level.WARN, operatorName + ": timePeriodCache: " + timePeriodCache.getOrDefault(vendorId, null));
				logger.log(Level.WARN, operatorName + ": stateCache: " + stateCache.getOrDefault(vendorId, null));

				if (!results.isEmpty()) {
					if (needResetState && !stateChanged) {
						logger.log(Level.WARN, operatorName + ": not passing - state did not change");
						return false;
					}
					if (timePeriod > 0 && !timeDidPass) {
						logger.log(Level.WARN, operatorName + ": not passing - time did not pass");
						return false;
					}
					logger.log(Level.WARN, operatorName + ": Event passed json query, results: " + results);

					if (timePeriod > 0) {
						timePeriodCache.put(vendorId, eventTime);
					}
					if (needResetState) {
						stateCache.put(vendorId, true);
					}
					return true;
				}
				if (stateCache.containsKey(vendorId)) {
					stateCache.remove(vendorId);
				}
			}

			logger.log(Level.WARN, operatorName + ": Event failed json query" );
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.WARN, operatorName + ": Executing json query failed, error: " + e.getMessage());
			return false;
		}
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkpoint(Checkpoint checkpoint) throws Exception {
		checkpoint.getOutputStream().writeObject(this.stateCache);
		checkpoint.getOutputStream().writeObject(this.timePeriodCache);
	}

	@Override
	public void drain() throws Exception {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	@Override
	public void reset(Checkpoint checkpoint) throws Exception {
		this.stateCache = (Map<String, Boolean>) checkpoint.getInputStream().readObject();
		this.timePeriodCache = (Map<String, Long>) checkpoint.getInputStream().readObject();
	}

	@Override
	public void resetToInitialState() throws Exception {
		this.stateCache.clear();
		this.timePeriodCache.clear();
	}

	@Override
	public void retireCheckpoint(long id) throws Exception {
		// TODO Auto-generated method stub

	}
}
