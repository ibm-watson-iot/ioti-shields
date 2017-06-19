package application;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ibm.json.java.JSONObject;
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
import com.ibm.streams.operator.model.OutputPortSet;
import com.ibm.streams.operator.model.OutputPortSet.WindowPunctuationOutputMode;

import com.ibm.streams.operator.model.OutputPorts;
import com.ibm.streams.operator.model.Parameter;
import com.ibm.streams.operator.model.PrimitiveOperator;

@PrimitiveOperator(name = "SimpleShield", namespace = "application", description = "Java Operator SimpleShield")
@InputPorts({
		@InputPortSet(description = "Port that ingests tuples", cardinality = 1, optional = false, windowingMode = WindowMode.NonWindowed, windowPunctuationInputMode = WindowPunctuationInputMode.Oblivious),
		@InputPortSet(description = "Optional input ports", optional = true, windowingMode = WindowMode.NonWindowed, windowPunctuationInputMode = WindowPunctuationInputMode.Oblivious) })
@OutputPorts({
		@OutputPortSet(description = "Port that produces tuples", cardinality = 1, optional = false, windowPunctuationOutputMode = WindowPunctuationOutputMode.Generating),
		@OutputPortSet(description = "Optional output ports", optional = true, windowPunctuationOutputMode = WindowPunctuationOutputMode.Generating) })
public class SimpleShield extends AbstractOperator {

	// enumeration type
	public enum OperationTypes {
		greaterThan, LessThan, equals, greaterThanOrEquals, lessThanOrEquals
	}

	private OperationTypes operationType;
	private String attributeName;
	private String attributeValue;
	private String hazardTitle;
	private String payloadAttributeValue;
	private String shieldId;

	@Parameter(name = "operationType", optional = false)
	public void setOperationType(OperationTypes operationType) {
		this.operationType = operationType;
	}

	@Parameter(name = "attributeName", optional = false)
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	@Parameter(name = "attributeValue", optional = false)
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	@Parameter(name = "hazardTitle", optional = true)
	public void setHazardTitle(String hazardTitle) {
		this.hazardTitle = hazardTitle;
	}

	@Parameter(name = "shieldId", optional = false)
	public void setShieldId(String shieldId) {
		this.shieldId = shieldId;
	}

	@Override
	public synchronized void initialize(OperatorContext context) throws Exception {
		super.initialize(context);
		Logger.getLogger(this.getClass()).log(Level.WARN, "Operator " + context.getName() + " initializing in PE: "
				+ context.getPE().getPEId() + " in Job: " + context.getPE().getJobId());

	}

	private void setPayloadAttributeValue(String payload, String attributeName) {
		JSONObject jsonObject;
		try {
			jsonObject = JSONObject.parse(payload);
			@SuppressWarnings("unchecked")
			Set<String> keys = jsonObject.keySet();
			Iterator<String> iterator = keys.iterator();
			String key = null;
			while (iterator.hasNext()) {
				key = iterator.next();
				if (key.equals(attributeName)) {
					this.payloadAttributeValue = jsonObject.get(key).toString();
				} else {
					setPayloadAttributeValue(jsonObject.get(key).toString(), attributeName);
				}
			}
		} catch (Exception e) {
		}
	}

	private void submitTuple(String payload) {
		try {
			StreamingOutput<OutputTuple> outStream = getOutput(0);
			OutputTuple outTuple = outStream.newTuple();

			JSONObject output = new JSONObject();
			output.put("payload", payload);
			output.put("shieldId", this.shieldId);
			output.put("hazardTitle", this.hazardTitle);
			outTuple.setString("data", output.toString());
			Logger.getLogger(this.getClass()).log(Level.WARN, "submitted tuple: " + outTuple.toString());
			outStream.submit(outTuple);

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).log(Level.WARN,
					"Submitting tuple failed, error: " + e.getLocalizedMessage());
		}
	}

	@Override
	public final void process(StreamingInput<Tuple> inputStream, Tuple tuple) throws Exception {

		String payload = tuple.toString().replaceAll("\\s*\\bTUPLE\\b\\s*", "");
		// remove extra brackets
		payload = payload.substring(1, payload.length() - 1);

		Logger.getLogger(this.getClass()).log(Level.WARN, " attribute name: " + this.attributeName);
		setPayloadAttributeValue(payload, this.attributeName);

		Logger.getLogger(this.getClass()).log(Level.WARN, "payloadAttributeValue: " + this.payloadAttributeValue);

		try {
			if (this.payloadAttributeValue != null) {
				switch (this.operationType.toString()) {
				case "greaterThan":
					if (Double.valueOf(this.payloadAttributeValue) > Double.valueOf(this.attributeValue)) {
						submitTuple(payload);
					}
					break;
				case "LessThan":
					if (Double.valueOf(this.payloadAttributeValue) < Double.valueOf(this.attributeValue)) {
						submitTuple(payload);
					}
					break;
				case "equals":
					if (this.attributeValue.equals(this.payloadAttributeValue)) {
						submitTuple(payload);
					}
					break;
				case "greaterThanOrEquals":
					if (Double.valueOf(this.payloadAttributeValue) >= Double.valueOf(this.attributeValue)) {
						submitTuple(payload);
					}
					break;
				case "lessThanOrEquals":
					if (Double.valueOf(this.payloadAttributeValue) <= Double.valueOf(this.attributeValue)) {
						submitTuple(payload);
					}
					break;
				}
			} else {
				Logger.getLogger(this.getClass()).log(Level.WARN, "Attribute wasn't found in the payload");
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).log(Level.WARN, "Shield logic failed, error: " + e.getLocalizedMessage());
		}
	}

	public synchronized void shutdown() throws Exception {
		OperatorContext context = getOperatorContext();
		Logger.getLogger(this.getClass()).log(Level.WARN, "Operator " + context.getName() + " shutting down in PE: "
				+ context.getPE().getPEId() + " in Job: " + context.getPE().getJobId());

		super.shutdown();
	}
}
