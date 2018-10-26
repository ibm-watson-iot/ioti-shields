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
import PanicButtonShield from 'shields/panic-button-shield';

const Stream = utils.stream.Stream;

should();

describe('Panic Button', function() {
  let shield;

  it('should trigger', () => {
    const shieldInfo = {
      shieldCodeId: 'sc_pb1234'
    };
    let called = false;
    const inputStream = new Stream();
    const outputStream = { emit: () => { called = true; } };
    shield = new PanicButtonShield({ shieldInfo, inputStream, outputStream });
    shield.subscribe();
    let payload = { data: { RightButton: true } };
    if (shield.entryCondition(payload)) {
      payload = shield.preProcessing(payload) || payload;
      shield.processing(payload);
    }
    called.should.be.equal(true);
  });

  it('should trigger', () => {
    const shieldInfo = {
      shieldCodeId: 'sc_pb1234'
    };
    let called = false;
    const inputStream = new Stream();
    const outputStream = { emit: () => { called = true; } };
    shield = new PanicButtonShield({ shieldInfo, inputStream, outputStream });
    shield.subscribe();
    const payload = { data: { RightButton: true } };
    inputStream.emit('event', payload);
    called.should.be.equal(true);
  });

  it('should not trigger', () => {
    const shieldInfo = {
      shieldCodeId: 'sc_pb1234'
    };
    let called = false;
    const inputStream = new Stream();
    const outputStream = { emit: () => { called = true; } };
    shield = new PanicButtonShield({ shieldInfo, inputStream, outputStream });
    shield.subscribe();
    const payload = { data: { AnotherButton: true } };
    inputStream.emit('event', payload);
    called.should.be.equal(false);
  });
});
