// dnsServer.test.js
const {  DNSRecordTypes} = require('./dns-structure'); 
const { createDNSResponse } = require('./utils');
describe('DNS Response Tests', () => {
    test('should create a valid DNS response', () => {
        
            const id = 1234
            const question =  {
                name: 'example.com',
                type: 'A'
            }
        

        const response = createDNSResponse(id, question);
        expect(response.id).toBe(id);
        expect(response.name).toBe(question.name);
        expect(response.type).toBe(DNSRecordTypes.A);
        expect(response.ttl).toBe(10);
        expect(response.data).toBe('93.184.216.34'); // Example IP address
    });
});
