/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2018. All Rights Reserved.
 *
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 *******************************************************************************/

import { should } from 'chai';
import { utils } from 'edge-toolkit';
import HeartRateShield from 'shields/heart-rate-shield';

const Stream = utils.stream.Stream;

should();

describe('Heart rate', function() {
  let shield;

  it('should trigger', () => {
    const shieldInfo = {
      shieldCodeId: 'sc_hr123456'
    };
    let called = false;
    const inputStream = new Stream();
    const outputStream = { emit: () => { called = true; } };
    shield = new HeartRateShield({ shieldInfo, inputStream, outputStream });
    let payload = { data: { currentHeartRate: 150 } };
    if (shield.entryCondition(payload)) {
      payload = shield.preProcessing(payload) || payload;
      shield.processing(payload);
      return;
    }
    called.should.be.equal(true);
  });

  it('should trigger', () => {
    const shieldInfo = {
      shieldCodeId: 'sc_hr123456'
    };
    let called = false;
    const inputStream = new Stream();
    const outputStream = { emit: () => { called = true; } };
    shield = new HeartRateShield({ shieldInfo, inputStream, outputStream });
    shield.subscribe();
    const payload = { data: { currentHeartRate: 150 } };
    inputStream.emit('event', payload);
    called.should.be.equal(true);
  });

  it('should not trigger', () => {
    const shieldInfo = {
      shieldCodeId: 'sc_hr123456'
    };
    let called = false;
    const inputStream = new Stream();
    const outputStream = { emit: () => { called = true; } };
    shield = new HeartRateShield({ shieldInfo, inputStream, outputStream });
    const payload = { AnotherButton: true };
    inputStream.emit('event', payload);
    called.should.be.equal(false);
  });
});
