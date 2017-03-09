(function() {
  var shieldUuid = 99999;
  var shieldName = 'enocean-water-detection-shield';
  var hazardTitle = 'Water Leakage Detected';

  var delay = 5000;
  var preProcessing = undefined;

  function safelet(payload) {
    return (payload.d.states.liquidDetected.value);
  }

  function entryCondition(payload) {
    return (payload.d && payload.d.states && payload.d.states.liquidDetected);
  }

  function message(payload) {
    var hazardUuid = shieldName + '_' + Date.now();
    return (constructMessage(payload, shieldUuid, hazardUuid, hazardTitle));
  }

  registerShield(shieldUuid, shieldName, entryCondition, preProcessing, safelet, message, delay);
})();