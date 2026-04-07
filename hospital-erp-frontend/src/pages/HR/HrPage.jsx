import { useState } from 'react';
import ApiFormCard from '../../components/ApiFormCard.jsx';
import PageHeader from '../../components/PageHeader.jsx';
import QueryTableCard from '../../components/QueryTableCard.jsx';
import StatusBadge from '../../components/StatusBadge.jsx';
import { attendanceStatuses, leaveStatuses, leaveTypes } from '../shared/forms.js';

export default function HrPage() {
  const [centerId, setCenterId] = useState('');
  return (
    <>
      <PageHeader eyebrow="Phase 7" title="HR, Attendance, Leave & Payroll" description="Create staff profiles, mark attendance, approve leave, and generate payroll from attendance." />
      <div className="grid gap-6 xl:grid-cols-2">
        <ApiFormCard title="Create Staff Profile" endpoint="/hr/staff" invalidate={['staff']} fields={[
          { name: 'userId', label: 'User ID', type: 'number', required: true },
          { name: 'department', label: 'Department' },
          { name: 'designation', label: 'Designation' },
          { name: 'dateOfJoining', label: 'Date of Joining', type: 'date', required: true },
          { name: 'baseSalary', label: 'Base Salary', type: 'number' },
          { name: 'emergencyContact', label: 'Emergency Contact' },
          { name: 'emergencyName', label: 'Emergency Name' }
        ]} />
        <ApiFormCard title="Attendance Bulk Mark JSON" endpoint="/hr/attendance/mark" invalidate={['attendance']} sample={[
          { userId: 1, centerId: 1, date: '2026-04-07', checkIn: '09:00:00', checkOut: '17:00:00', status: attendanceStatuses[0], remarks: '' }
        ]} submitLabel="Mark Attendance" />
        <ApiFormCard title="Apply Leave" endpoint="/hr/leave/apply" invalidate={['leaves']} fields={[
          { name: 'leaveType', label: 'Leave Type', type: 'select', options: leaveTypes, required: true },
          { name: 'fromDate', label: 'From Date', type: 'date', required: true },
          { name: 'toDate', label: 'To Date', type: 'date', required: true },
          { name: 'reason', label: 'Reason', type: 'textarea', full: true }
        ]} />
        <ApiFormCard title="Approve or Reject Leave" endpoint={(payload) => `/hr/leave/${payload.id}/approve`} method="put" invalidate={['leaves']} fields={[
          { name: 'id', label: 'Leave Request ID', type: 'number', required: true },
          { name: 'status', label: 'Action', type: 'select', options: leaveStatuses, required: true }
        ]} />
        <ApiFormCard title="Generate Payroll" endpoint="/hr/payroll/generate" invalidate={['payroll']} fields={[
          { name: 'centerId', label: 'Center ID', type: 'number', required: true },
          { name: 'month', label: 'Month', type: 'number', required: true },
          { name: 'year', label: 'Year', type: 'number', required: true },
          { name: 'deductions', label: 'Deductions', type: 'number' },
          { name: 'bonus', label: 'Bonus', type: 'number' }
        ]} />
      </div>
      <div className="mt-6">
        <input className="clinical-input mb-3 max-w-sm" placeholder="Center ID" value={centerId} onChange={(event) => setCenterId(event.target.value)} />
        <div className="grid gap-6 xl:grid-cols-2">
          <QueryTableCard title="Staff" queryKey="staff" endpoint="/hr/staff" params={centerId ? { centerId: Number(centerId) } : { enabled: false }} columns={[
            { key: 'id', label: 'ID' },
            { key: 'user.name', label: 'Name' },
            { key: 'department', label: 'Department' },
            { key: 'designation', label: 'Designation' },
            { key: 'baseSalary', label: 'Salary' }
          ]} />
          <QueryTableCard title="Pending Leaves" queryKey="leaves" endpoint="/hr/leave/pending" params={centerId ? { centerId: Number(centerId) } : { enabled: false }} columns={[
            { key: 'id', label: 'ID' },
            { key: 'user.name', label: 'User' },
            { key: 'leaveType', label: 'Type' },
            { key: 'fromDate', label: 'From' },
            { key: 'toDate', label: 'To' },
            { key: 'status', label: 'Status', render: (row) => <StatusBadge value={row.status} /> }
          ]} />
        </div>
      </div>
    </>
  );
}
