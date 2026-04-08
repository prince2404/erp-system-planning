import ApiFormCard from '../../components/ApiFormCard.jsx';
import PageHeader from '../../components/PageHeader.jsx';
import QueryTableCard from '../../components/QueryTableCard.jsx';
import { genderOptions } from '../shared/forms.js';

export default function PatientsPage() {
  return (
    <>
      <PageHeader eyebrow="Phase 3" title="Patient Management" description="Register patients with auto-generated UHID and search real patient records." />
      <div className="grid gap-6 xl:grid-cols-[1.1fr_0.9fr]">
        <ApiFormCard
          title="Register Patient"
          endpoint="/patients/register"
          invalidate={['patients']}
          fields={[
            { name: 'name', label: 'Full Name', required: true },
            { name: 'phone', label: 'Phone', required: true },
            { name: 'email', label: 'Email', type: 'email' },
            { name: 'gender', label: 'Gender', type: 'select', options: genderOptions },
            { name: 'age', label: 'Age', type: 'number' },
            { name: 'dateOfBirth', label: 'Date of Birth', type: 'date' },
            { name: 'bloodGroup', label: 'Blood Group' },
            { name: 'centerId', label: 'Center ID', type: 'number', required: true },
            { name: 'emergencyName', label: 'Emergency Name' },
            { name: 'emergencyContact', label: 'Emergency Contact' },
            { name: 'address', label: 'Address', type: 'textarea', full: true },
            { name: 'allergies', label: 'Allergies', type: 'textarea', full: true }
          ]}
          submitLabel="Register Patient"
        />
        <QueryTableCard title="Patients" queryKey="patients" endpoint="/patients" params={{ page: 0, size: 25 }} columns={[
          { key: 'id', label: 'ID' },
          { key: 'uhid', label: 'UHID' },
          { key: 'name', label: 'Name' },
          { key: 'phone', label: 'Phone' },
          { key: 'gender', label: 'Gender' },
          { key: 'center.id', label: 'Center' }
        ]} />
      </div>
    </>
  );
}
