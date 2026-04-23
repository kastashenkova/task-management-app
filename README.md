# 📆 Task Management App 📆
Manage tasks and projects effectively through a web-based application. This system enables task creation, assignment, progress tracking, and completion.

## 📁 Stack
**Backend application**: Java, Hibernate, Spring Boot

## 💡 Features
- ACID, CRUD and SOLID adherence
- PostgreSQL and Dropbox API intercommunication
- Security, JWT token and role allocation (ADMIN & USER) 🔐
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
- `PUT` */users/{id}/role* — Update user role
- `GET` */users/me* — Get my profile info
- `PATCH` */users/me* — Update profile info

#### 🥇 Project Controller 🥇
- `POST` */api/projects* — Create a new project
- `GET` */api/projects* — Retrieve user's projects
- `GET` */api/projects/{id}* — Retrieve project details
- `PUT` */api/projects/{id}* — Update project
- `DELETE` */api/projects/{id}* — Delete project

#### 🎲 Task Controller 🎲
- `POST` */api/tasks* — Create a new task
- `GET` */api/tasks?projectId={projectId}* — Retrieve tasks for a project
- `GET` */api/tasks/{id}* — Retrieve task details
- `PUT` */api/tasks/{id}* — Update task
- `DELETE` */api/tasks/{id}* — Delete task

#### ✍️ Comment Controller ✍️
- `POST` */api/comments* — Add a comment to a task
- `GET` */api/comments?taskId={taskId}* — Retrieve comments for a task
- `DELETE` */api/comments/{id}* — Delete comment

#### 🔗 Attachment Controller (interaction with Dropbox API) 🔗
- `POST` */api/attachments* — Upload an attachment to a task
- `GET` */api/attachments?taskId={taskId}* — Retrieve attachments for a task
- `GET` */api/attachments/{id}* — Download attachment
- `DELETE` */api/attachments/{id}* — Delete attachment

#### 🚩 Label Controller 🚩
- `POST` */api/labels* — Create a new label
- `GET` */api/labels* — Retrieve labels
- `PUT` */api/labels/{id}* — Update label
- `DELETE` */api/labels/{id}* — Delete label

## 📦 Setup
#### Steps to reproduce Docker configuration
1. Copy `.env.sample` file to new `.env` file
2. Fill your `.env` file with required environment variables
3. Run Docker application

🔎 **Build your project with `mvn clean package` and use `mvn clean verify` for CI check.**

## 📑 Documentation API
Swagger documentation is available at [http://localhost:8091/api/swagger-ui/index.html#/](http://localhost:8091/api/swagger-ui/index.html#/)
