(function() {
  var shieldUuid = 99999;
  var shieldName = 'button-shield';
  var hazardTitle = 'Button {button} was pressed';

  var delay = 5000;
  var preProcessing = undefined;

  function safelet(payload) {
    /* Value can be either released or pressed as string. */
    return (payload.d.states.buttonB0 !== undefined && payload.d.states.buttonB0.value === 'released') || (payload.d.states.buttonBI !== undefined && payload.d.states.buttonBI.value === 'released');
  }

  function entryCondition(payload) {
    return (payload.d && payload.d.states && (payload.d.states.buttonB0 !== undefined || payload.d.states.buttonBI !== undefined));
  }

  function message(payload) {
    var hazardUuid = shieldName + '_' + Date.now();
    var title = hazardTitle.replace( '{button}', payload.d.states.buttonB0 !== undefined ? 'B0' : 'BI');
    
    return (constructMessage(payload, shieldUuid, hazardUuid, title));
  }

  registerShield(shieldUuid, shieldName, entryCondition, preProcessing, safelet, message, delay);
})();