(function() {
	var shieldUuid = 99999;
	var shieldName = "HighTemperatureAndHumidityShied";
	var hazardUuid = "HighTemperatureAndHumidityHazard";
	var hazardTitle = "Humidity and hot temperature detected!";
	
	var delay = 20000;
	
	var safelet = function(payload) {
		return (payload.temp > 35 && payload.on && payload.wet);
	};

	var entryCondition = function(payload) {
		return (payload.on && payload.temp && payload.wet);
	};

	var message = function(payload) {
		payload.extra = payload.extra || {};
		payload.extra.isHandled = false;
		payload.extra.urgent = true;
		payload.extra.locationDesc = "Living Room";
		payload.extra.deviceDesc = "leakSmart Sensor";

		return (constructMessage(payload, shieldUuid, hazardUuid, hazardTitle));
	};

	registerShield(shieldUuid, shieldName, entryCondition, undefined, safelet, message, delay);
})();