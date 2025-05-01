#!/bin/bash

# Default values
STUDENT_FULL_ID="U058904"
STUDENT_ID=2
CLASS_ID=3
COURSE_CODE="CS401"
COURSE_NAME="Machine Learning with Python"
EVENT_TYPE="WAITLISTED"

# Get parameters
while getopts ":s:i:c:o:n:t:" opt; do
  case $opt in
    s) STUDENT_FULL_ID="$OPTARG" ;;
    i) STUDENT_ID="$OPTARG" ;;
    c) CLASS_ID="$OPTARG" ;;
    o) COURSE_CODE="$OPTARG" ;;
    n) COURSE_NAME="$OPTARG" ;;
    t) EVENT_TYPE="$OPTARG" ;;
    \?) echo "Invalid option -$OPTARG" >&2; exit 1 ;;
  esac
done

echo "Testing direct notification for student $STUDENT_FULL_ID ($STUDENT_ID) and class $CLASS_ID"
echo "Course: $COURSE_CODE - $COURSE_NAME"
echo "Event Type: $EVENT_TYPE"

# Test the notification API directly
curl -X POST "http://localhost:8084/api/notifications/notificationEvent?studentFullId=$STUDENT_FULL_ID&studentId=$STUDENT_ID&classId=$CLASS_ID&courseCode=$COURSE_CODE&courseName=$COURSE_NAME&eventType=$EVENT_TYPE"

echo ""
echo ""
echo "To check the notification in the database, run:"
echo "mysql -u root -p -e 'SELECT * FROM notification_db.notification ORDER BY notification_id DESC LIMIT 5;'" 