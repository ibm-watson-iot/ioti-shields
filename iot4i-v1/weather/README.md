##Weather Shields
The shields in this category are designed to work with the [IBM Weather Company Data service](https://console.ng.bluemix.net/catalog/services/weather-company-data/) 

### Weather shield
This shield is designed to check if the current temperature (as reported by the Weather Data service) is outside of a certain safe interval.

Shield code: [weather-shield.js](https://github.ibm.com/IoT-Insurance/iot4i-shield-repo/blob/master/weather/weather-shield.js)

#### Required sensors/devices/services
- IBM Weather Company Data service

#### Shield parameters
- Minimum temperature
- Maximum temperature

### Windows open weather shield
This shield covers the scenario where the a window is open and the current temperature is outside of a safe range.
In addition to checking for the current temperature, this shield also checks for the status of an EnOcean contact sensor.

Shield code: [weather-contact-sensor-shield.js](https://github.ibm.com/IoT-Insurance/iot4i-shield-repo/blob/master/weather/weather-contact-sensor-shield.js)

#### Required sensors/devices/services
- IBM Weather Company Data service
- EnOcean contact sensor

#### Shield parameters
- Minimum temperature
- Maximum temperature
