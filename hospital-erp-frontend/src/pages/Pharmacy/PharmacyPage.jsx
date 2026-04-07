import { useState } from 'react';
import ApiFormCard from '../../components/ApiFormCard.jsx';
import PageHeader from '../../components/PageHeader.jsx';
import QueryTableCard from '../../components/QueryTableCard.jsx';
import StatusBadge from '../../components/StatusBadge.jsx';
import { drugUnits } from '../shared/forms.js';

export default function PharmacyPage() {
  const [centerId, setCenterId] = useState('');
  return (
    <>
      <PageHeader eyebrow="Phase 5" title="Pharmacy & Inventory" description="Manage drug master, batch stock, FIFO dispense, and active stock alerts." />
      <div className="grid gap-6 xl:grid-cols-3">
        <ApiFormCard title="Add Drug" endpoint="/drugs" invalidate={['drugs']} fields={[
          { name: 'name', label: 'Drug Name', required: true },
          { name: 'genericName', label: 'Generic Name' },
          { name: 'category', label: 'Category' },
          { name: 'unit', label: 'Unit', type: 'select', options: drugUnits },
          { name: 'hsnCode', label: 'HSN Code' },
          { name: 'manufacturer', label: 'Manufacturer' }
        ]} />
        <ApiFormCard title="Add Stock Batch" endpoint="/drugs/stock" invalidate={['stock', 'alerts']} fields={[
          { name: 'drugId', label: 'Drug ID', type: 'number', required: true },
          { name: 'centerId', label: 'Center ID', type: 'number', required: true },
          { name: 'batchNumber', label: 'Batch Number', required: true },
          { name: 'expiryDate', label: 'Expiry Date', type: 'date', required: true },
          { name: 'quantity', label: 'Quantity', type: 'number', required: true },
          { name: 'purchasePrice', label: 'Purchase Price', type: 'number' },
          { name: 'sellingPrice', label: 'Selling Price', type: 'number' },
          { name: 'supplier', label: 'Supplier' }
        ]} />
        <ApiFormCard title="Dispense Medicine" endpoint="/drugs/dispense" invalidate={['stock', 'alerts']} fields={[
          { name: 'patientId', label: 'Patient ID', type: 'number', required: true },
          { name: 'opdVisitId', label: 'OPD Visit ID', type: 'number' },
          { name: 'drugId', label: 'Drug ID', type: 'number', required: true },
          { name: 'centerId', label: 'Center ID', type: 'number', required: true },
          { name: 'quantity', label: 'Quantity', type: 'number', required: true }
        ]} />
      </div>
      <div className="mt-6">
        <input className="clinical-input mb-3 max-w-sm" placeholder="Center ID" value={centerId} onChange={(event) => setCenterId(event.target.value)} />
        <div className="grid gap-6 xl:grid-cols-3">
          <QueryTableCard title="Drugs" queryKey="drugs" endpoint="/drugs" columns={[{ key: 'id', label: 'ID' }, { key: 'name', label: 'Name' }, { key: 'category', label: 'Category' }, { key: 'unit', label: 'Unit' }]} />
          <QueryTableCard title="Stock" queryKey="stock" endpoint="/drugs/stock" params={centerId ? { centerId: Number(centerId) } : { enabled: false }} columns={[
            { key: 'id', label: 'ID' },
            { key: 'drug.name', label: 'Drug' },
            { key: 'batchNumber', label: 'Batch' },
            { key: 'expiryDate', label: 'Expiry' },
            { key: 'quantity', label: 'Qty' }
          ]} />
          <QueryTableCard title="Alerts" queryKey="alerts" endpoint="/drugs/alerts" params={centerId ? { centerId: Number(centerId) } : { enabled: false }} columns={[
            { key: 'id', label: 'ID' },
            { key: 'drug.name', label: 'Drug' },
            { key: 'alertType', label: 'Type', render: (row) => <StatusBadge value={row.alertType} /> },
            { key: 'currentQuantity', label: 'Qty' }
          ]} />
        </div>
      </div>
    </>
  );
}
