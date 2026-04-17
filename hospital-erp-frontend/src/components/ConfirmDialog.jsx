import { AlertTriangle } from 'lucide-react';

export default function ConfirmDialog({ open, onClose, onConfirm, title, message, confirmText = 'Confirm', variant = 'danger', loading }) {
  if (!open) return null;

  const btnClass = variant === 'danger' ? 'btn-danger' : 'btn-primary';

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div className="fixed inset-0 bg-ash-950/50 backdrop-blur-sm animate-fade-in" onClick={onClose} />
      <div className="relative w-full max-w-md animate-scale-in">
        <div className="card p-6 shadow-soft">
          <div className="flex gap-4">
            <div className={`flex h-10 w-10 shrink-0 items-center justify-center rounded-full ${variant === 'danger' ? 'bg-danger-100' : 'bg-brand-100'}`}>
              <AlertTriangle size={20} className={variant === 'danger' ? 'text-danger-600' : 'text-brand-600'} />
            </div>
            <div>
              <h3 className="text-base font-semibold text-ash-900">{title}</h3>
              <p className="mt-1 text-sm text-ash-500">{message}</p>
            </div>
          </div>
          <div className="mt-6 flex justify-end gap-3">
            <button className="btn-secondary" onClick={onClose} disabled={loading}>Cancel</button>
            <button className={btnClass} onClick={onConfirm} disabled={loading}>
              {loading ? 'Processing...' : confirmText}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
