# Team Task Manager (Full Stack)

Full-stack assignment implementation for project/task management with role-based access (`ADMIN` / `MEMBER`).

## Features

- JWT authentication (`/auth/signup`, `/auth/login`)
- Project creation and team member management
- Task creation, assignment, and status tracking
- Dashboard summary (`total`, `todo`, `in_progress`, `done`, `overdue`)
- Role-based authorization:
  - `ADMIN`: can add members and create tasks
  - `MEMBER`: can view project data and update status for assigned tasks

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Vanilla JS frontend served from Spring static files

## Run Locally

1. Set environment variables:

   - `DATABASE_URL` (example: `jdbc:postgresql://localhost:5432/taskmanager`)
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `JWT_SECRET` (minimum 32 characters)

2. Run:

   ```bash
   ./mvnw spring-boot:run
   ```

3. Open:

   - [http://localhost:8080](http://localhost:8080)

## API Endpoints

- Auth
  - `POST /auth/signup`
  - `POST /auth/login`
- Projects
  - `POST /api/projects`
  - `GET /api/projects`
  - `GET /api/projects/{projectId}`
  - `POST /api/projects/{projectId}/members`
- Tasks
  - `POST /api/tasks`
  - `GET /api/tasks/project/{projectId}`
  - `PATCH /api/tasks/{taskId}/status`
  - `GET /api/tasks/my`
- Dashboard
  - `GET /api/dashboard/summary`
- Users
  - `GET /api/users`

## Railway Deployment

1. Push project to GitHub.
2. Create new Railway project from repo.
3. Add PostgreSQL plugin/service.
4. Configure environment variables:
   - `DATABASE_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `JWT_SECRET`
5. Railway detects Maven and deploys automatically.

## Submission Checklist

- Live URL (Railway)
- GitHub repository URL
- This README
- 2-5 minute demo video
