(function() {
	var shieldUuid = 99999;
	var shieldName = "CarShield";
	var hazardUuid = "CarHazard";
	
	var delay = 0;

	var safelet = function(payload) {
		console.log(payload.behaviorName);
		return (payload.behaviorName);
	};

	var entryCondition = function(payload) {
		console.log(payload.behaviorName);
		return (payload.behaviorName);
	};

	var message = function(payload) {
		payload.extra = payload.extra || {};
		payload.extra.isHandled = false;
		payload.extra.urgent = true;

		return (constructMessage(payload, shieldUuid, hazardUuid, payload.behaviorName));
	};

	registerShield(shieldUuid, shieldName, entryCondition, undefined, safelet, message, delay);
})();
