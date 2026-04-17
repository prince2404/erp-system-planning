import {
  Activity,
  BadgeIndianRupee,
  Bed,
  CalendarDays,
  ClipboardList,
  Gauge,
  HeartPulse,
  MapPinned,
  Pill,
  Settings,
  Stethoscope,
  Users,
  CircleDollarSign,
  BarChart3
} from 'lucide-react';

export const navigation = [
  {
    to: '/', label: 'Dashboard', icon: Gauge, section: 'main',
    roles: null // visible to all
  },
  {
    to: '/users', label: 'User Management', icon: Users, section: 'admin',
    roles: ['SUPER_ADMIN', 'ADMIN', 'STATE_MANAGER', 'DISTRICT_MANAGER', 'BLOCK_MANAGER', 'CENTER_MANAGER']
  },
  {
    to: '/centers', label: 'Health Centers', icon: MapPinned, section: 'admin',
    roles: ['SUPER_ADMIN', 'ADMIN', 'STATE_MANAGER', 'DISTRICT_MANAGER', 'BLOCK_MANAGER', 'CENTER_MANAGER']
  },
  {
    to: '/patients', label: 'Patients', icon: HeartPulse, section: 'clinical',
    roles: ['SUPER_ADMIN', 'ADMIN', 'DOCTOR', 'RECEPTIONIST', 'PHARMACIST', 'BLOCK_MANAGER', 'HR_MANAGER']
  },
  {
    to: '/opd', label: 'OPD Queue', icon: ClipboardList, section: 'clinical',
    roles: ['SUPER_ADMIN', 'ADMIN', 'DOCTOR', 'RECEPTIONIST', 'BLOCK_MANAGER']
  },
  {
    to: '/ipd', label: 'IPD & Beds', icon: Bed, section: 'clinical',
    roles: ['SUPER_ADMIN', 'ADMIN', 'DOCTOR', 'BLOCK_MANAGER']
  },
  {
    to: '/doctors', label: 'Doctors', icon: Stethoscope, section: 'clinical',
    roles: ['SUPER_ADMIN', 'ADMIN', 'DOCTOR', 'RECEPTIONIST', 'BLOCK_MANAGER']
  },
  {
    to: '/appointments', label: 'Appointments', icon: CalendarDays, section: 'clinical',
    roles: ['SUPER_ADMIN', 'ADMIN', 'DOCTOR', 'RECEPTIONIST', 'BLOCK_MANAGER']
  },
  {
    to: '/pharmacy', label: 'Pharmacy', icon: Pill, section: 'operations',
    roles: ['SUPER_ADMIN', 'ADMIN', 'PHARMACIST', 'DOCTOR', 'BLOCK_MANAGER']
  },
  {
    to: '/billing', label: 'Billing', icon: BadgeIndianRupee, section: 'operations',
    roles: ['SUPER_ADMIN', 'ADMIN', 'RECEPTIONIST', 'BLOCK_MANAGER']
  },
  {
    to: '/hr', label: 'HR & Payroll', icon: Activity, section: 'operations',
    roles: ['SUPER_ADMIN', 'ADMIN', 'HR_MANAGER', 'BLOCK_MANAGER']
  },
  {
    to: '/wallet', label: 'Wallet', icon: CircleDollarSign, section: 'operations',
    roles: ['SUPER_ADMIN', 'ADMIN', 'BLOCK_MANAGER', 'ASSOCIATE', 'RECEPTIONIST']
  },
  {
    to: '/reports', label: 'Reports', icon: BarChart3, section: 'analytics',
    roles: ['SUPER_ADMIN', 'ADMIN', 'STATE_MANAGER', 'DISTRICT_MANAGER', 'BLOCK_MANAGER', 'CENTER_MANAGER']
  },
  {
    to: '/profile', label: 'My Profile', icon: Settings, section: 'account',
    roles: null
  }
];

export const sectionLabels = {
  main: 'Overview',
  admin: 'Administration',
  clinical: 'Clinical',
  operations: 'Operations',
  analytics: 'Analytics',
  account: 'Account'
};

export function getVisibleNavigation(userRole) {
  return navigation.filter(item => item.roles === null || item.roles.includes(userRole));
}

export function getGroupedNavigation(userRole) {
  const visible = getVisibleNavigation(userRole);
  const groups = {};
  for (const item of visible) {
    const section = item.section || 'main';
    if (!groups[section]) groups[section] = [];
    groups[section].push(item);
  }
  return groups;
}

export const roles = [
  'SUPER_ADMIN', 'ADMIN', 'STATE_MANAGER', 'DISTRICT_MANAGER', 'BLOCK_MANAGER',
  'CENTER_MANAGER', 'HR_MANAGER', 'DOCTOR', 'PHARMACIST', 'RECEPTIONIST', 'ASSOCIATE', 'CENTER_STAFF', 'PATIENT'
];

export const scopeTypes = ['SYSTEM', 'STATE', 'DISTRICT', 'BLOCK', 'CENTER', 'SELF'];
