# Voting API Examples

## Overview
The voting system allows users to upvote or downvote both threads and comments. Users can only vote once per entity, and voting the same way twice removes the vote. The system uses a generic voting infrastructure that works for both threads and comments.

## API Endpoints

### Vote on a Thread
```http
PATCH /api/v1/thread/{id}
Content-Type: application/json
Authorization: Bearer <token>

{
  "isUpvote": true
}
```

**Response:**
```json
{
  "id": 123,
  "title": "Sample Thread",
  "content": "Thread content...",
  "upvotes": 5,
  "downvotes": 2,
  "userVote": true,
  "createdAt": "2024-01-15T10:30:00Z",
  "userId": 1,
  "box": { ... }
}
```

### Get Thread with Vote Status
```http
GET /api/v1/thread/{id}
```

**Response:**
```json
{
  "id": 123,
  "title": "Sample Thread", 
  "content": "Thread content...",
  "upvotes": 5,
  "downvotes": 2,
  "userVote": true,  // null = no vote, true = upvote, false = downvote
  "createdAt": "2024-01-15T10:30:00Z",
  "userId": 1,
  "box": { ... }
}
```

### Get Threads in Box with Vote Status
```http
GET /api/v1/box/{slug}/threads?page=0&size=10
```

**Response:**
```json
{
  "content": [
    {
      "id": 123,
      "title": "Sample Thread",
      "upvotes": 5,
      "downvotes": 2,
      "userVote": true,
      ...
    }
  ],
  "pageable": { ... },
  "totalElements": 25
}
```

### Vote on a Comment
```http
PATCH /api/v1/comment/{threadId}/{commentId}
Content-Type: application/json
Authorization: Bearer <token>

{
  "isUpvote": false
}
```

**Response:**
```json
{
  "id": 456,
  "content": "This is a comment",
  "upvotes": 3,
  "downvotes": 1,
  "userVote": false,
  "createdAt": "2024-01-15T11:00:00Z",
  "userId": 1,
  "threadId": 123
}
```

### Get Comments with Vote Status
```http
GET /api/v1/comment/{threadId}
```

**Response:**
```json
[
  {
    "id": 456,
    "content": "This is a comment",
    "upvotes": 3,
    "downvotes": 1,
    "userVote": false,
    "createdAt": "2024-01-15T11:00:00Z",
    "userId": 1,
    "threadId": 123
  }
]
```

## Voting Behavior

1. **First Vote**: User votes up/down → vote is recorded
2. **Same Vote Again**: User votes the same way → vote is removed
3. **Change Vote**: User switches from upvote to downvote (or vice versa) → vote is updated
4. **No Vote**: `userVote` field is `null`

## Vote Status Field
- `userVote: null` - User has not voted on this entity
- `userVote: true` - User has upvoted this entity  
- `userVote: false` - User has downvoted this entity

## Generic Voting System

The voting system uses a generic infrastructure that works for both threads and comments:

- **Single Vote Table**: All votes are stored in a generic `vote` table
- **Entity Type**: Distinguishes between "thread" and "comment" votes
- **Entity ID**: References the specific thread or comment ID
- **Reusable Logic**: Same voting logic for both threads and comments
- **Consistent API**: Similar endpoints and response formats for both entity types
