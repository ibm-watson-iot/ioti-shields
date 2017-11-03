# Queryable shield for single source 


This shield is using simple Json queries for the events coming from a single data source. 
The json queries are based on [JsonPath](https://github.com/json-path/JsonPath). 
The json queries are simple to learn since they use the same structure as XPath expressions. 
To test your json queries, you can use this [website](http://jsonpath.herokuapp.com/). 
You could simply put the event data coming from the sensor in a json format and run the json queries against it.

## How shield works

The shield has two IBM streams operators as explained below:

1. A [Filter operator](https://www.ibm.com/support/knowledgecenter/en/SSCRJU_4.2.1/com.ibm.streams.toolkits.doc/spldoc/dita/tk$spl/op$spl.relational$Filter.html) at the beginning of the shield which filter events based on an entry conditions. 
2. A [Functor operator](https://www.ibm.com/support/knowledgecenter/en/SSCRJU_4.2.1/com.ibm.streams.toolkits.doc/spldoc/dita/tk$spl/op$spl.relational$Functor.html) that generate hazards based on shield conditions.

Both operators uses json queries as an input which is executed using a helper java function [executeJsonQuery(message, query)](./impl/java/src/com/ibm/iot4i/examples/ExecuteJsonQueryImpl.java).

![Queryable Simple Shield](./images/queryable-simple-shield.png)


## Using shield

This shield expects the following parameters:

- **entryConditionJsonQueries** : the json queries used to provide an entry condition to filter events that doesn't belong to this shield.
- **shieldJsonQueries** : the json queries used to provide the main shield logic
- **hazardTimeInterval** : the minimum time between hazards (in seconds)
- **hazardNeedsStateReset** : if `true`, only generate new hazard if state changed
- **eventTimePath** : required for `hazardTimeInterval`, the jsonPath to the event's timestamp
- **actionParams** : the params needed by the external action, example is shown below:

```json
{
   "hazardTitle":"A potential water leak was detected by the humidity sensor.",
   "emailSubject":"Alert from IoT for Insurance",
   "emailText":"Hello IoT for Insurance user. You have a hazard!!!"
}

 ```


These parameters needs to be provided when submitting/updating the shield code using the [createShieldCode API](https://ioti.us-south.containers.mybluemix.net/docs/#!/shield-codes/createShieldCode) or [updateShieldCode API](https://ioti.us-south.containers.mybluemix.net/docs/#!/shield-codes/updateShieldCode). The parameters are provided as part of the **jobOptions** field like:
 
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

Here are some examples of what this shield can do. 

### Wally Shields 

Below are examples of what job options needs to be provided for this shield to behave differently and cover different use cases based on wally sensor event data. An example of wally sensor event is shown below:

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
 In this case a hazard will be generated only once. 
 Only after the humidityPct went over 60 again a new hazard will be generated.
 If `hazardTimeInterval` is specified, it will only generate the hazard after the time passed since the last hazard,
 even if the state changed.

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

Below are examples of what job options needs to be provided for this shield to behave differently and cover different use cases based on enocean sensor event data. An example of enocean sensor event is shown below:   

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
#### Enocean panic button shield
    
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

It is also possible to have a generic shield that run multiple queries on the incoming events. For example as shown below, we have a door-open shield that accepts events from enocean and wally sensors.

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