# Workforce Management API - Complete Implementation

This is a Spring Boot application for the Backend Engineer assignment with all bug fixes and new features implemented.

## Bug Fixes Implemented

### Bug 1: Task Re-assignment Creates Duplicates ✅
- **Fixed**: `assignByReference` now properly reassigns one task and cancels duplicates
- **Location**: TaskManagementServiceImpl.assignByReference()

### Bug 2: Cancelled Tasks Clutter the View ✅
- **Fixed**: `fetchTasksByDate` now filters out cancelled tasks
- **Location**: TaskManagementServiceImpl.fetchTasksByDate()

## New Features Implemented

### Feature 1: Smart Daily Task View ✅
- **Enhanced**: Date-based fetching now includes tasks created in range PLUS overdue active tasks
- **Implementation**: Smart filtering logic in fetchTasksByDate()

### Feature 2: Task Priority Management ✅
- **Added**: Endpoint to update task priority: `PUT /task-mgmt/{id}/priority`
- **Added**: Endpoint to filter by priority: `GET /task-mgmt/priority/{priority}`

### Feature 3: Task Comments & Activity History ✅
- **Added**: Automatic activity logging for all task operations
- **Added**: User comments functionality
- **Added**: Enhanced task details endpoint: `GET /task-mgmt/{id}/details`
- **Added**: Add comments endpoint: `POST /task-mgmt/{id}/comments`

## How to Run

1. Ensure you have Java 17 and Gradle installed
2. Run: `./gradlew bootRun`
3. Application starts on `http://localhost:8080`

## API Endpoints

### Existing Endpoints (Fixed)
```bash
# Get single task
curl --location 'http://localhost:8080/task-mgmt/1'

# Create tasks
curl --location 'http://localhost:8080/task-mgmt/create' \
--header 'Content-Type: application/json' \
--data '{
  "requests": [{
    "reference_id": 105,
    "reference_type": "ORDER", 
    "task": "CREATE_INVOICE",
    "assignee_id": 1,
    "priority": "HIGH",
    "task_deadline_time": 1728192000000
  }]
}'

# Update task status
curl --location 'http://localhost:8080/task-mgmt/update' \
--header 'Content-Type: application/json' \
--data '{
  "requests": [{
    "task_id": 1,
    "task_status": "STARTED",
    "description": "Work has been started"
  }]
}'

# Assign by reference (Bug #1 FIXED)
curl --location 'http://localhost:8080/task-mgmt/assign-by-ref' \
--header 'Content-Type: application/json' \
--data '{
  "reference_id": 201,
  "reference_type": "ENTITY",
  "assignee_id": 5
}'

# Fetch by date (Bug #2 FIXED + Smart view)
curl --location 'http://localhost:8080/task-mgmt/fetch-by-date/v2' \
--header 'Content-Type: application/json' \
--data '{
  "start_date": 1672531200000,
  "end_date": 1735689599000,
  "assignee_ids": [1, 2]
}'
```

### New Endpoints (Features)
```bash
# Update task priority
curl --location --request PUT 'http://localhost:8080/task-mgmt/1/priority' \
--header 'Content-Type: application/json' \
--data '{
  "priority": "HIGH"
}'

# Get tasks by priority  
curl --location 'http://localhost:8080/task-mgmt/priority/HIGH'

# Get task details with history and comments
curl --location 'http://localhost:8080/task-mgmt/1/details'

# Add comment to task
curl --location 'http://localhost:8080/task-mgmt/1/comments' \
--header 'Content-Type: application/json' \
--data '{
  "comment": "This task needs immediate attention",
  "user_id": 123
}'
```

## Testing the Fixes

### Bug 1 Testing:
1. Create duplicate tasks for same reference
2. Use assign-by-ref endpoint
3. Verify only one task remains active, others are cancelled

### Bug 2 Testing:
1. Create cancelled tasks
2. Use fetch-by-date endpoint
3. Verify cancelled tasks don't appear in results

### Smart Daily View Testing:
1. Create tasks with different creation dates
2. Use fetch-by-date with specific range
3. Verify it returns tasks in range + overdue active tasks

### Priority Features Testing:
1. Update task priority and verify change
2. Filter tasks by priority and verify results

### Activity & Comments Testing:
1. Perform various operations on a task
2. Add comments to the task
3. Fetch task details and verify complete history