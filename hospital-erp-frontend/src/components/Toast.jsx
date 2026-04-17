import { useEffect, useState } from 'react';
import { CheckCircle, XCircle, AlertTriangle, Info, X } from 'lucide-react';

let toastId = 0;
let listeners = [];

function subscribe(listener) {
  listeners.push(listener);
  return () => { listeners = listeners.filter(l => l !== listener); };
}

function notify(toasts) {
  listeners.forEach(l => l(toasts));
}

let toasts = [];

export const toast = {
  success: (message) => addToast('success', message),
  error: (message) => addToast('error', message),
  warning: (message) => addToast('warning', message),
  info: (message) => addToast('info', message)
};

function addToast(type, message) {
  const id = ++toastId;
  toasts = [...toasts, { id, type, message }];
  notify(toasts);
  setTimeout(() => removeToast(id), 4000);
}

function removeToast(id) {
  toasts = toasts.filter(t => t.id !== id);
  notify(toasts);
}

const icons = {
  success: CheckCircle,
  error: XCircle,
  warning: AlertTriangle,
  info: Info
};

const styles = {
  success: 'border-emerald-200 bg-emerald-50 text-emerald-800',
  error: 'border-danger-200 bg-danger-50 text-danger-800',
  warning: 'border-amber-200 bg-amber-50 text-amber-800',
  info: 'border-brand-200 bg-brand-50 text-brand-800'
};

const iconStyles = {
  success: 'text-emerald-500',
  error: 'text-danger-500',
  warning: 'text-amber-500',
  info: 'text-brand-500'
};

export default function ToastContainer() {
  const [items, setItems] = useState([]);

  useEffect(() => subscribe(setItems), []);

  if (items.length === 0) return null;

  return (
    <div className="fixed bottom-4 right-4 z-[100] flex flex-col-reverse gap-2">
      {items.map(t => {
        const Icon = icons[t.type];
        return (
          <div key={t.id} className={`flex items-center gap-3 rounded-xl border px-4 py-3 shadow-soft animate-slide-up ${styles[t.type]}`}>
            <Icon size={18} className={iconStyles[t.type]} />
            <p className="text-sm font-medium flex-1">{t.message}</p>
            <button onClick={() => removeToast(t.id)} className="text-current opacity-50 hover:opacity-100">
              <X size={14} />
            </button>
          </div>
        );
      })}
    </div>
  );
}
