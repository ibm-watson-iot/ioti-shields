# Queryable shield for single source 


The queryable simple shield, also known as the queryable shield for single source, uses simple JSON queries for the events that come from a single data source. 
The JSON queries are based on [JSONPath](https://github.com/json-path/JsonPath). 
The JSON queries are simple to learn as they use the same structure as XPath expressions. 
To test your JSON queries, you can use the [JSONPath Evaluator website](http://jsonpath.herokuapp.com/). 
You can put the event data coming from the sensor in a JSON format and run the JSON queries against it.

## How the shield works

The shield has the following IBM streams operators:

1. A [Filter operator](https://www.ibm.com/support/knowledgecenter/en/SSCRJU_4.2.1/com.ibm.streams.toolkits.doc/spldoc/dita/tk$spl/op$spl.relational$Filter.html) at the beginning of the shield which filter events that are based on an entry conditions. 
2. A [Functor operator](https://www.ibm.com/support/knowledgecenter/en/SSCRJU_4.2.1/com.ibm.streams.toolkits.doc/spldoc/dita/tk$spl/op$spl.relational$Functor.html) that generate hazards that are based on shield conditions.

Both operators use JSON queries as an input which is executed by using a helper Java function [executeJsonQuery(message, query)](./impl/java/src/com/ibm/iot4i/examples/ExecuteJsonQueryImpl.java).

![Queryable Simple Shield](./images/queryable-simple-shield.png)


## Using the shield

This shield expects the following parameters:

- **entryConditionJsonQueries** : The JSON queries that provide an entry condition to filter events that do not belong to this shield.
- **shieldJsonQueries** : The JSON queries that provide the main shield logic.
- **hazardTimeInterval** : The minimum time between hazards in seconds.
- **hazardNeedsStateReset** : If set to `true`, the shield generates a new hazard only if the state changes.
- **eventTimePath** : The JSONPath to the event's timestamp. Required for `hazardTimeInterval`.
- **actionParams** : The parameters that are needed by the external action as shown in the example below:

```json
{
   "hazardTitle":"A potential water leak was detected by the humidity sensor.",
   "emailSubject":"Alert from IoT for Insurance",
   "emailText":"Hello IoT for Insurance user. You have a hazard!!!"
}

 ```


These parameters need to be provided when creating or updating the shield code by using [createShieldCode API](https://ioti.us-south.containers.mybluemix.net/docs/#!/shield-codes/createShieldCode) or the [updateShieldCode API](https://ioti.us-south.containers.mybluemix.net/docs/#!/shield-codes/updateShieldCode). The parameters are provided as part of the **jobOptions** field as shown in the following example:
 
 ```json
{
   "jobOptions":{
      "entryConditionJsonQueries": ["", ""],
      "shieldJsonQueries": ["" , ""],      
      "actionParams": "",
      // optional, values here are the default ones
      "hazardTimeInterval": 0 // in seconds,
      "hazardNeedsStateReset": false // only create new hazard if state changed,
      "eventTimePath": "$.timestamp" // path to events timestamp
   }
}
 
 ```

## Examples

The following Wally shield, EnOcean shield, and vendor independent shield examples demonstrate what the queryable simple shield can do.

### Wally Shields 

The following Wally shield examples show what job options need to be provided for this shield to behave differently and cover different use cases that are based on Wally sensor event data. 

See the following example of a Wally sensor event:

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
         "Humidity":{
            "traitType":"humidityTrait",
            "updated":"2017-09-22T19:02:12.086Z",
            "humidityPct":65
         },
         "WaterSense":{
            "traitType":"waterSenseTrait",
            "updated":"2017-09-22T19:02:12.086Z",
            "waterPresent":true
         },
         "Temperature":{
            "traitType":"temperatureTrait",
            "updated":"2017-09-22T19:02:12.086Z",
            "temperatureC":5
         },
         "ContactSense":{
            "traitType":"contactSenseTrait",
            "updated":"2017-09-06T00:51:41.325Z",
            "contactDetected":true
         }
      }
   }
}

```

#### Wally humidity shield
 
 ```Json
 {
    "jobOptions":{
       "entryConditionJsonQueries": ["$.traitStates.traitStates[?(@.Humidity.humidityPct)]"],
       "shieldJsonQueries": ["$.traitStates.traitStates[?(@.Humidity.humidityPct > 60)]" ],
       "actionParams": {
          "hazardTitle":"A potential water leak was detected by the humidity sensor.",
          "emailSubject":"Alert from IoT for Insurance",
          "emailText":"Hello IoT for Insurance user. You have a hazard!!!"
       },
       "hazardTimeInterval": 3600,
       "hazardNeedsStateReset": true,
       "eventTimePath": "$.traitStates.traitStates.Humidity.updated"
    }
 }
 ```
 In this case a hazard is generated only once. 
 A new hazard will be generated only after the humidityPct goes over 60 again.
 If `hazardTimeInterval` is specified, the hazard is generated only after the time interval has passed since the last hazard even if the state changed.

#### Wally temperature shield
 
 ```Json
 {
    "jobOptions":{
       "entryConditionJsonQueries": ["$.traitStates.traitStates[?(@.Temperature.temperatureC)]"],
       "shieldJsonQueries": ["$.traitStates.traitStates[?(@.Temperature.temperatureC < 6)]"],
       "actionParams": {
          "hazardTitle":"A low temperature detected !",
          "emailSubject":"Alert from IoT for Insurance",
          "emailText":"Hello IoT for Insurance user. You have a hazard!!!"
       },
       "hazardTimeInterval": 3600,
       "hazardNeedsStateReset": true,
       "eventTimePath": "$.traitStates.traitStates.Temperature.updated"
    }
 }
 ``` 
 
#### Wally door open shield
  
 ```Json
  {
     "jobOptions":{
        "entryConditionJsonQueries": ["$.traitStates.traitStates[?(@.ContactSense.contactDetected)]"],
        "shieldJsonQueries": ["$.traitStates.traitStates[?(@.ContactSense.contactDetected == true)]"],
        "actionParams": {
           "hazardTitle":"A door is opened !",
           "emailSubject":"Alert from IoT for Insurance",
           "emailText":"Hello IoT for Insurance user. You have a hazard!!!"
        },
        "hazardTimeInterval": 3600,
        "hazardNeedsStateReset": true,
        "eventTimePath": "$.traitStates.traitStates.ContactSense.updated"
     }
  }
  ``` 
  
#### Wally water leakage shield
   
 ```Json
   {
      "jobOptions":{
         "entryConditionJsonQueries": ["$.traitStates.traitStates[?(@.WaterSense.waterPresent)]"],
         "shieldJsonQueries": ["$.traitStates.traitStates[?(@.WaterSense.waterPresent == true)]"],
         "actionParams": {
            "hazardTitle":"Water has been detected !",
            "emailSubject":"Alert from IoT for Insurance",
            "emailText":"Hello IoT for Insurance user. You have a hazard!!!"
         },
         "hazardTimeInterval": 3600,
         "hazardNeedsStateReset": true,
         "eventTimePath": "$.traitStates.traitStates.WaterSense.updated"
      }
   }
 ```  
 
#### Wally complex condition shield
    
 ```Json
    {
       "jobOptions":{
          "entryConditionJsonQueries": [".traitStates.traitStates[?(@.Humidity.humidityPct && @.ContactSense.contactDetected &&  @.Temperature.temperatureC)]"],
          "shieldJsonQueries": ["$.traitStates.traitStates[?(@.Humidity.humidityPct>60 && @.Temperature.temperatureC < 6 && @.ContactSense.contactDetected == true && @.WaterSense.waterPresent == true)]"],
          "actionParams": {
             "hazardTitle":"Hazard has been detected !",
             "emailSubject":"Alert from IoT for Insurance",
             "emailText":"Hello IoT for Insurance user. You have a hazard!!!"
          }
       }
    }
 ```  
   
### Enocean Shields 

The following examples show what job options need to be provided for this shield to behave differently and cover different use cases based on EnOcean sensor event data. See the following example of an EnOcean sensor event:   

 ```Json
{
   "d":{
      "addOns":{
         "gatewayId":"someVendorId"
      },
      "states":{
         "buttonB0":{
            "value":"released"
         }
      }
   }
}
```  
#### EnOcean panic button shield
    
 ```Json
    {
       "jobOptions":{
          "entryConditionJsonQueries": ["$.d.states[?(@.buttonB0.value || @.buttonBI.value)]"],
          "shieldJsonQueries": ["$.d.states[?(@.buttonB0.value == 'released' || @.buttonBI.value == 'released')]"],
          "actionParams": {
             "hazardTitle":"A panic button was pressed !",
             "emailSubject":"Alert from IoT for Insurance",
             "emailText":"Hello IoT for Insurance user. You have a hazard!!!"
          }
       }
    }
 ```  
   
### Vendor-independent door open shield

It is also possible to have a generic shield that runs multiple queries on the incoming events. In the following example, the door open shield accepts events from EnOcean and Wally sensors.

 ```Json
    {
       "jobOptions":{
          "entryConditionJsonQueries": ["$.traitStates.traitStates[?(@.ContactSense.contactDetected)]", "$.d.states[?(@.contact.value)]"],
          "shieldJsonQueries": ["$.traitStates.traitStates[?(@.ContactSense.contactDetected == true)]", "$.d.states[?(@.contact.value == 'closed')]"],
          "actionParams": {
             "hazardTitle":"A door is opened !",
             "emailSubject":"Alert from IoT for Insurance",
             "emailText":"Hello IoT for Insurance user. You have a hazard!!!"
          }
       }
    }
 ```      
