package com.ibm.iot4i.hazards.common;

import com.ibm.streams.function.model.Function;

public class IsHttpsImpl  {
	
    @Function(namespace="com.ibm.iot4i.hazards.common", name="isHttps", description="", stateful=false)
    public static boolean isHttps(String apiURL) { 
   	    return apiURL.startsWith("https://");
    }
    
}
