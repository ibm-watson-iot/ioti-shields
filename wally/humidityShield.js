(function() {
	var shieldUuid = 99999;	// replace with a new UUID
	var shieldName = 'humidityShield';
	var hazardUuid = 'Humidity';
	var hazardTitle = 'A potential water leak was detected by the humidity sensor.';
	var delay = 20000;

	var safelet = function(payload) {
		return payload.traitStates.traitStates.Humidity.humidityPct*1>75;
	};

	var entryCondition = function(payload) {
		return payload.traitStates && payload.traitStates.traitStates && payload.traitStates.traitStates.Humidity;
	};

	var message = function(payload) {
		payload.extra = payload.extra || {};
		payload.extra.isHandled = false;
		payload.extra.urgent = true;

		var msg = hazardTitle;
		if( payload.location) {
			payload.extra.locationDesc = payload.location.appliance;
			payload.extra.deviceDesc = payload.location.floor;

			msg = hazardTitle + ' Location: ' + payload.location.appliance + ', ' + payload.location.room + ', ' + payload.location.floor;
		}

		return constructMessage(payload, shieldUuid, hazardUuid, msg);
	};

	registerShield(shieldUuid, shieldName, entryCondition, undefined, safelet, message, delay);
})();
