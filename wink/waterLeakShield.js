(function() {
	var shieldUuid = 99999;
	var shieldName = "waterLeakShield";
	var hazardUuid = "WaterLeakHazard";
	var hazardTitle = "A water leak was detected.";
	
	var delay = 20000;

	var safelet = function(payload) {
		return (JSON.parse(payload.liquid_detected));
	};

	var entryCondition = function(payload) {
		return (payload.liquid_detected);
	};

	var message = function(payload) {
		payload.extra = payload.extra || {};
		payload.extra.isHandled = false;
		payload.extra.urgent = true;
		payload.extra.locationDesc = "Living Room";
		payload.extra.deviceDesc = "leakSmart Sensor";

		return (constructMessage(payload, shieldUuid, hazardUuid, hazardTitle));
	};

	var waterLeakShield = function(payload) {
		var shield = getShieldByName(shieldName);
		return (commonShield(payload, shield));
	};

	registerShield(shieldUuid, shieldName, entryCondition, undefined, safelet, message, delay);
})();