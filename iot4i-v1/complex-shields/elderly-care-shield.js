(function() {
  var shieldUuid = 99999;
  var shieldName = 'not-at-home-shield';

  var delay = 0;
  var preProcessing = undefined;

  /*private to shield*/
  /* Timeout is milliseconds. */
  var timeout = 30 * 1000;
  var isAtHome = true;
  var leftHomeMoment = null;

  var safelet = function (payload) {
    if (payload.timeLastMotion !== undefined && !isAtHome) {
      if (Date.now() - payload.timeLastMotion > timeout) {
        /* send hazard once */
        isAtHome = true;
        leftHomeMoment = null;
        return true;
      }
      /* back home */
      if (payload.timeLastMotion > leftHomeMoment) {
        isAtHome = true;
        leftHomeMoment = null;
      }
      return false;
    }

    if (payload.d && payload.d.states && payload.d.states.contact.value === 'closed') {
      isAtHome = false;
      leftHomeMoment = Date.now();
    }

    return false;
  };

  var entryCondition = function (payload) {
    var hasContactData = payload.d && payload.d.states && payload.d.states.contact;
    var hasMotionData = payload.timeLastMotion;
    return hasContactData || hasMotionData;
  };

  var message = function (payload) {
    return constructMessage(payload, shieldUuid, 'ElderlyCareShield', 'Still not at home after ' + timeout/1000 + ' seconds');
  };

  registerShield(shieldUuid, shieldName, entryCondition, preProcessing, safelet, message, delay);
})();