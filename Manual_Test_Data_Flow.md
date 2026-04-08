# Manual Test Data Flow

Use this file to test the ERP manually from the UI after creating your first `SUPER_ADMIN`.

Important:

- These are reference values only. Nothing here is inserted automatically.
- IDs shown below are examples. Always use the actual ID returned by your UI tables after each create action.
- Enum values are case-sensitive. Use uppercase exactly as shown.
- If a step says "copy the ID", use the ID from your table list before moving to the next step.

## Goal

By the end of this flow you should be able to verify:

1. Geography and center creation
2. User creation
3. Doctor profile and schedule
4. Patient registration
5. OPD visit
6. IPD admission and discharge
7. Pharmacy stock and dispense
8. Billing and payment
9. HR and payroll
10. Wallet and reports

## Suggested Test Data

Use these names consistently:

```text
State: Punjab
District: Ludhiana
Block: Block A
Center: Apana Swastha Kendra Ludhiana

Doctor User: Dr Amit Kumar
Pharmacist User: Ravi Sharma
Receptionist User: Neha Verma
HR User: Sunita Mehra
Center Staff User: Mohan Lal

Patient: Raj Kumar
Drug: Paracetamol 500
```

---

## Phase A: Basic Master Setup

### Step 1: Create State

Go to `Centers -> Create State`

Enter:

```text
name: Punjab
code: PB
```

After save:

- Copy the created `stateId`
- Example: `stateId = 1`

### Step 2: Create District

Go to `Centers -> Create District`

Enter:

```text
name: Ludhiana
stateId: 1
```

After save:

- Copy the created `districtId`
- Example: `districtId = 1`

### Step 3: Create Block

Go to `Centers -> Create Block`

Enter:

```text
name: Block A
districtId: 1
```

After save:

- Copy the created `blockId`
- Example: `blockId = 1`

### Step 4: Create Center

Go to `Centers -> Create Center`

Enter:

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

After save:

- Copy the created `centerId`
- Example: `centerId = 1`

---

## Phase B: Permission Setup

Create a few permissions first.

Go to `Users -> Create Permission`

Create these one by one:

```text
module: PATIENT
action: READ
description: Can view patient records
```

```text
module: PATIENT
action: CREATE
description: Can register new patients
```

```text
module: OPD
action: UPDATE_STATUS
description: Can update OPD visit status
```

```text
module: PHARMACY
action: DISPENSE
description: Can dispense medicines
```

```text
module: BILLING
action: PROCESS_PAYMENT
description: Can record invoice payment
```

```text
module: HR
action: RUN_PAYROLL
description: Can generate payroll
```

---

## Phase C: Create Users

Go to `Users -> Create User`

### Step 5: Create Doctor User

```text
name: Dr Amit Kumar
email: doctor.amit@example.com
password: Doctor@123
phone: 9876500001
role: DOCTOR
scopeType: CENTER
scopeId: 1
centerId: 1
```

After save:

- Copy the doctor `userId`
- Example: `doctorUserId = 2`

### Step 6: Create Pharmacist User

```text
name: Ravi Sharma
email: ravi.pharma@example.com
password: Pharma@123
phone: 9876500002
role: PHARMACIST
scopeType: CENTER
scopeId: 1
centerId: 1
```

Copy:

- `pharmacistUserId`

### Step 7: Create Receptionist User

```text
name: Neha Verma
email: neha.reception@example.com
password: Reception@123
phone: 9876500003
role: RECEPTIONIST
scopeType: CENTER
scopeId: 1
centerId: 1
```

Copy:

- `receptionistUserId`

### Step 8: Create HR User

```text
name: Sunita Mehra
email: sunita.hr@example.com
password: HR@12345
phone: 9876500004
role: HR_MANAGER
scopeType: CENTER
scopeId: 1
centerId: 1
```

Copy:

- `hrUserId`

### Step 9: Create Center Staff User

```text
name: Mohan Lal
email: mohan.staff@example.com
password: Staff@123
phone: 9876500005
role: CENTER_STAFF
scopeType: CENTER
scopeId: 1
centerId: 1
```

---

## Phase D: Doctor Setup

### Step 10: Create Doctor Profile

Go to `Doctors -> Create Doctor Profile`

Enter:

```text
userId: 2
centerId: 1
specialization: General Medicine
qualification: MBBS
experienceYears: 8
consultationFee: 500
```

After save:

- Copy the doctor profile ID
- Example: `doctorProfileId = 1`

### Step 11: Add Doctor Schedule

Go to `Doctors -> Schedule JSON`

Use:

```json
[
  {
    "dayOfWeek": "MON",
    "startTime": "09:00:00",
    "endTime": "13:00:00",
    "slotDurationMins": 15,
    "maxPatients": 16,
    "active": true
  },
  {
    "dayOfWeek": "TUE",
    "startTime": "09:00:00",
    "endTime": "13:00:00",
    "slotDurationMins": 15,
    "maxPatients": 16,
    "active": true
  }
]
```

Note:

- If your page still uses `/doctors/1/schedule`, make sure the doctor profile ID really is `1`
- If not, replace the path with your actual doctor profile ID

---

## Phase E: Patient And OPD

### Step 12: Register Patient

Go to `Patients -> Register Patient`

Enter:

```text
name: Raj Kumar
phone: 9999911111
email: raj.kumar@example.com
gender: MALE
age: 35
bloodGroup: B+
centerId: 1
emergencyName: Sita Kumar
emergencyContact: 9999922222
address: House 10, Ludhiana
allergies: Penicillin
```

After save:

- Copy `patientId`
- Copy generated `UHID`

### Step 13: Create OPD Visit

Go to `OPD Queue -> New OPD Visit`

Enter:

```text
patientId: 1
doctorId: 2
centerId: 1
visitDate: 2026-04-08
fee: 500
symptoms: Fever and cough
```

After save:

- Copy the `opdVisitId`
- Check queue table for token number

### Step 14: Update OPD Status

Go to `OPD Queue -> Update Visit Status`

Enter:

```text
id: 1
status: COMPLETED
diagnosis: Viral fever
prescriptionNotes: Paracetamol 500 mg twice daily for 3 days
```

---

## Phase F: IPD And Beds

### Step 15: Create Bed

Go to `IPD & Beds -> Create Bed`

Enter:

```text
centerId: 1
ward: General
bedNumber: G-101
```

After save:

- Copy `bedId`

### Step 16: Admit Patient

Go to `IPD & Beds -> Admit Patient`

Enter:

```text
patientId: 1
doctorId: 2
centerId: 1
bedId: 1
dailyCharge: 1200
diagnosis: Dengue observation
```

After save:

- Copy `ipdAdmissionId`

### Step 17: Discharge Patient

Go to `IPD & Beds -> Discharge Patient`

Enter:

```text
id: 1
treatmentNotes: Stable, advised rest and fluids
```

Expected result:

- IPD status becomes `DISCHARGED`
- Bed becomes available again
- Draft IPD invoice should be created automatically

---

## Phase G: Appointment Test

### Step 18: Check Slots

Go to `Appointments`

Use:

```text
doctorId: 1
date: 2026-04-08
```

Make sure slots appear.

### Step 19: Book Appointment

Go to `Appointments -> Book Appointment`

Enter:

```text
patientId: 1
doctorId: 1
centerId: 1
appointmentDate: 2026-04-08
slotTime: 09:15:00
type: OPD
bookingFee: 100
notes: Follow-up check
```

Expected result:

- Appointment appears in appointment table
- Same slot should not be bookable again

---

## Phase H: Pharmacy

### Step 20: Add Drug

Go to `Pharmacy -> Add Drug`

Enter:

```text
name: Paracetamol 500
genericName: Paracetamol
category: Painkiller
unit: TABLET
hsnCode: 3004
manufacturer: ABC Pharma
```

After save:

- Copy `drugId`

### Step 21: Add Stock Batch

Go to `Pharmacy -> Add Stock Batch`

Enter:

```text
drugId: 1
centerId: 1
batchNumber: PCM-B001
expiryDate: 2027-04-30
quantity: 100
purchasePrice: 1.5
sellingPrice: 3
supplier: Local Distributor
```

### Step 22: Dispense Medicine

Go to `Pharmacy -> Dispense Medicine`

Enter:

```text
patientId: 1
opdVisitId: 1
drugId: 1
centerId: 1
quantity: 10
```

Expected result:

- Stock quantity decreases
- Dispense history exists
- If quantity goes too low later, stock alert should be created

---

## Phase I: Billing

### Step 23: Create Invoice

Go to `Billing -> Invoice JSON`

Use:

```json
{
  "patientId": 1,
  "centerId": 1,
  "type": "COMBINED",
  "discount": 0,
  "items": [
    {
      "description": "Consultation fee",
      "itemType": "CONSULTATION",
      "quantity": 1,
      "unitPrice": 500
    },
    {
      "description": "Paracetamol 500",
      "itemType": "MEDICINE",
      "quantity": 10,
      "unitPrice": 3
    }
  ]
}
```

After save:

- Copy `invoiceId`
- Copy `invoiceNumber`

### Step 24: Record Payment

Go to `Billing -> Record Payment`

Enter:

```text
id: 1
paymentMode: UPI
```

Expected result:

- Invoice status becomes `PAID`
- Wallet balance should update
- Wallet transaction should be created
- Commission record should be created

---

## Phase J: HR And Payroll

### Step 25: Create Staff Profile

Go to `HR & Payroll -> Create Staff Profile`

Use pharmacist or HR user. Example with pharmacist:

```text
userId: 3
department: Pharmacy
designation: Pharmacist
dateOfJoining: 2026-04-08
baseSalary: 25000
emergencyContact: 9999933333
emergencyName: Ramesh Sharma
```

### Step 26: Mark Attendance

Go to `HR & Payroll -> Attendance Bulk Mark JSON`

Use:

```json
[
  {
    "userId": 3,
    "centerId": 1,
    "date": "2026-04-08",
    "checkIn": "09:00:00",
    "checkOut": "17:00:00",
    "status": "PRESENT",
    "remarks": "Full day"
  },
  {
    "userId": 4,
    "centerId": 1,
    "date": "2026-04-08",
    "checkIn": "09:15:00",
    "checkOut": "17:00:00",
    "status": "PRESENT",
    "remarks": "Full day"
  }
]
```

### Step 27: Generate Payroll

Go to `HR & Payroll -> Generate Payroll`

Enter:

```text
centerId: 1
month: 4
year: 2026
deductions: 500
bonus: 1000
```

Expected result:

- Payroll records are created
- Net salary is calculated

---

## Phase K: Wallet And Reports

### Step 28: Check Wallet

Go to `Wallet`

Use:

```text
entityType: CENTER
entityId: 1
```

Expected result:

- Wallet balance should show invoice money after payment

### Step 29: Check Reports

Go to `Reports`

Use:

```text
centerId: 1
month: 4
year: 2026
groupBy: DAY
```

Check:

- Revenue report
- Inventory report
- HR summary
- Wallet movement

---

## If You Want To Test Role Login

After creating the users above, you can log out and test with:

### Doctor Login

```text
email: doctor.amit@example.com
password: Doctor@123
```

### Pharmacist Login

```text
email: ravi.pharma@example.com
password: Pharma@123
```

### Receptionist Login

```text
email: neha.reception@example.com
password: Reception@123
```

### HR Login

```text
email: sunita.hr@example.com
password: HR@12345
```

## Quick Troubleshooting

- `403` means your logged-in role is not allowed for that action.
- `400` usually means wrong input format or missing required field.
- `404` usually means you used an ID that does not exist.
- If ID values are not `1`, `2`, `3`, use whatever your system actually created.
- For center-level users, keep `scopeType: CENTER`, `scopeId: centerId`, and `centerId: centerId`.
