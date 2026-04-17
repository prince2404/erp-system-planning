export function Skeleton({ className = '', ...props }) {
  return <div className={`animate-pulse rounded-xl bg-ash-200/60 ${className}`} {...props} />;
}

export function SkeletonCard() {
  return (
    <div className="card p-5 space-y-4">
      <Skeleton className="h-4 w-1/3" />
      <Skeleton className="h-3 w-2/3" />
      <div className="space-y-2 pt-2">
        <Skeleton className="h-10 w-full" />
        <Skeleton className="h-10 w-full" />
        <Skeleton className="h-10 w-3/4" />
      </div>
    </div>
  );
}

export function SkeletonTable({ rows = 5 }) {
  return (
    <div className="card overflow-hidden">
      <div className="border-b border-ash-100 p-4">
        <Skeleton className="h-5 w-1/4" />
      </div>
      <div className="divide-y divide-ash-100">
        {Array.from({ length: rows }).map((_, i) => (
          <div key={i} className="flex gap-4 p-4">
            <Skeleton className="h-4 w-1/5" />
            <Skeleton className="h-4 w-1/4" />
            <Skeleton className="h-4 w-1/6" />
            <Skeleton className="h-4 w-1/5" />
          </div>
        ))}
      </div>
    </div>
  );
}

export function SkeletonKpi() {
  return (
    <div className="card p-5">
      <Skeleton className="h-3 w-1/2 mb-3" />
      <Skeleton className="h-8 w-2/3" />
    </div>
  );
}
