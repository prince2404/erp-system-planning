import { useState } from 'react';
import ApiFormCard from '../../components/ApiFormCard.jsx';
import PageHeader from '../../components/PageHeader.jsx';
import QueryTableCard from '../../components/QueryTableCard.jsx';
import StatusBadge from '../../components/StatusBadge.jsx';
import { invoiceTypes, paymentModes } from '../shared/forms.js';

export default function BillingPage() {
  const [centerId, setCenterId] = useState('');
  return (
    <>
      <PageHeader eyebrow="Phase 6" title="Billing & Payments" description="Create invoices, calculate totals/GST in the backend, record payment, and trigger wallet plus commission workflows." />
      <div className="grid gap-6 xl:grid-cols-3">
        <ApiFormCard title="Invoice JSON" endpoint="/billing/invoice" invalidate={['invoices', 'wallet']} sample={{
          patientId: 1,
          centerId: 1,
          type: invoiceTypes[0],
          discount: 0,
          items: [{ description: 'Consultation fee', itemType: 'CONSULTATION', quantity: 1, unitPrice: 500 }]
        }} submitLabel="Create Invoice" />
        <ApiFormCard title="Record Payment" endpoint={(payload) => `/billing/invoice/${payload.id}/pay`} invalidate={['invoices', 'wallet']} fields={[
          { name: 'id', label: 'Invoice ID', type: 'number', required: true },
          { name: 'paymentMode', label: 'Payment Mode', type: 'select', options: paymentModes, required: true },
          { name: 'insuranceProvider', label: 'Insurance Provider' },
          { name: 'insuranceClaimId', label: 'Insurance Claim ID' }
        ]} submitLabel="Record Payment" />
        <ApiFormCard title="Refund Invoice" endpoint={(payload) => `/billing/invoice/${payload.id}/refund`} invalidate={['invoices', 'wallet']} fields={[
          { name: 'id', label: 'Invoice ID', type: 'number', required: true }
        ]} submitLabel="Refund" />
      </div>
      <div className="mt-6">
        <input className="clinical-input mb-3 max-w-sm" placeholder="Center ID" value={centerId} onChange={(event) => setCenterId(event.target.value)} />
        <QueryTableCard title="Invoices" queryKey="invoices" endpoint="/billing/invoices" params={centerId ? { centerId: Number(centerId) } : { enabled: false }} columns={[
          { key: 'id', label: 'ID' },
          { key: 'invoiceNumber', label: 'Invoice' },
          { key: 'patient.name', label: 'Patient' },
          { key: 'type', label: 'Type' },
          { key: 'totalAmount', label: 'Total' },
          { key: 'paymentStatus', label: 'Status', render: (row) => <StatusBadge value={row.paymentStatus} /> }
        ]} />
      </div>
    </>
  );
}
