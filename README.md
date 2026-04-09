# Apana Swastha Kendra Platform

Full-stack implementation evolving from the original hospital ERP into the healthcare business-network platform for Apana Swastha Kendra.

## Stack

- Backend: Spring Boot 3, Spring Security, Spring Data JPA, JWT, MySQL 8
- Frontend: React 18, Vite, Tailwind CSS, Zustand, React Query, Axios
- Package manager: Bun for frontend dependency and script commands

## Phase 1 Scope

Phase 1 is now focused on the business-network foundation:

- simple user creation with auto-generated temporary passwords
- role template based permissions with toggle overrides
- system-defined permission catalog for all future modules
- dropdown-based geography and center creation
- self-service user profile, bank details, and contact verification
- email/SMS onboarding and OTP flows with `LOG_ONLY` delivery fallback until provider credentials are added

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

5. Optional for Phase 1 notification testing:

```env
EMAIL_DELIVERY_MODE=LOG_ONLY
SMS_DELIVERY_MODE=LOG_ONLY
```

In `LOG_ONLY` mode, welcome messages and OTPs are saved in the backend notification audit and OTP previews are returned in the profile verification API for local testing.

6. Run backend:

```powershell
cd hospital-erp-backend
mvn spring-boot:run
```

7. Run frontend with Bun:

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
