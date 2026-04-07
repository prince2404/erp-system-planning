export default function KpiCard({ title, value, tone = 'blue', helper }) {
  const tones = {
    blue: 'text-blue-600',
    purple: 'text-purple-600',
    emerald: 'text-emerald-600',
    amber: 'text-amber-600',
    rose: 'text-rose-600'
  };
  return (
    <div className="clinical-card p-5">
      <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">{title}</p>
      <p className={`mt-3 text-3xl font-semibold ${tones[tone] || tones.blue}`}>{value ?? '-'}</p>
      {helper ? <p className="mt-2 text-xs text-slate-500">{helper}</p> : null}
    </div>
  );
}
