import { useState } from 'react';
import PageHeader from '../../components/PageHeader.jsx';
import QueryTableCard from '../../components/QueryTableCard.jsx';
import SectionCard from '../../components/SectionCard.jsx';

export default function ReportsPage() {
  const [centerId, setCenterId] = useState('');
  const [month, setMonth] = useState('4');
  const [year, setYear] = useState('2026');
  return (
    <>
      <PageHeader eyebrow="Phase 8" title="Reports & Analytics" description="Operational reports use backend aggregation endpoints and can be exported later from these datasets." />
      <SectionCard title="Report Filters">
        <div className="grid gap-3 md:grid-cols-3">
          <input className="clinical-input" placeholder="Center ID" value={centerId} onChange={(event) => setCenterId(event.target.value)} />
          <input className="clinical-input" placeholder="Month" value={month} onChange={(event) => setMonth(event.target.value)} />
          <input className="clinical-input" placeholder="Year" value={year} onChange={(event) => setYear(event.target.value)} />
        </div>
      </SectionCard>
      <div className="mt-6 grid gap-6 xl:grid-cols-2">
        <QueryTableCard title="Revenue Trend" queryKey="report-revenue" endpoint="/reports/revenue" params={{ centerId: centerId ? Number(centerId) : undefined, groupBy: 'DAY' }} columns={[
          { key: 'period', label: 'Period' },
          { key: 'revenue', label: 'Revenue' }
        ]} />
        <QueryTableCard title="Wallet Movement" queryKey="wallet-movement" endpoint="/reports/wallet-movement" columns={[
          { key: 'walletId', label: 'Wallet' },
          { key: 'type', label: 'Type' },
          { key: 'amount', label: 'Amount' },
          { key: 'referenceId', label: 'Reference' },
          { key: 'createdAt', label: 'Created' }
        ]} />
        <QueryTableCard title="Inventory Summary" queryKey="report-inventory" endpoint="/reports/inventory" params={centerId ? { centerId: Number(centerId) } : { enabled: false }} columns={[
          { key: 'stockValue', label: 'Stock Value' },
          { key: 'batchCount', label: 'Batch Count' }
        ]} />
        <QueryTableCard title="HR Summary" queryKey="report-hr" endpoint="/reports/hr" params={centerId ? { centerId: Number(centerId), month: Number(month), year: Number(year) } : { enabled: false }} columns={[
          { key: 'centerId', label: 'Center' },
          { key: 'month', label: 'Month' },
          { key: 'year', label: 'Year' },
          { key: 'pendingLeaves', label: 'Pending Leaves' }
        ]} />
      </div>
    </>
  );
}
