package com.ibm.iot4i.services;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ServiceGenerator {

	static Logger logger = Logger.getLogger(ServiceGenerator.class);

	public static <S> S createService(String apiURL, String apiToken, Class<S> serviceClass) {

		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		httpClient.addInterceptor(new Interceptor() {
			@Override
			public Response intercept(Interceptor.Chain chain) throws IOException {
				Request original = chain.request();

				Request request = original.newBuilder().header("Authorization", "Bearer " + apiToken)
						.method(original.method(), original.body()).build();

				return chain.proceed(request);
			}
		});

		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };

		// TODO: add a certificate instead of allowing all.
		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new SecureRandom());
			httpClient.sslSocketFactory(sc.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
			logger.log(Level.WARN, "trust manager installed");
		} catch (Exception e) {
			// logger.log(Level.WARN, "Failed to install trust manager" + e);
		}

		OkHttpClient client = httpClient.build();
		Retrofit retrofit = new Retrofit.Builder().baseUrl(apiURL).addConverterFactory(GsonConverterFactory.create())
				.client(client).build();

		return retrofit.create(serviceClass);
	}
}

