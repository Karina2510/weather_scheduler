#!/bin/bash

# Function to check queue messages
check_queue() {
    local QUEUE_NAME=$1
    local ENDPOINT="http://localhost:4566"
    local QUEUE_URL="$ENDPOINT/000000000000/$QUEUE_NAME"

    echo -e "\nChecking messages in queue: $QUEUE_NAME"
    echo "----------------------------------------"

    # Get queue attributes to count messages
    MESSAGE_COUNT=$(aws sqs get-queue-attributes \
        --endpoint-url $ENDPOINT \
        --queue-url $QUEUE_URL \
        --attribute-names ApproximateNumberOfMessages \
        --query 'Attributes.ApproximateNumberOfMessages' \
        --output text)

    echo "Number of messages in queue: $MESSAGE_COUNT"

    if [ "$MESSAGE_COUNT" -gt 0 ]; then
        echo -e "\nMessage contents:"
        echo "----------------"

        # Receive and display messages
        for ((i=1; i<=$MESSAGE_COUNT; i++))
        do
            echo -e "\nMessage $i:"
            aws sqs receive-message \
                --endpoint-url $ENDPOINT \
                --queue-url $QUEUE_URL \
                --max-number-of-messages 1 \
                --visibility-timeout 30 \
                --output json

            echo "----------------"
        done
    fi
}

# Check both queues
check_queue "solicita-temperatura-queue"
check_queue "retorno-temperatura-queue" 