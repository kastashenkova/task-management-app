# 📆 Task Management App 📆
Manage tasks and projects effectively through a web-based application. This system enables task creation, assignment, progress tracking, and completion.

## 📁 Stack
**Backend application**: Java, Hibernate, Spring Boot

## 💡 Features
- ACID, CRUD and SOLID adherence
- PostgreSQL and Dropbox API intercommunication
- Security, JWT token and role allocation (ADMIN acts as a project manager who creates and assigns work, USER acts as a developer who executes tasks) 🔐
- Email notification usage for user's tasks
- Integration with third-party application of Google Calendar
- Docker and Swagger usage
- +80% test coverage
- CI/CD checkstyle pipeline: custom & Qodana
- Deployment into AWS

## 📌 API endpoints
#### 🔑 Auth Controller 🔑
- `POST` */api/auth/register* — User registration
- `POST` */api/auth/login* — User authentication

#### 👤 Users Controller 👤
- `PUT` */users/{id}/role* — Update user role (for ADMIN only)
- `GET` */users/me* — Get my profile info (for authorized users)
- `PATCH` */users/me* — Update profile info (for authorized users)

#### 🥇 Project Controller 🥇
- `POST` */api/projects* — Create a new project (for ADMIN only)
- `GET` */api/projects* — Retrieve user's projects (ADMIN retrieves all projects, USER retrieves their own projects)
- `GET` */api/projects/{id}* — Retrieve project details (ADMIN retrieves any project, USER can retrieve only their own project)
- `PUT` */api/projects/{id}* — Update project (ADMIN updates any project, USER can update only their own project)
- `DELETE` */api/projects/{id}* — Delete project (for ADMIN only)

#### 🎲 Task Controller 🎲
- `POST` */api/tasks* — Create a new task (for ADMIN only)
- `GET` */api/tasks?projectId={projectId}* — Retrieve tasks for a project (ADMIN retrieves any project's tasks, USER can retrieve only their own projects' tasks)
- `GET` */api/tasks/{id}* — Retrieve task details (ADMIN retrieves any task, USER can retrieve only their own task)
- `PUT` */api/tasks/{id}* — Update task (ADMIN updates any task, USER can update only their own task)
- `DELETE` */api/tasks/{id}* — Delete task (for ADMIN only)

#### ✍️ Comment Controller ✍️
- `POST` */api/comments* — Add a comment to a task (for authorized users)
- `GET` */api/comments?taskId={taskId}* — Retrieve comments for a task (ADMIN retrieves any task's comments, USER can retrieve only their own tasks' comments)
- `DELETE` */api/comments/{id}* — Delete comment (ADMIN deletes any comment, USER can delete only their own comment)

#### 🔗 Attachment Controller (interaction with Dropbox API) 🔗
- `POST` */api/attachments?taskId={taskId}* — Upload an attachment to a task (ADMIN uploads attachment for any task, USER can upload attachment only for their own task)
- `GET` */api/attachments?taskId={taskId}* — Retrieve attachments for a task (ADMIN retrieves any task's attachments, USER can retrieve only their own tasks' attachments)
- `GET` */api/attachments/{id}* — Download attachment (ADMIN retrieves any attachment, USER can retrieve only their own attachment)
- `DELETE` */api/attachments/{id}* — Delete attachment (ADMIN deletes any attachment, USER can delete only their own attachment)

#### 🚩 Label Controller 🚩
- `POST` */api/labels* — Create a new label (for ADMIN only)
- `GET` */api/labels* — Retrieve labels (for authorized users)
- `PUT` */api/labels/{id}* — Update label (for ADMIN only)
- `DELETE` */api/labels/{id}* — Delete label (for ADMIN only)

## 📦 Setup
#### Steps to reproduce Docker configuration
1. Copy `.env.sample` file to new `.env` file
2. Fill your `.env` file with required environment variables
3. Run Docker application

🔎 **Build your project with `mvn clean package` and use `mvn clean verify` for CI check.**

## 📑 Documentation API
Swagger documentation is available at [http://localhost:8091/api/swagger-ui/index.html#/](http://localhost:8091/api/swagger-ui/index.html#/)
