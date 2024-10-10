const dgram = require('node:dgram');
const dnsPacket  = require('dns-packet');
const logger = require('./logger');
const { Interface } = require('node:readline');
const {DNSResponse, DNSRecordTypes} = require('./dns-structure');
const {createDNSResponse, sendDNSResponse} = require('./utils');
const { send } = require('node:process');
const udpServer = dgram.createSocket('udp4');

/**
 * We need to create an interface to answer the queries regarding the DNS 
 * We would send out information regarding record
 * For now let us use A record
 */


udpServer.on('error', (err) => {
    logger.error(`Error occurred while running UDP Server: ${err.stack}`);
});

udpServer.on('message', (msg, rinfo) => {
    const packetInformation = dnsPacket.decode(msg);
    logger.debug(`msg questions: -> ${JSON.stringify(packetInformation, null, 2)}`);
    logger.debug(`Address -> ${rinfo.address}:${rinfo.port}`);
    const questions = packetInformation.questions;
    const question = questions[0];
    const responsePacket = createDNSResponse(packetInformation.id, question);
    
    sendDNSResponse(udpServer, responsePacket, rinfo.port, rinfo.address);

});

udpServer.on('listening', () => {
    const address = udpServer.address();
    logger.info(`Server listening on ${address.address}:${address.port}`);
});
  
udpServer.bind(53);
  