import ApiFormCard from '../../components/ApiFormCard.jsx';
import PageHeader from '../../components/PageHeader.jsx';
import QueryTableCard from '../../components/QueryTableCard.jsx';

export default function CentersPage() {
  return (
    <>
      <PageHeader eyebrow="Phase 2" title="Geographic & Center Setup" description="Create states, districts, blocks, and centers for real operating locations." />
      <div className="grid gap-6 xl:grid-cols-2">
        <ApiFormCard title="Create State" endpoint="/states" invalidate={['states']} fields={[
          { name: 'name', label: 'State Name', required: true },
          { name: 'code', label: 'Code', required: true }
        ]} />
        <ApiFormCard title="Create District" endpoint="/districts" invalidate={['districts']} fields={[
          { name: 'name', label: 'District Name', required: true },
          { name: 'stateId', label: 'State ID', type: 'number', required: true }
        ]} />
        <ApiFormCard title="Create Block" endpoint="/blocks" invalidate={['blocks']} fields={[
          { name: 'name', label: 'Block Name', required: true },
          { name: 'districtId', label: 'District ID', type: 'number', required: true }
        ]} />
        <ApiFormCard title="Create Center" endpoint="/centers" invalidate={['centers']} fields={[
          { name: 'name', label: 'Center Name', required: true },
          { name: 'city', label: 'City' },
          { name: 'stateId', label: 'State ID', type: 'number', required: true },
          { name: 'districtId', label: 'District ID', type: 'number', required: true },
          { name: 'blockId', label: 'Block ID', type: 'number', required: true },
          { name: 'phone', label: 'Phone' },
          { name: 'email', label: 'Email', type: 'email' },
          { name: 'address', label: 'Address', type: 'textarea', full: true }
        ]} />
      </div>
      <div className="mt-6 grid gap-6 xl:grid-cols-2">
        <QueryTableCard title="States" queryKey="states" endpoint="/states" columns={[{ key: 'id', label: 'ID' }, { key: 'name', label: 'Name' }, { key: 'code', label: 'Code' }]} />
        <QueryTableCard title="Centers" queryKey="centers" endpoint="/centers" columns={[
          { key: 'id', label: 'ID' },
          { key: 'name', label: 'Name' },
          { key: 'city', label: 'City' },
          { key: 'stateId', label: 'State' },
          { key: 'districtId', label: 'District' },
          { key: 'blockId', label: 'Block' },
          { key: 'active', label: 'Active' }
        ]} />
      </div>
    </>
  );
}
