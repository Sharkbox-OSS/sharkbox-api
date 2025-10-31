-- Test data for comment voting integration tests

-- Insert test box
INSERT INTO box (id, access, created_at, description, slug, name, owner) 
VALUES (200, 0, CURRENT_TIMESTAMP, 'Comment Test box', 'comment-test-box', 'Comment Test Box', 'test-user');

-- Insert test thread (owned by different user than the voter)
INSERT INTO thread (id, user_id, box_id, created_at, type, content, title) 
VALUES (200, 'thread-owner-uuid', 200, CURRENT_TIMESTAMP, 0, 'Comment test thread content', 'Comment Test Thread');

-- Insert test comments (owned by different user than the voter)
INSERT INTO comment (id, created_at, thread_id, user_id, content) 
VALUES (200, CURRENT_TIMESTAMP, 200, 'comment-owner-uuid', 'Comment test comment 1');

INSERT INTO comment (id, created_at, thread_id, user_id, content) 
VALUES (201, CURRENT_TIMESTAMP, 200, 'comment-owner-uuid', 'Comment test comment 2');
