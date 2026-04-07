import { useQuery } from '@tanstack/react-query';
import { Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { endpoints } from '../../api/endpoints.js';
import KpiCard from '../../components/KpiCard.jsx';
import PageHeader from '../../components/PageHeader.jsx';
import SectionCard from '../../components/SectionCard.jsx';
import { useAuthStore } from '../../store/authStore.js';

export default function DashboardPage() {
  const user = useAuthStore((state) => state.user);
  const centerId = user?.centerId;
  const dashboard = useQuery({
    queryKey: ['dashboard', centerId],
    queryFn: () => endpoints.get('/reports/dashboard', centerId ? { centerId } : {})
  });
  const revenue = useQuery({
    queryKey: ['revenue', centerId],
    queryFn: () => endpoints.get('/reports/revenue', { centerId, groupBy: 'DAY' }),
    enabled: Boolean(user)
  });

  const data = dashboard.data || {};

  return (
    <>
      <PageHeader
        eyebrow="Phase 8"
        title="Role-Based Dashboard"
        description="Live KPIs come from the backend reports endpoints and remain scoped by the signed-in user's role and center where applicable."
      />
      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <KpiCard title="Total Patients" value={data.totalPatients} tone="blue" />
        <KpiCard title="Waiting OPD Today" value={data.todayOpdWaiting} tone="purple" />
        <KpiCard title="Paid Revenue" value={data.paidRevenue} tone="emerald" />
        <KpiCard title="Open Stock Alerts" value={data.activeStockAlerts} tone="amber" />
      </div>
      <div className="mt-6">
        <SectionCard title="Revenue Trend" description="Paid invoice revenue grouped by day.">
          <div className="h-80">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={revenue.data || []}>
                <XAxis dataKey="period" />
                <YAxis />
                <Tooltip />
                <Line type="monotone" dataKey="revenue" stroke="#2563EB" strokeWidth={3} dot={false} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </SectionCard>
      </div>
    </>
  );
}
