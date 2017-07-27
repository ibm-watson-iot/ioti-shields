package com.ibm.iot4i.services;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;

public class ServiceGenerator {

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

		OkHttpClient client = httpClient.build();
		Retrofit retrofit = new Retrofit.Builder().baseUrl(apiURL).addConverterFactory(GsonConverterFactory.create())
				.client(client).build();

		return retrofit.create(serviceClass);
	}
}
