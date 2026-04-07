import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { endpoints } from '../../api/endpoints.js';
import ApiFormCard from '../../components/ApiFormCard.jsx';
import DataTable from '../../components/DataTable.jsx';
import PageHeader from '../../components/PageHeader.jsx';
import QueryTableCard from '../../components/QueryTableCard.jsx';
import SectionCard from '../../components/SectionCard.jsx';
import { roleOptions, scopeOptions } from '../shared/forms.js';

const userColumns = [
  { key: 'id', label: 'ID' },
  { key: 'name', label: 'Name' },
  { key: 'email', label: 'Email' },
  { key: 'role', label: 'Role' },
  { key: 'scopeType', label: 'Scope' },
  { key: 'centerId', label: 'Center' },
  { key: 'active', label: 'Active' }
];

export default function UsersPage() {
  return (
    <>
      <PageHeader eyebrow="Phase 2" title="User Management" description="Rank-guarded user creation, permission catalog management, and Excel bulk import preview." />
      <div className="grid gap-6 xl:grid-cols-2">
        <ApiFormCard
          title="Create User"
          endpoint="/users/create"
          invalidate={['users']}
          fields={[
            { name: 'name', label: 'Name', required: true },
            { name: 'email', label: 'Email', type: 'email', required: true },
            { name: 'password', label: 'Password', type: 'password', required: true },
            { name: 'phone', label: 'Phone' },
            { name: 'role', label: 'Role', type: 'select', options: roleOptions, required: true },
            { name: 'scopeType', label: 'Scope Type', type: 'select', options: scopeOptions },
            { name: 'scopeId', label: 'Scope ID', type: 'number' },
            { name: 'centerId', label: 'Center ID', type: 'number' }
          ]}
          submitLabel="Create User"
        />
        <ApiFormCard
          title="Create Permission"
          description="Use this for real permission metadata only; no sample permission rows are seeded."
          endpoint="/permissions"
          invalidate={['permissions']}
          fields={[
            { name: 'module', label: 'Module', required: true },
            { name: 'action', label: 'Action', required: true },
            { name: 'description', label: 'Description', full: true }
          ]}
          submitLabel="Create Permission"
        />
      </div>
      <div className="mt-6 grid gap-6 xl:grid-cols-2">
        <BulkUploadCard />
        <QueryTableCard title="Permission Catalog" queryKey="permissions" endpoint="/permissions" columns={[
          { key: 'id', label: 'ID' },
          { key: 'module', label: 'Module' },
          { key: 'action', label: 'Action' },
          { key: 'description', label: 'Description' }
        ]} />
      </div>
      <div className="mt-6">
        <QueryTableCard title="Users" queryKey="users" endpoint="/users" params={{ page: 0, size: 25 }} columns={userColumns} />
      </div>
    </>
  );
}

function BulkUploadCard() {
  const queryClient = useQueryClient();
  const [rows, setRows] = useState([]);
  const mutation = useMutation({
    mutationFn: (file) => endpoints.upload('/users/bulk-upload', file),
    onSuccess: (data) => {
      setRows(data);
      queryClient.invalidateQueries({ queryKey: ['users'] });
    }
  });

  return (
    <SectionCard title="Bulk Upload Preview" description="Upload an Excel sheet using the backend template to validate before confirm.">
      <input className="clinical-input w-full" type="file" accept=".xlsx" onChange={(event) => event.target.files?.[0] && mutation.mutate(event.target.files[0])} />
      {mutation.isError ? <p className="mt-3 text-sm font-medium text-red-600">{mutation.error.response?.data?.message || mutation.error.message}</p> : null}
      <div className="mt-4">
        <DataTable
          rows={rows}
          columns={[
            { key: 'rowNumber', label: 'Row' },
            { key: 'name', label: 'Name' },
            { key: 'email', label: 'Email' },
            { key: 'role', label: 'Role' },
            { key: 'status', label: 'Status' },
            { key: 'reason', label: 'Reason' }
          ]}
          empty="No upload preview yet"
        />
      </div>
    </SectionCard>
  );
}
