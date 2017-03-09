(function() {
	var shieldUuid = 99999;
	var shieldName = "TrappedDetectionShield";
	var hazardUuid = "TrappedDetectionHazard";
	var hazardTitle = "A Trapped Person was detected.";

	var delay = 10000;
	var waitingSeconds = 20;

	// Local Buffer
	context.doorOpen = context.doorOpen || {};
	context.humanPresence = context.humanPresence || {};
	context.smookeDetected = context.smookeDetected || {};
	context.userTimestamp = context.userTimestamp || {};

	var safelet = function(payload) {

		var currentUser = payload.username;

		if (typeof currentUser !== "string") {
			console.log("No user name in payload");
			return false;
		}

		var doorOpenStatus = context.doorOpen[currentUser].state || false;
		var humanPresenceStatus = context.humanPresence[currentUser].state || false;
		var smokeDetectedStatus = context.smookeDetected[currentUser].state || false;
		var doorOpen = context.doorOpen[currentUser].on || false;
		var humanPresence = context.humanPresence[currentUser].on || false;
		var smokeDetected = context.smookeDetected[currentUser].on || false;

		if (!doorOpen || !humanPresence || !smokeDetected
				|| !smokeDetectedStatus || !humanPresenceStatus
				|| doorOpenStatus) {
			context.userTimestamp[currentUser] = Date.now();
			return false;
		}

		var dif = Date.now() - context.userTimestamp[currentUser];
		var secondsPassed = dif / 1000;
		var secondsPassedBetweenDates = Math.abs(secondsPassed);
		if (secondsPassedBetweenDates > waitingSeconds) {
			context.userTimestamp[currentUser] = Date.now();
			return true;
		}

		return false;

	};

	var entryCondition = function(payload) {
		return (payload.SmokeDetectorOn !== undefined)
				|| (payload.MovementSensorOn !== undefined)
				|| (payload.DoorWindowOn !== undefined);
	};

	var preProcessing = function(payload) {

		var currentUser = payload.username;
		if (typeof currentUser !== "string") {
			console.log("No user name in payload");
		}

		context.doorOpen[currentUser] = context.doorOpen[currentUser] || {};
		context.humanPresence[currentUser] = context.humanPresence[currentUser]
				|| {};
		context.smookeDetected[currentUser] = context.smookeDetected[currentUser]
				|| {};

		context.doorOpen[currentUser].on = context.doorOpen[currentUser].on || false;
		context.humanPresence[currentUser].on = context.humanPresence[currentUser].on || false;
		context.smookeDetected[currentUser].on = context.smookeDetected[currentUser].on || false;

		context.doorOpen[currentUser].state = context.doorOpen[currentUser].state || false;
		context.humanPresence[currentUser].state = context.humanPresence[currentUser].state || false;
		context.smookeDetected[currentUser].state = context.smookeDetected[currentUser].state || false;
		context.userTimestamp[currentUser] = context.userTimestamp[currentUser]
				|| Date.now();

		if (payload.DoorWindowOn !== undefined) {
			context.doorOpen[currentUser].on = payload.DoorWindowOn;
		}
		if (payload.MovementSensorOn !== undefined) {
			context.humanPresence[currentUser].on = payload.MovementSensorOn;
		}
		if (payload.SmokeDetectorOn !== undefined) {
			context.smookeDetected[currentUser].on = payload.SmokeDetectorOn;
		}
		if (payload.DoorWindowOpen !== undefined) {
			context.doorOpen[currentUser].state = payload.DoorWindowOpen;
		}
		if (payload.MovementSensorMove !== undefined) {
			context.humanPresence[currentUser].state = payload.MovementSensorMove;
		}
		if (payload.SmokeDetectorSmoke !== undefined) {
			context.smookeDetected[currentUser].state = payload.SmokeDetectorSmoke;
		}

		return payload;

	};

	var message = function(payload) {
		payload.extra = payload.extra || {};
		payload.extra.isHandled = false;
		payload.extra.urgent = true;
		payload.extra.locationDesc = "Living Room";
		payload.extra.deviceDesc = "Multiple Sensors";

		return (constructMessage(payload, shieldUuid, hazardUuid, hazardTitle));
	};

	registerShield(shieldUuid, shieldName, entryCondition,preProcessing, safelet, message, delay);
})();