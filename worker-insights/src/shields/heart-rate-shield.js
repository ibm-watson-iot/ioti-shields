/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2018. All Rights Reserved.
 *
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 *******************************************************************************/

import mathCore from 'mathjs/core';
import statistics from 'mathjs/lib/function/statistics';
import { BaseShield, models, utils } from 'edge-toolkit';
import { version as __TOOLKIT__VERSION__ } from '@iot4i/sw-edge-processing-toolkit/edge-toolkit-info.json';

const helpers = utils.helpers;
const math = mathCore.create();
math.import(statistics);

class HeartRateShield extends BaseShield {

  constructor(args) {
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
    super(args);
    this.logger.info('constructor');
  }

  entryCondition(payload) {
    this.logger.debug('entryCondition');
    return !!helpers.get(payload, 'data.currentHeartRate');
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

export default HeartRateShield;
export { __TOOLKIT__VERSION__ };
