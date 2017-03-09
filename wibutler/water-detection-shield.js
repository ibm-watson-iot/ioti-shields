(function() {
  var shieldUuid = 99999;
  var shieldName = 'wibutler-water-detection-shield';
  var hazardTitle = 'Water Leakage Detected';

  var delay = 5000;
  var preProcessing = undefined;

  function safelet(payload) {
    return ((payload.cname === 'WTRLEAK') && (payload.val === 1));
  }

  function entryCondition(payload) {
    return (payload.cname === 'WTRLEAK');
  }

  function message(payload) {
    var hazardUuid = shieldName + '_' + Date.now();
    return (constructMessage(payload, shieldUuid, hazardUuid, hazardTitle));
  }

  registerShield(shieldUuid, shieldName, entryCondition, preProcessing, safelet, message, delay);
})();