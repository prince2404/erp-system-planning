const tones = {
  PAID: 'bg-emerald-50 text-emerald-700 ring-emerald-200',
  PENDING: 'bg-amber-50 text-amber-700 ring-amber-200',
  APPROVED: 'bg-emerald-50 text-emerald-700 ring-emerald-200',
  REJECTED: 'bg-rose-50 text-rose-700 ring-rose-200',
  CANCELLED: 'bg-rose-50 text-rose-700 ring-rose-200',
  COMPLETED: 'bg-blue-50 text-blue-700 ring-blue-200',
  ADMITTED: 'bg-purple-50 text-purple-700 ring-purple-200',
  DISCHARGED: 'bg-emerald-50 text-emerald-700 ring-emerald-200',
  ACTIVE: 'bg-emerald-50 text-emerald-700 ring-emerald-200'
};

export default function StatusBadge({ value }) {
  const label = value || 'UNKNOWN';
  return (
    <span className={`inline-flex rounded-full px-2.5 py-1 text-xs font-semibold ring-1 ${tones[label] || 'bg-slate-50 text-slate-700 ring-slate-200'}`}>
      {label}
    </span>
  );
}
