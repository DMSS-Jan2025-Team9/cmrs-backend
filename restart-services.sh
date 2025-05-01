#!/bin/bash

echo "Rebuilding and restarting services..."

# Navigate to the parent directory
cd ..

# Stop running containers
docker-compose down

# Rebuild the services
docker-compose build courseregistration-service notification-service

# Start the services
docker-compose up -d

echo "Services have been restarted. Check logs with: docker-compose logs -f" 