import {
  Activity,
  BadgeIndianRupee,
  Bed,
  CalendarDays,
  CircleDollarSign,
  ClipboardList,
  Gauge,
  HeartPulse,
  MapPinned,
  Pill,
  Stethoscope,
  Users
} from 'lucide-react';

export const navigation = [
  { to: '/', label: 'Dashboard', icon: Gauge },
  { to: '/users', label: 'Users', icon: Users },
  { to: '/centers', label: 'Centers', icon: MapPinned },
  { to: '/patients', label: 'Patients', icon: HeartPulse },
  { to: '/opd', label: 'OPD Queue', icon: ClipboardList },
  { to: '/ipd', label: 'IPD & Beds', icon: Bed },
  { to: '/doctors', label: 'Doctors', icon: Stethoscope },
  { to: '/appointments', label: 'Appointments', icon: CalendarDays },
  { to: '/pharmacy', label: 'Pharmacy', icon: Pill },
  { to: '/billing', label: 'Billing', icon: BadgeIndianRupee },
  { to: '/hr', label: 'HR & Payroll', icon: Activity },
  { to: '/wallet', label: 'Wallet', icon: CircleDollarSign },
  { to: '/reports', label: 'Reports', icon: Gauge }
];

export const roles = [
  'SUPER_ADMIN',
  'ADMIN',
  'STATE_MANAGER',
  'DISTRICT_MANAGER',
  'BLOCK_MANAGER',
  'HR_MANAGER',
  'DOCTOR',
  'PHARMACIST',
  'RECEPTIONIST',
  'CENTER_STAFF',
  'PATIENT'
];

export const scopeTypes = ['SYSTEM', 'STATE', 'DISTRICT', 'BLOCK', 'CENTER', 'SELF'];
