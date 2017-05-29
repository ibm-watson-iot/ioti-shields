## Wally
The shields in this category are designed to work with [Wally devices](https://www.wallyhome.com/)

### Wally Water Leak Shield

#### Required sensors/devices/services
- Wally gateway
- Wally 4 in 1 sensor
- Wally organisation
- Wally developer account with access to the Wally organisation

#### Configuration
You need to configure the IoT4I Transformer as described in the [IoT4I Infocenter](https://console.ng.bluemix.net/docs/services/IotInsurance/iotinsurance_wally_integration.html#wallysupport).

#### Behavior
The shield is configured to triger an alarm everytime the humity percentage raises above 75 points. You can adjust this value in the safelet function of the shield.

```JavaScript
var safelet = function(payload) {
  return payload.traitStates.traitStates.Humidity.humidityPct*1>75;
};
```
