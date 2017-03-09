# IoT for Insurance Shield Repository
A collection of shields for use with [IBM Watson IoT for Insurance](https://www.ibm.com/internet-of-things/iot-solutions/iot-insurance/). 

## Shield Usage
The [Shield Toolkit](https://console.ng.bluemix.net/docs/services/IotInsurance/iotinsurance_shield_toolkit.html) section in the Infocenter describes in detail the process of deploying new shields in your IoT4I instance.

To use a shield from this collection you need to replace `shieldUuid` variable in the shield code. There you need to use the same `UUID` as that of the shield object you created in IoT4I.

## Shield Design
When designing a new shield there are a few important points to remember:  
 
- encapsulate the shield in a JavaScript self invoking function `(function(){...code...})();`
- `shield UUID` must be of type ***number***
- when creating a `shield` in database the `shield UUID` must be ***string***
- when creating a `jscode` in database the `shield UUID` must be a number
- The internal function constructMessage **must** be called

## Additional Resources
- [IoT4I Documentation](https://console.ng.bluemix.net/docs/services/IotInsurance/index.html) 
- [IoT4I API Docs](https://iot4i-api-docs.mybluemix.net/) 
- [IoT4I API Examples](https://github.com/IBM-Bluemix/iot4i-api-examples-nodejs/#iot-for-insurance-api-examples) 
- [IoT4I Bluemix Catalog Entry](https://console.ng.bluemix.net/docs/services/IotInsurance/index.html)  
