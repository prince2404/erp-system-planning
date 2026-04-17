export default function SectionCard({ title, description, action, children, className = '' }) {
  return (
    <div className={`card overflow-hidden ${className}`}>
      {(title || description || action) && (
        <div className="flex items-start justify-between border-b border-ash-100 px-5 py-4">
          <div>
            {title && <h2 className="text-base font-semibold text-ash-900">{title}</h2>}
            {description && <p className="mt-0.5 text-sm text-ash-500">{description}</p>}
          </div>
          {action && <div className="shrink-0">{action}</div>}
        </div>
      )}
      <div className="p-5">{children}</div>
    </div>
  );
}
