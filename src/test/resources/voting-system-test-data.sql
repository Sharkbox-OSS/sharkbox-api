-- Test data for voting system integration tests

-- Insert test box
INSERT INTO box (id, access, created_at, description, slug, name, owner) 
VALUES (300, 0, CURRENT_TIMESTAMP, 'Voting System Test box', 'voting-system-test-box', 'Voting System Test Box', 'test-user');

-- Insert test thread (owned by different user than the voter)
INSERT INTO thread (id, user_id, box_id, created_at, type, content, title) 
VALUES (300, 'thread-owner-uuid', 300, CURRENT_TIMESTAMP, 0, 'Voting system test thread content', 'Voting System Test Thread');

-- Insert test comment (owned by different user than the voter)
INSERT INTO comment (id, created_at, thread_id, user_id, content) 
VALUES (300, CURRENT_TIMESTAMP, 300, 'comment-owner-uuid', 'Voting system test comment');
