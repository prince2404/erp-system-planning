import { useState } from 'react';

export default function SmartForm({ fields, submitLabel = 'Save', onSubmit, busy }) {
  const [values, setValues] = useState(() => Object.fromEntries(fields.map((field) => [field.name, field.defaultValue ?? ''])));

  const update = (name, value) => setValues((current) => ({ ...current, [name]: value }));

  const handleSubmit = async (event) => {
    event.preventDefault();
    const payload = {};
    fields.forEach((field) => {
      const value = values[field.name];
      if (value === '' || value === undefined) {
        return;
      }
      if (field.type === 'number') {
        payload[field.name] = Number(value);
      } else {
        payload[field.name] = value;
      }
    });
    await onSubmit(payload);
  };

  return (
    <form onSubmit={handleSubmit} className="grid gap-4 md:grid-cols-2">
      {fields.map((field) => (
        <label key={field.name} className={field.full ? 'md:col-span-2' : ''}>
          <span className="mb-1 block text-xs font-semibold uppercase tracking-wide text-slate-500">
            {field.label} {field.required ? <span className="text-red-500">*</span> : null}
          </span>
          {field.type === 'select' ? (
            <select className="clinical-input w-full" value={values[field.name]} required={field.required} onChange={(event) => update(field.name, event.target.value)}>
              <option value="">Select</option>
              {field.options?.map((option) => (
                <option key={option} value={option}>
                  {option}
                </option>
              ))}
            </select>
          ) : field.type === 'textarea' ? (
            <textarea className="min-h-24 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-100" value={values[field.name]} required={field.required} onChange={(event) => update(field.name, event.target.value)} />
          ) : (
            <input className="clinical-input w-full" type={field.type || 'text'} value={values[field.name]} required={field.required} onChange={(event) => update(field.name, event.target.value)} />
          )}
        </label>
      ))}
      <div className="md:col-span-2">
        <button className="clinical-button" type="submit" disabled={busy}>
          {busy ? 'Saving...' : submitLabel}
        </button>
      </div>
    </form>
  );
}
