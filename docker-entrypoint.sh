#!/bin/bash

# Check if user provided a config file in /app/config
if [ -f "/app/config/simulation-config.json" ]; then
    echo "Using user-provided config from /app/config/simulation-config.json"
    cp /app/config/simulation-config.json /app/simulation-config.json
else
    echo "No user config found, using default config"
    cp /app/default-config.json /app/simulation-config.json
fi

# Run the simulation from the fat JAR
java -jar /app/life-simulation.jar

# After simulation ends, if ffmpeg and snapshots exist generate timelapse
if [ -f /app/generate_timelapse.sh ]; then
    /app/generate_timelapse.sh "${OUTPUT_DIR:-output}" "${TIMELAPSE_FPS:-10}" "${TIMELAPSE_FILE:-timelapse.mp4}"
fi
