import { useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { HeartPulse, Eye, EyeOff, ArrowRight, Shield } from 'lucide-react';
import { endpoints } from '../../api/endpoints.js';
import { useAuthStore } from '../../store/authStore.js';

export default function LoginPage() {
  const navigate = useNavigate();
  const { accessToken, setSession } = useAuthStore();
  const [mode, setMode] = useState('login');
  const [form, setForm] = useState({ email: '', password: '', name: '', phone: '', bootstrapToken: '' });
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  if (accessToken) {
    return <Navigate to="/" replace />;
  }

  const update = (key, value) => setForm(c => ({ ...c, [key]: value }));

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
      navigate(response.user?.mustChangePassword ? '/profile' : '/');
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Authentication failed');
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="grid min-h-screen lg:grid-cols-2">
      {/* Left — Branding Panel */}
      <div className="relative hidden overflow-hidden bg-ash-950 lg:flex lg:flex-col lg:justify-between">
        {/* Gradient orbs */}
        <div className="absolute -left-32 -top-32 h-96 w-96 rounded-full bg-brand-600/20 blur-3xl" />
        <div className="absolute -bottom-48 -right-32 h-96 w-96 rounded-full bg-emerald-500/15 blur-3xl" />
        <div className="absolute left-1/2 top-1/3 h-64 w-64 rounded-full bg-purple-500/10 blur-3xl" />

        <div className="relative z-10 flex flex-1 flex-col justify-between p-12">
          <div className="flex items-center gap-3">
            <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-gradient-to-br from-brand-400 to-brand-600">
              <HeartPulse size={22} className="text-white" />
            </div>
            <div>
              <p className="text-[11px] font-bold uppercase tracking-[0.25em] text-brand-300">Apana Swastha Kendra</p>
              <p className="text-xs text-ash-400">Healthcare Operations Platform</p>
            </div>
          </div>

          <div className="max-w-lg">
            <h1 className="text-4xl font-bold leading-tight text-white xl:text-5xl">
              Rural healthcare delivery,{' '}
              <span className="bg-gradient-to-r from-brand-400 to-emerald-400 bg-clip-text text-transparent">reimagined.</span>
            </h1>
            <p className="mt-5 text-base leading-relaxed text-ash-400">
              A comprehensive platform connecting health centers, associates, and families 
              across Bihar, Jharkhand, and Uttar Pradesh. Manage operations, track prescriptions, 
              and distribute care — all from one system.
            </p>
            <div className="mt-8 grid grid-cols-3 gap-4">
              {[
                { label: 'Health Centers', value: '500+' },
                { label: 'Target Population', value: '6M' },
                { label: 'States Covered', value: '3' }
              ].map(stat => (
                <div key={stat.label} className="rounded-xl border border-white/10 bg-white/5 px-4 py-3 backdrop-blur">
                  <p className="text-xl font-bold text-white">{stat.value}</p>
                  <p className="mt-0.5 text-[11px] text-ash-400">{stat.label}</p>
                </div>
              ))}
            </div>
          </div>

          <p className="text-xs text-ash-600">© 2026 Apana Swastha Kendra. All rights reserved.</p>
        </div>
      </div>

      {/* Right — Login Form */}
      <div className="flex items-center justify-center bg-ash-50 p-6">
        <div className="w-full max-w-md animate-fade-in">
          {/* Mobile logo */}
          <div className="mb-8 flex items-center gap-3 lg:hidden">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-br from-brand-500 to-brand-700">
              <HeartPulse size={20} className="text-white" />
            </div>
            <span className="text-lg font-bold text-ash-900">Apana Swastha Kendra</span>
          </div>

          <div className="card p-8 shadow-soft">
            <div className="mb-7">
              <div className="flex items-center gap-2 mb-2">
                <Shield size={16} className="text-brand-500" />
                <p className="text-xs font-bold uppercase tracking-widest text-brand-600">
                  {mode === 'login' ? 'Secure Login' : 'System Bootstrap'}
                </p>
              </div>
              <h2 className="text-2xl font-bold text-ash-900">
                {mode === 'login' ? 'Welcome back' : 'Create Super Admin'}
              </h2>
              <p className="mt-1 text-sm text-ash-500">
                {mode === 'login'
                  ? 'Enter your credentials to access the platform.'
                  : 'Initialize the system with the first administrator account.'}
              </p>
            </div>

            <form className="space-y-4" onSubmit={submit}>
              {mode === 'bootstrap' && (
                <>
                  <FormField label="Bootstrap Token" value={form.bootstrapToken} onChange={v => update('bootstrapToken', v)} required type="password" placeholder="Paste your bootstrap token" />
                  <FormField label="Full Name" value={form.name} onChange={v => update('name', v)} required placeholder="Dr. Rajesh Kumar" />
                  <FormField label="Phone" value={form.phone} onChange={v => update('phone', v)} placeholder="+91 9876543210" />
                </>
              )}
              <FormField label="Email Address" type="email" value={form.email} onChange={v => update('email', v)} required placeholder="admin@ask.health" />
              <div className="relative">
                <FormField
                  label="Password"
                  type={showPassword ? 'text' : 'password'}
                  value={form.password}
                  onChange={v => update('password', v)}
                  required
                  placeholder="Enter your password"
                />
                <button
                  type="button"
                  className="absolute right-3 top-[30px] text-ash-400 hover:text-ash-600"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                </button>
              </div>

              {error && (
                <div className="rounded-xl border border-danger-200 bg-danger-50 px-4 py-3 text-sm font-medium text-danger-700 animate-slide-up">
                  {error}
                </div>
              )}

              <button className="btn-primary w-full h-11 text-base" disabled={busy} type="submit">
                {busy ? (
                  <span className="flex items-center gap-2">
                    <span className="h-4 w-4 animate-spin rounded-full border-2 border-white/30 border-t-white" />
                    Processing...
                  </span>
                ) : (
                  <span className="flex items-center gap-2">
                    {mode === 'login' ? 'Sign In' : 'Create Super Admin'}
                    <ArrowRight size={16} />
                  </span>
                )}
              </button>
            </form>

            <div className="mt-6 border-t border-ash-100 pt-4">
              <button
                className="text-sm font-semibold text-brand-600 hover:text-brand-700 transition-colors"
                type="button"
                onClick={() => { setMode(mode === 'login' ? 'bootstrap' : 'login'); setError(''); }}
              >
                {mode === 'login' ? '🔐 Need to bootstrap first admin?' : '← Back to login'}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

function FormField({ label, value, onChange, type = 'text', required = false, placeholder = '' }) {
  return (
    <label className="block">
      <span className="label">{label}{required && ' *'}</span>
      <input
        className="input"
        type={type}
        value={value}
        required={required}
        placeholder={placeholder}
        onChange={e => onChange(e.target.value)}
      />
    </label>
  );
}
