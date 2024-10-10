#!/bin/bash

# Start your DNS server in the background (adjust the command as necessary)
node my-server.js &

# Give the server a moment to start
sleep 2

# Run a dig command to query your local DNS server
response=$(dig @localhost example.com A +short)

# Check if the response contains the expected IP address
expected_ip="93.184.216.34"  # Adjust this to your expected response
if echo "$response" | grep -q "$expected_ip"; then
    echo "Test passed: Received expected IP address."
else
    echo "Test failed: Expected IP address $expected_ip, but got $response."
fi

# Kill the DNS server process
kill $!
