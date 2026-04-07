export default function SectionCard({ title, description, children, accent = 'blue' }) {
  const accents = {
    blue: 'bg-blue-50/60',
    purple: 'bg-purple-50/60',
    emerald: 'bg-emerald-50/60',
    amber: 'bg-amber-50/60',
    slate: 'bg-slate-50/60'
  };
  return (
    <section className="clinical-card overflow-hidden">
      <div className={`border-b border-slate-100 px-5 py-4 ${accents[accent] || accents.blue}`}>
        <h2 className="text-base font-semibold text-slate-950">{title}</h2>
        {description ? <p className="mt-1 text-sm text-slate-500">{description}</p> : null}
      </div>
      <div className="p-5">{children}</div>
    </section>
  );
}
