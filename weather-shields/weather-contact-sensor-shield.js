context.temperature = context.temperature || {};
context.contactSensor = context.contactSensor || {};

(function() {
    var shieldUuid = 99999;
    var shieldName = 'weather-shield';
    var hazardTitle = 'Temperature outside limits and window is open';

    var delay = 5000;

    var tempMin = -10;
    var tempMax = 35;

    function safelet(payload) {
        var currentUser = payload.username;

        var temperature = context.temperature[currentUser];
        var contactSensor = context.contactSensor[currentUser];

        return (temperature < tempMin || temperature > tempMax) && contactSensor;
    }

    function preProcessing(payload) {
        var currentUser = payload.username;

        context.temperature[currentUser] = payload.weatherData.temperature;
        context.contactSensor[currentUser] = payload.contactSensor;

        return payload; // required
    }

    function entryCondition(payload) {
        return (payload && payload.weatherData && payload.weatherData.temperature) || payload.contactSensor;
    }

    function message(payload) {
        var hazardUuid = shieldName + '_' + Date.now();
        return constructMessage(payload, shieldUuid, hazardUuid, hazardTitle);
    }

    registerShield(shieldUuid, shieldName, entryCondition, preProcessing, safelet, message, delay);
})();
