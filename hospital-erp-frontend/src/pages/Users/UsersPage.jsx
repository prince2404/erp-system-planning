import { useEffect, useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { endpoints } from '../../api/endpoints.js';
import DataTable from '../../components/DataTable.jsx';
import PageHeader from '../../components/PageHeader.jsx';
import SectionCard from '../../components/SectionCard.jsx';

const emptyForm = {
  name: '',
  email: '',
  phone: '',
  role: '',
  stateId: '',
  districtId: '',
  blockId: '',
  centerId: '',
  notifyByEmail: true,
  notifyBySms: false
};

export default function UsersPage() {
  const queryClient = useQueryClient();
  const [form, setForm] = useState(emptyForm);
  const [selectedPermissionKeys, setSelectedPermissionKeys] = useState([]);

  const optionsQuery = useQuery({ queryKey: ['user-options'], queryFn: () => endpoints.get('/users/options') });
  const statesQuery = useQuery({ queryKey: ['states'], queryFn: () => endpoints.get('/states') });
  const districtsQuery = useQuery({ queryKey: ['districts'], queryFn: () => endpoints.get('/districts') });
  const blocksQuery = useQuery({ queryKey: ['blocks'], queryFn: () => endpoints.get('/blocks') });
  const centersQuery = useQuery({ queryKey: ['centers'], queryFn: () => endpoints.get('/centers') });
  const usersQuery = useQuery({ queryKey: ['users'], queryFn: () => endpoints.get('/users', { page: 0, size: 50 }) });

  const manageableRoles = optionsQuery.data?.manageableRoles ?? [];
  const defaultPermissionKeysByRole = optionsQuery.data?.defaultPermissionKeysByRole ?? {};
  const permissionGroups = optionsQuery.data?.permissionGroups ?? [];
  const scopeMode = form.role ? optionsQuery.data?.defaultScopeByRole?.[form.role] : null;

  useEffect(() => {
    if (form.role && defaultPermissionKeysByRole[form.role]) {
      setSelectedPermissionKeys(defaultPermissionKeysByRole[form.role]);
    }
  }, [form.role, defaultPermissionKeysByRole]);

  const districts = useMemo(
    () => (districtsQuery.data ?? []).filter((district) => !form.stateId || String(district.stateId) === String(form.stateId)),
    [districtsQuery.data, form.stateId]
  );
  const blocks = useMemo(
    () => (blocksQuery.data ?? []).filter((block) => !form.districtId || String(block.districtId) === String(form.districtId)),
    [blocksQuery.data, form.districtId]
  );
  const centers = useMemo(
    () => (centersQuery.data ?? []).filter((center) => !form.blockId || String(center.blockId) === String(form.blockId)),
    [centersQuery.data, form.blockId]
  );
  const permissionRows = permissionGroups.flatMap((group) =>
    group.permissions.map((permission) => ({
      id: permission.id,
      group: group.label,
      key: permission.key,
      label: permission.label,
      description: permission.description,
      defaultRoles: permission.defaultRoles.join(', ')
    }))
  );

  const createMutation = useMutation({
    mutationFn: (payload) => endpoints.post('/users/create', payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] });
      setForm(emptyForm);
      setSelectedPermissionKeys([]);
    }
  });

  const updateForm = (name, value) => {
    setForm((current) => {
      const next = { ...current, [name]: value };
      if (name === 'stateId') {
        next.districtId = '';
        next.blockId = '';
        next.centerId = '';
      }
      if (name === 'districtId') {
        next.blockId = '';
        next.centerId = '';
      }
      if (name === 'blockId') {
        next.centerId = '';
      }
      return next;
    });
  };

  const togglePermission = (key) => {
    setSelectedPermissionKeys((current) =>
      current.includes(key) ? current.filter((item) => item !== key) : [...current, key]
    );
  };

  const submit = async (event) => {
    event.preventDefault();
    const payload = {
      name: form.name,
      email: form.email,
      phone: form.phone || undefined,
      role: form.role,
      stateId: form.stateId ? Number(form.stateId) : undefined,
      districtId: form.districtId ? Number(form.districtId) : undefined,
      blockId: form.blockId ? Number(form.blockId) : undefined,
      centerId: form.centerId ? Number(form.centerId) : undefined,
      notifyByEmail: form.notifyByEmail,
      notifyBySms: form.notifyBySms,
      permissionKeys: selectedPermissionKeys
    };
    await createMutation.mutateAsync(payload);
  };

  return (
    <>
      <PageHeader eyebrow="Phase 1" title="User & Access Setup" description="Create users with default role templates, permission toggles, and registration notification support." />
      <div className="grid gap-6 xl:grid-cols-[1.1fr_0.9fr]">
        <SectionCard title="Create User" description="The system generates a temporary password automatically and applies a role template that you can fine-tune with toggles.">
          <form className="grid gap-4 md:grid-cols-2" onSubmit={submit}>
            <Field label="Full Name" value={form.name} onChange={(value) => updateForm('name', value)} required />
            <Field label="Email" type="email" value={form.email} onChange={(value) => updateForm('email', value)} required />
            <Field label="Phone" value={form.phone} onChange={(value) => updateForm('phone', value)} />
            <SelectField label="Role" value={form.role} onChange={(value) => updateForm('role', value)} options={manageableRoles} required />
            <LocationFields
              scopeMode={scopeMode}
              form={form}
              states={statesQuery.data ?? []}
              districts={districts}
              blocks={blocks}
              centers={centers}
              onChange={updateForm}
            />
            <div className="md:col-span-2 flex flex-wrap gap-4 rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3">
              <ToggleCheckbox label="Send email notification" checked={form.notifyByEmail} onChange={(checked) => updateForm('notifyByEmail', checked)} />
              <ToggleCheckbox label="Send SMS notification" checked={form.notifyBySms} onChange={(checked) => updateForm('notifyBySms', checked)} />
              <button className="rounded-xl border border-blue-200 px-3 py-2 text-sm font-semibold text-blue-700 hover:bg-blue-50" type="button" onClick={() => setSelectedPermissionKeys(defaultPermissionKeysByRole[form.role] ?? [])}>
                Apply Role Defaults
              </button>
            </div>
            <div className="md:col-span-2">
              <button className="clinical-button" disabled={createMutation.isPending} type="submit">
                {createMutation.isPending ? 'Creating...' : 'Create User'}
              </button>
            </div>
          </form>
          {createMutation.isError ? <p className="mt-4 rounded-xl bg-red-50 px-3 py-2 text-sm font-medium text-red-700">{createMutation.error.response?.data?.message || createMutation.error.message}</p> : null}
          {createMutation.data ? <CredentialResult result={createMutation.data} /> : null}
        </SectionCard>

        <SectionCard title="Permission Toggles" description="These permissions cover the full business network model, from hierarchy management to cards, wallet, sales, commissions, and reporting.">
          <p className="text-sm text-slate-500">Selected permissions: <span className="font-semibold text-slate-900">{selectedPermissionKeys.length}</span></p>
          <div className="mt-4 space-y-4">
            {permissionGroups.map((group) => (
              <div key={group.key} className="rounded-2xl border border-slate-200">
                <div className="border-b border-slate-200 px-4 py-3">
                  <h3 className="font-semibold text-slate-900">{group.label}</h3>
                </div>
                <div className="space-y-3 p-4">
                  {group.permissions.map((permission) => (
                    <label key={permission.key} className="flex items-start justify-between gap-4 rounded-2xl border border-slate-100 px-3 py-3">
                      <div>
                        <p className="font-medium text-slate-900">{permission.label}</p>
                        <p className="mt-1 text-xs text-slate-500">{permission.description}</p>
                        <p className="mt-1 text-[11px] uppercase tracking-wide text-slate-400">Default roles: {permission.defaultRoles.join(', ')}</p>
                      </div>
                      <input className="mt-1 h-5 w-5 accent-blue-600" type="checkbox" checked={selectedPermissionKeys.includes(permission.key)} onChange={() => togglePermission(permission.key)} />
                    </label>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </SectionCard>
      </div>

      <div className="mt-6 grid gap-6 xl:grid-cols-2">
        <SectionCard title="Permission Catalog" description="System-defined permissions for the healthcare business network.">
          <DataTable
            rows={permissionRows}
            columns={[
              { key: 'group', label: 'Group' },
              { key: 'label', label: 'Permission' },
              { key: 'key', label: 'Key' },
              { key: 'defaultRoles', label: 'Default Roles' }
            ]}
          />
        </SectionCard>
        <SectionCard title="Users" description="Recently created users and their onboarding status.">
          {usersQuery.isError ? <p className="mb-3 text-sm font-medium text-red-600">{usersQuery.error.response?.data?.message || usersQuery.error.message}</p> : null}
          <DataTable
            rows={usersQuery.data?.content ?? []}
            columns={[
              { key: 'name', label: 'Name' },
              { key: 'email', label: 'Email' },
              { key: 'role', label: 'Role' },
              { key: 'scopeType', label: 'Scope' },
              { key: 'centerName', label: 'Center' },
              { key: 'emailVerified', label: 'Email Verified' },
              { key: 'phoneVerified', label: 'Phone Verified' },
              { key: 'mustChangePassword', label: 'Password Reset' }
            ]}
          />
        </SectionCard>
      </div>
    </>
  );
}

function LocationFields({ scopeMode, form, states, districts, blocks, centers, onChange }) {
  const needsState = ['STATE', 'DISTRICT', 'BLOCK', 'CENTER'].includes(scopeMode);
  const needsDistrict = ['DISTRICT', 'BLOCK', 'CENTER'].includes(scopeMode);
  const needsBlock = ['BLOCK', 'CENTER'].includes(scopeMode);
  const needsCenter = scopeMode === 'CENTER';

  return (
    <>
      {needsState ? <SelectField label="State" value={form.stateId} onChange={(value) => onChange('stateId', value)} options={states.map((state) => ({ value: state.id, label: `${state.name} (${state.code})` }))} required /> : null}
      {needsDistrict ? <SelectField label="District" value={form.districtId} onChange={(value) => onChange('districtId', value)} options={districts.map((district) => ({ value: district.id, label: district.name }))} required /> : null}
      {needsBlock ? <SelectField label="Block" value={form.blockId} onChange={(value) => onChange('blockId', value)} options={blocks.map((block) => ({ value: block.id, label: block.name }))} required /> : null}
      {needsCenter ? <SelectField label="Center" value={form.centerId} onChange={(value) => onChange('centerId', value)} options={centers.map((center) => ({ value: center.id, label: `${center.code} - ${center.name}` }))} required /> : null}
    </>
  );
}

function CredentialResult({ result }) {
  return (
    <div className="mt-4 rounded-2xl border border-emerald-200 bg-emerald-50 p-4 text-sm">
      <p className="font-semibold text-emerald-800">User created successfully.</p>
      <p className="mt-2 text-slate-700">Temporary password: <span className="font-mono font-semibold">{result.temporaryPassword}</span></p>
      <div className="mt-3 space-y-1">
        {result.notifications?.map((notification) => (
          <p key={`${notification.channel}-${notification.destination}`} className="text-slate-700">
            {notification.channel}: {notification.status} - {notification.message}
          </p>
        ))}
      </div>
    </div>
  );
}

function Field({ label, value, onChange, type = 'text', required = false }) {
  return (
    <label>
      <span className="mb-1 block text-xs font-semibold uppercase tracking-wide text-slate-500">{label}{required ? ' *' : ''}</span>
      <input className="clinical-input w-full" type={type} value={value} required={required} onChange={(event) => onChange(event.target.value)} />
    </label>
  );
}

function SelectField({ label, value, onChange, options, required = false }) {
  const normalized = (options ?? []).map((option) => (typeof option === 'string' ? { value: option, label: option } : option));
  return (
    <label>
      <span className="mb-1 block text-xs font-semibold uppercase tracking-wide text-slate-500">{label}{required ? ' *' : ''}</span>
      <select className="clinical-input w-full" value={value} required={required} onChange={(event) => onChange(event.target.value)}>
        <option value="">Select</option>
        {normalized.map((option) => (
          <option key={option.value} value={option.value}>{option.label}</option>
        ))}
      </select>
    </label>
  );
}

function ToggleCheckbox({ label, checked, onChange }) {
  return (
    <label className="inline-flex items-center gap-2 text-sm font-medium text-slate-700">
      <input className="h-4 w-4 accent-blue-600" type="checkbox" checked={checked} onChange={(event) => onChange(event.target.checked)} />
      {label}
    </label>
  );
}
