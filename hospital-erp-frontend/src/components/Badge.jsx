const roleColors = {
  SUPER_ADMIN: 'bg-brand-100 text-brand-800 border-brand-200',
  ADMIN: 'bg-purple-100 text-purple-800 border-purple-200',
  STATE_MANAGER: 'bg-emerald-100 text-emerald-800 border-emerald-200',
  DISTRICT_MANAGER: 'bg-teal-100 text-teal-800 border-teal-200',
  BLOCK_MANAGER: 'bg-cyan-100 text-cyan-800 border-cyan-200',
  HR_MANAGER: 'bg-orange-100 text-orange-800 border-orange-200',
  DOCTOR: 'bg-blue-100 text-blue-800 border-blue-200',
  PHARMACIST: 'bg-lime-100 text-lime-800 border-lime-200',
  RECEPTIONIST: 'bg-pink-100 text-pink-800 border-pink-200',
  ASSOCIATE: 'bg-amber-100 text-amber-800 border-amber-200',
  CENTER_STAFF: 'bg-gray-100 text-gray-800 border-gray-200',
  PATIENT: 'bg-rose-100 text-rose-800 border-rose-200'
};

const statusColors = {
  true: 'badge-green',
  false: 'badge-red',
  ACTIVE: 'badge-green',
  INACTIVE: 'badge-red',
  WAITING: 'badge-amber',
  IN_PROGRESS: 'badge-blue',
  COMPLETED: 'badge-green',
  CANCELLED: 'badge-red',
  NO_SHOW: 'badge-gray',
  PENDING: 'badge-amber',
  PAID: 'badge-green',
  REFUNDED: 'badge-purple',
  ADMITTED: 'badge-blue',
  DISCHARGED: 'badge-green',
  LOW_STOCK: 'badge-amber',
  OUT_OF_STOCK: 'badge-red',
  EXPIRY_SOON: 'badge-amber'
};

export function RoleBadge({ role }) {
  const color = roleColors[role] || 'bg-ash-100 text-ash-700 border-ash-200';
  const label = (role || '').replace(/_/g, ' ');
  return <span className={`badge border ${color}`}>{label}</span>;
}

export function StatusBadge({ value, label }) {
  const display = label || String(value);
  const colorClass = statusColors[String(value)] || 'badge-gray';
  return <span className={`badge ${colorClass}`}>{display.replace(/_/g, ' ')}</span>;
}

export function ActiveBadge({ active }) {
  return active
    ? <span className="badge badge-green">Active</span>
    : <span className="badge badge-red">Inactive</span>;
}

export function VerifiedBadge({ verified, label }) {
  return verified
    ? <span className="badge badge-green">✓ {label}</span>
    : <span className="badge badge-gray">✗ {label}</span>;
}
