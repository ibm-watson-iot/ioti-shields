(function() {
  var shieldUuid = 99999;
  var shieldName = 'smoke-detection-shield';
  var hazardTitle = 'Smoke was detected!';

  var delay = 20000;
  var preProcessing = undefined;

  function safelet(payload) {
    return ((payload.cname === 'SMOKE')) && (payload.val === 1);
  }

  function entryCondition(payload) {
    return (payload.cname === 'SMOKE');
  }

  function message(payload) {
    payload.extra = payload.extra || {};
    payload.extra.isHandled = false;
    payload.extra.urgent = true;
    payload.extra.locationDesc = 'Office';
    payload.extra.deviceDesc = 'SmokeSmart Sensor';

    var hazardUuid = shieldName + '_' + Date.now();
    return (constructMessage(payload, shieldUuid, hazardUuid, hazardTitle));
  }

  registerShield(shieldUuid, shieldName, entryCondition, preProcessing, safelet, message, delay);
})();