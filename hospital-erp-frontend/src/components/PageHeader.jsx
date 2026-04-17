export default function PageHeader({ eyebrow, title, description, action }) {
  return (
    <div className="mb-6 flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between animate-slide-up">
      <div>
        {eyebrow && (
          <p className="mb-1 text-xs font-bold uppercase tracking-widest text-brand-600">{eyebrow}</p>
        )}
        <h1 className="text-xl font-bold text-ash-900 sm:text-2xl">{title}</h1>
        {description && (
          <p className="mt-1 max-w-2xl text-sm text-ash-500">{description}</p>
        )}
      </div>
      {action && <div className="shrink-0">{action}</div>}
    </div>
  );
}
