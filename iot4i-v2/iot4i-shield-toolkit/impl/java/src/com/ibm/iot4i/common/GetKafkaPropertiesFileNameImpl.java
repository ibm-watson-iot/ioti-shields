package com.ibm.iot4i.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import java.util.logging.Logger;
import com.ibm.streams.operator.logging.*;

import com.ibm.streams.function.model.Function;

public class GetKafkaPropertiesFileNameImpl {

	private static final Logger trace = Logger.getLogger(GetKafkaPropertiesFileNameImpl.class.getName());
	private static final Logger log = Logger.getLogger("com.ibm.streams.operator.log");

	@Function(namespace = "com.ibm.iot4i.common", name = "getKafkaPropertiesFileName", description = "", stateful = false)
	public static String getKafkaPropertiesFileName(String dir, String kafkaBrokerSasl, String username,
			String password, String groupId, String clientId, String securityProtocol, String saslMechanism,
			String sslProtocol, String sslEnabledProtocol, String sslTrustStoreType, String sslEndpointIdAlgorithm,
			String retriesNumber, String requestTimeoutMs, String maxBlockMs) {
		try {
			String fileName = dir + "/etc/kafka.properties";
			Properties properties = new Properties();
			properties.setProperty("bootstrap.servers", kafkaBrokerSasl);
			properties.setProperty("sasl.jaas.config",
					"org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + username
							+ "\" password=\"" + password + "\";");
			properties.setProperty("group.id", groupId);
			if (!retriesNumber.equals(""))
				properties.setProperty("retries", retriesNumber);
			if (!requestTimeoutMs.equals(""))
				properties.setProperty("request.timeout.ms", requestTimeoutMs);
			if (!maxBlockMs.equals(""))
				properties.setProperty("max.block.ms", maxBlockMs);
			properties.setProperty("client.id", clientId);
			properties.setProperty("security.protocol", securityProtocol);
			properties.setProperty("sasl.mechanism", saslMechanism);
			properties.setProperty("ssl.protocol", sslProtocol);
			properties.setProperty("ssl.enabled.protocols", sslEnabledProtocol);
			properties.setProperty("ssl.truststore.type", sslTrustStoreType);
			properties.setProperty("ssl.endpoint.identification.algorithm", sslEndpointIdAlgorithm);

			File file = new File(fileName);
			file.getParentFile().mkdirs();
			file.createNewFile();
			FileOutputStream fileOut = new FileOutputStream(file);
			properties.store(fileOut, "Kafka properties");
			fileOut.close();

			return fileName;
		} catch (FileNotFoundException e) {
			log.log(LogLevel.ERROR, "get kafka properties file name failed, error: " + e.getLocalizedMessage());
		} catch (IOException e) {
			log.log(LogLevel.ERROR, "get kafka properties file name failed, error: " + e.getLocalizedMessage());
		}

		return "";
	}

}
