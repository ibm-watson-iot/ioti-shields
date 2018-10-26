# Edge Shields for IBM IoT Worker Insights

You can find more information in our [Knowledge Center]
(https://www.ibm.com/support/knowledgecenter/SSQNYQ_bas/iot-insurance/safer_workplace/worker_summary.html)

# Prerequisites
1. Download edge-processing-toolkit from Worker Insights API with your account. [Extending the solution with APIs](https://www.ibm.com/support/knowledgecenter/SSQNYQ_bas/iot-insurance/iotinsurance_rest_apis.html?pos=2)
2. Copy the downloaded javascript file (e.g. edge-toolkit-1.4.4.js) into `sw-edge-processing-toolkit` folder.
3. Rename the file to `edge-toolkit.js`
4. Run `npm install`

## How to implement a shield
Edge-processing-toolkit provides a base class to implement new shield. Every shield
has to extend from this base class `BaseShield`. Below you find an example shield
which processes heart-rate events. Please take an attention to the comments in the
code block.

```
class HeartRateShield extends BaseShield {

  constructor() {
    // Here you can define your shield parameters with default values and options.
    // These default values are used if you don't specify a default value in your
    // shield-code document. heartRateThreshold parameter is a required parameter
    // and it has a default value 110. If a shield developer doesn't provide a
    // default value here and mark this parameter as required and this parameter
    // doesn't have value in the shield-code document, then this shield cannot be
    // loaded into job-engine because of missing required parameter value.    
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
    // You should pass all arguments to base class's constructor.
    super(args);
    // You can log your messages with the logger object.
    this.logger.info('constructor');
  }

}
```

There are 3 steps which each event passes through during the event processing in a shield:
1. entryCondition
2. preProcessing
3. processing

`BaseShield` class has 3 methods which implement these 3 steps. Events flow as
the methods are listed on the following: First, job-engine calls `entryCondition`
method with a payload. If the payload satisfies entry conditions, then the job-engine
calls `preProcessing` method with same payload. If the `preProcessing` returns
either payload or true, the job-engine calls `processing` method with payload
which is the result of `preProcessing`.

`preProcessing` is optional. However, `entryCondition` and `processing` have to be
implemented in the shield implementation. `BaseShield` class does not have a default
implementation. Here is the further implementation of our example shield:

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

`processing` is a method where you should implement a logic of your shield. If all
the conditions are satisfied, you want to generate a hazard and send it to the
job-engine. There is a stream for hazards which comes from `BaseShield`:
`hazardStream`. You should push your hazard into the `hazardStream`. After you
push your hazard into `hazardStream`, the job-engine take cares of your hazard.
That means, the job-engine notifies gateway application or mobile application
and the related application manages rest lifecycle of your hazard. You can have
a look `createHazard` method above in order to learn how to push a hazard into
the `hazardStream`. **If you push too many hazards into the `hazardStream`, they
are throttled based on your `minHazardInterval` shield parameter.**


### How to use external libraries in a shield
You can use any Javascript library to implement your shield. For instance, we used
[mathjs](https://www.npmjs.com/package/mathjs) npm package in the above example
shield. You just need to add your npm package in the package.json file and
run `npm install`.


### How to fetch external data
It is not allowed to access filesystem and to make HTTP requests from a shield.
All HTTP requests must go through 'external-data.js'. This functionality is not
fully implemented yet.


### How to test a shield
TBD


### How to build and package a shield
IoT Worker Insights API accepts a single Javascript file per a shield-code.
Therefore, a shield developer has to have a single file to be able to upload his
code to the backend. In most cases you have a single Javascript file per shield.
However, if you want to keep your implementation in more than one Javascript file
and want to use external libraries, then you need to bundle all the required files
into a single Javascript file. We use [Webpack](https://webpack.js.org/) to do
this. You can run `webpack` on the command-line to create your final shield-code
file. You can find the generated file in the dist directory.

If you want to build in development mode, you can just run `npm run build`. For
production builds, you should use `npm run package`. Uglification and minification
are applied for the production builds. Detailed steps are below:

* Add your shield code to module.export[0].entry in the `<sw-edge-shields>/webpack.config.js`
* Run `npm run build` or `npm run package`
* You will find the generated packed file in `<sw-edge-shields>/dist/` directory
* Create a shield and upload the shield code file via the dashboard

## Test
Tests are located under tests directory. As long as we don't need webpack pre
build, we can just `npm run test` directly. Otherwise we need to run `webpack`
command first and then run mocha on dist/tests-js.
