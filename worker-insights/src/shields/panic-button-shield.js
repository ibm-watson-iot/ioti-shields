/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2018. All Rights Reserved.
 *
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 *******************************************************************************/

import { BaseShield, models } from 'edge-toolkit';
import { version as __TOOLKIT__VERSION__ } from '@iot4i/sw-edge-processing-toolkit/edge-toolkit-info.json';

class PanicButtonShield extends BaseShield {

  constructor(args) {
    args.shieldParametersDefinition = {
      hazardTitles: {
        type: 'object',
        default: { default: 'Panic button was pressed' },
        properties: [
          'default'
        ],
        description: 'Different titles per hazard'
      }
    };
    super(args);
    this.logger.info('constructor');
  }

  initialiseUserValues(userId) {
    this.riskLevel[userId] = {
      panicButton: 100
    };
  }

  entryCondition(payload) {
    this.logger.debug('entryCondition');
    return (payload.data.RightButton);
  }

  processing(payload) {
    this.logger.debug('processing', payload);
    if (payload.data.RightButton) {
      this.riskLevel[payload.userId].panicButton = 100;
      this.createHazard(payload);
    }
  }

  createHazard(payload) {
    this.logger.info('createHazard', payload);
    this.hazardStream.push(new models.Hazard(this.shieldInfo.shieldId, payload, {
      hazardTitle: this.params.hazardTitles.default,
      status: 'detected',
      class: 'Panic',
      actionParams: {
        riskLevel: {
          title: 'Panic',
          level: 100
        }
      }
    }));
  }
}

export default PanicButtonShield;
export { __TOOLKIT__VERSION__ };
