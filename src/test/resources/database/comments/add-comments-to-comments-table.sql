INSERT INTO comments (id, task_id, user_id, text, timestamp, is_deleted) VALUES
                                                                             (1, 1, 5, 'Initial database schema looks good, approved for implementation', '2026-01-26 09:15:00', false),
                                                                             (2, 2, 3, 'JWT token expiration should be set to 24 hours as per security policy', '2026-03-05 14:30:00', false),
                                                                             (3, 2, 5, 'Please also add refresh token support before closing this task', '2026-03-06 10:45:00', false),
                                                                             (4, 3, 2, 'Tax calculation logic needs to be verified with the finance team', '2026-06-20 16:00:00', false),
                                                                             (5, 4, 4, 'Training dataset has been uploaded to the shared drive for review', '2026-05-15 11:20:00', false);
