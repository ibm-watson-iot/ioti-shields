package com.ibm.iot4i.services;

import com.ibm.iot4i.models.ListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DeviceService {

	@GET("/api/v1/{tenantId}/devices")
	Call<ListResponse> listDevices(@Path("tenantId") String tenantId, @Query("skip") int skip,
			@Query("limit") int limit, @Query("includeDocs") boolean includeDocs,
			@Query("descending") boolean descending, @Query("userId") String userId,
			@Query("vendorId") String vendorId);
}
