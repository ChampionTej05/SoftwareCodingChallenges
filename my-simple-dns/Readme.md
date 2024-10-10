
# Simple DNS Server

This project is a simple mock DNS server built using Node.js. It listens for DNS queries and responds with predefined answers. The server is designed to be extensible, allowing for future additions of different record types and response formats.

## Table of Contents

- [Installation](#installation)
- [Dependencies](#dependencies)
- [Running the Server](#running-the-server)
- [Testing the Server](#testing-the-server)
  - [Unit Tests with Jest](#unit-tests-with-jest)
  - [Integration Tests](#integration-tests)

## Installation

To set up the project, follow these steps:

1. **Clone the repository**:

   ```bash
   git clone <repository-url>
   cd my-simple-dns
   ```

2. **Install Node.js** if you haven't already. You can download it from [Node.js official website](https://nodejs.org/).

3. **Install the necessary dependencies**:

   ```bash
   npm install
   ```

## Dependencies

The following dependencies are required for this project:

- **Node.js**: JavaScript runtime for building the server.
- **dns-packet**: A library to encode and decode DNS packets.
- **dgram**: Node.js module for UDP datagram sockets.
- **Winston**: A logging library for Node.js, used to log server activity and errors.

You can install these dependencies using the command:

```bash
npm install dns-packet dgram winston
```

## Running the Server

To run the DNS server, execute the following command in your terminal:

```bash
node my-server.js
```

The server will start listening for DNS queries on port 53.

## Testing the Server

### Unit Tests with Jest

To run unit tests using Jest, follow these steps:

1. **Install Jest**:

   ```bash
   npm install --save-dev jest
   ```

2. **Run the tests**:

   ```bash
   npx jest
   ```

Jest will execute all test files matching the pattern `*.test.js`.

### Integration Tests

You can perform integration tests using a Bash script. This script will send real DNS queries to the server and check the responses.

1. **Make the script executable**:

   ```bash
   chmod +x testDnsServer.sh
   ```

2. **Run the integration test**:

   ```bash
   ./testDnsServer.sh
   ```
