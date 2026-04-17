import { useEffect, useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Plus, UserPlus, ChevronDown, ChevronUp } from 'lucide-react';
import { endpoints } from '../../api/endpoints.js';
import { useAuthStore } from '../../store/authStore.js';
import { toast } from '../../components/Toast.jsx';
import PageHeader from '../../components/PageHeader.jsx';
import SectionCard from '../../components/SectionCard.jsx';
import DataTable from '../../components/DataTable.jsx';
import { RoleBadge, ActiveBadge, VerifiedBadge } from '../../components/Badge.jsx';
import ConfirmDialog from '../../components/ConfirmDialog.jsx';
import Modal from '../../components/Modal.jsx';

const emptyForm = { name: '', email: '', phone: '', role: '', stateId: '', districtId: '', blockId: '', centerId: '', notifyByEmail: true, notifyBySms: false };

export default function UsersPage() {
  const queryClient = useQueryClient();
  const { user } = useAuthStore();
  const [form, setForm] = useState(emptyForm);
  const [selectedPermissionKeys, setSelectedPermissionKeys] = useState([]);
  const [isTogglesOpen, setIsTogglesOpen] = useState(false);
  const [editingUserId, setEditingUserId] = useState(null);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [confirmDeactivate, setConfirmDeactivate] = useState(null);
  const [credentialResult, setCredentialResult] = useState(null);
  const [page, setPage] = useState(0);

  const isSuperAdmin = user?.role === 'SUPER_ADMIN';
  const isAdmin = user?.role === 'ADMIN';
  const canManage = isSuperAdmin || isAdmin;

  // Queries
  const optionsQuery = useQuery({ queryKey: ['user-options'], queryFn: () => endpoints.get('/users/options') });
  const statesQuery = useQuery({ queryKey: ['states'], queryFn: () => endpoints.get('/states') });
  const districtsQuery = useQuery({ queryKey: ['districts'], queryFn: () => endpoints.get('/districts') });
  const blocksQuery = useQuery({ queryKey: ['blocks'], queryFn: () => endpoints.get('/blocks') });
  const centersQuery = useQuery({ queryKey: ['centers'], queryFn: () => endpoints.get('/centers') });
  const usersQuery = useQuery({ queryKey: ['users', page], queryFn: () => endpoints.get('/users', { page, size: 20 }) });

  const manageableRoles = optionsQuery.data?.manageableRoles ?? [];
  const defaultPermissionKeysByRole = optionsQuery.data?.defaultPermissionKeysByRole ?? {};
  const permissionGroups = optionsQuery.data?.permissionGroups ?? [];
  const scopeMode = form.role ? optionsQuery.data?.defaultScopeByRole?.[form.role] : null;

  // Filtered dropdowns
  const districts = useMemo(
    () => (districtsQuery.data ?? []).filter(d => !form.stateId || String(d.stateId) === String(form.stateId)),
    [districtsQuery.data, form.stateId]
  );
  const blocks = useMemo(
    () => (blocksQuery.data ?? []).filter(b => !form.districtId || String(b.districtId) === String(form.districtId)),
    [blocksQuery.data, form.districtId]
  );
  const centers = useMemo(
    () => (centersQuery.data ?? []).filter(c => !form.blockId || String(c.blockId) === String(form.blockId)),
    [centersQuery.data, form.blockId]
  );

  // Auto-apply role defaults
  useEffect(() => {
    if (form.role && defaultPermissionKeysByRole[form.role]) {
      setSelectedPermissionKeys(defaultPermissionKeysByRole[form.role]);
    }
  }, [form.role, defaultPermissionKeysByRole]);

  // Mutations
  const createMutation = useMutation({
    mutationFn: (payload) => editingUserId
      ? endpoints.put(`/users/${editingUserId}`, payload)
      : endpoints.post('/users/create', payload),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['users'] });
      if (!editingUserId && data?.temporaryPassword) {
        setCredentialResult(data);
      }
      toast.success(editingUserId ? 'User updated successfully' : 'User created successfully');
      resetForm();
      setShowCreateModal(false);
    },
    onError: (err) => {
      toast.error(err.response?.data?.message || 'Failed to save user');
    }
  });

  const deactivateMutation = useMutation({
    mutationFn: (id) => endpoints.del(`/users/${id}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] });
      toast.success('User deactivated');
      setConfirmDeactivate(null);
    },
    onError: (err) => {
      toast.error(err.response?.data?.message || 'Failed to deactivate user');
    }
  });

  const updateForm = (name, value) => {
    setForm(c => {
      const next = { ...c, [name]: value };
      if (name === 'stateId') { next.districtId = ''; next.blockId = ''; next.centerId = ''; }
      if (name === 'districtId') { next.blockId = ''; next.centerId = ''; }
      if (name === 'blockId') { next.centerId = ''; }
      return next;
    });
  };

  const togglePermission = (key) => {
    setSelectedPermissionKeys(c => c.includes(key) ? c.filter(k => k !== key) : [...c, key]);
  };

  const resetForm = () => {
    setForm(emptyForm);
    setSelectedPermissionKeys([]);
    setEditingUserId(null);
  };

  const openEdit = (u) => {
    setEditingUserId(u.id);
    setForm({
      name: u.name || '', email: u.email || '', phone: u.phone || '',
      role: u.role || '', stateId: '', districtId: '', blockId: '',
      centerId: u.centerId ? String(u.centerId) : '',
      notifyByEmail: true, notifyBySms: false
    });
    setShowCreateModal(true);
  };

  const submit = async (e) => {
    e.preventDefault();
    const payload = {
      name: form.name, email: form.email,
      phone: form.phone || undefined,
      role: form.role,
      stateId: form.stateId ? Number(form.stateId) : undefined,
      districtId: form.districtId ? Number(form.districtId) : undefined,
      blockId: form.blockId ? Number(form.blockId) : undefined,
      centerId: form.centerId ? Number(form.centerId) : undefined,
      notifyByEmail: form.notifyByEmail, notifyBySms: form.notifyBySms,
      ...(!editingUserId ? { permissionKeys: selectedPermissionKeys } : {})
    };
    createMutation.mutate(payload);
  };

  const needsState = ['STATE', 'DISTRICT', 'BLOCK', 'CENTER'].includes(scopeMode);
  const needsDistrict = ['DISTRICT', 'BLOCK', 'CENTER'].includes(scopeMode);
  const needsBlock = ['BLOCK', 'CENTER'].includes(scopeMode);
  const needsCenter = scopeMode === 'CENTER';

  const userList = usersQuery.data?.content ?? [];
  const totalPages = usersQuery.data?.totalPages ?? 1;

  return (
    <>
      <PageHeader
        eyebrow="Administration"
        title="User Management"
        description="Create and manage platform users with role-based permissions and geographic scoping."
        action={
          <button className="btn-primary" onClick={() => { resetForm(); setShowCreateModal(true); }}>
            <UserPlus size={16} /> Create User
          </button>
        }
      />

      {/* Credential result banner */}
      {credentialResult && (
        <div className="mb-6 card border-emerald-200 bg-emerald-50 p-5 animate-slide-up">
          <p className="font-semibold text-emerald-800">✓ User created successfully</p>
          <p className="mt-2 text-sm text-ash-700">
            Temporary password: <code className="rounded bg-white px-2 py-0.5 font-mono font-bold text-emerald-700">{credentialResult.temporaryPassword}</code>
          </p>
          {credentialResult.notifications?.map((n, i) => (
            <p key={i} className="mt-1 text-xs text-ash-600">{n.channel}: {n.status} — {n.message}</p>
          ))}
          <button className="mt-3 text-xs font-semibold text-emerald-700 hover:underline" onClick={() => setCredentialResult(null)}>Dismiss</button>
        </div>
      )}

      {/* Users Table */}
      <SectionCard title="All Users" description={`${usersQuery.data?.totalElements ?? 0} total users`}>
        {usersQuery.isError && (
          <div className="mb-4 rounded-xl bg-danger-50 border border-danger-200 px-4 py-3 text-sm text-danger-700">
            {usersQuery.error?.response?.data?.message || usersQuery.error?.message || 'Failed to load users'}
          </div>
        )}
        <DataTable
          rows={userList}
          page={page}
          totalPages={totalPages}
          onPageChange={setPage}
          columns={[
            { key: 'name', label: 'Name', render: (row) => (
              <div>
                <p className="font-medium text-ash-900">{row.name}</p>
                <p className="text-xs text-ash-500">{row.email}</p>
              </div>
            )},
            { key: 'role', label: 'Role', render: (row) => <RoleBadge role={row.role} /> },
            { key: 'scopeType', label: 'Scope', render: (row) => (
              <span className="badge badge-gray">{row.scopeType}</span>
            )},
            { key: 'centerName', label: 'Center', render: (row) => row.centerName || <span className="text-ash-300">—</span> },
            { key: 'active', label: 'Status', render: (row) => <ActiveBadge active={row.active} /> },
            { key: 'emailVerified', label: 'Verified', render: (row) => (
              <div className="flex gap-1">
                <VerifiedBadge verified={row.emailVerified} label="Email" />
              </div>
            )},
            ...(canManage ? [{
              key: 'actions', label: 'Actions', render: (row) => (
                <div className="flex gap-1">
                  <button className="btn-secondary btn-sm" onClick={() => openEdit(row)}>Edit</button>
                  {row.active && (
                    <button className="btn-sm text-xs text-danger-600 hover:bg-danger-50 rounded-lg px-2"
                      onClick={() => setConfirmDeactivate(row)}>
                      Deactivate
                    </button>
                  )}
                </div>
              )
            }] : [])
          ]}
        />
      </SectionCard>

      {/* Create/Edit Modal */}
      <Modal
        open={showCreateModal}
        onClose={() => { setShowCreateModal(false); resetForm(); }}
        title={editingUserId ? 'Edit User' : 'Create New User'}
        description={editingUserId ? 'Update user details and permissions.' : 'Fill in user details. A temporary password will be generated automatically.'}
        size="lg"
      >
        <form className="space-y-5" onSubmit={submit}>
          <div className="grid gap-4 sm:grid-cols-2">
            <FormField label="Full Name" value={form.name} onChange={v => updateForm('name', v)} required />
            <FormField label="Email" type="email" value={form.email} onChange={v => updateForm('email', v)} required disabled={!!editingUserId} />
            <FormField label="Phone" value={form.phone} onChange={v => updateForm('phone', v)} placeholder="+91 9876543210" />
            <SelectField label="Role" value={form.role} onChange={v => updateForm('role', v)} options={manageableRoles} required />

            {needsState && (
              <SelectField label="State" value={form.stateId} onChange={v => updateForm('stateId', v)}
                options={(statesQuery.data ?? []).map(s => ({ value: s.id, label: `${s.name} (${s.code})` }))} required />
            )}
            {needsDistrict && (
              <SelectField label="District" value={form.districtId} onChange={v => updateForm('districtId', v)}
                options={districts.map(d => ({ value: d.id, label: d.name }))} required />
            )}
            {needsBlock && (
              <SelectField label="Block" value={form.blockId} onChange={v => updateForm('blockId', v)}
                options={blocks.map(b => ({ value: b.id, label: b.name }))} required />
            )}
            {needsCenter && (
              <SelectField label="Center" value={form.centerId} onChange={v => updateForm('centerId', v)}
                options={centers.map(c => ({ value: c.id, label: `${c.code} — ${c.name}` }))} required />
            )}
          </div>

          {/* Notification toggles */}
          {!editingUserId && (
            <div className="flex flex-wrap gap-4 rounded-xl border border-ash-200 bg-ash-50 px-4 py-3">
              <Toggle label="Email notification" checked={form.notifyByEmail} onChange={v => updateForm('notifyByEmail', v)} />
              <Toggle label="SMS notification" checked={form.notifyBySms} onChange={v => updateForm('notifyBySms', v)} />
            </div>
          )}

          {/* Permission toggles */}
          {!editingUserId && permissionGroups.length > 0 && (
            <div className="rounded-xl border border-ash-200">
              <button type="button" className="flex w-full items-center justify-between px-4 py-3 text-sm font-semibold text-ash-700 hover:bg-ash-50"
                onClick={() => setIsTogglesOpen(!isTogglesOpen)}>
                <span>Permission Overrides ({selectedPermissionKeys.length} selected)</span>
                {isTogglesOpen ? <ChevronUp size={16} /> : <ChevronDown size={16} />}
              </button>
              {isTogglesOpen && (
                <div className="border-t border-ash-200 p-4 space-y-3 max-h-60 overflow-y-auto">
                  {permissionGroups.filter(g =>
                    g.key?.toLowerCase().includes('user') || g.key?.toLowerCase().includes('center') ||
                    g.label?.toLowerCase().includes('user') || g.label?.toLowerCase().includes('center')
                  ).map(group => (
                    <div key={group.key}>
                      <p className="text-xs font-bold text-ash-600 uppercase tracking-wider mb-2">{group.label}</p>
                      <div className="space-y-1">
                        {group.permissions.map(perm => (
                          <label key={perm.key} className="flex items-center gap-3 rounded-lg px-3 py-2 hover:bg-ash-50 cursor-pointer">
                            <input type="checkbox" className="h-4 w-4 rounded accent-brand-600"
                              checked={selectedPermissionKeys.includes(perm.key)}
                              onChange={() => togglePermission(perm.key)} />
                            <div className="flex-1 min-w-0">
                              <p className="text-sm font-medium text-ash-800">{perm.label}</p>
                              <p className="text-xs text-ash-500 truncate">{perm.description}</p>
                            </div>
                          </label>
                        ))}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {createMutation.isError && (
            <div className="rounded-xl border border-danger-200 bg-danger-50 px-4 py-3 text-sm text-danger-700">
              {createMutation.error?.response?.data?.message || 'Failed to save user'}
            </div>
          )}

          <div className="flex justify-end gap-3 border-t border-ash-100 pt-4">
            <button type="button" className="btn-secondary" onClick={() => { setShowCreateModal(false); resetForm(); }}>Cancel</button>
            <button type="submit" className="btn-primary" disabled={createMutation.isPending}>
              {createMutation.isPending ? 'Saving...' : editingUserId ? 'Update User' : 'Create User'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Deactivate confirmation */}
      <ConfirmDialog
        open={!!confirmDeactivate}
        onClose={() => setConfirmDeactivate(null)}
        onConfirm={() => deactivateMutation.mutate(confirmDeactivate?.id)}
        title="Deactivate User"
        message={`Are you sure you want to deactivate ${confirmDeactivate?.name}? They will lose access to the platform.`}
        confirmText="Deactivate"
        variant="danger"
        loading={deactivateMutation.isPending}
      />
    </>
  );
}

function FormField({ label, value, onChange, type = 'text', required = false, placeholder = '', disabled = false }) {
  return (
    <label className="block">
      <span className="label">{label}{required && ' *'}</span>
      <input className={`input ${disabled ? 'bg-ash-50' : ''}`} type={type} value={value} required={required}
        placeholder={placeholder} disabled={disabled} onChange={e => onChange(e.target.value)} />
    </label>
  );
}

function SelectField({ label, value, onChange, options, required = false }) {
  const normalized = (options ?? []).map(o => typeof o === 'string' ? { value: o, label: o.replace(/_/g, ' ') } : o);
  return (
    <label className="block">
      <span className="label">{label}{required && ' *'}</span>
      <select className="select" value={value} required={required} onChange={e => onChange(e.target.value)}>
        <option value="">Select {label.toLowerCase()}</option>
        {normalized.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
      </select>
    </label>
  );
}

function Toggle({ label, checked, onChange }) {
  return (
    <label className="inline-flex cursor-pointer items-center gap-2 text-sm font-medium text-ash-700">
      <input type="checkbox" className="h-4 w-4 rounded accent-brand-600" checked={checked} onChange={e => onChange(e.target.checked)} />
      {label}
    </label>
  );
}
