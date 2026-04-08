# Hospital ERP Field Reference

This file is a reference for what to type in the frontend forms. These are examples only. They do not add dummy data automatically.

## Important Rules

- Most enum values are case-sensitive. Use uppercase exactly as shown.
- IDs must already exist in your database. Example: `centerId: 1` only works if center ID `1` exists.
- Empty means leave the field blank in the form.
- Dates use `YYYY-MM-DD`. Example: `2026-04-07`.
- Times use `HH:mm:ss`. Example: `09:30:00`.
- Money fields accept numbers. Example: `500`, `1250.75`.
- Create real setup in this order: State, District, Block, Center, Users, Doctor Profile, Patient, OPD/IPD/Appointments, Pharmacy, Billing, HR.

## Allowed Role Values

Use exactly one of these:

```text
SUPER_ADMIN
ADMIN
STATE_MANAGER
DISTRICT_MANAGER
BLOCK_MANAGER
HR_MANAGER
DOCTOR
PHARMACIST
RECEPTIONIST
CENTER_STAFF
PATIENT
```

## Allowed Scope Type Values

Use exactly one of these:

```text
SYSTEM
STATE
DISTRICT
BLOCK
CENTER
SELF
```

## User Scope Guide

`scopeType` controls how much data the user can access.

`scopeId` stores the ID for that scope. For example, if `scopeType` is `DISTRICT`, then `scopeId` must be a district ID.

`centerId` attaches a user to a center. Use it for center-level staff like doctors, pharmacists, receptionists, HR managers, and center staff.

| Role | scopeType | scopeId | centerId | Example |
|---|---|---|---|---|
| SUPER_ADMIN | SYSTEM | empty | empty | Full system access |
| ADMIN | SYSTEM | empty | empty | Admin across system |
| STATE_MANAGER | STATE | state ID | empty | `scopeId: 1` for state ID 1 |
| DISTRICT_MANAGER | DISTRICT | district ID | empty | `scopeId: 5` for district ID 5 |
| BLOCK_MANAGER | BLOCK | block ID | empty | `scopeId: 12` for block ID 12 |
| HR_MANAGER | CENTER | center ID | same center ID | `scopeId: 3`, `centerId: 3` |
| DOCTOR | CENTER | center ID | same center ID | `scopeId: 3`, `centerId: 3` |
| PHARMACIST | CENTER | center ID | same center ID | `scopeId: 3`, `centerId: 3` |
| RECEPTIONIST | CENTER | center ID | same center ID | `scopeId: 3`, `centerId: 3` |
| CENTER_STAFF | CENTER | center ID | same center ID | `scopeId: 3`, `centerId: 3` |
| PATIENT | SELF | empty for now | optional | Usually created via patient registration, not user form |

Example doctor user:

```text
name: Dr Amit Kumar
email: doctor1@example.com
password: StrongPassword123
phone: 9876543210
role: DOCTOR
scopeType: CENTER
scopeId: 3
centerId: 3
```

Example district manager:

```text
name: District Manager Ludhiana
email: district.manager@example.com
password: StrongPassword123
phone: 9876543210
role: DISTRICT_MANAGER
scopeType: DISTRICT
scopeId: 5
centerId: empty
```

## Permission Catalog

Create permissions with uppercase `module` and uppercase `action`. The backend stores these exactly as typed, so treat them as case-sensitive.

Recommended modules and actions:

| Module | Allowed Actions |
|---|---|
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

Permission examples:

```text
module: PATIENT
action: READ
description: Can view patient records
```

```text
module: PHARMACY
action: DISPENSE
description: Can dispense medicines to patients
```

```text
module: BILLING
action: PROCESS_PAYMENT
description: Can record invoice payments
```

```text
module: HR
action: RUN_PAYROLL
description: Can generate monthly payroll
```

## Geographic And Center Setup

Create geographic records first because users and patients depend on centers.

### State

Fields:

| Field | Meaning | Example |
|---|---|---|
| name | State name | Punjab |
| code | Short unique code | PB |

Example:

```text
name: Punjab
code: PB
```

### District

Fields:

| Field | Meaning | Example |
|---|---|---|
| name | District name | Ludhiana |
| stateId | Existing state ID | 1 |

Example:

```text
name: Ludhiana
stateId: 1
```

### Block

Fields:

| Field | Meaning | Example |
|---|---|---|
| name | Block name | Block A |
| districtId | Existing district ID | 1 |

Example:

```text
name: Block A
districtId: 1
```

### Center

Fields:

| Field | Meaning | Example |
|---|---|---|
| name | Clinic or hospital name | Apana Swastha Kendra Ludhiana |
| city | City name | Ludhiana |
| stateId | Existing state ID | 1 |
| districtId | Existing district ID | 1 |
| blockId | Existing block ID | 1 |
| phone | Center phone | 9876543210 |
| email | Center email | ludhiana.center@example.com |
| address | Full address | Main Road, Ludhiana |

Example:

```text
name: Apana Swastha Kendra Ludhiana
city: Ludhiana
stateId: 1
districtId: 1
blockId: 1
phone: 9876543210
email: ludhiana.center@example.com
address: Main Road, Ludhiana
```

## Patient Management

Allowed gender values:

```text
MALE
FEMALE
OTHER
```

Fields:

| Field | Meaning | Example |
|---|---|---|
| name | Patient full name | Raj Kumar |
| phone | Patient phone, required | 9876543210 |
| email | Optional email | patient@example.com |
| gender | Use allowed value | MALE |
| age | Age in years | 35 |
| dateOfBirth | Date of birth | 1991-04-07 |
| bloodGroup | Blood group | B+ |
| centerId | Existing center ID | 1 |
| emergencyName | Emergency contact name | Sita Kumar |
| emergencyContact | Emergency phone | 9876500000 |
| address | Patient address | House 10, Ludhiana |
| allergies | Allergy notes | Penicillin |

Example:

```text
name: Raj Kumar
phone: 9876543210
gender: MALE
age: 35
bloodGroup: B+
centerId: 1
emergencyName: Sita Kumar
emergencyContact: 9876500000
address: House 10, Ludhiana
allergies: Penicillin
```

## OPD Queue

Allowed OPD status values:

```text
WAITING
IN_PROGRESS
COMPLETED
CANCELLED
```

### New OPD Visit

Fields:

| Field | Meaning | Example |
|---|---|---|
| patientId | Existing patient ID | 1 |
| doctorId | Existing doctor user ID, optional | 2 |
| centerId | Existing center ID | 1 |
| visitDate | Visit date, blank means today | 2026-04-07 |
| fee | Consultation fee | 500 |
| symptoms | Patient symptoms | Fever and cough |

Example:

```text
patientId: 1
doctorId: 2
centerId: 1
visitDate: 2026-04-07
fee: 500
symptoms: Fever and cough
```

### Update Visit Status

Fields:

| Field | Meaning | Example |
|---|---|---|
| OPD Visit ID | ID in URL/form | 1 |
| status | Use allowed status | COMPLETED |
| diagnosis | Doctor diagnosis | Viral fever |
| prescriptionNotes | Doctor prescription notes | Paracetamol 500 mg twice daily |

Example:

```text
id: 1
status: COMPLETED
diagnosis: Viral fever
prescriptionNotes: Paracetamol 500 mg twice daily
```

## IPD And Beds

Allowed IPD status values are mostly system-managed:

```text
ADMITTED
DISCHARGED
TRANSFERRED
```

### Create Bed

Fields:

| Field | Meaning | Example |
|---|---|---|
| centerId | Existing center ID | 1 |
| ward | Ward name | General |
| bedNumber | Unique bed number in center | G-101 |

Example:

```text
centerId: 1
ward: General
bedNumber: G-101
```

### Admit Patient

Fields:

| Field | Meaning | Example |
|---|---|---|
| patientId | Existing patient ID | 1 |
| doctorId | Existing doctor user ID, optional | 2 |
| centerId | Existing center ID | 1 |
| bedId | Existing available bed ID | 1 |
| dailyCharge | Per-day bed charge | 1200 |
| diagnosis | Initial diagnosis | Dengue observation |

Example:

```text
patientId: 1
doctorId: 2
centerId: 1
bedId: 1
dailyCharge: 1200
diagnosis: Dengue observation
```

### Discharge Patient

Fields:

| Field | Meaning | Example |
|---|---|---|
| Admission ID | Existing IPD admission ID | 1 |
| treatmentNotes | Final notes | Stable, follow up after 7 days |

Example:

```text
id: 1
treatmentNotes: Stable, follow up after 7 days
```

## Doctors And Schedules

Allowed week day values:

```text
MON
TUE
WED
THU
FRI
SAT
SUN
```

### Create Doctor Profile

Fields:

| Field | Meaning | Example |
|---|---|---|
| userId | Existing user ID with role DOCTOR | 2 |
| centerId | Existing center ID | 1 |
| specialization | Doctor specialty | General Medicine |
| qualification | Doctor qualification | MBBS |
| experienceYears | Years of experience | 8 |
| consultationFee | Default fee | 500 |

Example:

```text
userId: 2
centerId: 1
specialization: General Medicine
qualification: MBBS
experienceYears: 8
consultationFee: 500
```

### Schedule JSON

Use uppercase day values and time as `HH:mm:ss`.

Example JSON:

```json
[
  {
    "dayOfWeek": "MON",
    "startTime": "09:00:00",
    "endTime": "13:00:00",
    "slotDurationMins": 15,
    "maxPatients": 16,
    "active": true
  }
]
```

## Appointments

Allowed appointment type values:

```text
OPD
FOLLOW_UP
EMERGENCY
```

Allowed appointment status values:

```text
BOOKED
CONFIRMED
CANCELLED
COMPLETED
NO_SHOW
```

Fields:

| Field | Meaning | Example |
|---|---|---|
| patientId | Existing patient ID | 1 |
| doctorId | Existing doctor profile ID, not user ID | 1 |
| centerId | Existing center ID | 1 |
| appointmentDate | Appointment date | 2026-04-08 |
| slotTime | Must match an available slot | 09:15:00 |
| type | Use allowed type | OPD |
| bookingFee | Optional fee | 100 |
| notes | Appointment notes | Follow-up visit |

Example:

```text
patientId: 1
doctorId: 1
centerId: 1
appointmentDate: 2026-04-08
slotTime: 09:15:00
type: OPD
bookingFee: 100
notes: Follow-up visit
```

## Pharmacy

Allowed drug unit values:

```text
TABLET
SYRUP
INJECTION
CAPSULE
CREAM
DROPS
OTHER
```

### Add Drug

Fields:

| Field | Meaning | Example |
|---|---|---|
| name | Brand or medicine name | Paracetamol 500 |
| genericName | Generic name | Paracetamol |
| category | Drug category | Painkiller |
| unit | Use allowed unit | TABLET |
| hsnCode | HSN code | 3004 |
| manufacturer | Manufacturer name | ABC Pharma |

Example:

```text
name: Paracetamol 500
genericName: Paracetamol
category: Painkiller
unit: TABLET
hsnCode: 3004
manufacturer: ABC Pharma
```

### Add Stock Batch

Fields:

| Field | Meaning | Example |
|---|---|---|
| drugId | Existing drug ID | 1 |
| centerId | Existing center ID | 1 |
| batchNumber | Batch code | PCM-B001 |
| expiryDate | Expiry date | 2027-04-30 |
| quantity | Quantity received | 100 |
| purchasePrice | Cost per unit | 1.50 |
| sellingPrice | Sale price per unit | 3 |
| supplier | Supplier name | Local Distributor |

Example:

```text
drugId: 1
centerId: 1
batchNumber: PCM-B001
expiryDate: 2027-04-30
quantity: 100
purchasePrice: 1.50
sellingPrice: 3
supplier: Local Distributor
```

### Dispense Medicine

Fields:

| Field | Meaning | Example |
|---|---|---|
| patientId | Existing patient ID | 1 |
| opdVisitId | Existing OPD visit ID, optional | 1 |
| drugId | Existing drug ID | 1 |
| centerId | Existing center ID | 1 |
| quantity | Quantity to dispense | 10 |

Example:

```text
patientId: 1
opdVisitId: 1
drugId: 1
centerId: 1
quantity: 10
```

## Billing

Allowed invoice type values:

```text
OPD
IPD
PHARMACY
COMBINED
```

Allowed invoice item type values:

```text
CONSULTATION
MEDICINE
PROCEDURE
BED_CHARGE
LAB_TEST
OTHER
```

Allowed payment mode values:

```text
CASH
UPI
CARD
INSURANCE
```

### Invoice JSON

Example JSON:

```json
{
  "patientId": 1,
  "centerId": 1,
  "type": "OPD",
  "discount": 0,
  "items": [
    {
      "description": "Consultation fee",
      "itemType": "CONSULTATION",
      "quantity": 1,
      "unitPrice": 500
    }
  ]
}
```

Medicine items automatically get pharmacy GST calculation:

```json
{
  "patientId": 1,
  "centerId": 1,
  "type": "PHARMACY",
  "discount": 0,
  "items": [
    {
      "description": "Paracetamol 500",
      "itemType": "MEDICINE",
      "quantity": 10,
      "unitPrice": 3
    }
  ]
}
```

### Record Payment

Fields:

| Field | Meaning | Example |
|---|---|---|
| id | Existing invoice ID | 1 |
| paymentMode | Use allowed payment mode | UPI |
| insuranceProvider | Only for insurance | empty |
| insuranceClaimId | Only for insurance | empty |

Example:

```text
id: 1
paymentMode: UPI
```

## HR And Payroll

Allowed attendance status values:

```text
PRESENT
ABSENT
HALF_DAY
ON_LEAVE
HOLIDAY
```

Allowed leave type values:

```text
SICK
CASUAL
EARNED
```

Allowed leave action values:

```text
APPROVED
REJECTED
```

### Create Staff Profile

Fields:

| Field | Meaning | Example |
|---|---|---|
| userId | Existing staff user ID | 4 |
| department | Department name | Pharmacy |
| designation | Job title | Pharmacist |
| dateOfJoining | Joining date | 2026-04-07 |
| baseSalary | Monthly salary | 25000 |
| emergencyContact | Emergency phone | 9876500000 |
| emergencyName | Emergency contact name | Ramesh Kumar |

Example:

```text
userId: 4
department: Pharmacy
designation: Pharmacist
dateOfJoining: 2026-04-07
baseSalary: 25000
emergencyContact: 9876500000
emergencyName: Ramesh Kumar
```

### Attendance Bulk Mark JSON

Example JSON:

```json
[
  {
    "userId": 4,
    "centerId": 1,
    "date": "2026-04-07",
    "checkIn": "09:00:00",
    "checkOut": "17:00:00",
    "status": "PRESENT",
    "remarks": "Full day"
  }
]
```

### Apply Leave

Example:

```text
leaveType: SICK
fromDate: 2026-04-10
toDate: 2026-04-11
reason: Fever
```

### Approve Or Reject Leave

Example:

```text
id: 1
status: APPROVED
```

### Generate Payroll

Fields:

| Field | Meaning | Example |
|---|---|---|
| centerId | Existing center ID | 1 |
| month | 1 to 12 | 4 |
| year | Year | 2026 |
| deductions | Optional deduction | 500 |
| bonus | Optional bonus | 1000 |

Example:

```text
centerId: 1
month: 4
year: 2026
deductions: 500
bonus: 1000
```

## Wallet

Allowed wallet entity type values:

```text
CENTER
BLOCK
DISTRICT
STATE
```

Fields:

| Field | Meaning | Example |
|---|---|---|
| entityType | Which level wallet to view | CENTER |
| entityId | ID of that entity | 1 |
| walletId | Wallet ID for transaction history | 1 |

Example:

```text
entityType: CENTER
entityId: 1
walletId: 1
```

Wallet balance and transactions are usually created automatically after invoice payment/refund.

## Reports

Reports mostly need filters.

Fields:

| Field | Meaning | Example |
|---|---|---|
| centerId | Existing center ID | 1 |
| month | Month for HR report | 4 |
| year | Year for HR report | 2026 |
| groupBy | Revenue grouping | DAY |

Allowed revenue group values:

```text
DAY
WEEK
MONTH
```

Example:

```text
centerId: 1
month: 4
year: 2026
groupBy: DAY
```

## Common ID Confusion

- `doctorId` in OPD visit means doctor user ID.
- `doctorId` in appointment booking means doctor profile ID.
- `userId` in staff profile means user ID.
- `patientId` means patient table ID, not UHID.
- `centerId` means center table ID.
- `scopeId` depends on `scopeType`.

## Recommended First Test Flow

1. Create `State`.
2. Create `District` using `stateId`.
3. Create `Block` using `districtId`.
4. Create `Center` using `stateId`, `districtId`, and `blockId`.
5. Create a `DOCTOR` user with `scopeType: CENTER`, `scopeId: centerId`, `centerId: centerId`.
6. Create a doctor profile using the doctor user ID.
7. Create a schedule for the doctor profile.
8. Register a patient.
9. Create OPD visit for the patient.
10. Create drug and stock if pharmacy is needed.
11. Create invoice and record payment.
