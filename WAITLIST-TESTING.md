# Testing the Waitlist Notification System

This guide will help you test the waitlist notification system using Docker and the provided testing interfaces.

## Prerequisites

- Docker and Docker Compose installed
- Java 21 and Maven (if running without Docker)

## Running the Services

### Using Docker Compose

1. Start all services using Docker Compose:

   ```bash
   docker-compose up -d
   ```

2. The following services will be available:
   - Course Registration Service: http://localhost:8083
   - Notification Service: http://localhost:8084
   - RabbitMQ Management UI: http://localhost:15672 (guest/guest)

### Running Manually

1. Start RabbitMQ (can be started with Docker):

   ```bash
   docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
   ```

2. Start the Notification Service:

   ```bash
   cd notificationservice
   mvn spring-boot:run
   ```

3. Start the Course Registration Service:
   ```bash
   cd courseregistration
   mvn spring-boot:run
   ```

## Testing the Notification System

### Testing with the WebSocket UI

1. Open the notification service WebSocket test UI:
   http://localhost:8084/index.html

2. Connect to the WebSocket by entering a User ID (e.g., 1) and clicking "Connect"

3. Send test notifications using the options provided:
   - Waitlisted: Simulates a student being waitlisted
   - Vacancy Available: Simulates a notification when a spot becomes available
   - Custom Message: Sends a custom notification

### Testing with API Endpoints

#### Course Registration Service Test Endpoints

1. Test waitlist notification:

   ```
   POST http://localhost:8083/api/courseRegistration/test/waitlist-notification?studentId=1&classId=101
   ```

2. Test vacancy notification:

   ```
   POST http://localhost:8083/api/courseRegistration/test/vacancy-notification?studentId=1&classId=101
   ```

3. Test updating vacancy:
   ```
   PUT http://localhost:8083/api/courseRegistration/test/update-vacancy?classId=101&vacancy=5
   ```

#### Notification Service Test Endpoints

1. Create a manual notification:

   ```
   POST http://localhost:8084/api/notifications/test/create?userId=1&message=Test%20notification
   ```

2. Send a notification event:

   ```
   POST http://localhost:8084/api/notifications/test/event?studentId=1&classId=101&eventType=WAITLISTED
   ```

3. Send a direct WebSocket message:
   ```
   POST http://localhost:8084/api/notifications/test/websocket?userId=1&message=Direct%20websocket%20message
   ```

### Testing the Complete Flow

1. **Simulate Waitlisting**: Use the course registration API to create a registration with no vacancy
2. **Observe Waitlist Notification**: The notification should appear in the WebSocket UI
3. **Simulate Vacancy Creation**: Update a class to have vacancy
4. **Observe Vacancy Notification**: Waitlisted students should receive a notification

## Troubleshooting

### WebSocket Connection Issues

- Ensure the notification service is running
- Check browser console for connection errors
- Verify that the ports are correctly mapped if using Docker

### Message Delivery Issues

- Check RabbitMQ management UI to see if messages are being published
- Verify that the notification service is consuming messages from the queue
- Check the notification service logs for any errors

### Database Issues

- Ensure MySQL is running and accessible
- Check the application logs for database connection errors
