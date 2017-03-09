(function() {
  var shieldUuid = 99999;
  var shieldName = 'emergency-button-shield';
  var hazardTitle = 'Emergency button was pressed.';

  var delay = 5000;
  var preProcessing = undefined;

  function safelet(payload) {
    /* Value can be either released or pressed as string. */
    return (payload.d.states.button.value === 'pressed');
  }

  function entryCondition(payload) {
    return (payload.d && payload.d.states && payload.d.states.button);
  }

  function message(payload) {
    var hazardUuid = shieldName + '_' + Date.now();
    return (constructMessage(payload, shieldUuid, hazardUuid, hazardTitle));
  }

  registerShield(shieldUuid, shieldName, entryCondition, preProcessing, safelet, message, delay);
})();