# Voting System Test Suite

This document describes the comprehensive test suite for the voting system, covering both threads and comments.

## Test Structure

### 1. Unit Tests

#### `VoteServiceTest.java`
- **Purpose**: Tests the core voting logic in isolation
- **Coverage**: 
  - New vote creation
  - Vote removal (same vote twice)
  - Vote change (upvote to downvote or vice versa)
  - Vote count calculations
  - User vote status retrieval
- **Mocking**: Uses Mockito to mock the VoteRepository
- **Status**: ✅ All 8 tests passing

### 2. Integration Tests

#### `ThreadVotingTest.java`
- **Purpose**: Tests thread voting endpoints via MockMvc
- **Coverage**:
  - Authentication requirements
  - Upvote/downvote functionality
  - Vote toggling behavior
  - Error handling (invalid IDs, null votes)
  - Response format validation
- **Status**: ✅ All 6 tests passing

#### `CommentVotingTest.java`
- **Purpose**: Tests comment voting endpoints via MockMvc
- **Coverage**:
  - Same as thread voting but for comments
  - Comment-specific endpoint testing
  - Vote change scenarios
- **Status**: ✅ All 7 tests passing

#### `ThreadVotingIntegrationTest.java`
- **Purpose**: End-to-end testing with real database
- **Coverage**:
  - Complete voting flow with data persistence
  - Vote count accuracy across operations
  - User vote status persistence
- **Data**: Uses test SQL scripts for setup/cleanup
- **Status**: ✅ All 2 tests passing

#### `CommentVotingIntegrationTest.java`
- **Purpose**: End-to-end testing for comment voting
- **Coverage**:
  - Comment voting flow with persistence
  - Multiple comment voting scenarios
  - Vote status across comment retrieval
- **Status**: ✅ All 2 tests passing

#### `VotingSystemIntegrationTest.java`
- **Purpose**: Tests the generic voting system works for both entities
- **Coverage**:
  - Independent voting on threads and comments
  - Vote count accuracy
  - Generic system functionality
- **Status**: ✅ All 2 tests passing

## Test Data

Each test class uses dedicated test data files to avoid ID conflicts:

### Thread Voting Tests (IDs 100+)
- **`thread-voting-test-data.sql`**: Sets up test box (ID 100) and thread (ID 100)
- **`thread-voting-cleanup.sql`**: Cleans up thread voting test data

### Comment Voting Tests (IDs 200+)  
- **`comment-voting-test-data.sql`**: Sets up test box (ID 200), thread (ID 200), and comments (IDs 200, 201)
- **`comment-voting-cleanup.sql`**: Cleans up comment voting test data

### Generic Voting System Tests (IDs 300+)
- **`voting-system-test-data.sql`**: Sets up test box (ID 300), thread (ID 300), and comment (ID 300)
- **`voting-system-cleanup.sql`**: Cleans up voting system test data

## Test Patterns

### Authentication Testing
```java
@Test
void testVoteNotAuthenticated() throws Exception {
    // Test without @WithSharkboxUser annotation
    // Expects 401 Unauthorized
}

@Test
@WithSharkboxUser
void testVoteAuthenticated() throws Exception {
    // Test with authentication
    // Expects successful response
}
```

### Vote Behavior Testing
```java
@Test
@WithSharkboxUser
void testVoteToggle() throws Exception {
    // 1. Vote up
    // 2. Vote up again (should remove)
    // 3. Vote down (should create new vote)
}
```

### Response Validation
```java
.andExpect(jsonPath("$.upvotes").value(1))
.andExpect(jsonPath("$.downvotes").value(0))
.andExpect(jsonPath("$.userVote").value(true))
```

## Running Tests

### All Voting Tests
```bash
./mvnw test -Dtest="*Voting*"
```

### Specific Test Classes
```bash
./mvnw test -Dtest="VoteServiceTest"
./mvnw test -Dtest="ThreadVotingTest"
./mvnw test -Dtest="CommentVotingTest"
```

### Integration Tests Only
```bash
./mvnw test -Dtest="*IntegrationTest"
```

## Test Coverage

The test suite covers:

✅ **Authentication & Authorization**
- Unauthenticated requests are rejected
- Authenticated users can vote

✅ **Vote Creation**
- New votes are properly created
- Vote counts are updated

✅ **Vote Removal**
- Voting the same way twice removes the vote
- Vote counts are decremented

✅ **Vote Changes**
- Switching from upvote to downvote updates the vote
- Vote counts are properly recalculated

✅ **Data Persistence**
- Votes persist across requests
- Vote counts remain accurate

✅ **Error Handling**
- Invalid entity IDs return appropriate errors
- Null vote values are rejected

✅ **Generic System**
- Same voting logic works for threads and comments
- Independent voting on different entity types

✅ **API Response Format**
- Consistent response structure
- Proper JSON field validation

## Test Base Classes

All tests extend `SharkboxApiTestBase` which provides:
- `MockMvc` for HTTP testing
- `ObjectMapper` for JSON serialization
- Test configuration with mock JWT decoder

Integration tests use `@WithSharkboxUser` for authentication and `@Sql` for data setup/cleanup.
