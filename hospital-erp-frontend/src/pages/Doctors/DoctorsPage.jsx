import ApiFormCard from '../../components/ApiFormCard.jsx';
import PageHeader from '../../components/PageHeader.jsx';
import QueryTableCard from '../../components/QueryTableCard.jsx';
import { weekDays } from '../shared/forms.js';

export default function DoctorsPage() {
  return (
    <>
      <PageHeader eyebrow="Phase 4" title="Doctor Profiles & Schedules" description="Create doctor profiles and manage weekly availability slots." />
      <div className="grid gap-6 xl:grid-cols-2">
        <ApiFormCard title="Create Doctor Profile" endpoint="/doctors/profile" invalidate={['doctors']} fields={[
          { name: 'userId', label: 'Doctor User ID', type: 'number', required: true },
          { name: 'centerId', label: 'Center ID', type: 'number' },
          { name: 'specialization', label: 'Specialization' },
          { name: 'qualification', label: 'Qualification' },
          { name: 'experienceYears', label: 'Experience Years', type: 'number' },
          { name: 'consultationFee', label: 'Consultation Fee', type: 'number' }
        ]} />
        <ApiFormCard title="Schedule JSON" description="PUT endpoint accepts an array. Replace 1 with the doctor profile ID in API if needed." endpoint="/doctors/1/schedule" method="put" sample={[
          { dayOfWeek: weekDays[0], startTime: '09:00:00', endTime: '13:00:00', slotDurationMins: 15, maxPatients: 16, active: true }
        ]} submitLabel="Save Schedule JSON" />
      </div>
      <div className="mt-6">
        <QueryTableCard title="Available Doctors" queryKey="doctors" endpoint="/doctors" columns={[
          { key: 'id', label: 'Profile ID' },
          { key: 'user.name', label: 'Doctor' },
          { key: 'specialization', label: 'Specialization' },
          { key: 'qualification', label: 'Qualification' },
          { key: 'consultationFee', label: 'Fee' },
          { key: 'available', label: 'Available' }
        ]} />
      </div>
    </>
  );
}
