# Edge Shields for IBM IoT Worker Insights

Information about IBM Maximo Worker Insights is available in [IBM Knowledge Center](https://www.ibm.com/support/knowledgecenter/SSQNYQ_bas/worker-insights/kc_welcome_bas.html)

# Prerequisites
1. Download the edge-processing-toolkit from Worker Insights API with your account. [Extending the solution with APIs](https://www.ibm.com/support/knowledgecenter/SSQNYQ_bas/worker-insights/rest_apis.html)
2. Copy the downloaded `edge-toolkit-1.4.4.js` file into `sw-edge-processing-toolkit` folder.
3. Rename the file to `edge-toolkit.js`
4. Run `npm install`

## How to implement a shield
Edge-processing-toolkit provides a base class to implement new shields. Every shield
has to extend from this base class, `BaseShield`. the following example shield processes heart-rate events. **Important:** Note the comments in the code block.

```
class HeartRateShield extends BaseShield {

  constructor() {
    // Here you can define your shield parameters with default values and options.
    // These default values are used if you don't specify a default value in your
    // shield-code document. The heartRateThreshold parameter is a required parameter
    // and it has a default value 110. If you do not provide a default value for 
    // this parameter and mark it as required, and this parameter does not have value 
    // in the shield-code document, then this shield cannot be loaded into job-engine, 
    // because of a missing required parameter value.    
    args.shieldParametersDefinition = {
      minHazardInterval: {
        default: 60,
        type: 'number',
        unit: 'seconds',
        description: 'Minimum interval before a new hazard is send.'
      },
      heartRateThreshold: {
        default: 110,
        type: 'number',
        unit: 'beats/min',
        description: 'The maximum heart rate, after which a hazard is send'
      },
      hazardTitles: {
        type: 'object',
        default: { default: 'High heart rate detected!' },
        properties: ['default'],
        description: 'Different titles per hazard'
      }
    };
    // Pass all arguments to base class's constructor.
    super(args);
    // Log your messages with the logger object.
    this.logger.info('constructor');
  }

}
```

Each event passes through 3 steps during the event processing in a shield:
1. entryCondition
2. preProcessing
3. processing

The `BaseShield` class has 3 methods which implement these 3 steps. The event flow is as
follows: 
1. The job-engine calls the `entryCondition` method with a payload. 
2. If the payload satisfies entry conditions, then the job-engine calls the `preProcessing` method with same payload. 
3. If the `preProcessing` method returns the payload or `true`, the job-engine calls the `processing` method with the payload, which is the result of `preProcessing`.

`preProcessing` is optional. However, `entryCondition` and `processing` have to be
implemented in the shield implementation. The `BaseShield` class does not have a default
implementation. 

Here is the remaining implementation of our example shield:

```
class HeartRateShield extends BaseShield {

  constructor() {
    ...
  }

  entryCondition(payload) {
    this.logger.debug('entryCondition');
    return !!helpers.get(payload, 'data.currentHeartRate');
  }

  preProcessing(payload) {
    ...
  }

  calcRisk(current) {
    let max = this.params.heartRateThreshold;
    current -= 70;
    max -= 70;
    const risk = (current / max) * 100;
    if (risk < 0) return 0;
    return math.max(100, risk);
  }

  processing(payload) {
    this.logger.debug('processing', payload);
    this.riskLevel[payload.userId].simpleHeartRate = this.calcRisk(payload.data.currentHeartRate);
    if (payload.data.currentHeartRate > this.params.heartRateThreshold) {
      this.createHazard(payload);
    }
  }

  createHazard(payload) {
    this.logger.debug('createHazard', payload);
    this.hazardStream.push(new models.Hazard(this.shieldInfo.shieldId, payload, {
      hazardTitle: this.params.hazardTitles.default,
      urgent: true,
      threshold: this.params.heartRateThreshold,
      heartRate: payload.data.currentHeartRate,
      status: 'detected',
      class: 'HeartRate',
    }));
  }

}
```

`processing` is the method where you should implement a logic of your shield. If all
of the conditions are satisfied, generate a hazard and send it to the
job-engine. There is a stream for hazards which comes from `BaseShield`:
`hazardStream`. Push your hazard into the `hazardStream`. After you
push your hazard into `hazardStream`, the job-engine handles your hazard.
Specifically, the job-engine notifies the gateway application or mobile application. 
The related application then manages the remaining lifecycle of your hazard. Refer to 
the `createHazard` method above to learn how to push a hazard into
the `hazardStream`. **If you push too many hazards into the `hazardStream`, they
are throttled based on your `minHazardInterval` shield parameter.**


### How to use external libraries in a shield
You can use any Javascript library to implement your shield. For instance, 
[mathjs](https://www.npmjs.com/package/mathjs) npm package was used in the example
shield above. You just need to add your npm package in the package.json file and
run `npm install`.


### How to fetch external data
You cannot access a filesystem and to make HTTP requests from a shield.
All HTTP requests must go through 'external-data.js'. This functionality is not
fully implemented yet.


### How to test a shield
TBD


### How to build and package a shield
IoT Worker Insights API accepts a single Javascript file per shield-code.
Therefore, you must have a single file to upload your code to 
the backend. In most cases, you have a single Javascript file per shield.
However, if you want to keep your implementation in more than one Javascript file
and want to use external libraries, then you need to bundle all the required files
into a single Javascript file. You can use [Webpack](https://webpack.js.org/) to bundle
files. You can run `webpack` on the command-line to create your final shield-code
file. You can find the generated file in the `dist` directory.

If you want to build in development mode, you can run `npm run build`. For
production builds, run `npm run package`. Uglification and minification
are applied for the production builds. Detailed steps are below:

1. Add your shield code to module.export[0].entry in the `<sw-edge-shields>/webpack.config.js`
2. Run `npm run build` or `npm run package`
3. Find the generated packed file in the `<sw-edge-shields>/dist/` directory
4. Create a shield and upload the shield code file by using the dashboard

## Test
Tests are located under the `tests` directory. If you do not need webpack before
building, you can run `npm run test` directly. Otherwise, you need to run the `webpack`
command and then run `mocha` on `dist/tests-js`.

#### Simple queryable shield

The simple queryable shields is used in simple use cases such as panic button pressed, high temperature, high stress, etc..
The shield can be configured with json based queries where internally the [json path query library](https://github.com/json-path/JsonPath) is used to evaluate the queries against the sensor event payloads. 
To use this shield, the following parameters need to be configured:


- `minHazardInterval`: an integer to describe the minimum interval in seconds before a new hazard is sent. Default is 60 seconds.
- `queriesDescription`: text to explain the purpose of the json queries (use case). 
- `hazardTitles`: an object to describe the possible hazard titles, for example:
 ```json
{
"title1": "hazard title 1"
}
```
- `jsonQueries`: an array that contains the json queries. Every query is an `object` and should have the following fields:
    * `type`: events type. This can be any of `[ steps, calories, ascent, intensityMinutes, heartRate, heartRateVariability, stress, oxygenLevel, ecg, accelerometer, gyroscope, magnetometer, barometricPressure, luxometer, battery, temperature, pressure, humidity, airQuality, button, motion ]`
    * `condition`: the json query based on json path. For example, to generate hazard when stress score is more than some threshold, the condition will be `$..[?(@.stressScore > 80)]`
    * `hazardTitle`: the name of the hazard title from the param `hazardTitles`. For example `title1`. If not specified, `default` title will be used, which is `Hazard detected!`.


For example, assume we want to detect if a panic button is pressed and we have the following data coming from the sensor (a sensor tag sensor in this case):

```json
{
"type": "button",
"data": {
  "userButtonClicked": false
}
}
```

Then the shield parameters will be configured as below:

```json

{
"minHazardInterval": 60,
"queriesDescription": "json queries to find if a panic button is pressed",
"hazardTitles": {
"title1": "Panic button pressed!"
},
"jsonQueries": [
    {
      "type": "button",
      "condition": "$..[?(@.userButtonClicked==true)]",
      "hazardTitle": "title1"
    }
  ]
}
```

To verify that the json queries will work properly on the sensor data, online tools can be used such as [this](https://jsonpath.herokuapp.com/),

