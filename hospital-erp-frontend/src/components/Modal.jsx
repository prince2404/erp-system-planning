import { X } from 'lucide-react';
import { useEffect } from 'react';

export default function Modal({ open, onClose, title, description, size = 'md', children }) {
  useEffect(() => {
    if (open) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
    }
    return () => { document.body.style.overflow = ''; };
  }, [open]);

  if (!open) return null;

  const sizes = {
    sm: 'max-w-md',
    md: 'max-w-lg',
    lg: 'max-w-2xl',
    xl: 'max-w-4xl'
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div className="fixed inset-0 bg-ash-950/50 backdrop-blur-sm animate-fade-in" onClick={onClose} />
      <div className={`relative w-full ${sizes[size]} animate-scale-in`}>
        <div className="card overflow-hidden shadow-soft">
          <div className="flex items-start justify-between border-b border-ash-100 px-6 py-4">
            <div>
              <h2 className="text-lg font-semibold text-ash-900">{title}</h2>
              {description && <p className="mt-0.5 text-sm text-ash-500">{description}</p>}
            </div>
            <button onClick={onClose} className="btn-icon text-ash-400 hover:text-ash-600 hover:bg-ash-100 -mr-1 -mt-1">
              <X size={18} />
            </button>
          </div>
          <div className="px-6 py-5">{children}</div>
        </div>
      </div>
    </div>
  );
}
