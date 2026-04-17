import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { User, Lock, ShieldCheck, CreditCard } from 'lucide-react';
import { endpoints } from '../../api/endpoints.js';
import { useAuthStore } from '../../store/authStore.js';
import { toast } from '../../components/Toast.jsx';
import PageHeader from '../../components/PageHeader.jsx';
import SectionCard from '../../components/SectionCard.jsx';
import { RoleBadge, VerifiedBadge } from '../../components/Badge.jsx';

export default function ProfilePage() {
  const queryClient = useQueryClient();
  const { user: authUser, updateUser } = useAuthStore();
  const [tab, setTab] = useState('personal');

  const profileQuery = useQuery({ queryKey: ['my-profile'], queryFn: () => endpoints.get('/me/profile') });
  const profile = profileQuery.data || {};

  const tabs = [
    { key: 'personal', label: 'Personal Info', icon: User },
    { key: 'security', label: 'Security', icon: Lock },
    { key: 'verification', label: 'Verification', icon: ShieldCheck },
    { key: 'bank', label: 'Bank Details', icon: CreditCard }
  ];

  return (
    <>
      <PageHeader eyebrow="Account" title="My Profile" description="Manage your personal information, security settings, and verification status." />

      {/* Profile header card */}
      <div className="card mb-6 p-6">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:gap-6">
          <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-gradient-to-br from-brand-500 to-brand-700 text-2xl font-bold text-white shadow-glow">
            {authUser?.name?.charAt(0)?.toUpperCase() || 'U'}
          </div>
          <div className="flex-1">
            <h2 className="text-xl font-bold text-ash-900">{authUser?.name}</h2>
            <p className="text-sm text-ash-500">{authUser?.email}</p>
            <div className="mt-2 flex flex-wrap gap-2">
              <RoleBadge role={authUser?.role} />
              <VerifiedBadge verified={profile.emailVerified ?? authUser?.emailVerified} label="Email" />
              <VerifiedBadge verified={profile.phoneVerified ?? authUser?.phoneVerified} label="Phone" />
            </div>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="mb-6 flex gap-1 overflow-x-auto rounded-xl bg-ash-100 p-1">
        {tabs.map(t => {
          const Icon = t.icon;
          return (
            <button key={t.key} onClick={() => setTab(t.key)}
              className={`flex items-center gap-2 whitespace-nowrap rounded-lg px-4 py-2 text-sm font-semibold transition-all ${
                tab === t.key ? 'bg-white text-ash-900 shadow-sm' : 'text-ash-500 hover:text-ash-700'
              }`}>
              <Icon size={15} /> {t.label}
            </button>
          );
        })}
      </div>

      {tab === 'personal' && <PersonalTab profile={profile} queryClient={queryClient} />}
      {tab === 'security' && <SecurityTab />}
      {tab === 'verification' && <VerificationTab profile={profile} queryClient={queryClient} />}
      {tab === 'bank' && <BankTab profile={profile} queryClient={queryClient} />}
    </>
  );
}

function PersonalTab({ profile, queryClient }) {
  const [form, setForm] = useState({
    dateOfBirth: profile.dateOfBirth || '',
    gender: profile.gender || '',
    bloodGroup: profile.bloodGroup || '',
    address: profile.address || '',
    city: profile.city || '',
    pincode: profile.pincode || '',
    emergencyName: profile.emergencyName || '',
    emergencyPhone: profile.emergencyPhone || ''
  });

  const mutation = useMutation({
    mutationFn: (p) => endpoints.put('/me/profile', p),
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['my-profile'] }); toast.success('Profile updated'); },
    onError: (e) => toast.error(e.response?.data?.message || 'Failed to update')
  });

  const submit = (e) => { e.preventDefault(); mutation.mutate(form); };
  const update = (k, v) => setForm(c => ({ ...c, [k]: v }));

  return (
    <SectionCard title="Personal Information" description="Update your personal details">
      <form className="grid gap-4 sm:grid-cols-2" onSubmit={submit}>
        <Field label="Date of Birth" type="date" value={form.dateOfBirth} onChange={v => update('dateOfBirth', v)} />
        <SelectField label="Gender" value={form.gender} onChange={v => update('gender', v)} options={['MALE', 'FEMALE', 'OTHER']} />
        <Field label="Blood Group" value={form.bloodGroup} onChange={v => update('bloodGroup', v)} placeholder="A+" />
        <Field label="City" value={form.city} onChange={v => update('city', v)} />
        <Field label="Pincode" value={form.pincode} onChange={v => update('pincode', v)} />
        <Field label="Emergency Contact Name" value={form.emergencyName} onChange={v => update('emergencyName', v)} />
        <Field label="Emergency Phone" value={form.emergencyPhone} onChange={v => update('emergencyPhone', v)} />
        <div className="sm:col-span-2">
          <label className="block">
            <span className="label">Address</span>
            <textarea className="textarea" value={form.address} onChange={e => update('address', e.target.value)} rows={2} />
          </label>
        </div>
        <div className="sm:col-span-2">
          <button type="submit" className="btn-primary" disabled={mutation.isPending}>
            {mutation.isPending ? 'Saving...' : 'Save Changes'}
          </button>
        </div>
      </form>
    </SectionCard>
  );
}

function SecurityTab() {
  const [form, setForm] = useState({ currentPassword: '', newPassword: '', confirmPassword: '' });
  const mutation = useMutation({
    mutationFn: (p) => endpoints.post('/me/change-password', p),
    onSuccess: () => { toast.success('Password changed successfully'); setForm({ currentPassword: '', newPassword: '', confirmPassword: '' }); },
    onError: (e) => toast.error(e.response?.data?.message || 'Failed')
  });

  const submit = (e) => {
    e.preventDefault();
    if (form.newPassword !== form.confirmPassword) { toast.error('Passwords do not match'); return; }
    if (form.newPassword.length < 8) { toast.error('Password must be at least 8 characters'); return; }
    mutation.mutate({ currentPassword: form.currentPassword, newPassword: form.newPassword });
  };

  return (
    <SectionCard title="Change Password" description="Update your account password">
      <form className="max-w-md space-y-4" onSubmit={submit}>
        <Field label="Current Password" type="password" value={form.currentPassword} onChange={v => setForm(c => ({ ...c, currentPassword: v }))} required />
        <Field label="New Password" type="password" value={form.newPassword} onChange={v => setForm(c => ({ ...c, newPassword: v }))} required />
        <Field label="Confirm Password" type="password" value={form.confirmPassword} onChange={v => setForm(c => ({ ...c, confirmPassword: v }))} required />
        <button type="submit" className="btn-primary" disabled={mutation.isPending}>
          {mutation.isPending ? 'Changing...' : 'Change Password'}
        </button>
      </form>
    </SectionCard>
  );
}

function VerificationTab({ profile, queryClient }) {
  const [emailCode, setEmailCode] = useState('');
  const [phoneCode, setPhoneCode] = useState('');
  const [preview, setPreview] = useState(null);

  const requestEmail = useMutation({
    mutationFn: () => endpoints.post('/me/verify-email/request'),
    onSuccess: (data) => { toast.info('Verification code sent to email'); if (data?.preview) setPreview(data.preview); },
    onError: (e) => toast.error(e.response?.data?.message || 'Failed')
  });

  const confirmEmail = useMutation({
    mutationFn: () => endpoints.post('/me/verify-email/confirm', { code: emailCode }),
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['my-profile'] }); toast.success('Email verified!'); setEmailCode(''); },
    onError: (e) => toast.error(e.response?.data?.message || 'Invalid code')
  });

  const requestPhone = useMutation({
    mutationFn: () => endpoints.post('/me/verify-phone/request'),
    onSuccess: (data) => { toast.info('Verification code sent via SMS'); if (data?.preview) setPreview(data.preview); },
    onError: (e) => toast.error(e.response?.data?.message || 'Failed')
  });

  const confirmPhone = useMutation({
    mutationFn: () => endpoints.post('/me/verify-phone/confirm', { code: phoneCode }),
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['my-profile'] }); toast.success('Phone verified!'); setPhoneCode(''); },
    onError: (e) => toast.error(e.response?.data?.message || 'Invalid code')
  });

  return (
    <div className="space-y-6">
      <SectionCard title="Email Verification" description={profile.emailVerified ? '✓ Email is verified' : 'Verify your email address'}>
        {!profile.emailVerified && (
          <div className="flex items-end gap-3 max-w-md">
            <div className="flex-1">
              <Field label="Verification Code" value={emailCode} onChange={setEmailCode} placeholder="Enter 6-digit code" />
            </div>
            <button type="button" className="btn-secondary h-10" onClick={() => requestEmail.mutate()} disabled={requestEmail.isPending}>
              {requestEmail.isPending ? 'Sending...' : 'Send Code'}
            </button>
            <button type="button" className="btn-primary h-10" onClick={() => confirmEmail.mutate()} disabled={!emailCode || confirmEmail.isPending}>
              Verify
            </button>
          </div>
        )}
        {preview && (
          <p className="mt-3 rounded-lg bg-amber-50 border border-amber-200 px-3 py-2 text-sm text-amber-700">
            LOG_ONLY preview: <code className="font-mono font-bold">{preview}</code>
          </p>
        )}
      </SectionCard>

      <SectionCard title="Phone Verification" description={profile.phoneVerified ? '✓ Phone is verified' : 'Verify your phone number'}>
        {!profile.phoneVerified && (
          <div className="flex items-end gap-3 max-w-md">
            <div className="flex-1">
              <Field label="Verification Code" value={phoneCode} onChange={setPhoneCode} placeholder="Enter 6-digit code" />
            </div>
            <button type="button" className="btn-secondary h-10" onClick={() => requestPhone.mutate()} disabled={requestPhone.isPending}>Send Code</button>
            <button type="button" className="btn-primary h-10" onClick={() => confirmPhone.mutate()} disabled={!phoneCode}>Verify</button>
          </div>
        )}
      </SectionCard>
    </div>
  );
}

function BankTab({ profile, queryClient }) {
  const [form, setForm] = useState({
    bankName: profile.bankName || '',
    accountNumber: profile.accountNumber || '',
    ifscCode: profile.ifscCode || '',
    accountHolderName: profile.accountHolderName || '',
    upiId: profile.upiId || ''
  });

  const mutation = useMutation({
    mutationFn: (p) => endpoints.put('/me/profile', p),
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['my-profile'] }); toast.success('Bank details saved'); },
    onError: (e) => toast.error(e.response?.data?.message || 'Failed')
  });

  const submit = (e) => { e.preventDefault(); mutation.mutate(form); };

  return (
    <SectionCard title="Bank Details" description="These details are used for salary crediting and commission payouts.">
      <form className="grid max-w-lg gap-4" onSubmit={submit}>
        <Field label="Bank Name" value={form.bankName} onChange={v => setForm(c => ({ ...c, bankName: v }))} />
        <Field label="Account Number" value={form.accountNumber} onChange={v => setForm(c => ({ ...c, accountNumber: v }))} />
        <Field label="IFSC Code" value={form.ifscCode} onChange={v => setForm(c => ({ ...c, ifscCode: v }))} />
        <Field label="Account Holder Name" value={form.accountHolderName} onChange={v => setForm(c => ({ ...c, accountHolderName: v }))} />
        <Field label="UPI ID" value={form.upiId} onChange={v => setForm(c => ({ ...c, upiId: v }))} placeholder="name@upi" />
        <button type="submit" className="btn-primary w-fit" disabled={mutation.isPending}>
          {mutation.isPending ? 'Saving...' : 'Save Bank Details'}
        </button>
      </form>
    </SectionCard>
  );
}

function Field({ label, value, onChange, type = 'text', required = false, placeholder = '' }) {
  return (
    <label className="block">
      <span className="label">{label}{required && ' *'}</span>
      <input className="input" type={type} value={value} required={required} placeholder={placeholder} onChange={e => onChange(e.target.value)} />
    </label>
  );
}

function SelectField({ label, value, onChange, options }) {
  return (
    <label className="block">
      <span className="label">{label}</span>
      <select className="select" value={value} onChange={e => onChange(e.target.value)}>
        <option value="">Select</option>
        {options.map(o => <option key={o} value={o}>{o}</option>)}
      </select>
    </label>
  );
}
