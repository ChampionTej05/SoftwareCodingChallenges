#!/bin/bash

# URL to test
URL="http://localhost:4221"

# Function to send a request
send_request() {
    curl -s -o /dev/null -w "%{http_code}\n" "$URL"
}

# Sending 5 concurrent requests
for i in {1..5}
do
    send_request &
done

# Wait for all background jobs to finish
wait

echo "All requests have been sent."
