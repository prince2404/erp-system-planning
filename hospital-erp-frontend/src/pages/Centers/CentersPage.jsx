import { useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { MapPinned, Plus, Building2, Phone, Mail } from 'lucide-react';
import { endpoints } from '../../api/endpoints.js';
import { toast } from '../../components/Toast.jsx';
import PageHeader from '../../components/PageHeader.jsx';
import SectionCard from '../../components/SectionCard.jsx';
import DataTable from '../../components/DataTable.jsx';
import Modal from '../../components/Modal.jsx';
import { ActiveBadge } from '../../components/Badge.jsx';
import ConfirmDialog from '../../components/ConfirmDialog.jsx';

export default function CentersPage() {
  const queryClient = useQueryClient();
  const [tab, setTab] = useState('centers');
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState('center');
  const [deactivateTarget, setDeactivateTarget] = useState(null);

  // State form
  const [stateForm, setStateForm] = useState({ name: '', code: '' });
  // District form
  const [districtForm, setDistrictForm] = useState({ name: '', code: '', stateId: '' });
  // Block form
  const [blockForm, setBlockForm] = useState({ name: '', code: '', districtId: '' });
  // Center form
  const [centerForm, setCenterForm] = useState({ name: '', code: '', address: '', city: '', phone: '', email: '', pincode: '', blockId: '', districtId: '', stateId: '' });

  // Queries
  const statesQuery = useQuery({ queryKey: ['states'], queryFn: () => endpoints.get('/states') });
  const districtsQuery = useQuery({ queryKey: ['districts'], queryFn: () => endpoints.get('/districts') });
  const blocksQuery = useQuery({ queryKey: ['blocks'], queryFn: () => endpoints.get('/blocks') });
  const centersQuery = useQuery({ queryKey: ['centers'], queryFn: () => endpoints.get('/centers') });

  // Filtered lists for cascading
  const filteredDistricts = useMemo(
    () => (districtsQuery.data ?? []).filter(d => !centerForm.stateId || String(d.stateId) === String(centerForm.stateId)),
    [districtsQuery.data, centerForm.stateId]
  );
  const filteredBlocks = useMemo(
    () => (blocksQuery.data ?? []).filter(b => !centerForm.districtId || String(b.districtId) === String(centerForm.districtId)),
    [blocksQuery.data, centerForm.districtId]
  );

  // Mutations
  const stateMutation = useMutation({
    mutationFn: (p) => endpoints.post('/states', p),
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['states'] }); toast.success('State created'); closeModal(); },
    onError: (e) => toast.error(e.response?.data?.message || 'Failed')
  });
  const districtMutation = useMutation({
    mutationFn: (p) => endpoints.post('/districts', p),
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['districts'] }); toast.success('District created'); closeModal(); },
    onError: (e) => toast.error(e.response?.data?.message || 'Failed')
  });
  const blockMutation = useMutation({
    mutationFn: (p) => endpoints.post('/blocks', p),
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['blocks'] }); toast.success('Block created'); closeModal(); },
    onError: (e) => toast.error(e.response?.data?.message || 'Failed')
  });
  const centerMutation = useMutation({
    mutationFn: (p) => endpoints.post('/centers', p),
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['centers'] }); toast.success('Center created'); closeModal(); },
    onError: (e) => toast.error(e.response?.data?.message || 'Failed')
  });
  const deactivateMutation = useMutation({
    mutationFn: (id) => endpoints.del(`/centers/${id}`),
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['centers'] }); toast.success('Center deactivated'); setDeactivateTarget(null); },
    onError: (e) => toast.error(e.response?.data?.message || 'Failed')
  });

  const openModal = (type) => { setModalType(type); setShowModal(true); };
  const closeModal = () => {
    setShowModal(false);
    setStateForm({ name: '', code: '' });
    setDistrictForm({ name: '', code: '', stateId: '' });
    setBlockForm({ name: '', code: '', districtId: '' });
    setCenterForm({ name: '', code: '', address: '', city: '', phone: '', email: '', pincode: '', blockId: '', districtId: '', stateId: '' });
  };

  const updateCenter = (key, value) => {
    setCenterForm(c => {
      const next = { ...c, [key]: value };
      if (key === 'stateId') { next.districtId = ''; next.blockId = ''; }
      if (key === 'districtId') { next.blockId = ''; }
      return next;
    });
  };

  const submitForm = (e) => {
    e.preventDefault();
    switch (modalType) {
      case 'state': stateMutation.mutate(stateForm); break;
      case 'district': districtMutation.mutate({ ...districtForm, stateId: Number(districtForm.stateId) }); break;
      case 'block': blockMutation.mutate({ ...blockForm, districtId: Number(blockForm.districtId) }); break;
      case 'center': centerMutation.mutate({
        ...centerForm,
        stateId: centerForm.stateId ? Number(centerForm.stateId) : undefined,
        districtId: centerForm.districtId ? Number(centerForm.districtId) : undefined,
        blockId: centerForm.blockId ? Number(centerForm.blockId) : undefined
      }); break;
    }
  };

  const tabs = [
    { key: 'centers', label: 'Centers', count: centersQuery.data?.length },
    { key: 'states', label: 'States', count: statesQuery.data?.length },
    { key: 'districts', label: 'Districts', count: districtsQuery.data?.length },
    { key: 'blocks', label: 'Blocks', count: blocksQuery.data?.length }
  ];

  return (
    <>
      <PageHeader
        eyebrow="Administration"
        title="Geographic Hierarchy & Centers"
        description="Manage states, districts, blocks, and health centers for the platform network."
        action={
          <div className="flex gap-2">
            <button className="btn-secondary btn-sm" onClick={() => openModal('state')}>+ State</button>
            <button className="btn-secondary btn-sm" onClick={() => openModal('district')}>+ District</button>
            <button className="btn-secondary btn-sm" onClick={() => openModal('block')}>+ Block</button>
            <button className="btn-primary" onClick={() => openModal('center')}>
              <Building2 size={16} /> New Center
            </button>
          </div>
        }
      />

      {/* Tabs */}
      <div className="mb-6 flex gap-1 rounded-xl bg-ash-100 p-1">
        {tabs.map(t => (
          <button key={t.key} onClick={() => setTab(t.key)}
            className={`flex-1 rounded-lg px-4 py-2 text-sm font-semibold transition-all ${
              tab === t.key ? 'bg-white text-ash-900 shadow-sm' : 'text-ash-500 hover:text-ash-700'
            }`}>
            {t.label} {t.count != null && <span className="ml-1 text-xs text-ash-400">({t.count})</span>}
          </button>
        ))}
      </div>

      {/* Tab content */}
      {tab === 'centers' && (
        <SectionCard title="Health Centers" description="Active health centers in the network">
          <DataTable rows={centersQuery.data ?? []} columns={[
            { key: 'code', label: 'Code', render: r => <span className="font-mono text-xs font-bold text-brand-600">{r.code}</span> },
            { key: 'name', label: 'Name', render: r => <span className="font-medium text-ash-900">{r.name}</span> },
            { key: 'city', label: 'City' },
            { key: 'stateName', label: 'State' },
            { key: 'districtName', label: 'District' },
            { key: 'blockName', label: 'Block' },
            { key: 'phone', label: 'Phone' },
            { key: 'active', label: 'Status', render: r => <ActiveBadge active={r.active} /> },
            { key: 'actions', label: '', render: r => r.active ? (
              <button className="btn-sm text-xs text-danger-600 hover:bg-danger-50 rounded-lg px-2"
                onClick={() => setDeactivateTarget(r)}>Deactivate</button>
            ) : null }
          ]} emptyTitle="No centers yet" emptyDescription="Create your first health center to get started." />
        </SectionCard>
      )}

      {tab === 'states' && (
        <SectionCard title="States" description="States where the platform operates">
          <DataTable rows={statesQuery.data ?? []} columns={[
            { key: 'id', label: 'ID' },
            { key: 'code', label: 'Code', render: r => <span className="font-mono font-bold text-brand-600">{r.code}</span> },
            { key: 'name', label: 'Name', render: r => <span className="font-medium text-ash-900">{r.name}</span> }
          ]} emptyTitle="No states yet" />
        </SectionCard>
      )}

      {tab === 'districts' && (
        <SectionCard title="Districts">
          <DataTable rows={districtsQuery.data ?? []} columns={[
            { key: 'id', label: 'ID' },
            { key: 'code', label: 'Code', render: r => <span className="font-mono font-bold">{r.code}</span> },
            { key: 'name', label: 'Name', render: r => <span className="font-medium text-ash-900">{r.name}</span> },
            { key: 'stateName', label: 'State' }
          ]} emptyTitle="No districts yet" />
        </SectionCard>
      )}

      {tab === 'blocks' && (
        <SectionCard title="Blocks">
          <DataTable rows={blocksQuery.data ?? []} columns={[
            { key: 'id', label: 'ID' },
            { key: 'code', label: 'Code', render: r => <span className="font-mono font-bold">{r.code}</span> },
            { key: 'name', label: 'Name', render: r => <span className="font-medium text-ash-900">{r.name}</span> },
            { key: 'districtName', label: 'District' }
          ]} emptyTitle="No blocks yet" />
        </SectionCard>
      )}

      {/* Create Modal */}
      <Modal open={showModal} onClose={closeModal}
        title={`Create ${modalType.charAt(0).toUpperCase() + modalType.slice(1)}`}
        size={modalType === 'center' ? 'lg' : 'md'}>
        <form className="space-y-4" onSubmit={submitForm}>
          {modalType === 'state' && (
            <div className="grid gap-4 sm:grid-cols-2">
              <Field label="State Name" value={stateForm.name} onChange={v => setStateForm(c => ({ ...c, name: v }))} required />
              <Field label="Code (e.g. BH)" value={stateForm.code} onChange={v => setStateForm(c => ({ ...c, code: v }))} required placeholder="BH" />
            </div>
          )}
          {modalType === 'district' && (
            <div className="grid gap-4 sm:grid-cols-2">
              <Field label="District Name" value={districtForm.name} onChange={v => setDistrictForm(c => ({ ...c, name: v }))} required />
              <Field label="Code" value={districtForm.code} onChange={v => setDistrictForm(c => ({ ...c, code: v }))} required />
              <SelectField label="State" value={districtForm.stateId} onChange={v => setDistrictForm(c => ({ ...c, stateId: v }))}
                options={(statesQuery.data ?? []).map(s => ({ value: s.id, label: `${s.name} (${s.code})` }))} required />
            </div>
          )}
          {modalType === 'block' && (
            <div className="grid gap-4 sm:grid-cols-2">
              <Field label="Block Name" value={blockForm.name} onChange={v => setBlockForm(c => ({ ...c, name: v }))} required />
              <Field label="Code" value={blockForm.code} onChange={v => setBlockForm(c => ({ ...c, code: v }))} required />
              <SelectField label="District" value={blockForm.districtId} onChange={v => setBlockForm(c => ({ ...c, districtId: v }))}
                options={(districtsQuery.data ?? []).map(d => ({ value: d.id, label: d.name }))} required />
            </div>
          )}
          {modalType === 'center' && (
            <div className="grid gap-4 sm:grid-cols-2">
              <Field label="Center Name" value={centerForm.name} onChange={v => updateCenter('name', v)} required />
              <Field label="Center Code" value={centerForm.code} onChange={v => updateCenter('code', v)} required placeholder="ASK-BH-PTN-001" />
              <SelectField label="State" value={centerForm.stateId} onChange={v => updateCenter('stateId', v)}
                options={(statesQuery.data ?? []).map(s => ({ value: s.id, label: `${s.name} (${s.code})` }))} required />
              <SelectField label="District" value={centerForm.districtId} onChange={v => updateCenter('districtId', v)}
                options={filteredDistricts.map(d => ({ value: d.id, label: d.name }))} required />
              <SelectField label="Block" value={centerForm.blockId} onChange={v => updateCenter('blockId', v)}
                options={filteredBlocks.map(b => ({ value: b.id, label: b.name }))} required />
              <Field label="City" value={centerForm.city} onChange={v => updateCenter('city', v)} />
              <Field label="Phone" value={centerForm.phone} onChange={v => updateCenter('phone', v)} />
              <Field label="Email" type="email" value={centerForm.email} onChange={v => updateCenter('email', v)} />
              <Field label="Pincode" value={centerForm.pincode} onChange={v => updateCenter('pincode', v)} />
              <div className="sm:col-span-2">
                <label className="block">
                  <span className="label">Address</span>
                  <textarea className="textarea" value={centerForm.address} onChange={e => updateCenter('address', e.target.value)} rows={2} />
                </label>
              </div>
            </div>
          )}
          <div className="flex justify-end gap-3 pt-2">
            <button type="button" className="btn-secondary" onClick={closeModal}>Cancel</button>
            <button type="submit" className="btn-primary" disabled={
              stateMutation.isPending || districtMutation.isPending || blockMutation.isPending || centerMutation.isPending
            }>Create</button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog open={!!deactivateTarget} onClose={() => setDeactivateTarget(null)}
        onConfirm={() => deactivateMutation.mutate(deactivateTarget?.id)}
        title="Deactivate Center" message={`Deactivate ${deactivateTarget?.name}? Staff will lose access.`}
        confirmText="Deactivate" variant="danger" loading={deactivateMutation.isPending} />
    </>
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

function SelectField({ label, value, onChange, options, required = false }) {
  const normalized = (options ?? []).map(o => typeof o === 'string' ? { value: o, label: o } : o);
  return (
    <label className="block">
      <span className="label">{label}{required && ' *'}</span>
      <select className="select" value={value} required={required} onChange={e => onChange(e.target.value)}>
        <option value="">Select</option>
        {normalized.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
      </select>
    </label>
  );
}
