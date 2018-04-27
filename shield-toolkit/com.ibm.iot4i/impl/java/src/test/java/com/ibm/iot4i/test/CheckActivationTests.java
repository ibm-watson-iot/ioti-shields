package com.ibm.iot4i.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibm.iot4i.events.common.CheckActivationOp;
import com.ibm.streams.flow.declare.InputPortDeclaration;
import com.ibm.streams.flow.declare.OperatorGraph;
import com.ibm.streams.flow.declare.OperatorGraphFactory;
import com.ibm.streams.flow.declare.OperatorInvocation;
import com.ibm.streams.flow.declare.OutputPortDeclaration;
import com.ibm.streams.flow.handlers.StreamCollector;
import com.ibm.streams.flow.javaprimitives.JavaOperatorTester;
import com.ibm.streams.flow.javaprimitives.JavaTestableGraph;
import com.ibm.streams.operator.OutputTuple;
import com.ibm.streams.operator.StreamingOutput;
import com.ibm.streams.operator.Tuple;
import com.ibm.iot4i.test.utils.*;

public class CheckActivationTests {

	private static final String wallyKey = "{\"orgId\":\"sy6pz0\",\"deviceType\":\"Wally\",\"deviceId\":\"90-7a-f1-00-51-d6-2a-70\",\"eventType\":\"event\",\"format\":\"json\",\"timestamp\":\"2017-12-06T04:42:10.708Z\"}"; //$NON-NLS-1$
	private static final String userWithShieldActivation = "dummyUserWithShieldActivation";
	private static final String userWithoutShieldActivation = "dummyUserWithoutShieldActivation";
	private JavaTestableGraph testableGraph;

	@Test
	public void TestShieldActivationExists() throws Exception {
		String eventPath = "/wally_event.json";
		InputStream isEvent = this.getClass().getResourceAsStream(eventPath); // $NON-NLS-1$

		Tuple outTuple = sendTupleToOp(userWithShieldActivation, wallyKey, Helper.convertStreamToString(isEvent));
		verifyOutput(outTuple,  eventPath,  wallyKey, userWithShieldActivation);

		shutDowngraph();
	}

	@Test
	public void TestShieldActivationNotExists() throws Exception {
		String eventPath = "/wally_event.json";
		InputStream isEvent = this.getClass().getResourceAsStream(eventPath); // $NON-NLS-1$

		Tuple outTuple = sendTupleToOp(userWithoutShieldActivation, wallyKey, Helper.convertStreamToString(isEvent));
		assertEquals(outTuple, null);

		shutDowngraph();
	}

	private void shutDowngraph() throws InterruptedException, ExecutionException {
		testableGraph.shutdown().get();
	}

	private Tuple sendTupleToOp(String userId, String key, String message) throws Exception {
		OperatorGraph graph = OperatorGraphFactory.newGraph();
		OperatorInvocation<CheckActivationOp> op = graph.addOperator("CheckActivationOp", CheckActivationOp.class); //$NON-NLS-1$
		op.setStringParameter("apiToken", "dummyApiToken"); //$NON-NLS-1$ //$NON-NLS-2$
		op.setStringParameter("apiURL", "https://dummyURL/"); //$NON-NLS-1$ //$NON-NLS-2$
		op.setStringParameter("tenantId", "dummyTenant"); //$NON-NLS-1$ //$NON-NLS-2$
		op.setStringParameter("shieldId", "dummyShieldId"); //$NON-NLS-1$ //$NON-NLS-2$

		InputPortDeclaration inputPort = op.addInput("tuple<rstring userId, rstring key, rstring message>"); //$NON-NLS-1$
		OutputPortDeclaration outputPort = op.addOutput("tuple<rstring userId, rstring key, rstring message>"); //$NON-NLS-1$

		List<Tuple> output = new ArrayList<Tuple>();
		StreamCollector<List<Tuple>, Tuple> collector = new StreamCollector<List<Tuple>, Tuple>(output);

		testableGraph = new JavaOperatorTester().executable(graph);
		testableGraph.registerStreamHandler(outputPort, collector);

		testableGraph.initialize().get().allPortsReady().get();

		StreamingOutput<OutputTuple> injector = testableGraph.getInputTester(inputPort);
		OutputTuple tuple = injector.newTuple();
		tuple.setString("userId", userId); //$NON-NLS-1$
		tuple.setString("key", key); //$NON-NLS-1$
		tuple.setString("message", message); //$NON-NLS-1$
		injector.submit(tuple);

		Iterator<Tuple> it = output.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	private void verifyOutput(Tuple outTuple, String eventPath, String expectedKey, String expectedUserId)
			throws IOException {
		String userId = outTuple.getString("userId"); //$NON-NLS-1$
		String key = outTuple.getString("key"); //$NON-NLS-1$
		String message = outTuple.getString("message"); //$NON-NLS-1$

		assertEquals(userId, expectedUserId); //$NON-NLS-1$
		assertEquals(key, expectedKey); // $NON-NLS-1$
		this.verifyEvent(message, eventPath);
	}

	private void verifyEvent(String event, String eventPath) throws IOException {
		InputStream isEvent = this.getClass().getResourceAsStream(eventPath); // $NON-NLS-1$
		ObjectMapper mapper = new ObjectMapper();
		JsonNode eventJSON = mapper.readTree(event);
		JsonNode expectedEventJSON = mapper.readTree(isEvent);
		assertEquals(eventJSON, expectedEventJSON);
	}
}
