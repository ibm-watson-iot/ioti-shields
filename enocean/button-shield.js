(function() {
  var shieldUuid = 99999;
  var shieldName = 'button-shield';
  var hazardTitle = 'Button was pressed.';

  var delay = 5000;
  var preProcessing = undefined;

  function safelet(payload) {
    /* Value can be either released or pressed as string. */
    return (payload.d.states.buttonB0.value === 'released');
  }

  function entryCondition(payload) {
    return (payload.d && payload.d.states && (payload.d.states.buttonB0 !== undefined));
  }

  function message(payload) {
    var hazardUuid = shieldName + '_' + Date.now();
    return (constructMessage(payload, shieldUuid, hazardUuid, hazardTitle));
  }

  registerShield(shieldUuid, shieldName, entryCondition, preProcessing, safelet, message, delay);
})();