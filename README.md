<img width="1280" height="486" alt="image" src="https://github.com/user-attachments/assets/d88575d4-ac03-4fcb-9c2e-e8c0cf677229" />

Manage tasks and projects effectively through a web-based application. This system enables task creation, assignment, progress tracking, and completion.

## 📁 Stack
- **Backend**: Java, Spring Boot, Hibernate, Maven
- **Database**: PostgreSQL
- **Migrations**: Liquibase
- **Security:** JWT, OAuth2
- **Third-party applications used:** Dropbox, WhatsApp, Google Calendar
- **Deployment**: AWS, Ngrok

## 💡 Features
- ACID, CRUD and SOLID adherence
- PostgreSQL and Dropbox API intercommunication
  
<img width="1751" height="177" alt="image" src="https://github.com/user-attachments/assets/ceb8d706-8bfa-438b-bb11-3aaf7632024f" />

- Internal security with JWT token and external security with OAuth2 flow
- Role allocation (ADMIN acts as a project manager who creates and assigns work, USER acts as a developer who executes tasks) 🔐
- Soft deletion in almost all entities for data consistency
- Transactional safety for method changing database state
- WhatsApp notification usage for user's tasks
- Integration with third-party application of Google Calendar for tasks assignment
- Docker and Swagger usage
- +80% test coverage (both unit and integration testing)
- CI/CD checkstyle pipeline: custom & Qodana
- AWS CI for flexible and rapid updates deployment

<img width="806" height="293" alt="Знімок екрана 2026-05-03 165555" src="https://github.com/user-attachments/assets/0bbdc1a9-001c-4b82-8994-c30230003267" />

## 📌 API endpoints
#### 🔑 Auth Controller 🔑
- `POST` */api/auth/register* — User registration
- `POST` */api/auth/login* — User authentication

#### 📅 Google Authentication Controller 📅
- `GET` */api/auth/google/authorize* — Authenticate Google Calendar account as ADMIN to assign tasks for users
- `GET` */api/auth/google/callback* — Google Calendar account url callback

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
- `GET` */api/projects/search?[status/end_date]={[status/end_date]}* — Search projects by parameter (for ADMIN only)

#### 🎲 Task Controller 🎲
- `POST` */api/tasks* — Create a new task (for ADMIN only)
- `GET` */api/tasks?projectId={projectId}* — Retrieve tasks for a project (ADMIN retrieves any project's tasks, USER can retrieve only their own projects' tasks)
- `GET` */api/tasks/{id}* — Retrieve task details (ADMIN retrieves any task, USER can retrieve only their own task)
- `PUT` */api/tasks/{id}* — Update task (ADMIN updates any task, USER can update only their own task)
- `DELETE` */api/tasks/{id}* — Delete task (for ADMIN only)
- `GET` */api/tasks/search?[priority/status]={[priority/status]}* — Search tasks by parameter (for ADMIN only)

#### ✍️ Comment Controller ✍️
- `POST` */api/comments* — Add a comment to a task (ADMIN comments any task, USER can comment only their own task)
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

We allow labels to be assigned to tasks for better organization. In our migrations we have default label colors, which are possible Google Calendar event colors (i.e. `11 TOMATO`, `6 TANGERINE`, `2 SAGE`). You can set any label to any task.
Mind: correspondence between label color and task priority is **on user** since user can create their own labels. However, we highly recommend setting `2 SAGE` for `LOW` priority tasks, `6 TANGERINE` for `MEDIUM` priority tasks and `11 TOMATO` for `HIGH` priority tasks.

<img width="1491" height="411" alt="Знімок екрана 2026-05-01 115510" src="https://github.com/user-attachments/assets/74d6c5d8-c06d-4ebd-a357-37d4c20aa54a" />

> Due to Google Calendar's policy, task's label color is visible only for ADMIN (assigner), but it can be changed in assignee's calendar manually as well.

## 📦 Setup
### Docker configuration
Steps to reproduce below.
1. Copy `.env.sample` file to new `.env` file
2. Fill your `.env` file with required environment variables
3. Run Docker application
   
### Third-party API integration
#### ⭐ Dropbox Database
Just fill your `.env` file with appropriate variable by generating your own Dropbox access token

<img width="400" height="538" alt="image" src="https://github.com/user-attachments/assets/2e7821ab-b48b-4201-833d-8cedf1fa2b37" />

> You can also join Dropbox in the way like we join Google Calendar API now (see below). OAuth 2.0 can give you more flexibility and safety; nevertheless, Dropbox API access token is easier to implement and works quicker locally.

#### ⭐ WhatsApp notification
Task assignment notifications are sent via WhatsApp Business API (Meta Cloud API). Also, we use Meta API message template with Google Calendar event reference in such messages.

<img width="400" height="526" alt="image" src="https://github.com/user-attachments/assets/52195ea6-9150-4622-ac9f-b38064aba804" />

> Due to Meta's policy, the system can only send free-form text messages to a user within a **24-hour window** after that user sends a message to the business account first. Each assignee must send any message to the business WhatsApp number before they can receive task notifications.

1. Do not forget to fill your `.env` file with appropriate variables by setting up your own Meta API
2. Open WhatsApp
3. Add the business number to your contacts: `+1 555 145 2899` (or your configured number)
4. Send any message, for example `start`
5. After that, task assignment notifications will be delivered automatically
> If the 24-hour window expires, you must send a message again to reactivate notifications.

#### ⭐ Google Calendar event invitation
> Due to Google's policy, the system can join the feature of sending Google Calendar event (task) invitation to users only manually through OAuth 2.0 standard.

Steps to reproduce below.
1. Do not forget to fill your `.env` file with appropriate variables by setting up your own Google Cloud API. Mind to add your `/api/auth/google/callback` endpoint as `GOOGLE_REDIRECT_URI` (for prod stage, we have link generated by Ngrok technology here)
2. Log into the system as an ADMIN
3. Apply for `GET /api/auth/google/authorize` endpoint to get special link for Google Calendar integration into your profile (as a project manager)
4. After successful page message return to the app's main page and test the system with assigning tasks to some users

<img width="1434" height="393" alt="image" src="https://github.com/user-attachments/assets/074f86f8-1599-4be5-bd2f-a86ef1ccecde" />

##### 🔎 Build your project with `mvn clean package` and use `mvn clean verify` for CI check

## 👣 Postman API
**API endpoints collection** is available at https://www.postman.com/astashenkova-katya-2138811/task-management-app

**Documentation** is available at https://documenter.getpostman.com/view/51183420/2sBXqKp1Ap

Steps to reproduce below.
1. **Authenticate** — Run the authentication request first to obtain a token
2. **Connect Google Calendar** — Run the authentication request first in order to get your connection link, past this link into your browser and select your email, after successfull message return back
3. **Explore available requests** — Browse the collection folders to find grouped endpoints; each request includes a description of its purpose and expected parameters
4. **Send requests** — Select any request, adjust path parameters or the request body as needed, click `Send` to interact with the API directly
5. **Use it for testing** — The collection can be run as a full test suite via the `Collection Runner` or with `Newman` for automated testing

## 🚀 Swagger API
Documentation is available at http://35.153.183.93/api/swagger-ui/index.html

Steps to reproduce below.
1. **Authenticate** — Run the authentication request first to obtain a token
2. **Connect Google Calendar** — Run the authentication request first in order to get your connection link, past this link into your browser and select your email, after successfull message return back
3. **Explore available requests** — Each controller includes a short description of its purpose
4. **Send requests** — Select any request, click `Try it out`, adjust path parameters or the request body as needed, click `Execute` to interact with the API directly

## 🎥 Demo video

See here: 
