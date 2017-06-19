(function() {
  var shieldUuid = 99999;
  var shieldName = 'contact-shield';
  var hazardTitle = 'Something is opened!';

  var delay = 5000;
  var preProcessing = undefined;

  function safelet(payload) {
    /* Value can be either closed or opened as string. */
    return (payload.d.states.contact.value === 'open');
  }

  function entryCondition(payload) {
    return (payload.d && payload.d.states && (payload.d.states.contact !== undefined));
  }

  function message(payload) {
    var hazardUuid = shieldName + '_' + Date.now();
    return (constructMessage(payload, shieldUuid, hazardUuid, hazardTitle));
  }

  registerShield(shieldUuid, shieldName, entryCondition, preProcessing, safelet, message, delay);
})();