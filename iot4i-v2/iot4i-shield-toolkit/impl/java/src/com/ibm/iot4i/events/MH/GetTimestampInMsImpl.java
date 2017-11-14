package com.ibm.iot4i.events.MH;


import com.ibm.streams.function.model.Function;

public class GetTimestampInMsImpl  {

    @Function(namespace="com.ibm.iot4i.events.MH", name="getTimestampInMs", description="", stateful=false)
    public static long getTimestampInMs() {
   	    return System.currentTimeMillis();
    }
    
}
