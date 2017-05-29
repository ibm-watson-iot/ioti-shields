## Wally
The shields in this category are designed to work with [Wally devices](https://www.wallyhome.com/)



### Required sensors/devices/services
- Wally gateway
- Wally 4 in 1 sensor
- Wally organisation
- Wally developer account with access to the Wally organisation

Make sure the IoT4I instance is up to date to gurantee you have access to the Wally functionality. To do that verify there is no upgrade option in the IoT4I Service Console. If the upgrade option exists you need to use it before attempting to integrate Wally data.

### IoT4I Configuration
The IoT4I Transformer must be configured to work with your Wally organisation as described in the [IoT4I Infocenter](https://console.ng.bluemix.net/docs/services/IotInsurance/iotinsurance_wally_integration.html#wallysupport).

### Humidity Shield

#### Behavior
The shield is configured to triger an alarm everytime the humity percentage raises above 75 points. You can adjust this value in the safelet function of the shield.

```JavaScript
var safelet = function(payload) {
  return payload.traitStates.traitStates.Humidity.humidityPct*1>75;
};
```

### Water Leak Shield

#### Behavior
The shield is configured to triger an alarm everytime the sensor detects water. There is no tuning required for this sensor.
