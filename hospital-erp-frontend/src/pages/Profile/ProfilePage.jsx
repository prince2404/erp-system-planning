import { useEffect, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { endpoints } from '../../api/endpoints.js';
import PageHeader from '../../components/PageHeader.jsx';
import SectionCard from '../../components/SectionCard.jsx';
import { useAuthStore } from '../../store/authStore.js';

const blankProfile = {
  name: '',
  phone: '',
  gender: '',
  dateOfBirth: '',
  alternatePhone: '',
  emergencyContactName: '',
  emergencyContactPhone: '',
  address: '',
  villageOrLocality: '',
  pincode: '',
  bankAccountName: '',
  bankName: '',
  bankAccountNumber: '',
  ifscCode: '',
  upiId: '',
  idProofType: '',
  idProofNumber: ''
};

export default function ProfilePage() {
  const queryClient = useQueryClient();
  const updateUser = useAuthStore((state) => state.updateUser);
  const profileQuery = useQuery({ queryKey: ['my-profile'], queryFn: () => endpoints.get('/me/profile') });
  const [form, setForm] = useState(blankProfile);
  const [emailCode, setEmailCode] = useState('');
  const [phoneCode, setPhoneCode] = useState('');
  const [lastVerification, setLastVerification] = useState(null);

  useEffect(() => {
    if (profileQuery.data) {
      setForm({
        name: profileQuery.data.name ?? '',
        phone: profileQuery.data.phone ?? '',
        gender: profileQuery.data.gender ?? '',
        dateOfBirth: profileQuery.data.dateOfBirth ?? '',
        alternatePhone: profileQuery.data.alternatePhone ?? '',
        emergencyContactName: profileQuery.data.emergencyContactName ?? '',
        emergencyContactPhone: profileQuery.data.emergencyContactPhone ?? '',
        address: profileQuery.data.address ?? '',
        villageOrLocality: profileQuery.data.villageOrLocality ?? '',
        pincode: profileQuery.data.pincode ?? '',
        bankAccountName: profileQuery.data.bankAccountName ?? '',
        bankName: profileQuery.data.bankName ?? '',
        bankAccountNumber: profileQuery.data.bankAccountNumber ?? '',
        ifscCode: profileQuery.data.ifscCode ?? '',
        upiId: profileQuery.data.upiId ?? '',
        idProofType: profileQuery.data.idProofType ?? '',
        idProofNumber: profileQuery.data.idProofNumber ?? ''
      });
    }
  }, [profileQuery.data]);

  const updateProfile = useMutation({
    mutationFn: (payload) => endpoints.put('/me/profile', payload),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['my-profile'] });
      updateUser({
        ...useAuthStore.getState().user,
        name: data.name,
        phone: data.phone,
        emailVerified: data.emailVerified,
        phoneVerified: data.phoneVerified,
        mustChangePassword: data.mustChangePassword,
        profileCompleted: data.profileCompleted
      });
    }
  });

  const passwordMutation = useMutation({
    mutationFn: (payload) => endpoints.post('/me/change-password', payload),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['my-profile'] });
      updateUser({
        ...useAuthStore.getState().user,
        mustChangePassword: data.mustChangePassword
      });
    }
  });

  const requestEmailCode = useMutation({
    mutationFn: () => endpoints.post('/me/verify-email/request', {}),
    onSuccess: (data) => setLastVerification(data)
  });
  const requestPhoneCode = useMutation({
    mutationFn: () => endpoints.post('/me/verify-phone/request', {}),
    onSuccess: (data) => setLastVerification(data)
  });
  const confirmEmail = useMutation({
    mutationFn: () => endpoints.post('/me/verify-email/confirm', { code: emailCode }),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['my-profile'] });
      updateUser({
        ...useAuthStore.getState().user,
        emailVerified: data.emailVerified
      });
      setEmailCode('');
    }
  });
  const confirmPhone = useMutation({
    mutationFn: () => endpoints.post('/me/verify-phone/confirm', { code: phoneCode }),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['my-profile'] });
      updateUser({
        ...useAuthStore.getState().user,
        phoneVerified: data.phoneVerified
      });
      setPhoneCode('');
    }
  });

  return (
    <>
      <PageHeader eyebrow="Phase 1" title="Personal Profile & Verification" description="Every user can complete personal details, verify contact info, and manage bank details for payouts." />
      <div className="grid gap-6 xl:grid-cols-[1.1fr_0.9fr]">
        <SectionCard title="Profile Details" description="Complete this after first login so the account becomes operational.">
          <form className="grid gap-4 md:grid-cols-2" onSubmit={(event) => { event.preventDefault(); updateProfile.mutate(form); }}>
            <Field label="Full Name" value={form.name} onChange={(value) => setForm((current) => ({ ...current, name: value }))} />
            <ReadOnlyField label="Login Email" value={profileQuery.data?.email ?? ''} />
            <Field label="Phone" value={form.phone} onChange={(value) => setForm((current) => ({ ...current, phone: value }))} />
            <Field label="Gender" value={form.gender} onChange={(value) => setForm((current) => ({ ...current, gender: value }))} />
            <Field label="Date of Birth" type="date" value={form.dateOfBirth} onChange={(value) => setForm((current) => ({ ...current, dateOfBirth: value }))} />
            <Field label="Alternate Phone" value={form.alternatePhone} onChange={(value) => setForm((current) => ({ ...current, alternatePhone: value }))} />
            <Field label="Emergency Name" value={form.emergencyContactName} onChange={(value) => setForm((current) => ({ ...current, emergencyContactName: value }))} />
            <Field label="Emergency Phone" value={form.emergencyContactPhone} onChange={(value) => setForm((current) => ({ ...current, emergencyContactPhone: value }))} />
            <Field label="Village / Locality" value={form.villageOrLocality} onChange={(value) => setForm((current) => ({ ...current, villageOrLocality: value }))} />
            <Field label="Pincode" value={form.pincode} onChange={(value) => setForm((current) => ({ ...current, pincode: value }))} />
            <Field label="Bank Account Name" value={form.bankAccountName} onChange={(value) => setForm((current) => ({ ...current, bankAccountName: value }))} />
            <Field label="Bank Name" value={form.bankName} onChange={(value) => setForm((current) => ({ ...current, bankName: value }))} />
            <Field label="Account Number" value={form.bankAccountNumber} onChange={(value) => setForm((current) => ({ ...current, bankAccountNumber: value }))} />
            <Field label="IFSC Code" value={form.ifscCode} onChange={(value) => setForm((current) => ({ ...current, ifscCode: value.toUpperCase() }))} />
            <Field label="UPI ID" value={form.upiId} onChange={(value) => setForm((current) => ({ ...current, upiId: value }))} />
            <Field label="ID Proof Type" value={form.idProofType} onChange={(value) => setForm((current) => ({ ...current, idProofType: value }))} />
            <Field label="ID Proof Number" value={form.idProofNumber} onChange={(value) => setForm((current) => ({ ...current, idProofNumber: value }))} />
            <div className="md:col-span-2">
              <label>
                <span className="mb-1 block text-xs font-semibold uppercase tracking-wide text-slate-500">Address</span>
                <textarea className="min-h-28 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-100" value={form.address} onChange={(event) => setForm((current) => ({ ...current, address: event.target.value }))} />
              </label>
            </div>
            <div className="md:col-span-2">
              <button className="clinical-button" disabled={updateProfile.isPending} type="submit">
                {updateProfile.isPending ? 'Saving...' : 'Save Profile'}
              </button>
            </div>
          </form>
          {updateProfile.isError ? <ErrorText error={updateProfile.error} /> : null}
          {profileQuery.data ? (
            <div className="mt-4 rounded-2xl border border-slate-200 bg-slate-50 p-4 text-sm">
              <p>Email verified: <span className="font-semibold">{profileQuery.data.emailVerified ? 'Yes' : 'No'}</span></p>
              <p>Phone verified: <span className="font-semibold">{profileQuery.data.phoneVerified ? 'Yes' : 'No'}</span></p>
              <p>Profile completed: <span className="font-semibold">{profileQuery.data.profileCompleted ? 'Yes' : 'No'}</span></p>
              <p>Password reset pending: <span className="font-semibold">{profileQuery.data.mustChangePassword ? 'Yes' : 'No'}</span></p>
            </div>
          ) : null}
        </SectionCard>

        <div className="space-y-6">
          <SectionCard title="Password Change" description="New users should change the temporary password immediately after first login.">
            <PasswordForm mutation={passwordMutation} />
          </SectionCard>
          <SectionCard title="Contact Verification" description="Codes are logged for development now. Once email/SMS provider credentials are added, they can be sent externally.">
            <div className="space-y-4">
              <VerificationBlock
                title="Email Verification"
                requestMutation={requestEmailCode}
                confirmMutation={confirmEmail}
                code={emailCode}
                setCode={setEmailCode}
                buttonLabel="Request Email Code"
              />
              <VerificationBlock
                title="Phone Verification"
                requestMutation={requestPhoneCode}
                confirmMutation={confirmPhone}
                code={phoneCode}
                setCode={setPhoneCode}
                buttonLabel="Request Phone Code"
              />
              {lastVerification ? (
                <div className="rounded-2xl border border-blue-200 bg-blue-50 p-4 text-sm text-blue-900">
                  <p className="font-semibold">{lastVerification.channel} code prepared</p>
                  <p className="mt-1">Status: {lastVerification.status}</p>
                  <p>Destination: {lastVerification.destination}</p>
                  {lastVerification.previewCode ? <p>Preview Code: <span className="font-mono font-semibold">{lastVerification.previewCode}</span></p> : null}
                </div>
              ) : null}
            </div>
          </SectionCard>
        </div>
      </div>
    </>
  );
}

function VerificationBlock({ title, requestMutation, confirmMutation, code, setCode, buttonLabel }) {
  return (
    <div className="rounded-2xl border border-slate-200 p-4">
      <p className="font-semibold text-slate-900">{title}</p>
      <div className="mt-3 flex flex-wrap gap-3">
        <button className="rounded-xl border border-blue-200 px-3 py-2 text-sm font-semibold text-blue-700 hover:bg-blue-50" type="button" onClick={() => requestMutation.mutate()} disabled={requestMutation.isPending}>
          {requestMutation.isPending ? 'Sending...' : buttonLabel}
        </button>
        <input className="clinical-input w-44" placeholder="Enter code" value={code} onChange={(event) => setCode(event.target.value)} />
        <button className="rounded-xl bg-slate-900 px-3 py-2 text-sm font-semibold text-white" type="button" onClick={() => confirmMutation.mutate()} disabled={confirmMutation.isPending || !code}>
          {confirmMutation.isPending ? 'Verifying...' : 'Verify'}
        </button>
      </div>
      {requestMutation.isError ? <ErrorText error={requestMutation.error} /> : null}
      {confirmMutation.isError ? <ErrorText error={confirmMutation.error} /> : null}
    </div>
  );
}

function PasswordForm({ mutation }) {
  const [form, setForm] = useState({ currentPassword: '', newPassword: '' });

  return (
    <form className="space-y-4" onSubmit={(event) => { event.preventDefault(); mutation.mutate(form); }}>
      <Field label="Current Password" type="password" value={form.currentPassword} onChange={(value) => setForm((current) => ({ ...current, currentPassword: value }))} required />
      <Field label="New Password" type="password" value={form.newPassword} onChange={(value) => setForm((current) => ({ ...current, newPassword: value }))} required />
      <button className="clinical-button" disabled={mutation.isPending} type="submit">
        {mutation.isPending ? 'Updating...' : 'Change Password'}
      </button>
      {mutation.isError ? <ErrorText error={mutation.error} /> : null}
    </form>
  );
}

function Field({ label, value, onChange, type = 'text', required = false }) {
  return (
    <label className="block">
      <span className="mb-1 block text-xs font-semibold uppercase tracking-wide text-slate-500">{label}{required ? ' *' : ''}</span>
      <input className="clinical-input w-full" type={type} value={value} required={required} onChange={(event) => onChange(event.target.value)} />
    </label>
  );
}

function ReadOnlyField({ label, value }) {
  return (
    <label className="block">
      <span className="mb-1 block text-xs font-semibold uppercase tracking-wide text-slate-500">{label}</span>
      <input className="clinical-input w-full bg-slate-50" value={value} readOnly />
    </label>
  );
}

function ErrorText({ error }) {
  return <p className="mt-3 rounded-xl bg-red-50 px-3 py-2 text-sm font-medium text-red-700">{error.response?.data?.message || error.message}</p>;
}
