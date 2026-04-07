# HOSPITAL ERP MANAGEMENT SYSTEM
## Apana Swastha Kendra
**Full Stack Enterprise Resource Planning Blueprint**
Spring Boot 3 | React 18 | MySQL 8 | JWT Security
**29 Tables | 8 Modules | 8 Phases**
Complete Project Planning Document

- **Prepared for:** Prince | GitHub: github.com/prince2404
- **Date:** April 04, 2026 | **Version:** 1.0

---

## TABLE OF CONTENTS

| Chapter | Title | Page |
|---------|-------|------|
| 01 | Project Overview & Vision | 3 |
| 02 | Tech Stack — Complete Breakdown | 4 |
| 03 | System Architecture | 5 |
| 04 | User Hierarchy & Role System | 6 |
| 05 | Database Schema — All 29 Tables | 8 |
| 06 | Module Breakdown — All 8 Modules | 13 |
| 07 | API Endpoints — Complete Reference | 18 |
| 08 | UI/UX Design System | 22 |
| 09 | Phase-by-Phase Build Plan | 24 |
| 10 | Security Architecture | 28 |
| 11 | Folder Structure | 29 |
| 12 | Deployment & DevOps | 30 |

---

## CHAPTER 01 — Project Overview & Vision

### What Are We Building?
A multi-center, role-based Hospital ERP Management System named **Apana Swastha Kendra**. This is a full-stack enterprise application covering patient management, OPD/IPD workflows, pharmacy, billing, HR, payroll, wallet/commission systems, and analytics — designed to operate across a geographic hierarchy of States, Districts, Blocks, and Centers.

### Key Goals

| Goal | Description |
|------|-------------|
| Multi-Center Operations | Support 3 states, ~60 districts, ~300 blocks, unlimited centers from a single system. |
| Role-Based Access Control | 11 distinct roles with rank-based hierarchy and granular per-user permission overrides. |
| Geographic Scoping | Every API auto-filters data based on logged-in user's state/district/block/center scope. |
| Enterprise UI | Light-theme, professional clinical design. Not a student project — a deployable product. |
| Bulk Operations | Excel/CSV bulk user import with validation preview and error reporting. |
| Real-time Notifications | Upline alerts for wallet updates, low stock, commission earned, leave requests. |

### Project Scope — 3 States Focus

| Level | Count (3-State Scope) | Role Assigned | Has Center? |
|-------|-----------------------|---------------|-------------|
| Super Admin | 1–2 users | SUPER_ADMIN | No — System-wide |
| State Manager | 3 users (1 per state) | STATE_MANAGER | No — State scope |
| District Manager | ~60 users | DISTRICT_MANAGER | No — District scope |
| Block Manager | ~300 users | BLOCK_MANAGER | No — Block scope |
| Center Staff/Doctor/etc | Unlimited | Multiple roles | Yes — Center bound |

---

## CHAPTER 02 — Tech Stack — Complete Breakdown

### Backend

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 (LTS) | Primary language |
| Spring Boot | 3.2.x | Application framework |
| Spring Security | 6.x | Authentication & authorization |
| Spring Data JPA | 3.2.x | ORM layer / repository pattern |
| Hibernate | 6.x | JPA implementation, SQL generation |
| jjwt (io.jsonwebtoken) | 0.12.x | JWT token generation & validation |
| Apache POI | 5.2.x | Excel (.xlsx) parsing for bulk upload |
| MapStruct | 1.5.x | Entity ↔ DTO mapping |
| Lombok | 1.18.x | Boilerplate reduction (@Getter, @Builder) |
| Maven | 3.9.x | Build tool & dependency management |
| Validation (Jakarta) | 3.x | Request DTO validation (@NotNull etc) |

### Database

| Technology | Version | Purpose |
|------------|---------|---------|
| MySQL | 8.0 | Primary relational database |
| HikariCP | Built-in | Connection pooling (Spring Boot default) |
| Flyway (optional) | 9.x | Database migration management |

### Frontend

| Technology | Version | Purpose |
|------------|---------|---------|
| React | 18.x | UI framework |
| Vite | 5.x | Build tool (fast HMR) |
| Tailwind CSS | 3.x | Utility-first styling |
| shadcn/ui | Latest | Pre-built accessible components |
| Zustand | 4.x | Global state management (auth, ui) |
| React Query (TanStack) | 5.x | Server state, caching, refetch |
| Axios | 1.x | HTTP client with interceptors |
| React Router | 6.x | Client-side routing & protected routes |
| Recharts | 2.x | Charts & data visualization |
| react-hook-form | 7.x | Form state & validation |
| Zod | 3.x | Schema validation (pairs with RHF) |
| react-dropzone | 14.x | Drag-and-drop file upload (bulk import) |
| date-fns | 3.x | Date formatting & manipulation |
| lucide-react | Latest | Icon library |

### Dev Tools & Environment

| Tool | Purpose |
|------|---------|
| IntelliJ IDEA | Backend IDE |
| VS Code | Frontend IDE |
| Postman | API testing |
| Git + GitHub | Version control (github.com/prince2404) |
| MySQL Workbench | Database GUI |
| DBeaver (optional) | Alternative DB GUI |
| Docker (optional) | Containerize MySQL for consistent dev env |

---

## CHAPTER 03 — System Architecture

### High-Level Architecture

```
┌──────────────────────────────────────────────────────────┐
│                 REACT FRONTEND (Vite)                    │
│   Zustand Store │ React Query │ Axios Interceptor        │
│         Protected Routes │ Role-based Sidebar            │
└──────────────────────────────────────────────────────────┘
                        │ HTTP/REST (JSON)
                        │ Bearer: JWT Token
                        ▼
┌──────────────────────────────────────────────────────────┐
│            SPRING BOOT 3 BACKEND (:8080)                 │
│   JwtAuthFilter → SecurityConfig → Controllers           │
│       Services (Business Logic + ScopeFilter)            │
│           Repositories (Spring Data JPA)                 │
└──────────────────────────────────────────────────────────┘
                        │ JPA / Hibernate
                        ▼
┌──────────────────────────────────────────────────────────┐
│                 MySQL 8.0 DATABASE                       │
│            hospital_erp_db (29 tables)                   │
└──────────────────────────────────────────────────────────┘
```

### Request Lifecycle

| Step | Where | What Happens |
|------|-------|--------------|
| 1 | React sends HTTP request | Axios adds `Authorization: Bearer` header automatically via interceptor |
| 2 | JwtAuthFilter intercepts | Validates JWT signature, expiry, extracts userId + role + permissions + scopeId |
| 3 | SecurityConfig checks | `@PreAuthorize` annotation on controller method validates role |
| 4 | ScopeFilter applies | Service layer injects scope (centerId/districtId/stateId) into every DB query |
| 5 | Repository executes | JPA query returns only data belonging to the logged-in user's scope |
| 6 | Response wrapped | ApiResponse wrapper: `{ success, data, message, timestamp }` returned |
| 7 | React Query caches | Frontend caches response, updates UI, shows data |

### JWT Token Structure

```json
Header: { "alg": "HS256", "typ": "JWT" }

Payload: {
  "sub": "45",
  "name": "Dr. Sharma",
  "role": "DOCTOR",
  "rank": 8,
  "centerId": 3,
  "centerName": "City Hospital",
  "scopeType": "CENTER",
  "scopeId": 3,
  "permissions": ["READ_PATIENT","READ_OPD","DISPENSE_MEDICINE"],
  "iat": 1704067200,
  "exp": 1704068100
}
```

- **Access Token Expiry:** 15 minutes
- **Refresh Token Expiry:** 7 days

---

## CHAPTER 04 — User Hierarchy & Role System

### Geographic Hierarchy

```
SUPER ADMIN (rank 1) — System-wide
  └── State Manager Punjab (rank 2) — STATE scope
        └── District Manager Ludhiana (rank 3) — DISTRICT scope
              └── Block Manager Block-A (rank 4) — BLOCK scope
                    ├── Center: City Hospital
                    │     ├── Doctor (rank 7)
                    │     ├── Pharmacist (rank 8)
                    │     ├── Receptionist (rank 8)
                    │     ├── HR Manager (rank 7)
                    │     └── Center Staff (rank 9)
                    └── Center: Village Clinic
                          └── ... same staff roles
```

### All 11 Roles — Permissions Matrix

| Role | Rank | Scope | Key Access |
|------|------|-------|------------|
| SUPER_ADMIN | 1 | SYSTEM | Full access — all modules, all centers, all data |
| ADMIN | 2 | SYSTEM | Full access except system config |
| STATE_MANAGER | 3 | STATE | All centers in their state, management dashboard |
| DISTRICT_MANAGER | 4 | DISTRICT | All centers in their district, user creation |
| BLOCK_MANAGER | 5 | BLOCK | All centers in their block |
| HR_MANAGER | 6 | CENTER | HR, attendance, payroll, leave approvals |
| DOCTOR | 7 | CENTER | Patient records, OPD, prescriptions, appointments |
| PHARMACIST | 8 | CENTER | Drug stock, dispense, alerts |
| RECEPTIONIST | 8 | CENTER | Patient registration, OPD queue, appointments |
| CENTER_STAFF | 9 | CENTER | Limited — view only most modules |
| PATIENT | 10 | SELF | Own records, appointments, bills only |

### Granular Permission System — Three-Layer Permission Model

- **Layer 1 — Role:** Broad default access (Doctor gets patient read, OPD manage, etc.)
- **Layer 2 — Permissions:** Granular actions stored in `user_permissions` table.
- **Layer 3 — Override:** At user creation, creator customizes permissions via checkbox UI.
  > **CRITICAL RULE:** Creator can only grant permissions they themselves possess.

### Available Permissions

| Module | Actions |
|--------|---------|
| PATIENT | READ, CREATE, EDIT, DELETE |
| OPD | READ, MANAGE_QUEUE, UPDATE_STATUS |
| IPD | READ, ADMIT, DISCHARGE |
| APPOINTMENTS | READ, BOOK, CANCEL |
| PHARMACY | READ_STOCK, ADD_STOCK, DISPENSE |
| BILLING | READ, CREATE_INVOICE, PROCESS_PAYMENT, REFUND |
| HR | READ_STAFF, MANAGE_ATTENDANCE, APPROVE_LEAVE, RUN_PAYROLL |
| WALLET | VIEW_BALANCE, VIEW_TRANSACTIONS |
| REPORTS | VIEW_CENTER, VIEW_DISTRICT, VIEW_STATE |
| USER_MGMT | CREATE_USER, EDIT_USER, DEACTIVATE_USER |

---

## CHAPTER 05 — Database Schema — All 29 Tables

### Schema Summary

- **Database:** hospital_erp_db
- **Engine:** InnoDB
- **Charset:** utf8mb4
- **Collation:** utf8mb4_unicode_ci
- **Total Tables:** 29

| Module | Tables | Table Names |
|--------|--------|-------------|
| Geographic | 3 | states, districts, blocks |
| Auth & Users | 4 | users, permissions, user_permissions, refresh_tokens |
| Centers & Wallets | 3 | centers, wallets, wallet_transactions |
| Patient | 3 | patients, opd_visits, ipd_admissions |
| Doctor & Appts | 4 | doctor_profiles, doctor_schedules, appointments, beds |
| Pharmacy | 4 | drugs, drug_stocks, drug_dispenses, stock_alerts |
| Billing | 3 | invoices, invoice_items, commissions |
| HR & Payroll | 5 | staff_profiles, attendance, leave_requests, leave_balances, payrolls |
| Notifications | 1 | notifications |
| **TOTAL** | **29** | |

---

### Geographic Tables

#### `states`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| name | VARCHAR(100) | NOT NULL — Punjab, Haryana, Bihar |
| code | VARCHAR(5) | NOT NULL UNIQUE — PB, HR, BR |

#### `districts`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| name | VARCHAR(100) | NOT NULL |
| state_id | BIGINT | FK → states.id ON DELETE CASCADE |

#### `blocks`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| name | VARCHAR(100) | NOT NULL |
| district_id | BIGINT | FK → districts.id ON DELETE CASCADE |

---

### Auth & User Tables

#### `users`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| name | VARCHAR(100) | NOT NULL |
| email | VARCHAR(100) | UNIQUE NOT NULL |
| password | VARCHAR(255) | BCrypt hashed, NOT NULL |
| phone | VARCHAR(15) | nullable |
| role | ENUM | SUPER_ADMIN, ADMIN, STATE_MANAGER, DISTRICT_MANAGER, BLOCK_MANAGER, HR_MANAGER, DOCTOR, PHARMACIST, RECEPTIONIST, CENTER_STAFF, PATIENT |
| rank | INT | NOT NULL — 1=highest authority, 10=lowest |
| center_id | BIGINT | FK → centers.id — NULL for managers |
| scope_type | ENUM | SYSTEM, STATE, DISTRICT, BLOCK, CENTER |
| scope_id | BIGINT | ID of state/district/block/center they manage |
| created_by | BIGINT | FK → users.id — self-referencing |
| is_active | BOOLEAN | DEFAULT true — soft delete flag |
| created_at | DATETIME | NOT NULL |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP |

#### `permissions`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| module | VARCHAR(50) | PATIENT, BILLING, HR, PHARMACY… |
| action | VARCHAR(50) | READ, CREATE, EDIT, DELETE, DISPENSE… |
| description | VARCHAR(200) | Human-readable description |

#### `user_permissions`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| user_id | BIGINT | FK → users.id ON DELETE CASCADE |
| permission_id | BIGINT | FK → permissions.id |
| granted_by | BIGINT | FK → users.id — who granted this |
| granted_at | DATETIME | NOT NULL |
| — | UNIQUE | (user_id, permission_id) — Prevent duplicate grants |

#### `refresh_tokens`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| token | VARCHAR(500) | UNIQUE NOT NULL |
| user_id | BIGINT | FK → users.id ON DELETE CASCADE |
| expiry | DATETIME | 7 days from creation |
| is_revoked | BOOLEAN | DEFAULT false |
| created_at | DATETIME | NOT NULL |

---

### Center & Wallet Tables

#### `centers`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| name | VARCHAR(150) | NOT NULL |
| address | TEXT | nullable |
| city | VARCHAR(100) | nullable |
| block_id | BIGINT | FK → blocks.id |
| district_id | BIGINT | FK → districts.id |
| state_id | BIGINT | FK → states.id |
| phone | VARCHAR(15) | nullable |
| email | VARCHAR(100) | nullable |
| is_active | BOOLEAN | DEFAULT true |
| created_at | DATETIME | NOT NULL |

#### `wallets`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| entity_type | ENUM | CENTER, BLOCK, DISTRICT, STATE |
| entity_id | BIGINT | ID of the entity this wallet belongs to |
| balance | DECIMAL(12,2) | DEFAULT 0.00 |
| last_updated | DATETIME | Updated on every transaction |

#### `wallet_transactions`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| wallet_id | BIGINT | FK → wallets.id |
| type | ENUM | CREDIT, DEBIT |
| amount | DECIMAL(10,2) | NOT NULL |
| reference_id | VARCHAR(100) | Invoice number / payroll ID |
| description | VARCHAR(255) | Human-readable reason |
| created_at | DATETIME | NOT NULL |

---

### Patient Tables

#### `patients`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uhid | VARCHAR(20) | UNIQUE — format: HOSP-2025-000001 |
| name | VARCHAR(100) | NOT NULL |
| age | INT | nullable — or computed from dob |
| date_of_birth | DATE | nullable |
| gender | ENUM | MALE, FEMALE, OTHER |
| blood_group | VARCHAR(5) | A+, B-, O+, AB+… |
| phone | VARCHAR(15) | NOT NULL |
| email | VARCHAR(100) | nullable |
| address | TEXT | nullable |
| emergency_contact | VARCHAR(15) | nullable |
| emergency_name | VARCHAR(100) | nullable |
| allergies | TEXT | Comma-separated or JSON tags |
| center_id | BIGINT | FK → centers.id |
| registered_by | BIGINT | FK → users.id |
| created_at | DATETIME | NOT NULL |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP |

#### `opd_visits`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| patient_id | BIGINT | FK → patients.id |
| doctor_id | BIGINT | FK → users.id |
| center_id | BIGINT | FK → centers.id |
| token_number | INT | Auto-increment per center per day, resets daily |
| visit_date | DATE | NOT NULL |
| symptoms | TEXT | nullable |
| diagnosis | TEXT | nullable — filled by doctor |
| prescription_notes | TEXT | nullable |
| status | ENUM | WAITING, IN_PROGRESS, COMPLETED, CANCELLED |
| fee | DECIMAL(8,2) | Consultation fee |
| created_at | DATETIME | NOT NULL |

#### `ipd_admissions`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| patient_id | BIGINT | FK → patients.id |
| doctor_id | BIGINT | FK → users.id |
| center_id | BIGINT | FK → centers.id |
| bed_id | BIGINT | FK → beds.id — marks bed as occupied |
| admission_date | DATETIME | NOT NULL |
| discharge_date | DATETIME | nullable — filled on discharge |
| ward | VARCHAR(50) | General, ICU, Maternity, Pediatric… |
| diagnosis | TEXT | nullable |
| treatment_notes | TEXT | nullable |
| daily_charge | DECIMAL(8,2) | Per-day bed/ward charge |
| status | ENUM | ADMITTED, DISCHARGED, TRANSFERRED |
| discharged_by | BIGINT | FK → users.id — nullable |
| created_at | DATETIME | NOT NULL |

---

### Doctor & Appointment Tables

#### `beds`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| center_id | BIGINT | FK → centers.id |
| ward | VARCHAR(50) | General, ICU, Maternity… |
| bed_number | VARCHAR(20) | NOT NULL — e.g. G-101, ICU-03 |
| is_occupied | BOOLEAN | DEFAULT false |
| — | UNIQUE | (center_id, bed_number) — No duplicate beds per center |

#### `doctor_profiles`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| user_id | BIGINT | FK → users.id UNIQUE — 1:1 with user |
| specialization | VARCHAR(100) | Cardiology, Orthopedics, General… |
| qualification | VARCHAR(200) | MBBS, MD, MS… |
| experience_years | INT | nullable |
| consultation_fee | DECIMAL(8,2) | Default fee |
| center_id | BIGINT | FK → centers.id |
| is_available | BOOLEAN | DEFAULT true |
| created_at | DATETIME | NOT NULL |

#### `doctor_schedules`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| doctor_id | BIGINT | FK → doctor_profiles.id |
| day_of_week | ENUM | MON, TUE, WED, THU, FRI, SAT, SUN |
| start_time | TIME | NOT NULL — e.g. 09:00:00 |
| end_time | TIME | NOT NULL — e.g. 13:00:00 |
| slot_duration_mins | INT | DEFAULT 15 |
| max_patients | INT | Max appointments per session |
| is_active | BOOLEAN | DEFAULT true |
| — | UNIQUE | (doctor_id, day_of_week) — One schedule per day per doctor |

#### `appointments`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| patient_id | BIGINT | FK → patients.id |
| doctor_id | BIGINT | FK → doctor_profiles.id |
| center_id | BIGINT | FK → centers.id |
| appointment_date | DATE | NOT NULL |
| slot_time | TIME | NOT NULL |
| type | ENUM | OPD, FOLLOW_UP, EMERGENCY |
| status | ENUM | BOOKED, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW |
| booking_fee | DECIMAL(8,2) | nullable |
| notes | TEXT | nullable |
| booked_by | BIGINT | FK → users.id |
| created_at | DATETIME | NOT NULL |
| — | UNIQUE | (doctor_id, appointment_date, slot_time) — Prevent double booking |

---

### Pharmacy Tables

#### `drugs`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| name | VARCHAR(150) | NOT NULL |
| generic_name | VARCHAR(150) | nullable |
| category | VARCHAR(100) | Antibiotic, Painkiller, Antihistamine… |
| unit | ENUM | TABLET, SYRUP, INJECTION, CAPSULE, CREAM, DROPS, OTHER |
| hsn_code | VARCHAR(20) | For GST compliance |
| manufacturer | VARCHAR(150) | nullable |
| is_active | BOOLEAN | DEFAULT true |
| created_at | DATETIME | NOT NULL |

#### `drug_stocks`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| drug_id | BIGINT | FK → drugs.id |
| center_id | BIGINT | FK → centers.id |
| batch_number | VARCHAR(50) | NOT NULL |
| expiry_date | DATE | NOT NULL — critical for alerts |
| quantity | INT | NOT NULL — decremented on dispense |
| purchase_price | DECIMAL(8,2) | Cost price |
| selling_price | DECIMAL(8,2) | MRP / sale price |
| supplier | VARCHAR(150) | nullable |
| received_date | DATE | NOT NULL |
| created_at | DATETIME | NOT NULL |

#### `drug_dispenses`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| patient_id | BIGINT | FK → patients.id |
| opd_visit_id | BIGINT | FK → opd_visits.id — nullable |
| drug_stock_id | BIGINT | FK → drug_stocks.id — FIFO batch |
| drug_id | BIGINT | FK → drugs.id |
| quantity | INT | NOT NULL |
| unit_price | DECIMAL(8,2) | Selling price at time of dispense |
| total_price | DECIMAL(8,2) | quantity × unit_price |
| dispensed_by | BIGINT | FK → users.id (Pharmacist) |
| dispensed_at | DATETIME | NOT NULL |

#### `stock_alerts`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| drug_id | BIGINT | FK → drugs.id |
| center_id | BIGINT | FK → centers.id |
| alert_type | ENUM | LOW_STOCK (<10 qty), EXPIRY_SOON (<30 days), OUT_OF_STOCK |
| current_quantity | INT | Qty at time of alert |
| expiry_date | DATE | For EXPIRY_SOON alerts |
| triggered_at | DATETIME | NOT NULL |
| is_resolved | BOOLEAN | DEFAULT false |
| resolved_at | DATETIME | nullable |

---

### Billing Tables

#### `invoices`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| invoice_number | VARCHAR(30) | UNIQUE — INV-2025-000001 |
| patient_id | BIGINT | FK → patients.id |
| center_id | BIGINT | FK → centers.id |
| type | ENUM | OPD, IPD, PHARMACY, COMBINED |
| subtotal | DECIMAL(10,2) | Before tax & discount |
| discount | DECIMAL(10,2) | DEFAULT 0 |
| tax_amount | DECIMAL(10,2) | GST 18% on pharmacy items |
| total_amount | DECIMAL(10,2) | Final payable amount |
| payment_status | ENUM | PENDING, PAID, PARTIAL, REFUNDED |
| payment_mode | ENUM | CASH, UPI, CARD, INSURANCE |
| insurance_provider | VARCHAR(100) | nullable |
| insurance_claim_id | VARCHAR(100) | nullable |
| created_by | BIGINT | FK → users.id |
| created_at | DATETIME | NOT NULL |
| paid_at | DATETIME | nullable — set when payment recorded |

#### `invoice_items`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| invoice_id | BIGINT | FK → invoices.id ON DELETE CASCADE |
| description | VARCHAR(255) | NOT NULL |
| item_type | ENUM | CONSULTATION, MEDICINE, PROCEDURE, BED_CHARGE, LAB_TEST, OTHER |
| quantity | INT | DEFAULT 1 |
| unit_price | DECIMAL(8,2) | NOT NULL |
| total_price | DECIMAL(8,2) | quantity × unit_price |

#### `commissions`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| invoice_id | BIGINT | FK → invoices.id UNIQUE — one per invoice |
| agent_user_id | BIGINT | FK → users.id |
| rate | DECIMAL(5,2) | DEFAULT 10.00 (10%) |
| amount | DECIMAL(10,2) | rate% of invoice total |
| status | ENUM | PENDING, PAID, CANCELLED |
| created_at | DATETIME | NOT NULL |
| paid_at | DATETIME | nullable |

---

### HR & Payroll Tables

#### `staff_profiles`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| user_id | BIGINT | FK → users.id UNIQUE |
| department | VARCHAR(100) | Cardiology, Pharmacy, Admin, HR… |
| designation | VARCHAR(100) | Senior Doctor, Pharmacist, Manager… |
| date_of_joining | DATE | NOT NULL |
| base_salary | DECIMAL(10,2) | Monthly gross salary |
| bank_account | VARCHAR(20) | nullable — for payslip |
| ifsc_code | VARCHAR(15) | nullable |
| pan_number | VARCHAR(15) | nullable |
| aadhar_number | VARCHAR(12) | nullable |
| emergency_contact | VARCHAR(15) | nullable |
| emergency_name | VARCHAR(100) | nullable |
| created_at | DATETIME | NOT NULL |

#### `attendance`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| user_id | BIGINT | FK → users.id |
| center_id | BIGINT | FK → centers.id |
| date | DATE | NOT NULL |
| check_in | TIME | nullable |
| check_out | TIME | nullable |
| status | ENUM | PRESENT, ABSENT, HALF_DAY, ON_LEAVE, HOLIDAY |
| marked_by | BIGINT | FK → users.id (HR who marked) |
| remarks | VARCHAR(255) | nullable |
| — | UNIQUE | (user_id, date) — One record per staff per day |

#### `leave_requests`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| user_id | BIGINT | FK → users.id |
| leave_type | ENUM | SICK, CASUAL, EARNED |
| from_date | DATE | NOT NULL |
| to_date | DATE | NOT NULL |
| total_days | INT | Computed: to_date - from_date + 1 |
| reason | TEXT | nullable |
| status | ENUM | PENDING, APPROVED, REJECTED |
| approved_by | BIGINT | FK → users.id — nullable |
| applied_at | DATETIME | NOT NULL |
| actioned_at | DATETIME | nullable — when approved/rejected |

#### `leave_balances`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| user_id | BIGINT | FK → users.id |
| year | INT | NOT NULL — calendar year |
| sick_total | INT | DEFAULT 12 |
| casual_total | INT | DEFAULT 12 |
| earned_total | INT | DEFAULT 15 |
| sick_used | INT | DEFAULT 0 |
| casual_used | INT | DEFAULT 0 |
| earned_used | INT | DEFAULT 0 |
| — | UNIQUE | (user_id, year) — One balance record per user per year |

#### `payrolls`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| user_id | BIGINT | FK → users.id |
| month | INT | 1–12 |
| year | INT | NOT NULL |
| base_salary | DECIMAL(10,2) | Snapshot of salary at time of generation |
| working_days | INT | Total working days that month |
| present_days | INT | From attendance records |
| absent_days | INT | working_days - present_days - leave_days |
| leave_days | INT | Approved leaves taken |
| gross_salary | DECIMAL(10,2) | base/working_days × present_days |
| deductions | DECIMAL(10,2) | PF, TDS, absent deductions |
| bonus | DECIMAL(10,2) | DEFAULT 0 |
| net_salary | DECIMAL(10,2) | gross - deductions + bonus |
| status | ENUM | DRAFT, PROCESSED, PAID |
| generated_by | BIGINT | FK → users.id |
| generated_at | DATETIME | NOT NULL |
| paid_at | DATETIME | nullable |
| — | UNIQUE | (user_id, month, year) — One payroll per staff per month |

---

### Notifications Table

#### `notifications`
| Column | Type | Constraints / Notes |
|--------|------|---------------------|
| id | BIGINT | PK AUTO_INCREMENT |
| user_id | BIGINT | FK → users.id — recipient |
| type | ENUM | WALLET_UPDATE, LOW_STOCK, LEAVE_REQUEST, COMMISSION_EARNED, APPOINTMENT_REMINDER, INVOICE_PAID |
| title | VARCHAR(200) | NOT NULL — short notification heading |
| message | TEXT | Full notification body |
| reference_id | BIGINT | nullable — invoice_id / alert_id / leave_id |
| reference_type | VARCHAR(50) | nullable — INVOICE, STOCK_ALERT, LEAVE |
| is_read | BOOLEAN | DEFAULT false |
| created_at | DATETIME | NOT NULL |

---

## CHAPTER 06 — Module Breakdown — All 8 Modules

### 6.1 Auth & User Management
- JWT login with access token (15min) + refresh token (7 days)
- 11 roles with rank hierarchy — lower number = higher authority
- Granular permission system — per-user override at creation time
- Rank guard — cannot create user of equal or higher rank
- Permission guard — cannot grant permissions you don't have
- Bulk user import from Excel/CSV with preview + error report
- Soft delete (is_active=false) — data preserved
- Scope-based data filtering — auto-applied on every query

### 6.2 Patient Management (OPD/IPD)
- Auto-generated UHID — format HOSP-YYYY-XXXXXX, globally unique
- Patient profile — demographics, allergies, emergency contact, history
- OPD visit creation — auto token number (resets daily per center)
- OPD queue board — real-time token status (WAITING/IN_PROGRESS/COMPLETED)
- IPD admission — bed assignment, ward selection, daily charges
- IPD discharge — auto-calculates total days, triggers billing
- Bed management — visual grid of occupied/available beds per ward
- Full visit history per patient — OPD + IPD + prescriptions

### 6.3 Doctor & Appointment Scheduling
- Doctor profile — specialization, qualification, experience, fee
- Weekly schedule per doctor — day, start time, end time, slot duration
- Slot generation — auto-compute available slots excluding booked ones
- Visual slot grid on frontend — green=available, gray=booked
- Appointment types — OPD, Follow-up, Emergency
- Status tracking — BOOKED → CONFIRMED → COMPLETED / NO_SHOW
- Double-booking prevention via unique constraint on doctor+date+slot
- Doctor availability toggle for leaves/holidays

### 6.4 Pharmacy & Inventory
- Drug master — generic name, category, unit, HSN code, manufacturer
- Stock management — batch-wise with expiry dates per center
- FIFO dispense — earliest batch used first automatically
- Drug dispense — linked to patient + OPD visit, updates stock
- Auto stock alerts — LOW_STOCK (<10), EXPIRY_SOON (<30 days), OUT_OF_STOCK
- Alert notifications pushed to center staff + upline manager
- Stock report — current quantity, value, expiring soon per center
- Download template + bulk stock upload from Excel

### 6.5 Billing & Finance
- Auto invoice number — INV-YYYY-XXXXXX format
- Combined billing — consultation + pharmacy + procedures in one invoice
- GST-compliant — 18% tax on pharmacy, 0% on consultation
- Payment modes — Cash, UPI, Card, Insurance
- Insurance claim tracking — provider + claim ID stored
- Discount support — flat or percentage
- 10% commission — auto-calculated on payment, credited to agent wallet
- Wallet rollup — Center → Block → District → State wallet update
- Upline notification on every payment received
- Refund workflow with reason tracking

### 6.6 HR & Payroll
- Staff profile — department, designation, joining date, salary, bank
- Daily attendance — mark PRESENT/ABSENT/HALF_DAY/ON_LEAVE/HOLIDAY
- Bulk attendance marking for entire center in one action
- Leave request workflow — Apply → HR Approve/Reject
- Leave balance tracking — Sick(12), Casual(12), Earned(15) per year
- Payroll generation — auto-computes from attendance, deductions, bonus
- Net salary formula: `(base/working_days × present_days) - deductions + bonus`
- Payslip download (PDF) per employee per month
- Payroll status — DRAFT → PROCESSED → PAID

### 6.7 Wallet & Commission System
- 4-level wallet hierarchy — Center, Block, District, State
- Every invoice payment auto-credits center wallet
- 10% commission deducted from center, credited to agent
- District wallet = sum of its centers' net amounts
- State wallet = sum of its districts' amounts
- Full transaction log — every credit/debit with reference ID
- Balance view scoped to logged-in user's level
- Push notification to upline on every wallet update

### 6.8 Reports & Analytics Dashboard
- Role-scoped dashboard — Super Admin sees all, Center Staff sees own center
- KPI cards — Today's OPD, Active IPD, Revenue, Low Stock Alerts
- Revenue trend — Line chart by day/week/month (Recharts)
- Patient volume — Bar chart by center/district
- Stock level — Pie chart of drug categories
- Attendance summary — monthly heatmap per center
- Commission & wallet movement — timeline view
- Export reports — PDF / Excel download
- Date range filter — from/to with preset shortcuts (Today, Week, Month)

---

## CHAPTER 07 — API Endpoints — Complete Reference

**Base URL:** `http://localhost:8080/api`
All endpoints (except `/auth/**`) require: `Authorization: Bearer <accessToken>`
All responses wrapped in: `{ success, data, message, timestamp }`

### Auth Endpoints

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | /auth/login | Public | Login — returns access + refresh tokens |
| POST | /auth/refresh | Public | New access token from refresh token |
| POST | /auth/logout | Authenticated | Revoke refresh token |

### User Management

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | /users/create | ADMIN+ | Create single user (rank check enforced) |
| POST | /users/bulk-upload | ADMIN+ | Upload Excel — returns preview with errors |
| POST | /users/bulk-confirm | ADMIN+ | Save valid rows from bulk preview |
| GET | /users/template | ADMIN+ | Download Excel template for bulk upload |
| GET | /users | ADMIN+ | Paginated user list (filter by role/center/status) |
| GET | /users/{id} | ADMIN+ | Single user with permissions |
| PUT | /users/{id} | ADMIN+ | Update user (rank check enforced) |
| DELETE | /users/{id} | ADMIN+ | Soft delete (sets is_active=false) |

### Geographic & Centers

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | /centers | SUPER_ADMIN | Create new center |
| GET | /centers | STATE_MANAGER+ | List centers (scoped to user) |
| GET | /centers/{id} | BLOCK_MANAGER+ | Center detail with wallet balance |
| GET | /states | SUPER_ADMIN | All states |
| GET | /districts?stateId= | STATE_MANAGER+ | Districts of a state |
| GET | /blocks?districtId= | DISTRICT_MANAGER+ | Blocks of a district |

### Patient Module

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | /patients/register | RECEPTIONIST+ | Register patient — auto-generates UHID |
| GET | /patients | DOCTOR+ | Search by name/phone/UHID (paginated) |
| GET | /patients/{uhid} | DOCTOR+ | Full profile + visit history |
| PUT | /patients/{id} | RECEPTIONIST+ | Update patient info |
| POST | /opd/visit | RECEPTIONIST+ | Create OPD visit — auto token assigned |
| GET | /opd/queue?centerId=&date= | CENTER_STAFF+ | Today's OPD queue |
| PUT | /opd/visit/{id}/status | DOCTOR+ | Update visit status |
| GET | /opd/visits/{patientId} | DOCTOR+ | OPD history of a patient |
| POST | /ipd/admit | DOCTOR+ | Admit patient — bed marked occupied |
| GET | /ipd/active?centerId= | CENTER_STAFF+ | Currently admitted patients |
| PUT | /ipd/discharge/{id} | DOCTOR+ | Discharge — frees bed, triggers billing |
| GET | /beds/available?centerId=&ward= | CENTER_STAFF+ | Available beds |

### Doctor & Appointments

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | /doctors/profile | ADMIN+ | Create doctor profile |
| GET | /doctors?centerId=&specialization= | PUBLIC* | List doctors (* filtered) |
| GET | /doctors/{id}/schedule | RECEPTIONIST+ | Doctor weekly schedule |
| PUT | /doctors/{id}/schedule | DOCTOR+ | Update own schedule |
| GET | /appointments/slots?doctorId=&date= | RECEPTIONIST+ | Available slots |
| POST | /appointments/book | RECEPTIONIST+ | Book appointment |
| GET | /appointments?doctorId=&date= | DOCTOR+ | Doctor appointment list |
| PUT | /appointments/{id}/status | DOCTOR+ | Confirm/cancel/complete |
| GET | /appointments/patient/{patientId} | DOCTOR+ | Patient appointment history |

### Pharmacy & Inventory

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | /drugs | ADMIN+ | Add drug to master list |
| GET | /drugs?search=&category= | PHARMACIST+ | Search drugs |
| POST | /drugs/stock | PHARMACIST+ | Add stock batch to center |
| GET | /drugs/stock?centerId= | PHARMACIST+ | Current stock with expiry |
| PUT | /drugs/stock/{id} | PHARMACIST+ | Update stock quantity |
| POST | /drugs/dispense | PHARMACIST+ | Dispense to patient — FIFO deduction |
| GET | /drugs/dispense/patient/{id} | PHARMACIST+ | Patient dispense history |
| GET | /drugs/alerts?centerId= | CENTER_STAFF+ | Active stock alerts |

### Billing & Wallet

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | /billing/invoice | RECEPTIONIST+ | Generate invoice with line items |
| POST | /billing/invoice/{id}/pay | RECEPTIONIST+ | Record payment — triggers wallet+commission |
| GET | /billing/invoices?centerId=&status= | RECEPTIONIST+ | Invoice list |
| GET | /billing/invoices/{id} | RECEPTIONIST+ | Invoice detail with items |
| POST | /billing/invoice/{id}/refund | ADMIN+ | Process refund |
| GET | /wallet/balance?entityType=&entityId= | BLOCK_MANAGER+ | Wallet balance |
| GET | /wallet/transactions?walletId=&from=&to= | BLOCK_MANAGER+ | Transaction history |

### HR & Payroll

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | /hr/staff | HR_MANAGER+ | Create staff profile |
| GET | /hr/staff?centerId=&dept= | HR_MANAGER+ | Staff list |
| PUT | /hr/staff/{id} | HR_MANAGER+ | Update staff profile |
| POST | /hr/attendance/mark | HR_MANAGER+ | Mark attendance (bulk supported) |
| GET | /hr/attendance?userId=&month=&year= | HR_MANAGER+ | Attendance records |
| PUT | /hr/attendance/{id} | HR_MANAGER+ | Correct attendance entry |
| POST | /hr/leave/apply | Authenticated | Apply for leave |
| PUT | /hr/leave/{id}/approve | HR_MANAGER+ | Approve/reject leave |
| GET | /hr/leave/pending?centerId= | HR_MANAGER+ | Pending leave requests |
| GET | /hr/leave/balance/{userId} | Authenticated | Leave balance |
| POST | /hr/payroll/generate | HR_MANAGER+ | Generate payroll (month+year+center) |
| GET | /hr/payroll/{userId}?month=&year= | HR_MANAGER+ | Payslip detail |
| PUT | /hr/payroll/{id}/pay | ADMIN+ | Mark payroll as PAID |

### Reports & Dashboard

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | /reports/dashboard?centerId=&from=&to= | CENTER_STAFF+ | Dashboard KPIs |
| GET | /reports/revenue?centerId=&groupBy= | BLOCK_MANAGER+ | Revenue trend data |
| GET | /reports/patients?centerId=&from=&to= | BLOCK_MANAGER+ | Patient statistics |
| GET | /reports/inventory?centerId= | PHARMACIST+ | Stock summary report |
| GET | /reports/hr?centerId=&month=&year= | HR_MANAGER+ | Attendance/payroll summary |
| GET | /reports/wallet-movement?from=&to= | DISTRICT_MANAGER+ | Wallet flow report |

---

## CHAPTER 08 — UI/UX Design System

### Color Palette

| Name | Hex Code | Usage |
|------|----------|-------|
| Background | #F8FAFC | Page/app background |
| Primary Blue | #2563EB | Buttons, links, active states |
| Dark Text | #0F172A | Primary body text |
| Slate Gray | #64748B | Secondary text, labels |
| Success Green | #16A34A | Pharmacy module, active badges |
| Warning Amber | #D97706 | Alerts, pending states |
| Danger Red | #DC2626 | Errors, critical alerts |
| Purple | #7C3AED | OPD/IPD module accent |
| Cyan | #0891B2 | Doctor module accent |
| Pink | #DB2777 | HR module accent |
| Emerald | #059669 | Wallet module accent |
| Indigo | #4F46E5 | Reports module accent |

### Typography

| Element | Font | Size | Weight |
|---------|------|------|--------|
| Page Heading | Inter | 24px | 600 SemiBold |
| Section Heading | Inter | 18px | 500 Medium |
| Card Title | Inter | 16px | 600 SemiBold |
| Body Text | Inter | 14px | 400 Regular |
| Form Label | Inter | 12px | 500 Medium + UPPERCASE |
| Small/Meta | Inter | 12px | 400 Regular |
| Code/Mono | JetBrains Mono | 13px | 400 Regular |

### Form Design Standards
- 2-column grid layout for related fields (name + DOB, phone + email)
- Sections with clear headings: PERSONAL INFO, CONTACT DETAILS, PERMISSIONS
- 44px input height, rounded-lg border, focus ring in primary blue
- Inline validation — error shown under field immediately, no page jump
- Required fields marked with red asterisk (*) on the label
- Action buttons right-aligned — Cancel (ghost) + Primary action (filled)
- Auto-fill behavior where possible (age from DOB, token from center+date)
- Permission matrix — checkboxes grouped by module with toggle-all per module
- Bulk upload tab on user creation form alongside manual tab

### Table/List Design Standards
- Page header: title + total count + primary CTA button (+ Add New)
- Search bar + Filters dropdown always visible above table
- Zebra striping — white and #F1F5F9 alternate rows
- Status shown as colored pill badge (not plain text)
- Action buttons appear on row hover — View, Edit, Delete icons
- Sortable column headers with visual sort indicator
- Pagination: prev/next + page numbers + items-per-page selector (10/25/50)
- Empty state illustration when no data — not just blank space
- Loading skeleton instead of spinner for better perceived performance

---

## CHAPTER 09 — Phase-by-Phase Build Plan

> **Build Philosophy:** We build backend first, then frontend for each phase. Each phase is fully functional and testable in Postman before moving to the next. No phase is skipped.
>
> Start every session by saying: **'Start Phase N'** — I will generate all code completely.

### Phase 1 — Project Setup + Auth + JWT

**Backend Tasks**
- Maven project setup — pom.xml with all dependencies
- application.yml — MySQL, JWT config, server port
- All JPA entities for users, permissions, user_permissions, refresh_tokens
- Geographic entities — states, districts, blocks, centers
- JwtService — generate, validate, extract claims
- JwtAuthFilter — intercept requests, validate Bearer token
- SecurityConfig — permit /auth/**, secure all else
- AuthController — POST /login, /refresh, /logout
- GlobalExceptionHandler — 401, 403, 404, 400
- ApiResponse wrapper for all responses
- ScopeFilter utility — inject scope into every service

**Frontend Tasks**
- Vite + React + Tailwind + shadcn/ui setup
- Inter font, global CSS variables, light theme
- Zustand authStore — token, user, permissions state
- Axios instance with request interceptor (attach JWT)
- Axios response interceptor (401 → auto refresh or logout)
- React Router setup with AppRouter
- ProtectedRoute + RoleRoute components
- Login page — email/password form, error handling, token storage
- DashboardLayout — sidebar + topbar shell

### Phase 2 — User Management + Geographic Setup

**Backend Tasks**
- UserService — create with rank guard + permission guard
- UserController — CRUD + bulk upload endpoints
- Apache POI — Excel parsing for bulk upload
- Bulk validation — duplicate email, rank check, role valid
- Preview response — VALID / ERROR per row with reason
- Bulk confirm — save only valid rows
- Template download endpoint — pre-formatted Excel
- GeographicController — states, districts, blocks, centers CRUD
- RankGuard utility — enforced on every user creation

**Frontend Tasks**
- User list page — table with search, filters, pagination
- Create user form — 2-col grid, role dropdown, permission matrix
- Permission matrix UI — module groups, toggle switches
- Bulk upload tab — react-dropzone, preview table, error badges
- Center management page — CRUD for centers
- Geographic selectors — cascading State → District → Block dropdowns

### Phase 3 — Patient Management — OPD & IPD

**Backend Tasks**
- Patient entity + UHID generation service (HOSP-YYYY-XXXXXX)
- OpdVisit — auto token number (resets daily per center)
- IpdAdmission — bed assignment, status management
- Bed entity — occupied/available tracking
- PatientController — register, search, profile
- OpdController — create visit, queue, status update
- IpdController — admit, discharge, active list, beds
- Discharge trigger — calculates total days, auto-creates invoice draft

**Frontend Tasks**
- Patient registration form — UHID shown as auto-generated badge
- Patient list + search (name, phone, UHID)
- Patient profile page — demographics + full visit history tabs
- OPD queue board — token cards with status color coding
- New OPD visit form — patient autocomplete, doctor dropdown
- IPD admission form — bed selector by ward
- Bed grid — visual occupied (red) / available (green) per ward
- Discharge modal — summary + auto-bill preview

### Phase 4 — Doctor Profiles + Appointment Scheduling

**Backend Tasks**
- DoctorProfile entity + service
- DoctorSchedule — weekly slots per doctor
- Slot generation algorithm — start/end/duration → available slots
- Appointment booking with double-booking prevention
- AppointmentController — book, slots, status, history
- Availability check on booking — validate against schedule

**Frontend Tasks**
- Doctor list + profile page
- Schedule editor — day-wise time range + duration
- Appointment booking page — date picker + visual slot grid
- Slot grid — green/gray visual, click to select
- Doctor's daily appointment list (admin view)
- Patient appointment history tab on profile page

### Phase 5 — Pharmacy + Inventory Management

**Backend Tasks**
- Drug master entity + service + controller
- DrugStock — batch-wise per center
- FIFO dispense logic — earliest expiry batch first
- Stock deduction on dispense with validation
- Auto alert trigger — LOW_STOCK, EXPIRY_SOON, OUT_OF_STOCK
- StockAlert entity + resolution workflow
- Notification service — push alert to center staff + upline

**Frontend Tasks**
- Drug master list + add drug form
- Stock management table — batch, expiry, quantity
- Add stock form — supplier, batch, expiry, pricing
- Dispense form — patient search + drug search + quantity
- Stock alerts panel — badge counts on sidebar
- Alert cards — type badge, drug name, quantity, resolve button

### Phase 6 — Billing + Payments + Wallet + Commission

**Backend Tasks**
- Invoice entity + auto invoice number generator
- InvoiceItem — line items with types
- GST calculation — 18% pharmacy, 0% consultation
- Payment recording — status transition PENDING → PAID
- Commission calculation — 10% auto on payment
- Wallet service — credit center, debit commission, rollup
- Wallet transaction logging — every credit/debit
- Upline notification on payment received
- Refund workflow with status tracking

**Frontend Tasks**
- Invoice generator — dynamic line-item table (add/remove rows)
- Live total calculation — subtotal, tax, discount, total as you type
- Payment modal — mode selector (Cash/UPI/Card/Insurance icons)
- Invoice list — filter by status, date, center
- Invoice detail view — print-friendly layout
- Wallet balance card per level
- Transaction history table — credit/debit with reference

### Phase 7 — HR + Attendance + Leave + Payroll

**Backend Tasks**
- StaffProfile entity + service
- Attendance — mark, bulk mark, correct
- LeaveRequest — apply, approve, reject workflow
- LeaveBalance — per year, per type, auto-deduct on approval
- Payroll generation — formula: `(base/working_days × present) - deductions + bonus`
- Payslip PDF generation
- Payroll status — DRAFT → PROCESSED → PAID

**Frontend Tasks**
- Staff list + create staff form (with HR-specific fields)
- Attendance marking page — staff table with status dropdowns
- Monthly attendance calendar view per staff
- Leave request form — type, date range, reason
- Leave approval table — pending requests with approve/reject
- Leave balance widget — visual bars per leave type
- Payroll generation page — month/year selector + generate button
- Payslip page — clean layout with download PDF button

### Phase 8 — Reports + Analytics + Full Dashboard

**Backend Tasks**
- DashboardController — aggregated KPIs in single call
- Revenue report — group by DAY / WEEK / MONTH
- Patient statistics report
- Inventory report — stock value, expiring soon
- HR summary — attendance rates, leave trends
- Wallet movement report — all levels
- All reports scoped to logged-in user's level

**Frontend Tasks**
- Dashboard page — KPI cards + 4 chart widgets
- Revenue line chart — Recharts with date range selector
- Patient bar chart — by center/district comparison
- Stock pie chart — by drug category
- Attendance heatmap — monthly grid per staff
- Role-based dashboard — different widgets per role
- Reports page — all reports with export (PDF/Excel)
- Final UI polish — consistent spacing, animations, empty states
- Mobile responsiveness — collapsed sidebar, responsive tables

---

## CHAPTER 10 — Security Architecture

### Security Layers

| Layer | Implementation |
|-------|----------------|
| Transport | HTTPS in production (HTTP locally for dev) |
| Authentication | JWT access token (15min) + refresh token (7 days, DB-stored) |
| Authorization — Role | `@PreAuthorize('hasRole(...)')` on every controller method |
| Authorization — Permission | permission_id checked in service layer for granular actions |
| Rank Guard | User creation blocked if target rank >= creator rank |
| Scope Filter | Every DB query auto-filtered by user's scope (center/district/state) |
| Password | BCrypt with strength 12 — never stored plain text |
| Token Revocation | Refresh tokens stored in DB — logout sets is_revoked=true |
| Input Validation | Jakarta Validation (@NotNull, @Email, @Size) on all DTOs |
| CORS | Configured to allow only React frontend origin (localhost:5173) |
| SQL Injection | JPA parameterized queries — no raw SQL concatenation |
| Sensitive Data | Bank account, Aadhar, PAN — never returned in API responses, write-only |

### Environment Variables (Never Hardcode)

```properties
# .env (add to .gitignore immediately)
DB_USERNAME=root
DB_PASSWORD=your_password_here
DB_NAME=hospital_erp_db
JWT_SECRET=your-256-bit-secret-key-here
JWT_EXPIRY=900000
JWT_REFRESH_EXPIRY=604800000
SERVER_PORT=8080
```

> ⚠️ Add `.env` and `application-local.yml` to `.gitignore` BEFORE first git push.

---

## CHAPTER 11 — Folder Structure

### Backend — Spring Boot

```
hospital-erp-backend/
├── pom.xml
└── src/main/
    ├── java/com/hospital/erp/
    │   ├── HospitalErpApplication.java
    │   ├── config/
    │   │   ├── SecurityConfig.java
    │   │   ├── CorsConfig.java
    │   │   └── AppConfig.java
    │   ├── auth/
    │   │   ├── AuthController.java
    │   │   ├── AuthService.java
    │   │   ├── JwtService.java
    │   │   ├── JwtAuthFilter.java
    │   │   └── dto/ (AuthRequest, AuthResponse, TokenRefreshRequest)
    │   ├── user/
    │   │   ├── User.java (entity)
    │   │   ├── UserRepository.java
    │   │   ├── UserService.java
    │   │   ├── UserController.java
    │   │   ├── RankGuard.java
    │   │   ├── ScopeFilter.java
    │   │   └── dto/
    │   ├── geographic/
    │   │   ├── entities/ (State, District, Block, Center)
    │   │   ├── repositories/
    │   │   ├── services/
    │   │   └── controllers/
    │   ├── patient/     (Patient, OpdVisit, IpdAdmission, Bed)
    │   ├── doctor/      (DoctorProfile, DoctorSchedule)
    │   ├── appointment/ (Appointment)
    │   ├── pharmacy/    (Drug, DrugStock, DrugDispense, StockAlert)
    │   ├── billing/     (Invoice, InvoiceItem, Commission)
    │   ├── wallet/      (Wallet, WalletTransaction)
    │   ├── hr/          (StaffProfile, Attendance, LeaveRequest, LeaveBalance, Payroll)
    │   ├── notification/ (Notification)
    │   ├── reports/     (ReportController, ReportService)
    │   └── common/
    │       ├── ApiResponse.java
    │       ├── GlobalExceptionHandler.java
    │       ├── CurrentUser.java (annotation)
    │       └── PageResponse.java
    └── resources/
        ├── application.yml
        └── application-local.yml (gitignored)
```

### Frontend — React + Vite

```
hospital-erp-frontend/
├── index.html
├── vite.config.js
├── tailwind.config.js
├── package.json
└── src/
    ├── main.jsx
    ├── App.jsx
    ├── api/
    │   ├── axios.js          ← base instance + interceptors
    │   ├── authApi.js
    │   ├── userApi.js
    │   ├── patientApi.js
    │   ├── opdApi.js
    │   ├── doctorApi.js
    │   ├── pharmacyApi.js
    │   ├── billingApi.js
    │   ├── hrApi.js
    │   ├── walletApi.js
    │   └── reportApi.js
    ├── store/
    │   ├── authStore.js      ← Zustand: user, token, permissions
    │   └── uiStore.js        ← Zustand: sidebar collapsed, toasts
    ├── routes/
    │   ├── AppRouter.jsx
    │   ├── ProtectedRoute.jsx
    │   └── RoleRoute.jsx
    ├── layouts/
    │   ├── DashboardLayout.jsx
    │   ├── Sidebar.jsx
    │   └── Topbar.jsx
    ├── components/
    │   ├── ui/               ← shadcn components
    │   ├── DataTable.jsx
    │   ├── PageHeader.jsx
    │   ├── StatusBadge.jsx
    │   ├── KpiCard.jsx
    │   ├── PermissionMatrix.jsx
    │   ├── BulkUpload.jsx
    │   └── ConfirmModal.jsx
    └── pages/
        ├── Login/
        ├── Dashboard/
        ├── Users/
        ├── Patients/
        ├── OPD/
        ├── IPD/
        ├── Doctors/
        ├── Appointments/
        ├── Pharmacy/
        ├── Billing/
        ├── HR/
        ├── Wallet/
        └── Reports/
```

---

## CHAPTER 12 — Deployment & DevOps

### Local Development Setup

| Step | Command / Action |
|------|-----------------|
| 1. Clone repo | `git clone https://github.com/prince2404/hospital-erp` |
| 2. Start MySQL | `mysql -u root -p` → `CREATE DATABASE hospital_erp_db;` |
| 3. Set env vars | Copy `.env.example` → `.env`, fill values |
| 4. Run backend | `cd backend` → `mvn spring-boot:run` |
| 5. Run frontend | `cd frontend` → `npm install` → `npm run dev` |
| 6. Access app | Frontend: http://localhost:5173 — API: http://localhost:8080 |

### Git Branching Strategy

| Branch | Purpose |
|--------|---------|
| main | Production-ready code only |
| develop | Integration branch — merge all features here first |
| feature/phase-1-auth | One branch per phase during development |
| feature/phase-2-users | Merged to develop when phase is complete |
| hotfix/* | Critical bug fixes on main |

### Future Deployment (Post-Development)

| Component | Platform Option | Free Tier? |
|-----------|----------------|------------|
| Backend (Spring Boot) | Railway / Render / AWS EC2 | Yes (Railway/Render) |
| Database (MySQL) | PlanetScale / AWS RDS / Railway | Yes (PlanetScale) |
| Frontend (React) | Vercel / Netlify | Yes — both |
| Domain | Namecheap / GoDaddy | No — ~₹800/year |

### Important Reminders Before First Push
- Add to `.gitignore`: `.env`, `application-local.yml`, `target/`, `node_modules/`, `*.class`
- Never commit real credentials — use environment variables always
- Set GitHub repo to **PRIVATE** until project is complete
- Tag each phase completion: `git tag v1.0-phase1`, `v1.0-phase2`…

---

*29 Tables | 11 Roles | 8 Modules | 8 Phases | Full Stack*
*Document generated on April 04, 2026 | github.com/prince2404 | Version 1.0*
