class DNSResponse {
    constructor(id, type, classCode, name, ttl, data) {
        this.id = id;         // Unique identifier for the DNS query
        this.type = type;     // Type of DNS record (e.g., A, AAAA, CNAME)
        this.name = name;     // Domain name
        this.ttl = ttl;       // Time to live
        this.data = data;     // IP address or other data
        this.classCode = classCode
    }

    // You can add methods if needed
    toString() {
        return `${this.name} (${this.type}): ${this.data} (TTL: ${this.ttl})`;
    }
}
const DNSRecordTypes = {
    A: 'A',
    AAAA: 'AAAA',
    CNAME: 'CNAME',
    MX: 'MX',
    TXT: 'TXT',
    // Add more as needed
};


const DNSResponseType = {
    QUERY: 'query',
    RESPONSE : 'response'
}

const DNSClassCodes = {
    IN :'IN' // internet 
}


module.exports = {
    DNSResponse, DNSRecordTypes, DNSResponseType, DNSClassCodes
};