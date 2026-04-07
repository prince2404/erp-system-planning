

## HOSPITAL ERP
## MANAGEMENT SYSTEM
## Apana Swastha Kendra
## Full Stack Enterprise Resource Planning Blueprint
Spring Boot 3  |  React 18  |  MySQL 8  |  JWT Security
## 29 Tables8 Modules8 Phases
## Complete Project Planning Document
Prepared for: Prince  |  GitHub: github.com/prince2404
## Date: April 04, 2026  |  Version 1.0

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 2
## CHAPTER —
Table of Contents
01Project Overview & Vision3
02Tech Stack — Complete Breakdown4
03System Architecture5
04User Hierarchy & Role System6
05Database Schema — All 29 Tables8
06Module Breakdown — All 8 Modules13
07API Endpoints — Complete Reference18
08UI/UX Design System22
09Phase-by-Phase Build Plan24
10Security Architecture28
11Folder Structure29
12Deployment & DevOps30

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 3
## CHAPTER 01
## Project Overview & Vision
## What Are We Building?
A multi-center, role-based Hospital ERP Management System named Apana Swastha Kendra. This
is a full-stack enterprise application covering patient management, OPD/IPD workflows, pharmacy, billing,
HR, payroll, wallet/commission systems, and analytics — designed to operate across a geographic
hierarchy of States, Districts, Blocks, and Centers.
## Key Goals
Multi-Center Operations
Support 3 states, ~60 districts, ~300 blocks, unlimited centers from a single
system.
Role-Based Access Control
11 distinct roles with rank-based hierarchy and granular per-user permission
overrides.
## Geographic Scoping
Every API auto-filters data based on logged-in user's state/district/block/center
scope.
Enterprise UI
Light-theme, professional clinical design. Not a student project — a deployable
product.
Bulk OperationsExcel/CSV bulk user import with validation preview and error reporting.
Real-time NotificationsUpline alerts for wallet updates, low stock, commission earned, leave requests.
## Project Scope — 3 States Focus
LevelCount (3-State Scope)Role AssignedHas Center?
Super Admin1–2 usersSUPER_ADMINNo — System-wide
State Manager3 users (1 per state)STATE_MANAGERNo — State scope
District Manager~60 usersDISTRICT_MANAGERNo — District scope
Block Manager~300 usersBLOCK_MANAGERNo — Block scope
Center Staff/Doctor/etcUnlimitedMultiple roles
## Yes — Center
bound

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 4
## CHAPTER 02
## Tech Stack — Complete Breakdown
## Backend
TechnologyVersionPurpose
Java17 (LTS)Primary language
Spring Boot3.2.xApplication framework
Spring Security6.xAuthentication & authorization
Spring Data JPA3.2.xORM layer / repository pattern
Hibernate6.xJPA implementation, SQL generation
jjwt (io.jsonwebtoken)0.12.xJWT token generation & validation
Apache POI5.2.xExcel (.xlsx) parsing for bulk upload
MapStruct1.5.xEntity ↔ DTO mapping
Lombok1.18.xBoilerplate reduction (@Getter, @Builder)
Maven3.9.xBuild tool & dependency management
Validation (Jakarta)3.xRequest DTO validation (@NotNull etc)
## Database
TechnologyVersionPurpose
MySQL8.0Primary relational database
HikariCPBuilt-inConnection pooling (Spring Boot default)
Flyway (optional)9.xDatabase migration management
## Frontend
TechnologyVersionPurpose
React18.xUI framework
Vite5.xBuild tool (fast HMR)
Tailwind CSS3.xUtility-first styling
shadcn/uiLatestPre-built accessible components

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 5
TechnologyVersionPurpose
Zustand4.xGlobal state management (auth, ui)
React Query (TanStack)5.xServer state, caching, refetch
Axios1.xHTTP client with interceptors
React Router6.xClient-side routing & protected routes
Recharts2.xCharts & data visualization
react-hook-form7.xForm state & validation
Zod3.xSchema validation (pairs with RHF)
react-dropzone14.xDrag-and-drop file upload (bulk import)
date-fns3.xDate formatting & manipulation
lucide-reactLatestIcon library
## Dev Tools & Environment
ToolPurpose
IntelliJ IDEABackend IDE
VS CodeFrontend IDE
PostmanAPI testing
Git + GitHubVersion control (github.com/prince2404)
MySQL WorkbenchDatabase GUI
DBeaver (optional)Alternative DB GUI
Docker (optional)Containerize MySQL for consistent dev env

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 6
## CHAPTER 03
## System Architecture
High-Level Architecture
nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
n                   REACT FRONTEND (Vite)                 n
n  Zustand Store n React Query n Axios Interceptor        n
n  Protected Routes n Role-based Sidebar                  n
nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
n HTTP/REST (JSON)
n Bearer: JWT Token
nnnnnnnnnnnnnnnnnnnnnnntnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
n              SPRING BOOT 3 BACKEND (:8080)              n
n  JwtAuthFilter → SecurityConfig → Controllers           n
n  Services (Business Logic + ScopeFilter)                n
n  Repositories (Spring Data JPA)                         n
nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
n JPA / Hibernate
nnnnnnnnnnnnnnnnnnnnnnntnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
n                  MySQL 8.0 DATABASE                     n
n              hospital_erp_db  (29 tables)               n
nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
## Request Lifecycle
StepWhereWhat Happens
1React sends HTTP requestAxios adds Authorization: Bearer header automatically via interceptor
2JwtAuthFilter intercepts
Validates JWT signature, expiry, extracts userId + role + permissions +
scopeId
3SecurityConfig checks@PreAuthorize annotation on controller method validates role
4ScopeFilter appliesService layer injects scope (centerId/districtId/stateId) into every DB query
5Repository executesJPA query returns only data belonging to the logged-in user's scope
6Response wrappedApiResponse wrapper: { success, data, message, timestamp } returned
7React Query cachesFrontend caches response, updates UI, shows data
JWT Token Structure
Header: { "alg": "HS256", "typ": "JWT" }
## Payload: {
## "sub": "45",
"name": "Dr. Sharma",
"role": "DOCTOR",

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 7
## "rank": 8,
"centerId": 3,
"centerName": "City Hospital",
"scopeType": "CENTER",
"scopeId": 3,
"permissions": ["READ_PATIENT","READ_OPD","DISPENSE_MEDICINE"],
## "iat": 1704067200,
## "exp": 1704068100
## }
Access Token Expiry:  15 minutes
Refresh Token Expiry: 7 days

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 8
## CHAPTER 04
## User Hierarchy & Role System
## Geographic Hierarchy
SUPER ADMIN (rank 1) — System-wide
nnn State Manager Punjab (rank 2) — STATE scope
nnn District Manager Ludhiana (rank 3) — DISTRICT scope
nnn Block Manager Block-A (rank 4) — BLOCK scope
nnn Center: City Hospital
n     nnn Doctor        (rank 7)
n     nnn Pharmacist     (rank 8)
n     nnn Receptionist   (rank 8)
n     nnn HR Manager     (rank 7)
n     nnn Center Staff   (rank 9)
nnn Center: Village Clinic
nnn ... same staff roles
## All 11 Roles — Permissions Matrix
RoleRankScopeKey Access
SUPER_ADMIN1SYSTEMFull access — all modules, all centers, all data
ADMIN2SYSTEMFull access except system config
STATE_MANAGER3STATEAll centers in their state, management dashboard
DISTRICT_MANAGER4DISTRICTAll centers in their district, user creation
BLOCK_MANAGER5BLOCKAll centers in their block
HR_MANAGER6CENTERHR, attendance, payroll, leave approvals
DOCTOR7CENTERPatient records, OPD, prescriptions, appointments
PHARMACIST8CENTERDrug stock, dispense, alerts
RECEPTIONIST8CENTERPatient registration, OPD queue, appointments
CENTER_STAFF9CENTERLimited — view only most modules
PATIENT10SELFOwn records, appointments, bills only
## Granular Permission System
Three-Layer Permission Model
Layer 1 — Role: Broad default access (Doctor gets patient read, OPD manage, etc.)

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 9
Layer 2 — Permissions: Granular actions stored in user_permissions table.
Layer 3 — Override: At user creation, creator customizes permissions via checkbox UI. CRITICAL
RULE: Creator can only grant permissions they themselves possess.
## PATIENTREAD, CREATE, EDIT, DELETE
## OPDREAD, MANAGE_QUEUE, UPDATE_STATUS
## IPDREAD, ADMIT, DISCHARGE
## APPOINTMENTSREAD, BOOK, CANCEL
## PHARMACYREAD_STOCK, ADD_STOCK, DISPENSE
## BILLINGREAD, CREATE_INVOICE, PROCESS_PAYMENT, REFUND
## HRREAD_STAFF, MANAGE_ATTENDANCE, APPROVE_LEAVE, RUN_PAYROLL
## WALLETVIEW_BALANCE, VIEW_TRANSACTIONS
## REPORTSVIEW_CENTER, VIEW_DISTRICT, VIEW_STATE
## USER_MGMTCREATE_USER, EDIT_USER, DEACTIVATE_USER

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 10
## CHAPTER 05
## Database Schema — All 29 Tables
## Schema Summary
Database: hospital_erp_db | Engine: InnoDB | Charset: utf8mb4 | Collation: utf8mb4_unicode_ci |
## Total Tables: 29
ModuleTablesTable Names
Geographic3states, districts, blocks
Auth & Users4users, permissions, user_permissions, refresh_tokens
Centers & Wallets3centers, wallets, wallet_transactions
Patient3patients, opd_visits, ipd_admissions
Doctor & Appts4doctor_profiles, doctor_schedules, appointments, beds
Pharmacy4drugs, drug_stocks, drug_dispenses, stock_alerts
Billing3invoices, invoice_items, commissions
HR & Payroll5staff_profiles, attendance, leave_requests, leave_balances, payrolls
## Notifications1notifications
## TOTAL29—
## Geographic Tables
states
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
nameVARCHAR(100)NOT NULL — Punjab, Haryana, Bihar
codeVARCHAR(5)NOT NULL UNIQUE — PB, HR, BR

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 11
districts
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
nameVARCHAR(100)NOT NULL
state_idBIGINT FK→ states.id ON DELETE CASCADE
blocks
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
nameVARCHAR(100)NOT NULL
district_idBIGINT FK→ districts.id ON DELETE CASCADE
## Auth & User Tables

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 12
users
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
nameVARCHAR(100)NOT NULL
emailVARCHAR(100)UNIQUE NOT NULL
passwordVARCHAR(255)BCrypt hashed, NOT NULL
phoneVARCHAR(15)nullable
roleENUM
## SUPER_ADMIN,ADMIN,STATE_MANAGER,DISTR
## ICT_MANAGER, BLOCK_MANAGER,HR_MANAG
## ER,DOCTOR,PHARMACIST,
## RECEPTIONIST,CENTER_STAFF,PATIENT
rankINTNOT NULL — 1=highest authority, 10=lowest
center_idBIGINT FK→ centers.id — NULL for managers
scope_typeENUMSYSTEM,STATE,DISTRICT,BLOCK,CENTER
scope_idBIGINTID of state/district/block/center they manage
created_byBIGINT FK→ users.id — self-referencing
is_activeBOOLEANDEFAULT true — soft delete flag
created_atDATETIMENOT NULL
updated_atDATETIMEON UPDATE CURRENT_TIMESTAMP
permissions
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
moduleVARCHAR(50)PATIENT, BILLING, HR, PHARMACY...
actionVARCHAR(50)READ, CREATE, EDIT, DELETE, DISPENSE...
descriptionVARCHAR(200)Human-readable description

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 13
user_permissions
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
user_idBIGINT FK→ users.id ON DELETE CASCADE
permission_idBIGINT FK→ permissions.id
granted_byBIGINT FK→ users.id — who granted this
granted_atDATETIMENOT NULL
UNIQUE(user_id, permission_id)Prevent duplicate grants
refresh_tokens
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
tokenVARCHAR(500)UNIQUE NOT NULL
user_idBIGINT FK→ users.id ON DELETE CASCADE
expiryDATETIME7 days from creation
is_revokedBOOLEANDEFAULT false
created_atDATETIMENOT NULL
## Center & Wallet Tables

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 14
centers
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
nameVARCHAR(150)NOT NULL
addressTEXTnullable
cityVARCHAR(100)nullable
block_idBIGINT FK→ blocks.id
district_idBIGINT FK→ districts.id
state_idBIGINT FK→ states.id
phoneVARCHAR(15)nullable
emailVARCHAR(100)nullable
is_activeBOOLEANDEFAULT true
created_atDATETIMENOT NULL
wallets
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
entity_typeENUMCENTER, BLOCK, DISTRICT, STATE
entity_idBIGINTID of the entity this wallet belongs to
balanceDECIMAL(12,2)DEFAULT 0.00
last_updatedDATETIMEUpdated on every transaction

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 15
wallet_transactions
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
wallet_idBIGINT FK→ wallets.id
typeENUMCREDIT, DEBIT
amountDECIMAL(10,2)NOT NULL
reference_idVARCHAR(100)Invoice number / payroll ID
descriptionVARCHAR(255)Human-readable reason
created_atDATETIMENOT NULL

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 16
## Patient Tables
patients
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
uhidVARCHAR(20)UNIQUE — format: HOSP-2025-000001
nameVARCHAR(100)NOT NULL
ageINTnullable — or computed from dob
date_of_birthDATEnullable
genderENUMMALE, FEMALE, OTHER
blood_groupVARCHAR(5)A+, B-, O+, AB+...
phoneVARCHAR(15)NOT NULL
emailVARCHAR(100)nullable
addressTEXTnullable
emergency_contactVARCHAR(15)nullable
emergency_nameVARCHAR(100)nullable
allergiesTEXTComma-separated or JSON tags
center_idBIGINT FK→ centers.id
registered_byBIGINT FK→ users.id
created_atDATETIMENOT NULL
updated_atDATETIMEON UPDATE CURRENT_TIMESTAMP

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 17
opd_visits
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
patient_idBIGINT FK→ patients.id
doctor_idBIGINT FK→ users.id
center_idBIGINT FK→ centers.id
token_numberINTAuto-increment per center per day, resets daily
visit_dateDATENOT NULL
symptomsTEXTnullable
diagnosisTEXTnullable — filled by doctor
prescription_notesTEXTnullable
statusENUM
## WAITING, IN_PROGRESS, COMPLETED,
## CANCELLED
feeDECIMAL(8,2)Consultation fee
created_atDATETIMENOT NULL

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 18
ipd_admissions
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
patient_idBIGINT FK→ patients.id
doctor_idBIGINT FK→ users.id
center_idBIGINT FK→ centers.id
bed_idBIGINT FK→ beds.id — marks bed as occupied
admission_dateDATETIMENOT NULL
discharge_dateDATETIMEnullable — filled on discharge
wardVARCHAR(50)General, ICU, Maternity, Pediatric...
diagnosisTEXTnullable
treatment_notesTEXTnullable
daily_chargeDECIMAL(8,2)Per-day bed/ward charge
statusENUMADMITTED, DISCHARGED, TRANSFERRED
discharged_byBIGINT FK→ users.id — nullable
created_atDATETIMENOT NULL
## Doctor & Appointment Tables
beds
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
center_idBIGINT FK→ centers.id
wardVARCHAR(50)General, ICU, Maternity...
bed_numberVARCHAR(20)NOT NULL — e.g. G-101, ICU-03
is_occupiedBOOLEANDEFAULT false
UNIQUE(center_id, bed_number)No duplicate beds per center

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 19
doctor_profiles
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
user_idBIGINT FK→ users.id UNIQUE — 1:1 with user
specializationVARCHAR(100)Cardiology, Orthopedics, General...
qualificationVARCHAR(200)MBBS, MD, MS...
experience_yearsINTnullable
consultation_feeDECIMAL(8,2)Default fee
center_idBIGINT FK→ centers.id
is_availableBOOLEANDEFAULT true
created_atDATETIMENOT NULL
doctor_schedules
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
doctor_idBIGINT FK→ doctor_profiles.id
day_of_weekENUMMON, TUE, WED, THU, FRI, SAT, SUN
start_timeTIMENOT NULL — e.g. 09:00:00
end_timeTIMENOT NULL — e.g. 13:00:00
slot_duration_minsINTDEFAULT 15
max_patientsINTMax appointments per session
is_activeBOOLEANDEFAULT true
## UNIQUE
## (doctor_id,
day_of_week)
One schedule per day per doctor

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 20
appointments
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
patient_idBIGINT FK→ patients.id
doctor_idBIGINT FK→ doctor_profiles.id
center_idBIGINT FK→ centers.id
appointment_dateDATENOT NULL
slot_timeTIMENOT NULL
typeENUMOPD, FOLLOW_UP, EMERGENCY
statusENUM
## BOOKED, CONFIRMED, CANCELLED,
## COMPLETED, NO_SHOW
booking_feeDECIMAL(8,2)nullable
notesTEXTnullable
booked_byBIGINT FK→ users.id
created_atDATETIMENOT NULL
## UNIQUE
## (doctor_id,
appointment_date,
slot_time)
Prevent double booking

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 21
## Pharmacy Tables
drugs
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
nameVARCHAR(150)NOT NULL
generic_nameVARCHAR(150)nullable
categoryVARCHAR(100)Antibiotic, Painkiller, Antihistamine...
unitENUM
## TABLET, SYRUP, INJECTION, CAPSULE, CREAM,
## DROPS, OTHER
hsn_codeVARCHAR(20)For GST compliance
manufacturerVARCHAR(150)nullable
is_activeBOOLEANDEFAULT true
created_atDATETIMENOT NULL
drug_stocks
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
drug_idBIGINT FK→ drugs.id
center_idBIGINT FK→ centers.id
batch_numberVARCHAR(50)NOT NULL
expiry_dateDATENOT NULL — critical for alerts
quantityINTNOT NULL — decremented on dispense
purchase_priceDECIMAL(8,2)Cost price
selling_priceDECIMAL(8,2)MRP / sale price
supplierVARCHAR(150)nullable
received_dateDATENOT NULL
created_atDATETIMENOT NULL

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 22
drug_dispenses
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
patient_idBIGINT FK→ patients.id
opd_visit_idBIGINT FK→ opd_visits.id — nullable
drug_stock_idBIGINT FK→ drug_stocks.id — FIFO batch
drug_idBIGINT FK→ drugs.id
quantityINTNOT NULL
unit_priceDECIMAL(8,2)Selling price at time of dispense
total_priceDECIMAL(8,2)quantity × unit_price
dispensed_byBIGINT FK→ users.id (Pharmacist)
dispensed_atDATETIMENOT NULL
stock_alerts
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
drug_idBIGINT FK→ drugs.id
center_idBIGINT FK→ centers.id
alert_typeENUM
LOW_STOCK (<10 qty), EXPIRY_SOON (<30
days), OUT_OF_STOCK
current_quantityINTQty at time of alert
expiry_dateDATEFor EXPIRY_SOON alerts
triggered_atDATETIMENOT NULL
is_resolvedBOOLEANDEFAULT false
resolved_atDATETIMEnullable
## Billing Tables

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 23
invoices
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
invoice_numberVARCHAR(30)UNIQUE — INV-2025-000001
patient_idBIGINT FK→ patients.id
center_idBIGINT FK→ centers.id
typeENUMOPD, IPD, PHARMACY, COMBINED
subtotalDECIMAL(10,2)Before tax & discount
discountDECIMAL(10,2)DEFAULT 0
tax_amountDECIMAL(10,2)GST 18% on pharmacy items
total_amountDECIMAL(10,2)Final payable amount
payment_statusENUMPENDING, PAID, PARTIAL, REFUNDED
payment_modeENUMCASH, UPI, CARD, INSURANCE
insurance_providerVARCHAR(100)nullable
insurance_claim_idVARCHAR(100)nullable
created_byBIGINT FK→ users.id
created_atDATETIMENOT NULL
paid_atDATETIMEnullable — set when payment recorded
invoice_items
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
invoice_idBIGINT FK→ invoices.id ON DELETE CASCADE
descriptionVARCHAR(255)NOT NULL
item_typeENUM
## CONSULTATION, MEDICINE, PROCEDURE,
## BED_CHARGE, LAB_TEST, OTHER
quantityINTDEFAULT 1
unit_priceDECIMAL(8,2)NOT NULL
total_priceDECIMAL(8,2)quantity × unit_price

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 24
commissions
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
invoice_idBIGINT FK→ invoices.id UNIQUE — one per invoice
agent_user_idBIGINT FK→ users.id
rateDECIMAL(5,2)DEFAULT 10.00 (10%)
amountDECIMAL(10,2)rate% of invoice total
statusENUMPENDING, PAID, CANCELLED
created_atDATETIMENOT NULL
paid_atDATETIMEnullable
HR & Payroll Tables
staff_profiles
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
user_idBIGINT FK→ users.id UNIQUE
departmentVARCHAR(100)Cardiology, Pharmacy, Admin, HR...
designationVARCHAR(100)Senior Doctor, Pharmacist, Manager...
date_of_joiningDATENOT NULL
base_salaryDECIMAL(10,2)Monthly gross salary
bank_accountVARCHAR(20)nullable — for payslip
ifsc_codeVARCHAR(15)nullable
pan_numberVARCHAR(15)nullable
aadhar_numberVARCHAR(12)nullable
emergency_contactVARCHAR(15)nullable
emergency_nameVARCHAR(100)nullable
created_atDATETIMENOT NULL

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 25
attendance
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
user_idBIGINT FK→ users.id
center_idBIGINT FK→ centers.id
dateDATENOT NULL
check_inTIMEnullable
check_outTIMEnullable
statusENUM
## PRESENT, ABSENT, HALF_DAY, ON_LEAVE,
## HOLIDAY
marked_byBIGINT FK→ users.id (HR who marked)
remarksVARCHAR(255)nullable
UNIQUE(user_id, date)One record per staff per day
leave_requests
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
user_idBIGINT FK→ users.id
leave_typeENUMSICK, CASUAL, EARNED
from_dateDATENOT NULL
to_dateDATENOT NULL
total_daysINTComputed: to_date - from_date + 1
reasonTEXTnullable
statusENUMPENDING, APPROVED, REJECTED
approved_byBIGINT FK→ users.id — nullable
applied_atDATETIMENOT NULL
actioned_atDATETIMEnullable — when approved/rejected

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 26
leave_balances
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
user_idBIGINT FK→ users.id
yearINTNOT NULL — calendar year
sick_totalINTDEFAULT 12
casual_totalINTDEFAULT 12
earned_totalINTDEFAULT 15
sick_usedINTDEFAULT 0
casual_usedINTDEFAULT 0
earned_usedINTDEFAULT 0
UNIQUE(user_id, year)One balance record per user per year

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 27
payrolls
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
user_idBIGINT FK→ users.id
monthINT1–12
yearINTNOT NULL
base_salaryDECIMAL(10,2)Snapshot of salary at time of generation
working_daysINTTotal working days that month
present_daysINTFrom attendance records
absent_daysINTworking_days - present_days - leave_days
leave_daysINTApproved leaves taken
gross_salaryDECIMAL(10,2)base/working_days × present_days
deductionsDECIMAL(10,2)PF, TDS, absent deductions
bonusDECIMAL(10,2)DEFAULT 0
net_salaryDECIMAL(10,2)gross - deductions + bonus
statusENUMDRAFT, PROCESSED, PAID
generated_byBIGINT FK→ users.id
generated_atDATETIMENOT NULL
paid_atDATETIMEnullable
UNIQUE(user_id, month, year)One payroll per staff per month
## Notifications Table

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 28
notifications
ColumnTypeConstraints / Notes
idBIGINT PKAUTO_INCREMENT
user_idBIGINT FK→ users.id — recipient
typeENUM
## WALLET_UPDATE, LOW_STOCK,
## LEAVE_REQUEST, COMMISSION_EARNED,
## APPOINTMENT_REMINDER, INVOICE_PAID
titleVARCHAR(200)NOT NULL — short notification heading
messageTEXTFull notification body
reference_idBIGINTnullable — invoice_id / alert_id / leave_id
reference_typeVARCHAR(50)nullable — INVOICE, STOCK_ALERT, LEAVE
is_readBOOLEANDEFAULT false
created_atDATETIMENOT NULL

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 29
## CHAPTER 06
## Module Breakdown — All 8 Modules
## 6.1 Auth & User Management
-  JWT login with access token (15min) + refresh token (7 days)
-  11 roles with rank hierarchy — lower number = higher authority
-  Granular permission system — per-user override at creation time
-  Rank guard — cannot create user of equal or higher rank
-  Permission guard — cannot grant permissions you don't have
-  Bulk user import from Excel/CSV with preview + error report
-  Soft delete (is_active=false) — data preserved
-  Scope-based data filtering — auto-applied on every query
6.2 Patient Management (OPD/IPD)
-  Auto-generated UHID — format HOSP-YYYY-XXXXXX, globally unique
-  Patient profile — demographics, allergies, emergency contact, history
-  OPD visit creation — auto token number (resets daily per center)
-  OPD queue board — real-time token status (WAITING/IN_PROGRESS/COMPLETED)
-  IPD admission — bed assignment, ward selection, daily charges
-  IPD discharge — auto-calculates total days, triggers billing
-  Bed management — visual grid of occupied/available beds per ward
-  Full visit history per patient — OPD + IPD + prescriptions
## 6.3 Doctor & Appointment Scheduling
-  Doctor profile — specialization, qualification, experience, fee
-  Weekly schedule per doctor — day, start time, end time, slot duration
-  Slot generation — auto-compute available slots excluding booked ones
-  Visual slot grid on frontend — green=available, gray=booked
-  Appointment types — OPD, Follow-up, Emergency
-  Status tracking — BOOKED → CONFIRMED → COMPLETED / NO_SHOW
-  Double-booking prevention via unique constraint on doctor+date+slot
-  Doctor availability toggle for leaves/holidays

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 30
## 6.4 Pharmacy & Inventory
-  Drug master — generic name, category, unit, HSN code, manufacturer
-  Stock management — batch-wise with expiry dates per center
-  FIFO dispense — earliest batch used first automatically
-  Drug dispense — linked to patient + OPD visit, updates stock
-  Auto stock alerts — LOW_STOCK (<10), EXPIRY_SOON (<30 days), OUT_OF_STOCK
-  Alert notifications pushed to center staff + upline manager
-  Stock report — current quantity, value, expiring soon per center
-  Download template + bulk stock upload from Excel
## 6.5 Billing & Finance
-  Auto invoice number — INV-YYYY-XXXXXX format
-  Combined billing — consultation + pharmacy + procedures in one invoice
-  GST-compliant — 18% tax on pharmacy, 0% on consultation
-  Payment modes — Cash, UPI, Card, Insurance
-  Insurance claim tracking — provider + claim ID stored
-  Discount support — flat or percentage
-  10% commission — auto-calculated on payment, credited to agent wallet
-  Wallet rollup — Center → Block → District → State wallet update
-  Upline notification on every payment received
-  Refund workflow with reason tracking
6.6 HR & Payroll
-  Staff profile — department, designation, joining date, salary, bank
-  Daily attendance — mark PRESENT/ABSENT/HALF_DAY/ON_LEAVE/HOLIDAY
-  Bulk attendance marking for entire center in one action
-  Leave request workflow — Apply → HR Approve/Reject
-  Leave balance tracking — Sick(12), Casual(12), Earned(15) per year
-  Payroll generation — auto-computes from attendance, deductions, bonus
-  Net salary formula: (base/working_days × present_days) - deductions + bonus
-  Payslip download (PDF) per employee per month
-  Payroll status — DRAFT → PROCESSED → PAID

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 31
## 6.7 Wallet & Commission System
-  4-level wallet hierarchy — Center, Block, District, State
-  Every invoice payment auto-credits center wallet
-  10% commission deducted from center, credited to agent
-  District wallet = sum of its centers' net amounts
-  State wallet = sum of its districts' amounts
-  Full transaction log — every credit/debit with reference ID
-  Balance view scoped to logged-in user's level
-  Push notification to upline on every wallet update
## 6.8 Reports & Analytics Dashboard
-  Role-scoped dashboard — Super Admin sees all, Center Staff sees own center
-  KPI cards — Today's OPD, Active IPD, Revenue, Low Stock Alerts
-  Revenue trend — Line chart by day/week/month (Recharts)
-  Patient volume — Bar chart by center/district
-  Stock level — Pie chart of drug categories
-  Attendance summary — monthly heatmap per center
-  Commission & wallet movement — timeline view
-  Export reports — PDF / Excel download
-  Date range filter — from/to with preset shortcuts (Today, Week, Month)

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 32
## CHAPTER 07
API Endpoints — Complete Reference
Base URL & Auth
Base URL: http://localhost:8080/api
All endpoints (except /auth/**) require: Authorization: Bearer <accessToken>
All responses wrapped in: { success, data, message, timestamp }
## Auth Endpoints
MethodEndpointAccessDescription
POST/auth/loginPublic
Login — returns access + refresh
tokens
POST/auth/refreshPublicNew access token from refresh token
POST/auth/logoutAuthenticatedRevoke refresh token
## User Management
MethodEndpointAccessDescription
POST/users/createADMIN+
Create single user (rank check
enforced)
POST/users/bulk-uploadADMIN+
Upload Excel — returns preview with
errors
POST/users/bulk-confirmADMIN+Save valid rows from bulk preview
GET/users/templateADMIN+
Download Excel template for bulk
upload
GET/usersADMIN+
Paginated user list (filter by
role/center/status)
GET/users/{id}ADMIN+Single user with permissions
PUT/users/{id}ADMIN+Update user (rank check enforced)
DELETE/users/{id}ADMIN+Soft delete (sets is_active=false)

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 33
## Geographic & Centers
MethodEndpointAccessDescription
POST/centersSUPER_ADMINCreate new center
GET/centers
## STATE_MANAGE
## R+
List centers (scoped to user)
GET/centers/{id}
## BLOCK_MANAGE
## R+
Center detail with wallet balance
GET/statesSUPER_ADMINAll states
GET/districts?stateId=
## STATE_MANAGE
## R+
Districts of a state
GET/blocks?districtId=
## DISTRICT_MANA
## GER+
Blocks of a district
## Patient Module
MethodEndpointAccessDescription
POST/patients/registerRECEPTIONIST+
Register patient — auto-generates
## UHID
GET/patientsDOCTOR+
Search by name/phone/UHID
## (paginated)
GET/patients/{uhid}DOCTOR+Full profile + visit history
PUT/patients/{id}RECEPTIONIST+Update patient info
POST/opd/visitRECEPTIONIST+
Create OPD visit — auto token
assigned
GET/opd/queue?centerId=&date;=
## CENTER_STAFF
## +
Today's OPD queue
PUT/opd/visit/{id}/statusDOCTOR+Update visit status
GET/opd/visits/{patientId}DOCTOR+OPD history of a patient
POST/ipd/admitDOCTOR+
Admit patient — bed marked
occupied
GET/ipd/active?centerId=
## CENTER_STAFF
## +
Currently admitted patients
PUT/ipd/discharge/{id}DOCTOR+
Discharge — frees bed, triggers
billing
GET/beds/available?centerId=&ward;=
## CENTER_STAFF
## +
Available beds

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 34
## Doctor & Appointments
MethodEndpointAccessDescription
POST/doctors/profileADMIN+Create doctor profile
GET/doctors?centerId=&specialization;=PUBLIC*List doctors (* filtered)
GET/doctors/{id}/scheduleRECEPTIONIST+Doctor weekly schedule
PUT/doctors/{id}/scheduleDOCTOR+Update own schedule
GET/appointments/slots?doctorId=&date;=RECEPTIONIST+Available slots
POST/appointments/bookRECEPTIONIST+Book appointment
GET/appointments?doctorId=&date;=DOCTOR+Doctor appointment list
PUT/appointments/{id}/statusDOCTOR+Confirm/cancel/complete
GET/appointments/patient/{patientId}DOCTOR+Patient appointment history
## Pharmacy & Inventory
MethodEndpointAccessDescription
POST/drugsADMIN+Add drug to master list
GET/drugs?search=&category;=PHARMACIST+Search drugs
POST/drugs/stockPHARMACIST+Add stock batch to center
GET/drugs/stock?centerId=PHARMACIST+Current stock with expiry
PUT/drugs/stock/{id}PHARMACIST+Update stock quantity
POST/drugs/dispensePHARMACIST+
Dispense to patient — FIFO
deduction
GET/drugs/dispense/patient/{id}PHARMACIST+Patient dispense history
GET/drugs/alerts?centerId=
## CENTER_STAFF
## +
Active stock alerts

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 35
## Billing & Wallet
MethodEndpointAccessDescription
POST/billing/invoiceRECEPTIONIST+Generate invoice with line items
POST/billing/invoice/{id}/payRECEPTIONIST+
Record payment — triggers
wallet+commission
GET/billing/invoices?centerId=&status;=RECEPTIONIST+Invoice list
GET/billing/invoices/{id}RECEPTIONIST+Invoice detail with items
POST/billing/invoice/{id}/refundADMIN+Process refund
GET/wallet/balance?entityType=&entityId;=
## BLOCK_MANAGE
## R+
Wallet balance
GET/wallet/transactions?walletId=&from;=&to;=
## BLOCK_MANAGE
## R+
Transaction history
HR & Payroll
MethodEndpointAccessDescription
POST/hr/staffHR_MANAGER+Create staff profile
GET/hr/staff?centerId=&dept;=HR_MANAGER+Staff list
PUT/hr/staff/{id}HR_MANAGER+Update staff profile
POST/hr/attendance/markHR_MANAGER+Mark attendance (bulk supported)
GET/hr/attendance?userId=&month;=&year;=HR_MANAGER+Attendance records
PUT/hr/attendance/{id}HR_MANAGER+Correct attendance entry
POST/hr/leave/applyAuthenticatedApply for leave
PUT/hr/leave/{id}/approveHR_MANAGER+Approve/reject leave
GET/hr/leave/pending?centerId=HR_MANAGER+Pending leave requests
GET/hr/leave/balance/{userId}AuthenticatedLeave balance
POST/hr/payroll/generateHR_MANAGER+
Generate payroll
## (month+year+center)
GET/hr/payroll/{userId}?month=&year;=HR_MANAGER+Payslip detail
PUT/hr/payroll/{id}/payADMIN+Mark payroll as PAID

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 36
## Reports & Dashboard
MethodEndpointAccessDescription
## GET
/reports/dashboard?centerId=&from;=&to;
## =
## CENTER_STAFF
## +
Dashboard KPIs
GET/reports/revenue?centerId=&groupBy;=
## BLOCK_MANAGE
## R+
Revenue trend data
GET/reports/patients?centerId=&from;=&to;=
## BLOCK_MANAGE
## R+
Patient statistics
GET/reports/inventory?centerId=PHARMACIST+Stock summary report
GET/reports/hr?centerId=&month;=&year;=HR_MANAGER+Attendance/payroll summary
GET/reports/wallet-movement?from=&to;=
## DISTRICT_MANA
## GER+
Wallet flow report

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 37
## CHAPTER 08
UI/UX Design System
## Color Palette
NameHex CodeUsage
Background#F8FAFCPage/app background
Primary Blue#2563EBButtons, links, active states
Dark Text#0F172APrimary body text
Slate Gray#64748BSecondary text, labels
Success Green#16A34APharmacy module, active badges
Warning Amber#D97706Alerts, pending states
Danger Red#DC2626Errors, critical alerts
Purple#7C3AEDOPD/IPD module accent
Cyan#0891B2Doctor module accent
Pink#DB2777HR module accent
Emerald#059669Wallet module accent
Indigo#4F46E5Reports module accent
## Typography
ElementFontSizeWeight
Page HeadingInter24px600 SemiBold
Section HeadingInter18px500 Medium
Card TitleInter16px600 SemiBold
Body TextInter14px400 Regular
Form LabelInter12px500 Medium + UPPERCASE
Small/MetaInter12px400 Regular
Code/MonoJetBrains Mono13px400 Regular
## Form Design Standards

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 38
-  2-column grid layout for related fields (name + DOB, phone + email)
-  Sections with clear headings: PERSONAL INFO, CONTACT DETAILS, PERMISSIONS
-  44px input height, rounded-lg border, focus ring in primary blue
-  Inline validation — error shown under field immediately, no page jump
-  Required fields marked with red asterisk (*) on the label
-  Action buttons right-aligned — Cancel (ghost) + Primary action (filled)
-  Auto-fill behavior where possible (age from DOB, token from center+date)
-  Permission matrix — checkboxes grouped by module with toggle-all per module
-  Bulk upload tab on user creation form alongside manual tab
Table/List Design Standards
-  Page header: title + total count + primary CTA button (+ Add New)
-  Search bar + Filters dropdown always visible above table
-  Zebra striping — white and #F1F5F9 alternate rows
-  Status shown as colored pill badge (not plain text)
-  Action buttons appear on row hover — View, Edit, Delete icons
-  Sortable column headers with visual sort indicator
-  Pagination: prev/next + page numbers + items-per-page selector (10/25/50)
-  Empty state illustration when no data — not just blank space
-  Loading skeleton instead of spinner for better perceived performance

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 39
## CHAPTER 09
Phase-by-Phase Build Plan
## Build Philosophy
We build backend first, then frontend for each phase. Each phase is fully functional and testable in
Postman before moving to the next. No phase is skipped.
Start every session by saying: 'Start Phase N' — I will generate all code completely.
Phase 1 — Project Setup + Auth + JWT
## Backend Tasks
3 Maven project setup — pom.xml with all
dependencies
3 application.yml — MySQL, JWT config, server port
3 All JPA entities for users, permissions,
user_permissions, refresh_tokens
3 Geographic entities — states, districts, blocks, centers
3 JwtService — generate, validate, extract claims
3 JwtAuthFilter — intercept requests, validate Bearer
token
3 SecurityConfig — permit /auth/**, secure all else
3 AuthController — POST /login, /refresh, /logout
3 GlobalExceptionHandler — 401, 403, 404, 400
3 ApiResponse wrapper for all responses
3 ScopeFilter utility — inject scope into every service
## Frontend Tasks
3 Vite + React + Tailwind + shadcn/ui setup
3 Inter font, global CSS variables, light theme
3 Zustand authStore — token, user, permissions state
3 Axios instance with request interceptor (attach JWT)
3 Axios response interceptor (401 → auto refresh or
logout)
3 React Router setup with AppRouter
3 ProtectedRoute + RoleRoute components
3 Login page — email/password form, error handling,
token storage
3 DashboardLayout — sidebar + topbar shell
## Phase 2 — User Management + Geographic Setup

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 40
## Backend Tasks
3 UserService — create with rank guard + permission
guard
3 UserController — CRUD + bulk upload endpoints
3 Apache POI — Excel parsing for bulk upload
3 Bulk validation — duplicate email, rank check, role
valid
3 Preview response — VALID / ERROR per row with
reason
3 Bulk confirm — save only valid rows
3 Template download endpoint — pre-formatted Excel
3 GeographicController — states, districts, blocks,
centers CRUD
3 RankGuard utility — enforced on every user creation
## Frontend Tasks
3 User list page — table with search, filters, pagination
3 Create user form — 2-col grid, role dropdown,
permission matrix
3 Permission matrix UI — module groups, toggle
switches
3 Bulk upload tab — react-dropzone, preview table,
error badges
3 Center management page — CRUD for centers
3 Geographic selectors — cascading State → District →
Block dropdowns
Phase 3 — Patient Management — OPD & IPD
## Backend Tasks
3 Patient entity + UHID generation service
## (HOSP-YYYY-XXXXXX)
3 OpdVisit — auto token number (resets daily per
center)
3 IpdAdmission — bed assignment, status management
3 Bed entity — occupied/available tracking
3 PatientController — register, search, profile
3 OpdController — create visit, queue, status update
3 IpdController — admit, discharge, active list, beds
3 Discharge trigger — calculates total days,
auto-creates invoice draft
## Frontend Tasks
3 Patient registration form — UHID shown as
auto-generated badge
3 Patient list + search (name, phone, UHID)
3 Patient profile page — demographics + full visit history
tabs
3 OPD queue board — token cards with status color
coding
3 New OPD visit form — patient autocomplete, doctor
dropdown
3 IPD admission form — bed selector by ward
3 Bed grid — visual occupied (red) / available (green)
per ward
3 Discharge modal — summary + auto-bill preview
## Phase 4 — Doctor Profiles + Appointment Scheduling

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 41
## Backend Tasks
3 DoctorProfile entity + service
3 DoctorSchedule — weekly slots per doctor
3 Slot generation algorithm — start/end/duration →
available slots
3 Appointment booking with double-booking prevention
3 AppointmentController — book, slots, status, history
3 Availability check on booking — validate against
schedule
## Frontend Tasks
3 Doctor list + profile page
3 Schedule editor — day-wise time range + duration
3 Appointment booking page — date picker + visual slot
grid
3 Slot grid — green/gray visual, click to select
3 Doctor's daily appointment list (admin view)
3 Patient appointment history tab on profile page
## Phase 5 — Pharmacy + Inventory Management
## Backend Tasks
3 Drug master entity + service + controller
3 DrugStock — batch-wise per center
3 FIFO dispense logic — earliest expiry batch first
3 Stock deduction on dispense with validation
3 Auto alert trigger — LOW_STOCK, EXPIRY_SOON,
## OUT_OF_STOCK
3 StockAlert entity + resolution workflow
3 Notification service — push alert to center staff +
upline
## Frontend Tasks
3 Drug master list + add drug form
3 Stock management table — batch, expiry, quantity
3 Add stock form — supplier, batch, expiry, pricing
3 Dispense form — patient search + drug search +
quantity
3 Stock alerts panel — badge counts on sidebar
3 Alert cards — type badge, drug name, quantity,
resolve button
## Phase 6 — Billing + Payments + Wallet + Commission

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 42
## Backend Tasks
3 Invoice entity + auto invoice number generator
3 InvoiceItem — line items with types
3 GST calculation — 18% pharmacy, 0% consultation
3 Payment recording — status transition PENDING →
## PAID
3 Commission calculation — 10% auto on payment
3 Wallet service — credit center, debit commission,
rollup
3 Wallet transaction logging — every credit/debit
3 Upline notification on payment received
3 Refund workflow with status tracking
## Frontend Tasks
3 Invoice generator — dynamic line-item table
(add/remove rows)
3 Live total calculation — subtotal, tax, discount, total as
you type
3 Payment modal — mode selector
(Cash/UPI/Card/Insurance icons)
3 Invoice list — filter by status, date, center
3 Invoice detail view — print-friendly layout
3 Wallet balance card per level
3 Transaction history table — credit/debit with reference
Phase 7 — HR + Attendance + Leave + Payroll
## Backend Tasks
3 StaffProfile entity + service
3 Attendance — mark, bulk mark, correct
3 LeaveRequest — apply, approve, reject workflow
3 LeaveBalance — per year, per type, auto-deduct on
approval
3 Payroll generation — formula: (base/working_days ×
present) - deductions + bonus
3 Payslip PDF generation (using reportlab-style or iText)
3 Payroll status — DRAFT → PROCESSED → PAID
## Frontend Tasks
3 Staff list + create staff form (with HR-specific fields)
3 Attendance marking page — staff table with status
dropdowns
3 Monthly attendance calendar view per staff
3 Leave request form — type, date range, reason
3 Leave approval table — pending requests with
approve/reject
3 Leave balance widget — visual bars per leave type
3 Payroll generation page — month/year selector +
generate button
3 Payslip page — clean layout with download PDF
button
## Phase 8 — Reports + Analytics + Full Dashboard

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 43
## Backend Tasks
3 DashboardController — aggregated KPIs in single call
3 Revenue report — group by DAY / WEEK / MONTH
3 Patient statistics report
3 Inventory report — stock value, expiring soon
3 HR summary — attendance rates, leave trends
3 Wallet movement report — all levels
3 All reports scoped to logged-in user's level
## Frontend Tasks
3 Dashboard page — KPI cards + 4 chart widgets
3 Revenue line chart — Recharts with date range
selector
3 Patient bar chart — by center/district comparison
3 Stock pie chart — by drug category
3 Attendance heatmap — monthly grid per staff
3 Role-based dashboard — different widgets per role
3 Reports page — all reports with export (PDF/Excel)
3 Final UI polish — consistent spacing, animations,
empty states
3 Mobile responsiveness — collapsed sidebar,
responsive tables

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 44
## CHAPTER 10
## Security Architecture
## Security Layers
LayerImplementation
TransportHTTPS in production (HTTP locally for dev)
AuthenticationJWT access token (15min) + refresh token (7 days, DB-stored)
Authorization — Role@PreAuthorize('hasRole(...)') on every controller method
Authorization — Permissionpermission_id checked in service layer for granular actions
Rank GuardUser creation blocked if target rank >= creator rank
Scope FilterEvery DB query auto-filtered by user's scope (center/district/state)
PasswordBCrypt with strength 12 — never stored plain text
Token RevocationRefresh tokens stored in DB — logout sets is_revoked=true
Input ValidationJakarta Validation (@NotNull, @Email, @Size) on all DTOs
CORSConfigured to allow only React frontend origin (localhost:5173)
SQL InjectionJPA parameterized queries — no raw SQL concatenation
Sensitive DataBank account, Aadhar, PAN — never returned in API responses, write-only
Environment Variables (Never Hardcode)
# .env (add to .gitignore immediately)
DB_USERNAME=root
DB_PASSWORD=your_password_here
DB_NAME=hospital_erp_db
JWT_SECRET=your-256-bit-secret-key-here
## JWT_EXPIRY=900000
## JWT_REFRESH_EXPIRY=604800000
## SERVER_PORT=8080
n Add .env and application-local.yml to .gitignore BEFORE first git push.

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 45
## CHAPTER 11
## Folder Structure
## Backend — Spring Boot
hospital-erp-backend/
nnn pom.xml
nnn src/main/
n   nnn java/com/hospital/erp/
n   n   nnn HospitalErpApplication.java
n   n   nnn config/
n   n   n   nnn SecurityConfig.java
n   n   n   nnn CorsConfig.java
n   n   n   nnn AppConfig.java
n   n   nnn auth/
n   n   n   nnn AuthController.java
n   n   n   nnn AuthService.java
n   n   n   nnn JwtService.java
n   n   n   nnn JwtAuthFilter.java
n   n   n   nnn dto/ (AuthRequest, AuthResponse, TokenRefreshRequest)
n   n   nnn user/
n   n   n   nnn User.java (entity)
n   n   n   nnn UserRepository.java
n   n   n   nnn UserService.java
n   n   n   nnn UserController.java
n   n   n   nnn RankGuard.java
n   n   n   nnn ScopeFilter.java
n   n   n   nnn dto/
n   n   nnn geographic/
n   n   n   nnn entities/ (State, District, Block, Center)
n   n   n   nnn repositories/
n   n   n   nnn services/
n   n   n   nnn controllers/
n   n   nnn patient/ (Patient, OpdVisit, IpdAdmission, Bed)
n   n   nnn doctor/ (DoctorProfile, DoctorSchedule)
n   n   nnn appointment/ (Appointment)
n   n   nnn pharmacy/ (Drug, DrugStock, DrugDispense, StockAlert)
n   n   nnn billing/ (Invoice, InvoiceItem, Commission)
n   n   nnn wallet/ (Wallet, WalletTransaction)
n   n   nnn hr/ (StaffProfile, Attendance, LeaveRequest, LeaveBalance, Payroll)
n   n   nnn notification/ (Notification)
n   n   nnn reports/ (ReportController, ReportService)
n   n   nnn common/
n   n       nnn ApiResponse.java
n   n       nnn GlobalExceptionHandler.java
n   n       nnn CurrentUser.java (annotation)
n   n       nnn PageResponse.java
n   nnn resources/
n       nnn application.yml
n       nnn application-local.yml (gitignored)

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 46
## Frontend — React + Vite
hospital-erp-frontend/
nnn index.html
nnn vite.config.js
nnn tailwind.config.js
nnn package.json
nnn src/
nnn main.jsx
nnn App.jsx
nnn api/
n   nnn axios.js          ← base instance + interceptors
n   nnn authApi.js
n   nnn userApi.js
n   nnn patientApi.js
n   nnn opdApi.js
n   nnn doctorApi.js
n   nnn pharmacyApi.js
n   nnn billingApi.js
n   nnn hrApi.js
n   nnn walletApi.js
n   nnn reportApi.js
nnn store/
n   nnn authStore.js      ← Zustand: user, token, permissions
n   nnn uiStore.js        ← Zustand: sidebar collapsed, toasts
nnn routes/
n   nnn AppRouter.jsx
n   nnn ProtectedRoute.jsx
n   nnn RoleRoute.jsx
nnn layouts/
n   nnn DashboardLayout.jsx
n   nnn Sidebar.jsx
n   nnn Topbar.jsx
nnn components/
n   nnn ui/               ← shadcn components
n   nnn DataTable.jsx
n   nnn PageHeader.jsx
n   nnn StatusBadge.jsx
n   nnn KpiCard.jsx
n   nnn PermissionMatrix.jsx
n   nnn BulkUpload.jsx
n   nnn ConfirmModal.jsx
nnn pages/
nnn Login/
nnn Dashboard/
nnn Users/
nnn Patients/
nnn OPD/
nnn IPD/
nnn Doctors/
nnn Appointments/
nnn Pharmacy/
nnn Billing/
nnn HR/

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 47
nnn Wallet/
nnn Reports/

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 48
## CHAPTER 12
Deployment & DevOps
## Local Development Setup
StepCommand / Action
- Clone repogit clone https://github.com/prince2404/hospital-erp
- Start MySQLmysql -u root -p → CREATE DATABASE hospital_erp_db;
- Set env varsCopy .env.example → .env, fill values
- Run backendcd backend → mvn spring-boot:run
- Run frontendcd frontend → npm install → npm run dev
- Access appFrontend: http://localhost:5173 API: http://localhost:8080
## Git Branching Strategy
BranchPurpose
mainProduction-ready code only
developIntegration branch — merge all features here first
feature/phase-1-authOne branch per phase during development
feature/phase-2-usersMerged to develop when phase is complete
hotfix/*Critical bug fixes on main
Future Deployment (Post-Development)
ComponentPlatform OptionFree Tier?
Backend (Spring Boot)Railway / Render / AWS EC2Yes (Railway/Render)
Database (MySQL)PlanetScale / AWS RDS / RailwayYes (PlanetScale)
Frontend (React)Vercel / NetlifyYes — both
DomainNamecheap / GoDaddyNo — ~n800/year
## Important Reminders Before First Push
-  Add to .gitignore: .env, application-local.yml, target/, node_modules/, *.class
-  Never commit real credentials — use environment variables always

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 49
-  Set GitHub repo to PRIVATE until project is complete
-  Tag each phase completion: git tag v1.0-phase1, v1.0-phase2...

HOSPITAL ERP — Apana Swastha KendraComplete Project Planning Document
Confidential — For Development Use OnlyPage 50
Ready to Build
Apana Swastha Kendra — Hospital ERP
29 Tables | 11 Roles | 8 Modules | 8 Phases | Full StackSay   "Start Phase 1"   to begin building.
Document generated on April 04, 2026 | github.com/prince2404 | Version 1.0