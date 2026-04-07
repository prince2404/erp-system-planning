# Apana Swastha Kendra Hospital ERP

Full-stack Hospital ERP implementation based on `Hospital_ERP_ASK_Complete.md`.

## Stack

- Backend: Spring Boot 3, Spring Security, Spring Data JPA, JWT, MySQL 8
- Frontend: React 18, Vite, Tailwind CSS, Zustand, React Query, Axios
- Package manager: Bun for frontend dependency and script commands

## Local Setup

1. Create MySQL database:

```sql
CREATE DATABASE hospital_erp_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Copy environment placeholders:

```powershell
Copy-Item .env.example .env
```

3. Fill `.env` values locally. Do not commit real credentials.

4. Run backend:

```powershell
cd hospital-erp-backend
mvn spring-boot:run
```

5. Run frontend with Bun:

```powershell
cd hospital-erp-frontend
bun install
bun run dev
```

## First Admin Bootstrap

No dummy data is seeded. To create the first production administrator, set `APP_BOOTSTRAP_TOKEN` and call:

```http
POST /api/auth/bootstrap-super-admin
```

This endpoint only works while there are no users in the database and requires the bootstrap token.
