import { useState } from 'react';
import ApiFormCard from '../../components/ApiFormCard.jsx';
import PageHeader from '../../components/PageHeader.jsx';
import QueryTableCard from '../../components/QueryTableCard.jsx';
import StatusBadge from '../../components/StatusBadge.jsx';
import { appointmentTypes } from '../shared/forms.js';

export default function AppointmentsPage() {
  const [doctorId, setDoctorId] = useState('');
  const [date, setDate] = useState('');
  return (
    <>
      <PageHeader eyebrow="Phase 4" title="Appointment Scheduling" description="Generate slots from weekly doctor schedules and book without double-booking." />
      <div className="grid gap-6 xl:grid-cols-2">
        <ApiFormCard title="Book Appointment" endpoint="/appointments/book" invalidate={['appointments', 'slots']} fields={[
          { name: 'patientId', label: 'Patient ID', type: 'number', required: true },
          { name: 'doctorId', label: 'Doctor Profile ID', type: 'number', required: true },
          { name: 'centerId', label: 'Center ID', type: 'number' },
          { name: 'appointmentDate', label: 'Date', type: 'date', required: true },
          { name: 'slotTime', label: 'Slot Time (HH:mm:ss)', required: true },
          { name: 'type', label: 'Type', type: 'select', options: appointmentTypes },
          { name: 'bookingFee', label: 'Booking Fee', type: 'number' },
          { name: 'notes', label: 'Notes', type: 'textarea', full: true }
        ]} />
        <div className="clinical-card p-5">
          <h2 className="text-base font-semibold text-slate-950">Slot & Appointment Filters</h2>
          <div className="mt-4 grid gap-3 md:grid-cols-2">
            <input className="clinical-input" placeholder="Doctor Profile ID" value={doctorId} onChange={(event) => setDoctorId(event.target.value)} />
            <input className="clinical-input" type="date" value={date} onChange={(event) => setDate(event.target.value)} />
          </div>
        </div>
      </div>
      <div className="mt-6 grid gap-6 xl:grid-cols-2">
        <QueryTableCard title="Available Slots" queryKey="slots" endpoint="/appointments/slots" params={doctorId && date ? { doctorId: Number(doctorId), date } : { enabled: false }} columns={[
          { key: 'time', label: 'Time' },
          { key: 'available', label: 'Available' }
        ]} />
        <QueryTableCard title="Appointments" queryKey="appointments" endpoint="/appointments" params={doctorId ? { doctorId: Number(doctorId), date } : { enabled: false }} columns={[
          { key: 'id', label: 'ID' },
          { key: 'patient.name', label: 'Patient' },
          { key: 'slotTime', label: 'Slot' },
          { key: 'status', label: 'Status', render: (row) => <StatusBadge value={row.status} /> }
        ]} />
      </div>
    </>
  );
}
