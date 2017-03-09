(function() {
  var shieldUuid = 99999;
  var shieldName = 'crash-detection-shield';

  var delay = 5000;
  var preProcessing = undefined;

  var crashMessage;

  function safelet(payload) {
    var crash = false;
    if (payload.crashStatus === 2 && payload.crashIndex > 2) {
      crashMessage = 'normal. But it might indicate begining of a crash';
      crash = true;
    } else if (payload.crashStatus === 3 && payload.crashIndex > 40) {
      crashMessage = 'medium';
      crash = true;
    } else if (payload.crashStatus === 4 && payload.crashIndex > 50) {
      crashMessage = 'massive';
      crash = true;
    } else {
      crash = false;
    }
    return (crash);
  }

  function entryCondition(payload) {
    return (payload.crashIndex && payload.crashStatus && payload.eventType);
  }

  function message(payload) {
    var hazardUuid = shieldName + '_' + Date.now();
    return (constructMessage(payload, shieldUuid, hazardUuid, 'Crash was detected. Level of the crash: ' + crashMessage));
  }

  registerShield(shieldUuid, shieldName, entryCondition, preProcessing, safelet, message, delay);
})();