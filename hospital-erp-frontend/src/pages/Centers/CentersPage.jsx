import { useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { endpoints } from '../../api/endpoints.js';
import DataTable from '../../components/DataTable.jsx';
import PageHeader from '../../components/PageHeader.jsx';
import SectionCard from '../../components/SectionCard.jsx';

export default function CentersPage() {
  const queryClient = useQueryClient();
  const [stateForm, setStateForm] = useState({ name: '', code: '' });
  const [districtForm, setDistrictForm] = useState({ name: '', stateId: '' });
  const [blockForm, setBlockForm] = useState({ name: '', districtId: '' });
  const [centerForm, setCenterForm] = useState({ name: '', city: '', stateId: '', districtId: '', blockId: '', phone: '', email: '', pincode: '', address: '' });

  const statesQuery = useQuery({ queryKey: ['states'], queryFn: () => endpoints.get('/states') });
  const districtsQuery = useQuery({ queryKey: ['districts'], queryFn: () => endpoints.get('/districts') });
  const blocksQuery = useQuery({ queryKey: ['blocks'], queryFn: () => endpoints.get('/blocks') });
  const centersQuery = useQuery({ queryKey: ['centers'], queryFn: () => endpoints.get('/centers') });

  const districts = useMemo(
    () => (districtsQuery.data ?? []).filter((district) => !centerForm.stateId || String(district.stateId) === String(centerForm.stateId)),
    [districtsQuery.data, centerForm.stateId]
  );
  const blocks = useMemo(
    () => (blocksQuery.data ?? []).filter((block) => !centerForm.districtId || String(block.districtId) === String(centerForm.districtId)),
    [blocksQuery.data, centerForm.districtId]
  );

  const stateMutation = useMutation({
    mutationFn: (payload) => endpoints.post('/states', payload),
    onSuccess: () => {
      setStateForm({ name: '', code: '' });
      queryClient.invalidateQueries({ queryKey: ['states'] });
    }
  });
  const districtMutation = useMutation({
    mutationFn: (payload) => endpoints.post('/districts', payload),
    onSuccess: () => {
      setDistrictForm({ name: '', stateId: '' });
      queryClient.invalidateQueries({ queryKey: ['districts'] });
    }
  });
  const blockMutation = useMutation({
    mutationFn: (payload) => endpoints.post('/blocks', payload),
    onSuccess: () => {
      setBlockForm({ name: '', districtId: '' });
      queryClient.invalidateQueries({ queryKey: ['blocks'] });
    }
  });
  const centerMutation = useMutation({
    mutationFn: (payload) => endpoints.post('/centers', payload),
    onSuccess: () => {
      setCenterForm({ name: '', city: '', stateId: '', districtId: '', blockId: '', phone: '', email: '', pincode: '', address: '' });
      queryClient.invalidateQueries({ queryKey: ['centers'] });
    }
  });

  return (
    <>
      <PageHeader eyebrow="Phase 1" title="Geography & Center Setup" description="Create states, districts, blocks, and centers using dropdown-based forms instead of raw IDs." />
      <div className="grid gap-6 xl:grid-cols-2">
        <SectionCard title="Create State">
          <SimpleForm
            fields={[
              { label: 'State Name', value: stateForm.name, onChange: (value) => setStateForm((current) => ({ ...current, name: value })), required: true },
              { label: 'State Code', value: stateForm.code, onChange: (value) => setStateForm((current) => ({ ...current, code: value.toUpperCase() })), required: true }
            ]}
            busy={stateMutation.isPending}
            submitLabel="Create State"
            error={stateMutation.error}
            onSubmit={() => stateMutation.mutateAsync(stateForm)}
          />
        </SectionCard>
        <SectionCard title="Create District">
          <SimpleForm
            fields={[
              { label: 'District Name', value: districtForm.name, onChange: (value) => setDistrictForm((current) => ({ ...current, name: value })), required: true },
              { label: 'State', type: 'select', value: districtForm.stateId, onChange: (value) => setDistrictForm((current) => ({ ...current, stateId: value })), options: (statesQuery.data ?? []).map((state) => ({ value: state.id, label: state.name })), required: true }
            ]}
            busy={districtMutation.isPending}
            submitLabel="Create District"
            error={districtMutation.error}
            onSubmit={() => districtMutation.mutateAsync({ name: districtForm.name, stateId: Number(districtForm.stateId) })}
          />
        </SectionCard>
        <SectionCard title="Create Block">
          <SimpleForm
            fields={[
              { label: 'Block Name', value: blockForm.name, onChange: (value) => setBlockForm((current) => ({ ...current, name: value })), required: true },
              { label: 'District', type: 'select', value: blockForm.districtId, onChange: (value) => setBlockForm((current) => ({ ...current, districtId: value })), options: (districtsQuery.data ?? []).map((district) => ({ value: district.id, label: `${district.name} (${district.stateName})` })), required: true }
            ]}
            busy={blockMutation.isPending}
            submitLabel="Create Block"
            error={blockMutation.error}
            onSubmit={() => blockMutation.mutateAsync({ name: blockForm.name, districtId: Number(blockForm.districtId) })}
          />
        </SectionCard>
        <SectionCard title="Create Center" description="A center code is generated automatically.">
          <SimpleForm
            fields={[
              { label: 'Center Name', value: centerForm.name, onChange: (value) => setCenterForm((current) => ({ ...current, name: value })), required: true },
              { label: 'City', value: centerForm.city, onChange: (value) => setCenterForm((current) => ({ ...current, city: value })) },
              { label: 'State', type: 'select', value: centerForm.stateId, onChange: (value) => setCenterForm((current) => ({ ...current, stateId: value, districtId: '', blockId: '' })), options: (statesQuery.data ?? []).map((state) => ({ value: state.id, label: state.name })), required: true },
              { label: 'District', type: 'select', value: centerForm.districtId, onChange: (value) => setCenterForm((current) => ({ ...current, districtId: value, blockId: '' })), options: districts.map((district) => ({ value: district.id, label: district.name })), required: true },
              { label: 'Block', type: 'select', value: centerForm.blockId, onChange: (value) => setCenterForm((current) => ({ ...current, blockId: value })), options: blocks.map((block) => ({ value: block.id, label: block.name })), required: true },
              { label: 'Phone', value: centerForm.phone, onChange: (value) => setCenterForm((current) => ({ ...current, phone: value })) },
              { label: 'Email', type: 'email', value: centerForm.email, onChange: (value) => setCenterForm((current) => ({ ...current, email: value })) },
              { label: 'Pincode', value: centerForm.pincode, onChange: (value) => setCenterForm((current) => ({ ...current, pincode: value })) },
              { label: 'Address', type: 'textarea', full: true, value: centerForm.address, onChange: (value) => setCenterForm((current) => ({ ...current, address: value })) }
            ]}
            busy={centerMutation.isPending}
            submitLabel="Create Center"
            error={centerMutation.error}
            onSubmit={() => centerMutation.mutateAsync({
              ...centerForm,
              stateId: Number(centerForm.stateId),
              districtId: Number(centerForm.districtId),
              blockId: Number(centerForm.blockId)
            })}
          />
        </SectionCard>
      </div>
      <div className="mt-6 grid gap-6 xl:grid-cols-2">
        <SectionCard title="States">
          <DataTable rows={statesQuery.data ?? []} columns={[{ key: 'name', label: 'State' }, { key: 'code', label: 'Code' }]} />
        </SectionCard>
        <SectionCard title="Centers">
          <DataTable
            rows={centersQuery.data ?? []}
            columns={[
              { key: 'code', label: 'Code' },
              { key: 'name', label: 'Center' },
              { key: 'city', label: 'City' },
              { key: 'stateName', label: 'State' },
              { key: 'districtName', label: 'District' },
              { key: 'blockName', label: 'Block' },
              { key: 'active', label: 'Active' }
            ]}
          />
        </SectionCard>
      </div>
    </>
  );
}

function SimpleForm({ fields, submitLabel, busy, error, onSubmit }) {
  return (
    <form className="grid gap-4 md:grid-cols-2" onSubmit={(event) => { event.preventDefault(); onSubmit(); }}>
      {fields.map((field) => (
        <label key={field.label} className={field.full ? 'md:col-span-2' : ''}>
          <span className="mb-1 block text-xs font-semibold uppercase tracking-wide text-slate-500">{field.label}{field.required ? ' *' : ''}</span>
          {field.type === 'select' ? (
            <select className="clinical-input w-full" value={field.value} required={field.required} onChange={(event) => field.onChange(event.target.value)}>
              <option value="">Select</option>
              {(field.options ?? []).map((option) => (
                <option key={option.value} value={option.value}>{option.label}</option>
              ))}
            </select>
          ) : field.type === 'textarea' ? (
            <textarea className="min-h-24 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-100" value={field.value} required={field.required} onChange={(event) => field.onChange(event.target.value)} />
          ) : (
            <input className="clinical-input w-full" type={field.type || 'text'} value={field.value} required={field.required} onChange={(event) => field.onChange(event.target.value)} />
          )}
        </label>
      ))}
      <div className="md:col-span-2">
        <button className="clinical-button" disabled={busy} type="submit">
          {busy ? 'Saving...' : submitLabel}
        </button>
      </div>
      {error ? <p className="md:col-span-2 rounded-xl bg-red-50 px-3 py-2 text-sm font-medium text-red-700">{error.response?.data?.message || error.message}</p> : null}
    </form>
  );
}
