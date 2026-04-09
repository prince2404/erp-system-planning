# Phase 1 Test Flow

## 1. Sign In

- Log in as your existing `SUPER_ADMIN`
- If you created a new user, that user should be redirected to `/profile` after first login until password is changed

## 2. Create Geography

- Go to `Centers`
- Create one `State`
- Create one `District` under that state
- Create one `Block` under that district

## 3. Create Center

- On the same `Centers` page, choose the state, district, and block from dropdowns
- Create the center
- Confirm the generated center code appears in the center list

## 4. Create Users

- Go to `Users`
- Choose a role such as `CENTER_MANAGER`, `DOCTOR`, `PHARMACIST`, `RECEPTIONIST`, or `ASSOCIATE`
- The page auto-applies default permissions for that role
- Toggle any extra permissions you want
- Submit the form

Expected result:

- a temporary password is shown on-screen
- notification status rows are shown
- the user appears in the users table

## 5. Verify New User Onboarding

- Log out
- Log in with the created user's email and temporary password
- You should land on `Profile`
- Change the password
- Fill address and bank details

## 6. Verify Contact Details

- On `Profile`, request email verification
- Request phone verification
- In `LOG_ONLY` mode, the OTP preview is shown on the page
- Enter the codes and confirm

Expected result:

- `Email verified` becomes `Yes`
- `Phone verified` becomes `Yes`
- `Profile completed` becomes `Yes` once required profile and bank fields are filled

## 7. When Real Providers Are Ready

- keep the same APIs and screens
- replace `EMAIL_DELIVERY_MODE=LOG_ONLY` and `SMS_DELIVERY_MODE=LOG_ONLY` with real provider-backed implementations later
