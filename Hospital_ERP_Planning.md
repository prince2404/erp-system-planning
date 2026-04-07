||
||
||
||
||
||

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> CHAPTER —
>
> **Table** **of** **Contents**

||
||
||
||
||
||
||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 2

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> CHAPTER 01
>
> **Project** **Overview** **&** **Vision**
>
> **What** **Are** **We** **Building?**
>
> A **multi-center,** **role-based** **Hospital** **ERP** **Management**
> **System** named **Apana** **Swastha** **Kendra**. This is a
> full-stack enterprise application covering patient management, OPD/IPD
> workflows, pharmacy, billing, HR, payroll, wallet/commission systems,
> and analytics — designed to operate across a geographic hierarchy of
> States, Districts, Blocks, and Centers.
>
> **Key** **Goals**

||
||
||
||
||
||
||
||

> **Project** **Scope** **—** **3** **States** **Focus**

||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 3

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> CHAPTER 02
>
> **Tech** **Stack** **—** **Complete** **Breakdown**
>
> **Backend**

||
||
||
||
||
||
||
||
||
||
||
||
||
||

> **Database**

||
||
||
||
||
||

> **Frontend**

||
||
||
||
||
||
||

Confidential — For Development Use Only Page 4

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

||
||
||
||
||
||
||
||
||
||
||
||
||

> **Dev** **Tools** **&** **Environment**

||
||
||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 5

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> CHAPTER 03
>
> **System** **Architecture**
>
> **High-Level** **Architecture**
>
> REACT FRONTEND (Vite) Zustand Store React Query Axios Interceptor
> Protected Routes Role-based Sidebar
>
> HTTP/REST (JSON) Bearer: JWT Token
>
> SPRING BOOT 3 BACKEND (:8080) JwtAuthFilter → SecurityConfig
> → Controllers
>
> Services (Business Logic + ScopeFilter) Repositories (Spring Data JPA)
>
> JPA / Hibernate
>
> MySQL 8.0 DATABASE hospital_erp_db (29 tables)
>
> **Request** **Lifecycle**

||
||
||
||
||
||
||
||
||
||

> **JWT** **Token** **Structure**
>
> Header: { "alg": "HS256", "typ": "JWT" } Payload: {
>
> "sub": "45",
>
> "name": "Dr. Sharma", "role": "DOCTOR",

Confidential — For Development Use Only Page 6

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> "rank": 8, "centerId": 3,
>
> "centerName": "City Hospital", "scopeType": "CENTER", "scopeId": 3,
>
> "permissions": \["READ_PATIENT","READ_OPD","DISPENSE_MEDICINE"\],
> "iat": 1704067200,
>
> "exp": 1704068100 }
>
> Access Token Expiry: 15 minutes Refresh Token Expiry: 7 days

Confidential — For Development Use Only Page 7

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> CHAPTER 04
>
> **User** **Hierarchy** **&** **Role** **System**
>
> **Geographic** **Hierarchy**
>
> SUPER ADMIN (rank 1) — System-wide
>
> State Manager Punjab (rank 2) — STATE scope
>
> District Manager Ludhiana (rank 3) — DISTRICT scope Block Manager
> Block-A (rank 4) — BLOCK scope
>
> Center: City Hospital
>
> Doctor Pharmacist Receptionist HR Manager
>
> Center Staff

(rank 7) (rank 8) (rank 8) (rank 7)

> (rank 9)
>
> Center: Village Clinic ... same staff roles
>
> **All** **11** **Roles** **—** **Permissions** **Matrix**

||
||
||
||
||
||
||
||
||
||
||
||
||
||

> **Granular** **Permission** **System**
>
> **Three-Layer** **Permission** **Model**
>
> **Layer** **1** **—** **Role:** Broad default access (Doctor gets
> patient read, OPD manage, etc.)

Confidential — For Development Use Only Page 8

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **Layer** **2** **—** **Permissions:** Granular actions stored in
> user_permissions table.
>
> **Layer** **3** **—** **Override:** At user creation, creator
> customizes permissions via checkbox UI. CRITICAL RULE: Creator can
> only grant permissions they themselves possess.

||
||
||
||
||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 9

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> CHAPTER 05
>
> **Database** **Schema** **—** **All** **29** **Tables**
>
> **Schema** **Summary**
>
> Database: **hospital_erp_db** \| Engine: **InnoDB** \| Charset:
> **utf8mb4** \| Collation: **utf8mb4_unicode_ci** \| Total Tables:
> **29**

||
||
||
||
||
||
||
||
||
||
||
||
||

> **Geographic** **Tables**
>
> **states**

||
||
||
||
||
||

Confidential — For Development Use Only Page 10

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **districts**

||
||
||
||
||
||

> **blocks**

||
||
||
||
||
||

> **Auth** **&** **User** **Tables**

Confidential — For Development Use Only Page 11

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **users**

||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||

> **permissions**

||
||
||
||
||
||
||

Confidential — For Development Use Only Page 12

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **user_permissions**

||
||
||
||
||
||
||
||
||

> **refresh_tokens**

||
||
||
||
||
||
||
||
||

> **Center** **&** **Wallet** **Tables**

Confidential — For Development Use Only Page 13

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **centers**

||
||
||
||
||
||
||
||
||
||
||
||
||
||

> **wallets**

||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 14

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **wallet_transactions**

||
||
||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 15

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **Patient** **Tables**
>
> **patients**

||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 16

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **opd_visits**

||
||
||
||
||
||
||
||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 17

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **ipd_admissions**

||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||

> **Doctor** **&** **Appointment** **Tables**
>
> **beds**

||
||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 18

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **doctor_profiles**

||
||
||
||
||
||
||
||
||
||
||
||

> **doctor_schedules**

||
||
||
||
||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 19

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **appointments**

||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 20

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **Pharmacy** **Tables**
>
> **drugs**

||
||
||
||
||
||
||
||
||
||
||
||

> **drug_stocks**

||
||
||
||
||
||
||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 21

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **drug_dispenses**

||
||
||
||
||
||
||
||
||
||
||
||
||

> **stock_alerts**

||
||
||
||
||
||
||
||
||
||
||
||

> **Billing** **Tables**

Confidential — For Development Use Only Page 22

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **invoices**

||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||

> **invoice_items**

||
||
||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 23

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **commissions**

||
||
||
||
||
||
||
||
||
||
||

> **HR** **&** **Payroll** **Tables**
>
> **staff_profiles**

||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 24

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **attendance**

||
||
||
||
||
||
||
||
||
||
||
||
||

> **leave_requests**

||
||
||
||
||
||
||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 25

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **leave_balances**

||
||
||
||
||
||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 26

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **payrolls**

||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||

> **Notifications** **Table**

Confidential — For Development Use Only Page 27

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **notifications**

||
||
||
||
||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 28

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> CHAPTER 06
>
> **Module** **Breakdown** **—** **All** **8** **Modules**
>
> **6.1** **Auth** **&** **User** **Management**
>
> • JWT login with access token (15min) + refresh token (7 days)
>
> • 11 roles with rank hierarchy — lower number = higher authority
>
> • Granular permission system — per-user override at creation time
>
> • Rank guard — cannot create user of equal or higher rank
>
> • Permission guard — cannot grant permissions you don't have
>
> • Bulk user import from Excel/CSV with preview + error report
>
> • Soft delete (is_active=false) — data preserved
>
> • Scope-based data filtering — auto-applied on every query
>
> **6.2** **Patient** **Management** **(OPD/IPD)**
>
> • Auto-generated UHID — format HOSP-YYYY-XXXXXX, globally unique
>
> • Patient profile — demographics, allergies, emergency contact,
> history
>
> • OPD visit creation — auto token number (resets daily per center)
>
> • OPD queue board — real-time token status
> (WAITING/IN_PROGRESS/COMPLETED)
>
> • IPD admission — bed assignment, ward selection, daily charges
>
> • IPD discharge — auto-calculates total days, triggers billing
>
> • Bed management — visual grid of occupied/available beds per ward
>
> • Full visit history per patient — OPD + IPD + prescriptions
>
> **6.3** **Doctor** **&** **Appointment** **Scheduling**
>
> • Doctor profile — specialization, qualification, experience, fee
>
> • Weekly schedule per doctor — day, start time, end time, slot
> duration
>
> • Slot generation — auto-compute available slots excluding booked ones
>
> • Visual slot grid on frontend — green=available, gray=booked
>
> • Appointment types — OPD, Follow-up, Emergency
>
> • Status tracking — BOOKED → CONFIRMED → COMPLETED / NO_SHOW •
> Double-booking prevention via unique constraint on doctor+date+slot
>
> • Doctor availability toggle for leaves/holidays

Confidential — For Development Use Only Page 29

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **6.4** **Pharmacy** **&** **Inventory**
>
> • Drug master — generic name, category, unit, HSN code, manufacturer
>
> • Stock management — batch-wise with expiry dates per center
>
> • FIFO dispense — earliest batch used first automatically
>
> • Drug dispense — linked to patient + OPD visit, updates stock
>
> • Auto stock alerts — LOW_STOCK (\<10), EXPIRY_SOON (\<30 days),
> OUT_OF_STOCK
>
> • Alert notifications pushed to center staff + upline manager
>
> • Stock report — current quantity, value, expiring soon per center
>
> • Download template + bulk stock upload from Excel
>
> **6.5** **Billing** **&** **Finance**
>
> • Auto invoice number — INV-YYYY-XXXXXX format
>
> • Combined billing — consultation + pharmacy + procedures in one
> invoice
>
> • GST-compliant — 18% tax on pharmacy, 0% on consultation
>
> • Payment modes — Cash, UPI, Card, Insurance
>
> • Insurance claim tracking — provider + claim ID stored
>
> • Discount support — flat or percentage
>
> • 10% commission — auto-calculated on payment, credited to agent
> wallet • Wallet rollup — Center → Block → District → State wallet
> update
>
> • Upline notification on every payment received • Refund workflow with
> reason tracking
>
> **6.6** **HR** **&** **Payroll**
>
> • Staff profile — department, designation, joining date, salary, bank
>
> • Daily attendance — mark PRESENT/ABSENT/HALF_DAY/ON_LEAVE/HOLIDAY
>
> • Bulk attendance marking for entire center in one action • Leave
> request workflow — Apply → HR Approve/Reject
>
> • Leave balance tracking — Sick(12), Casual(12), Earned(15) per year
>
> • Payroll generation — auto-computes from attendance, deductions,
> bonus
>
> • Net salary formula: (base/working_days × present_days) -
> deductions + bonus • Payslip download (PDF) per employee per month
>
> • Payroll status — DRAFT → PROCESSED → PAID

Confidential — For Development Use Only Page 30

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **6.7** **Wallet** **&** **Commission** **System**
>
> • 4-level wallet hierarchy — Center, Block, District, State
>
> • Every invoice payment auto-credits center wallet
>
> • 10% commission deducted from center, credited to agent
>
> • District wallet = sum of its centers' net amounts
>
> • State wallet = sum of its districts' amounts
>
> • Full transaction log — every credit/debit with reference ID
>
> • Balance view scoped to logged-in user's level
>
> • Push notification to upline on every wallet update
>
> **6.8** **Reports** **&** **Analytics** **Dashboard**
>
> • Role-scoped dashboard — Super Admin sees all, Center Staff sees own
> center
>
> • KPI cards — Today's OPD, Active IPD, Revenue, Low Stock Alerts
>
> • Revenue trend — Line chart by day/week/month (Recharts)
>
> • Patient volume — Bar chart by center/district
>
> • Stock level — Pie chart of drug categories
>
> • Attendance summary — monthly heatmap per center
>
> • Commission & wallet movement — timeline view
>
> • Export reports — PDF / Excel download
>
> • Date range filter — from/to with preset shortcuts (Today, Week,
> Month)

Confidential — For Development Use Only Page 31

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> CHAPTER 07
>
> **API** **Endpoints** **—** **Complete** **Reference**
>
> **Base** **URL** **&** **Auth**
>
> Base URL: **http://localhost:8080/api**
>
> All endpoints (except /auth/\*\*) require: **Authorization:**
> **Bearer** **\<accessToken\>**
>
> All responses wrapped in: **{** **success,** **data,** **message,**
> **timestamp** **}**
>
> **Auth** **Endpoints**

||
||
||
||
||
||

> **User** **Management**

||
||
||
||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 32

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **Geographic** **&** **Centers**

||
||
||
||
||
||
||
||
||

> **Patient** **Module**

||
||
||
||
||
||
||
||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 33

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **Doctor** **&** **Appointments**

||
||
||
||
||
||
||
||
||
||
||
||

> **Pharmacy** **&** **Inventory**

||
||
||
||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 34

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **Billing** **&** **Wallet**

||
||
||
||
||
||
||
||
||
||

> **HR** **&** **Payroll**

||
||
||
||
||
||
||
||
||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 35

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **Reports** **&** **Dashboard**

||
||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 36

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> CHAPTER 08
>
> **UI/UX** **Design** **System**
>
> **Color** **Palette**

||
||
||
||
||
||
||
||
||
||
||
||
||
||
||

> **Typography**

||
||
||
||
||
||
||
||
||
||

> **Form** **Design** **Standards**

Confidential — For Development Use Only Page 37

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> • 2-column grid layout for related fields (name + DOB, phone + email)
>
> • Sections with clear headings: PERSONAL INFO, CONTACT DETAILS,
> PERMISSIONS
>
> • 44px input height, rounded-lg border, focus ring in primary blue
>
> • Inline validation — error shown under field immediately, no page
> jump
>
> • Required fields marked with red asterisk (\*) on the label
>
> • Action buttons right-aligned — Cancel (ghost) + Primary action
> (filled)
>
> • Auto-fill behavior where possible (age from DOB, token from
> center+date)
>
> • Permission matrix — checkboxes grouped by module with toggle-all per
> module
>
> • Bulk upload tab on user creation form alongside manual tab
>
> **Table/List** **Design** **Standards**
>
> • Page header: title + total count + primary CTA button (+ Add New) •
> Search bar + Filters dropdown always visible above table
>
> • Zebra striping — white and \#F1F5F9 alternate rows • Status shown as
> colored pill badge (not plain text)
>
> • Action buttons appear on row hover — View, Edit, Delete icons •
> Sortable column headers with visual sort indicator
>
> • Pagination: prev/next + page numbers + items-per-page selector
> (10/25/50) • Empty state illustration when no data — not just blank
> space
>
> • Loading skeleton instead of spinner for better perceived performance

Confidential — For Development Use Only Page 38

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> CHAPTER 09
>
> **Phase-by-Phase** **Build** **Plan**
>
> **Build** **Philosophy**
>
> We build backend first, then frontend for each phase. Each phase is
> fully functional and testable in Postman before moving to the next. No
> phase is skipped.
>
> Start every session by saying: **'Start** **Phase** **N'** — I will
> generate all code completely.
>
> **Phase** **1** **—** **Project** **Setup** **+** **Auth** **+**
> **JWT**

||
||
||
||
||
||
||
||
||
||
||
||
||
||

||
||
||
||
||
||
||
||
||
||
||
||

> **Phase** **2** **—** **User** **Management** **+** **Geographic**
> **Setup**

Confidential — For Development Use Only Page 39

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

||
||
||
||
||
||
||
||
||
||
||
||

||
||
||
||
||
||
||
||
||

> **Phase** **3** **—** **Patient** **Management** **—** **OPD** **&**
> **IPD**

||
||
||
||
||
||
||
||
||
||
||

||
||
||
||
||
||
||
||
||
||
||

> **Phase** **4** **—** **Doctor** **Profiles** **+** **Appointment**
> **Scheduling**

Confidential — For Development Use Only Page 40

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

||
||
||
||
||
||
||
||
||

||
||
||
||
||
||
||
||
||

> **Phase** **5** **—** **Pharmacy** **+** **Inventory** **Management**

||
||
||
||
||
||
||
||
||
||

||
||
||
||
||
||
||
||
||

> **Phase** **6** **—** **Billing** **+** **Payments** **+** **Wallet**
> **+** **Commission**

Confidential — For Development Use Only Page 41

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

||
||
||
||
||
||
||
||
||
||
||
||

||
||
||
||
||
||
||
||
||
||

> **Phase** **7** **—** **HR** **+** **Attendance** **+** **Leave**
> **+** **Payroll**

||
||
||
||
||
||
||
||
||
||

||
||
||
||
||
||
||
||
||
||
||

> **Phase** **8** **—** **Reports** **+** **Analytics** **+** **Full**
> **Dashboard**

Confidential — For Development Use Only Page 42

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

||
||
||
||
||
||
||
||
||
||

||
||
||
||
||
||
||
||
||
||
||
||

Confidential — For Development Use Only Page 43

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> CHAPTER 10
>
> **Security** **Architecture**
>
> **Security** **Layers**

||
||
||
||
||
||
||
||
||
||
||
||
||
||
||

> **Environment** **Variables** **(Never** **Hardcode)**
>
> \# .env (add to .gitignore immediately) DB_USERNAME=root
> DB_PASSWORD=your_password_here DB_NAME=hospital_erp_db
> JWT_SECRET=your-256-bit-secret-key-here JWT_EXPIRY=900000
> JWT_REFRESH_EXPIRY=604800000 SERVER_PORT=8080
>
> *Add* *.env* *and* *application-local.yml* *to* *.gitignore* *BEFORE*
> *first* *git* *push.*

Confidential — For Development Use Only Page 44

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> CHAPTER 11
>
> **Folder** **Structure**
>
> **Backend** **—** **Spring** **Boot**
>
> hospital-erp-backend/ pom.xml
>
> src/main/ java/com/hospital/erp/
>
> HospitalErpApplication.java config/
>
> SecurityConfig.java CorsConfig.java AppConfig.java
>
> auth/ AuthController.java AuthService.java JwtService.java
> JwtAuthFilter.java
>
> dto/ (AuthRequest, AuthResponse, TokenRefreshRequest) user/
>
> User.java (entity) UserRepository.java UserService.java
> UserController.java RankGuard.java ScopeFilter.java dto/
>
> geographic/
>
> entities/ (State, District, Block, Center) repositories/
>
> services/ controllers/
>
> patient/ (Patient, OpdVisit, IpdAdmission, Bed) doctor/
> (DoctorProfile, DoctorSchedule) appointment/ (Appointment)
>
> pharmacy/ (Drug, DrugStock, DrugDispense, StockAlert) billing/
> (Invoice, InvoiceItem, Commission)
>
> wallet/ (Wallet, WalletTransaction)
>
> hr/ (StaffProfile, Attendance, LeaveRequest, LeaveBalance, Payroll)
> notification/ (Notification)
>
> reports/ (ReportController, ReportService) common/
>
> ApiResponse.java GlobalExceptionHandler.java CurrentUser.java
> (annotation) PageResponse.java
>
> resources/ application.yml
>
> application-local.yml (gitignored)

Confidential — For Development Use Only Page 45

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> **Frontend** **—** **React** **+** **Vite**
>
> hospital-erp-frontend/ index.html vite.config.js tailwind.config.js
> package.json
>
> src/ main.jsx App.jsx api/
>
> axios.js authApi.js userApi.js patientApi.js opdApi.js doctorApi.js
> pharmacyApi.js billingApi.js hrApi.js walletApi.js reportApi.js
>
> store/ authStore.js uiStore.js
>
> routes/
>
> AppRouter.jsx

← base instance + interceptors

← Zustand: user, token, permissions

← Zustand: sidebar collapsed, toasts

> ProtectedRoute.jsx RoleRoute.jsx
>
> layouts/ DashboardLayout.jsx Sidebar.jsx Topbar.jsx
>
> components/
>
> ui/ ← shadcn components
>
> DataTable.jsx PageHeader.jsx StatusBadge.jsx KpiCard.jsx
> PermissionMatrix.jsx BulkUpload.jsx ConfirmModal.jsx
>
> pages/ Login/
>
> Dashboard/ Users/ Patients/ OPD/
>
> IPD/ Doctors/ Appointments/ Pharmacy/ Billing/
>
> HR/

Confidential — For Development Use Only Page 46

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> Wallet/ Reports/

Confidential — For Development Use Only Page 47

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> CHAPTER 12
>
> **Deployment** **&** **DevOps**
>
> **Local** **Development** **Setup**

||
||
||
||
||
||
||
||
||

> **Git** **Branching** **Strategy**

||
||
||
||
||
||
||
||

> **Future** **Deployment** **(Post-Development)**

||
||
||
||
||
||
||

> **Important** **Reminders** **Before** **First** **Push**
>
> • Add to .gitignore: .env, application-local.yml, target/,
> node_modules/, \*.class
>
> • Never commit real credentials — use environment variables always

Confidential — For Development Use Only Page 48

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> • Set GitHub repo to PRIVATE until project is complete
>
> • Tag each phase completion: git tag v1.0-phase1, v1.0-phase2…

Confidential — For Development Use Only Page 49

**HOSPITAL** **ERP** **—** **Apana** **Swastha** **Kendra** Complete
Project Planning Document

> 29 Tables \| 11 Roles \| 8 Modules \| 8 Phases \| Full Stack
>
> Document generated on April 04, 2026 \| github.com/prince2404 \|
> Version 1.0

Confidential — For Development Use Only Page 50
