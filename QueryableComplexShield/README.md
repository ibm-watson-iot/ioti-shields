# Queryable complex shield

This shield shows how to join two data streams together to support more complex use cases. The shield also uses simple JSON queries to filter streams events. The JSON queries are based on [JSONPath](https://github.com/json-path/JsonPath). 

To test your JSON queries, you can use the [JSONPath Evaluator website](http://jsonpath.herokuapp.com/). You can put the event data coming from the sensor in a JSON format and run the JSON queries against it.  

## How the shield works

The shield has the following IBM streams operators:

1. A [Filter operator](https://www.ibm.com/support/knowledgecenter/en/SSCRJU_4.2.1/com.ibm.streams.toolkits.doc/spldoc/dita/tk$spl/op$spl.relational$Filter.html) at the beginning of each data stream to filter the events based on the shield logic.
2. A [Join operator](https://www.ibm.com/support/knowledgecenter/en/SSCRJU_4.2.1/com.ibm.streams.toolkits.doc/spldoc/dita/tk$spl/op$spl.relational$Join.html), which is a power operator that enables you to correlate events from two streams that are based on user-specified match predicates and window configurations. This operator is used in this shield to join and partition the two streams based on an event's userId. 

The events in both streams are filtered by using JSON queries as an input which is executed by a helper Java function [executeJsonQuery(message, query)](./impl/java/src/com/ibm/iot4i/examples/ExecuteJsonQueryImpl.java).

The main shield code is [here](./com.ibm.iot4i.examples/QueryableComplexShield.spl)

![Queryable Complex Shield](./images/queryable-complex-shield.png)

## Using the shield

This shield expects the following parameters:

- **stream1EntryConditionJsonQueries** : The JSON queries that provide an entry condition to filter events that do not belong to this shield.
- **stream2EntryConditionJsonQueries** : The JSON queries that provide the main shield logic.
- **stream1SlidingWindowInSeconds** : The time-based eviction policy time in seconds for the first stream. For more information, see [SPL sliding windows explained](https://developer.ibm.com/streamsdev/2014/08/22/spl-sliding-windows-explained/).
- **actionParams** : The parameters that are needed by the external action, as shown in the following example:

```json
{
   "hazardTitle":"A hazard was detected.",
   "emailSubject":"Alert from IoT for Insurance",
   "emailText":"Hello IoT for Insurance user. You have a hazard!!!"
}

 ```


These parameters need to be provided when creating or updating the shield code by using the [createShieldCode API](https://ioti.us-south.containers.mybluemix.net/docs/#!/shield-codes/createShieldCode) or [updateShieldCode API](https://ioti.us-south.containers.mybluemix.net/docs/#!/shield-codes/updateShieldCode). The parameters are provided as part of the **jobOptions** field as shown in the following example:
 
 ```json
{
   "jobOptions":{
      "stream1EntryConditionJsonQueries": ["", ""],
      "stream2EntryConditionJsonQueries": ["", ""],
      "stream1SlidingWindowInSeconds": 30,
      "actionParams": ""
   }
}
 
 ```

## Examples

The following examples show what this shield can do. 

### Severe temperature Shield 

The use case for this shield is that it determines if a physical window in a house is opened and after some time a severe temperature is detected. The contact sensor events are in the first stream and temperature events are in the second stream. The shield joins both streams based on userId and generates a hazard. The time that the shield waits after the contact sensor event arrives to check for a severe temperature event is configured by using the `stream1SlidingWindowInSeconds` parameter.

This example shield uses the Wally contact and temperature sensors. 

**Wally Contact Sensor Event Payload** :

 ```json
{
   "deviceType":"Wally",
   "id":"2fd2a0b6-e38d-4dda-a315-db51860362aa",
   "snid":"90-7a-f1-00-51-d6-bf-a6",
   "gatewayId":"someVendorId",
   "type":"SENSOR",
   "hwType":8,
   "location":{
      "room":"Utility",
      "floor":"Basement",
      "appliance":"Utility Room"
   },
   "data_type":"EVENT",
   "traitStates":{
      "traitStates":{ 
        "ContactSense":{
            "traitType":"contactSenseTrait",
            "updated":"2017-09-06T00:51:41.325Z",
            "contactDetected":true
         }
      }
   }
}

```

**Wally Temperature Sensor Event Payload** :

 ```json
{
   "deviceType":"Wally",
   "id":"2fd2a0b6-e38d-4dda-a315-db51860362aa",
   "snid":"90-7a-f1-00-51-d6-bf-a6",
   "gatewayId":"someVendorId",
   "type":"SENSOR",
   "hwType":8,
   "location":{
      "room":"Utility",
      "floor":"Basement",
      "appliance":"Utility Room"
   },
   "data_type":"EVENT",
   "traitStates":{
      "traitStates":{
         "Temperature":{
            "traitType":"temperatureTrait",
            "updated":"2017-09-22T19:02:12.086Z",
            "temperatureC": 44
         }
      }
   }
}

```


In this case, the shield **jobOptions** are as follows:     

 ```json
{
   "jobOptions":{
      "stream1EntryConditionJsonQueries":[
         "$.traitStates.traitStates[?(@.Temperature.temperatureC > 42)]"
      ],
      "stream2EntryConditionJsonQueries":[
         "$.traitStates.traitStates[?(@.ContactSense.contactDetected == true )]"
      ],
      "stream1SlidingWindowInSeconds": 30,
      "actionParams":{
         "hazardTitle":"A severe temperature was detected !",
         "emailSubject":"Alert from IoT for Insurance",
         "emailText":"Hello IoT for Insurance user. You have a hazard!!!"
      }
   }
}

```
