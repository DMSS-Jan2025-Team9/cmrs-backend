#!/bin/bash

# Default values
STUDENT_FULL_ID="U058904"
CLASS_ID=3
COURSE_CODE="CS401"
COURSE_NAME="Machine Learning with Python"

# Get parameters
while getopts ":s:c:o:n:" opt; do
  case $opt in
    s) STUDENT_FULL_ID="$OPTARG" ;;
    c) CLASS_ID="$OPTARG" ;;
    o) COURSE_CODE="$OPTARG" ;;
    n) COURSE_NAME="$OPTARG" ;;
    \?) echo "Invalid option -$OPTARG" >&2; exit 1 ;;
  esac
done

echo "Testing waitlist notification for student $STUDENT_FULL_ID and class $CLASS_ID"
echo "Course: $COURSE_CODE - $COURSE_NAME"

# Test the waitlist notification API
curl -X POST "http://localhost:8083/api/courseRegistration/waitlist-notification?studentFullId=$STUDENT_FULL_ID&classId=$CLASS_ID"

echo ""
echo ""
echo "To check the notification in the database, run:"
echo "mysql -u root -p -e 'SELECT * FROM notification_db.notification ORDER BY notification_id DESC LIMIT 5;'" 