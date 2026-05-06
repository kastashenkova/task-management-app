INSERT INTO tasks (id, name, description, priority, status, due_date, project_id, assignee_id, calendar_event_id, label_id, is_deleted)
VALUES
    (1, 'Design Database Schema', 'Create and document the relational database schema including all tables and relationships', 'HIGH', 'COMPLETED', '2026-12-31', 1, 3, 101, 11, false),
    (2, 'Implement Authentication', 'Develop JWT-based user authentication with login, registration and password reset flows', 'HIGH', 'IN_PROGRESS', '2026-12-31', 1, 5, 102, 11, false),
    (3, 'Build Payroll Module', 'Develop salary calculation module with tax deductions and automated monthly payslip generation', 'MEDIUM', 'NOT_STARTED', '2026-07-15', 3, 3, 103, 6, false),
    (4, 'Train Chatbot Model', 'Collect training data and fine-tune the AI model for customer support response accuracy', 'MEDIUM', 'NOT_STARTED', '2026-06-01', 4, 5, 104, 6, false),
    (5, 'Create Sales Report View', 'Design and implement an SQL view aggregating weekly and monthly sales data for the dashboard', 'LOW', 'IN_PROGRESS', '2026-11-20', 5, 3, 105, 2, false);
