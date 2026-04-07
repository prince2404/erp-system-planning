export default function PageHeader({ title, eyebrow, description, action }) {
  return (
    <div className="mb-6 flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
      <div>
        {eyebrow ? <p className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-600">{eyebrow}</p> : null}
        <h1 className="mt-1 text-2xl font-semibold text-slate-950">{title}</h1>
        {description ? <p className="mt-2 max-w-3xl text-sm text-slate-500">{description}</p> : null}
      </div>
      {action ? <div>{action}</div> : null}
    </div>
  );
}
