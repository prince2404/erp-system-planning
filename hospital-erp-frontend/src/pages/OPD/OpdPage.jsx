import { useState } from 'react';
import ApiFormCard from '../../components/ApiFormCard.jsx';
import PageHeader from '../../components/PageHeader.jsx';
import QueryTableCard from '../../components/QueryTableCard.jsx';
import StatusBadge from '../../components/StatusBadge.jsx';
import { visitStatuses } from '../shared/forms.js';

export default function OpdPage() {
  const [centerId, setCenterId] = useState('');
  const [visitId, setVisitId] = useState('');

  return (
    <>
      <PageHeader eyebrow="Phase 3" title="OPD Queue" description="Create OPD visits with daily per-center token generation and update doctor status." />
      <div className="grid gap-6 xl:grid-cols-2">
        <ApiFormCard title="New OPD Visit" endpoint="/opd/visit" invalidate={['opd-queue']} fields={[
          { name: 'patientId', label: 'Patient ID', type: 'number', required: true },
          { name: 'doctorId', label: 'Doctor User ID', type: 'number' },
          { name: 'centerId', label: 'Center ID', type: 'number', required: true },
          { name: 'visitDate', label: 'Visit Date', type: 'date' },
          { name: 'fee', label: 'Consultation Fee', type: 'number' },
          { name: 'symptoms', label: 'Symptoms', type: 'textarea', full: true }
        ]} submitLabel="Create Visit" />
        <div className="clinical-card p-5">
          <h2 className="text-base font-semibold text-slate-950">Update Visit Status</h2>
          <div className="mt-4">
            <input className="clinical-input mb-3 w-full" placeholder="OPD Visit ID" value={visitId} onChange={(event) => setVisitId(event.target.value)} />
            {visitId ? (
              <ApiFormCard title="Status Payload" endpoint={`/opd/visit/${visitId}/status`} method="put" invalidate={['opd-queue']} fields={[
                { name: 'status', label: 'Status', type: 'select', options: visitStatuses, required: true },
                { name: 'diagnosis', label: 'Diagnosis', type: 'textarea', full: true },
                { name: 'prescriptionNotes', label: 'Prescription Notes', type: 'textarea', full: true }
              ]} submitLabel="Update Status" />
            ) : <p className="text-sm text-slate-500">Enter an OPD visit ID to update status.</p>}
          </div>
        </div>
      </div>
      <div className="mt-6">
        <div className="mb-3 flex max-w-sm gap-2">
          <input className="clinical-input flex-1" placeholder="Center ID for queue" value={centerId} onChange={(event) => setCenterId(event.target.value)} />
        </div>
        <QueryTableCard title="Today Queue" queryKey="opd-queue" endpoint="/opd/queue" params={centerId ? { centerId: Number(centerId) } : { enabled: false }} columns={[
          { key: 'tokenNumber', label: 'Token' },
          { key: 'patient.name', label: 'Patient' },
          { key: 'doctor.name', label: 'Doctor' },
          { key: 'visitDate', label: 'Date' },
          { key: 'status', label: 'Status', render: (row) => <StatusBadge value={row.status} /> }
        ]} />
      </div>
    </>
  );
}
