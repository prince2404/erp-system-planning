import { roles, scopeTypes } from '../../lib/navigation.js';

export const roleOptions = roles;
export const scopeOptions = scopeTypes;
export const genderOptions = ['MALE', 'FEMALE', 'OTHER'];
export const visitStatuses = ['WAITING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'];
export const invoiceTypes = ['OPD', 'IPD', 'PHARMACY', 'COMBINED'];
export const paymentModes = ['CASH', 'UPI', 'CARD', 'INSURANCE'];
export const drugUnits = ['TABLET', 'SYRUP', 'INJECTION', 'CAPSULE', 'CREAM', 'DROPS', 'OTHER'];
export const appointmentTypes = ['OPD', 'FOLLOW_UP', 'EMERGENCY'];
export const appointmentStatuses = ['BOOKED', 'CONFIRMED', 'CANCELLED', 'COMPLETED', 'NO_SHOW'];
export const leaveTypes = ['SICK', 'CASUAL', 'EARNED'];
export const leaveStatuses = ['APPROVED', 'REJECTED'];
export const attendanceStatuses = ['PRESENT', 'ABSENT', 'HALF_DAY', 'ON_LEAVE', 'HOLIDAY'];
export const weekDays = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN'];
