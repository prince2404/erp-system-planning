import { Inbox } from 'lucide-react';

export default function EmptyState({ icon: Icon = Inbox, title = 'No data found', description, action }) {
  return (
    <div className="flex flex-col items-center justify-center py-12 text-center animate-fade-in">
      <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-ash-100">
        <Icon size={24} className="text-ash-400" />
      </div>
      <h3 className="mt-4 text-sm font-semibold text-ash-700">{title}</h3>
      {description && <p className="mt-1 max-w-sm text-sm text-ash-500">{description}</p>}
      {action && <div className="mt-4">{action}</div>}
    </div>
  );
}
