import { useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { endpoints } from '../../api/endpoints.js';
import { useAuthStore } from '../../store/authStore.js';

export default function LoginPage() {
  const navigate = useNavigate();
  const { accessToken, setSession } = useAuthStore();
  const [mode, setMode] = useState('login');
  const [form, setForm] = useState({ email: '', password: '', name: '', phone: '', bootstrapToken: '' });
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  if (accessToken) {
    return <Navigate to="/" replace />;
  }

  const update = (key, value) => setForm((current) => ({ ...current, [key]: value }));

  const submit = async (event) => {
    event.preventDefault();
    setBusy(true);
    setError('');
    try {
      const response = mode === 'login'
        ? await endpoints.login({ email: form.email, password: form.password })
        : await endpoints.bootstrap({
            bootstrapToken: form.bootstrapToken,
            name: form.name,
            email: form.email,
            password: form.password,
            phone: form.phone
          });
      setSession(response);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Authentication failed');
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="grid min-h-screen bg-slate-950 lg:grid-cols-[1.05fr_0.95fr]">
      <div className="hidden bg-[radial-gradient(circle_at_top_left,_#2563eb,_transparent_32%),linear-gradient(135deg,_#020617,_#0f172a)] p-12 text-white lg:flex lg:flex-col lg:justify-between">
        <div>
          <p className="text-xs font-semibold uppercase tracking-[0.35em] text-blue-200">Apana Swastha Kendra</p>
          <h1 className="mt-6 max-w-xl text-5xl font-semibold leading-tight">Professional hospital ERP for multi-center operations.</h1>
          <p className="mt-5 max-w-lg text-sm leading-6 text-slate-300">
            Secure JWT auth, geographic scoping, OPD/IPD, pharmacy, billing, wallet, HR, payroll, and analytics in one system.
          </p>
        </div>
        <p className="text-xs text-slate-400">No dummy data is bundled. Initialize the first admin through the bootstrap token only.</p>
      </div>
      <div className="flex items-center justify-center bg-slate-50 p-6">
        <div className="w-full max-w-md rounded-3xl bg-white p-8 shadow-soft">
          <div className="mb-8">
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-600">Hospital ERP</p>
            <h2 className="mt-2 text-2xl font-semibold text-slate-950">{mode === 'login' ? 'Sign in' : 'Create first super admin'}</h2>
            <p className="mt-2 text-sm text-slate-500">Use your MySQL-backed account credentials.</p>
          </div>
          <form className="space-y-4" onSubmit={submit}>
            {mode === 'bootstrap' ? (
              <>
                <Field label="Bootstrap Token" value={form.bootstrapToken} onChange={(value) => update('bootstrapToken', value)} required />
                <Field label="Full Name" value={form.name} onChange={(value) => update('name', value)} required />
                <Field label="Phone" value={form.phone} onChange={(value) => update('phone', value)} />
              </>
            ) : null}
            <Field label="Email" type="email" value={form.email} onChange={(value) => update('email', value)} required />
            <Field label="Password" type="password" value={form.password} onChange={(value) => update('password', value)} required />
            {error ? <p className="rounded-xl bg-red-50 px-3 py-2 text-sm font-medium text-red-700">{error}</p> : null}
            <button className="clinical-button w-full" disabled={busy} type="submit">
              {busy ? 'Please wait...' : mode === 'login' ? 'Login' : 'Create Super Admin'}
            </button>
          </form>
          <button className="mt-5 text-sm font-semibold text-blue-600" type="button" onClick={() => setMode(mode === 'login' ? 'bootstrap' : 'login')}>
            {mode === 'login' ? 'Need first admin bootstrap?' : 'Back to login'}
          </button>
        </div>
      </div>
    </div>
  );
}

function Field({ label, value, onChange, type = 'text', required = false }) {
  return (
    <label className="block">
      <span className="mb-1 block text-xs font-semibold uppercase tracking-wide text-slate-500">{label}</span>
      <input className="clinical-input w-full" type={type} value={value} required={required} onChange={(event) => onChange(event.target.value)} />
    </label>
  );
}
