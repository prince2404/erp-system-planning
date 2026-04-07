import { useState } from 'react';
import ApiFormCard from '../../components/ApiFormCard.jsx';
import PageHeader from '../../components/PageHeader.jsx';
import QueryTableCard from '../../components/QueryTableCard.jsx';
import StatusBadge from '../../components/StatusBadge.jsx';

export default function IpdPage() {
  const [centerId, setCenterId] = useState('');
  return (
    <>
      <PageHeader eyebrow="Phase 3" title="IPD & Bed Management" description="Manage beds, admit patients, discharge patients, and trigger draft billing." />
      <div className="grid gap-6 xl:grid-cols-3">
        <ApiFormCard title="Create Bed" endpoint="/beds" invalidate={['beds']} fields={[
          { name: 'centerId', label: 'Center ID', type: 'number', required: true },
          { name: 'ward', label: 'Ward', required: true },
          { name: 'bedNumber', label: 'Bed Number', required: true }
        ]} />
        <ApiFormCard title="Admit Patient" endpoint="/ipd/admit" invalidate={['ipd-active', 'beds']} fields={[
          { name: 'patientId', label: 'Patient ID', type: 'number', required: true },
          { name: 'doctorId', label: 'Doctor User ID', type: 'number' },
          { name: 'centerId', label: 'Center ID', type: 'number', required: true },
          { name: 'bedId', label: 'Bed ID', type: 'number', required: true },
          { name: 'dailyCharge', label: 'Daily Charge', type: 'number' },
          { name: 'diagnosis', label: 'Diagnosis', type: 'textarea', full: true }
        ]} />
        <ApiFormCard title="Discharge Patient" endpoint={(payload) => `/ipd/discharge/${payload.id}`} method="put" invalidate={['ipd-active', 'beds']} fields={[
          { name: 'id', label: 'Admission ID', type: 'number', required: true },
          { name: 'treatmentNotes', label: 'Treatment Notes', type: 'textarea', full: true }
        ]} submitLabel="Discharge" />
      </div>
      <div className="mt-6">
        <input className="clinical-input mb-3 max-w-sm" placeholder="Center ID" value={centerId} onChange={(event) => setCenterId(event.target.value)} />
        <div className="grid gap-6 xl:grid-cols-2">
          <QueryTableCard title="Available Beds" queryKey="beds" endpoint="/beds/available" params={centerId ? { centerId: Number(centerId) } : { enabled: false }} columns={[
            { key: 'id', label: 'ID' },
            { key: 'ward', label: 'Ward' },
            { key: 'bedNumber', label: 'Bed' },
            { key: 'occupied', label: 'Occupied' }
          ]} />
          <QueryTableCard title="Active Admissions" queryKey="ipd-active" endpoint="/ipd/active" params={centerId ? { centerId: Number(centerId) } : { enabled: false }} columns={[
            { key: 'id', label: 'ID' },
            { key: 'patient.name', label: 'Patient' },
            { key: 'bed.bedNumber', label: 'Bed' },
            { key: 'admissionDate', label: 'Admitted' },
            { key: 'status', label: 'Status', render: (row) => <StatusBadge value={row.status} /> }
          ]} />
        </div>
      </div>
    </>
  );
}
