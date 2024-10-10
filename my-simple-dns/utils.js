const { DNSRecordTypes, DNSResponse, DNSResponseType, DNSClassCodes} =  require('./dns-structure');

const dnsPacket  = require('dns-packet');

function createDNSResponse (id, question) {
    const dnsResponse = new DNSResponse(
        id, DNSRecordTypes.A, DNSClassCodes.IN, question.name, 10, '93.184.216.34' 
    );
    return dnsResponse;
}

function sendDNSResponse(udpServer, response, clientPort, clientAddress){
    const responsePacket = dnsPacket.encode({
        type: DNSResponseType.RESPONSE,
        id: response.id, 
        flags: dnsPacket.AUTHORITATIVE_ANSWER,
        answers: [
            {
                name: response.name, 
                type: response.type, 
                ttl: response.ttl,
                data: response.data,
                class: response.classCode
            }
        ]
    });
    // Send the response back to the client
    udpServer.send(responsePacket, clientPort, clientAddress, (err) => {
        if (err) {
            console.error('Error sending response:', err);
        } else {
            console.log('Response sent to:', clientAddress, clientPort);
        }
    });

}

module.exports = {
    createDNSResponse, sendDNSResponse
}