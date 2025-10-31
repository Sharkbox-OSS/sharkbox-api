-- Test data for thread voting integration tests

-- Insert test box
INSERT INTO box (id, access, created_at, description, slug, name, owner) 
VALUES (100, 0, CURRENT_TIMESTAMP, 'Thread Test box', 'thread-test-box', 'Thread Test Box', 'test-user');

-- Insert test thread (owned by different user than the voter)
INSERT INTO thread (id, user_id, box_id, created_at, type, content, title) 
VALUES (100, 'thread-owner-uuid', 100, CURRENT_TIMESTAMP, 0, 'Thread test content', 'Thread Test Thread');
