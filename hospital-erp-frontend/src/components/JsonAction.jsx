import { useState } from 'react';

export default function JsonAction({ label, sample, onSubmit, busy }) {
  const [value, setValue] = useState(JSON.stringify(sample, null, 2));
  const [error, setError] = useState('');

  const submit = async () => {
    try {
      setError('');
      await onSubmit(JSON.parse(value));
    } catch (err) {
      setError(err.message || 'Invalid JSON');
    }
  };

  return (
    <div className="space-y-3">
      <textarea className="min-h-56 w-full rounded-2xl border border-slate-200 bg-slate-950 p-4 font-mono text-xs text-slate-100 outline-none focus:ring-4 focus:ring-blue-100" value={value} onChange={(event) => setValue(event.target.value)} />
      {error ? <p className="text-sm font-medium text-red-600">{error}</p> : null}
      <button type="button" className="clinical-button" onClick={submit} disabled={busy}>
        {busy ? 'Submitting...' : label}
      </button>
    </div>
  );
}
