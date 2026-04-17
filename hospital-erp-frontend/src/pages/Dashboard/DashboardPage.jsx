import { useQuery } from '@tanstack/react-query';
import { Users, HeartPulse, ClipboardList, AlertTriangle, MapPinned, TrendingUp } from 'lucide-react';
import { Area, AreaChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { endpoints } from '../../api/endpoints.js';
import PageHeader from '../../components/PageHeader.jsx';
import SectionCard from '../../components/SectionCard.jsx';
import StatCard from '../../components/StatCard.jsx';
import { SkeletonKpi } from '../../components/Skeleton.jsx';
import { useAuthStore } from '../../store/authStore.js';

export default function DashboardPage() {
  const user = useAuthStore(s => s.user);
  const centerId = user?.centerId;

  const dashboard = useQuery({
    queryKey: ['dashboard', centerId],
    queryFn: () => endpoints.get('/reports/dashboard', centerId ? { centerId } : {}),
    retry: false
  });

  const revenue = useQuery({
    queryKey: ['revenue', centerId],
    queryFn: () => endpoints.get('/reports/revenue', { centerId, groupBy: 'DAY' }),
    enabled: Boolean(user),
    retry: false
  });

  const data = dashboard.data || {};
  const isLoading = dashboard.isLoading;

  const greeting = getGreeting();
  const roleName = (user?.role || '').replace(/_/g, ' ');

  return (
    <>
      <PageHeader
        title={`${greeting}, ${user?.name?.split(' ')[0] || 'User'}`}
        description={`You're signed in as ${roleName}. Here's your operational overview.`}
      />

      {/* KPI Cards */}
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {isLoading ? (
          Array.from({ length: 4 }).map((_, i) => <SkeletonKpi key={i} />)
        ) : (
          <>
            <StatCard
              title="Total Patients"
              value={data.totalPatients ?? 0}
              icon={HeartPulse}
              tone="blue"
              subtitle="Registered patients"
            />
            <StatCard
              title="OPD Waiting Today"
              value={data.todayOpdWaiting ?? 0}
              icon={ClipboardList}
              tone="amber"
              subtitle="Current queue"
            />
            <StatCard
              title="Total Revenue"
              value={data.paidRevenue ? `₹${Number(data.paidRevenue).toLocaleString('en-IN')}` : '₹0'}
              icon={TrendingUp}
              tone="green"
              subtitle="Paid invoices"
            />
            <StatCard
              title="Stock Alerts"
              value={data.activeStockAlerts ?? 0}
              icon={AlertTriangle}
              tone={data.activeStockAlerts > 0 ? 'red' : 'green'}
              subtitle={data.activeStockAlerts > 0 ? 'Needs attention' : 'All clear'}
            />
          </>
        )}
      </div>

      {/* Charts Row */}
      <div className="mt-6 grid gap-6 xl:grid-cols-3">
        <div className="xl:col-span-2">
          <SectionCard title="Revenue Trend" description="Daily paid invoice revenue">
            <div className="h-72">
              {revenue.data && revenue.data.length > 0 ? (
                <ResponsiveContainer width="100%" height="100%">
                  <AreaChart data={revenue.data}>
                    <defs>
                      <linearGradient id="revenueGrad" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="5%" stopColor="#3b93ff" stopOpacity={0.2} />
                        <stop offset="95%" stopColor="#3b93ff" stopOpacity={0} />
                      </linearGradient>
                    </defs>
                    <XAxis dataKey="period" axisLine={false} tickLine={false} tick={{ fontSize: 11, fill: '#667591' }} />
                    <YAxis axisLine={false} tickLine={false} tick={{ fontSize: 11, fill: '#667591' }} />
                    <Tooltip
                      contentStyle={{ borderRadius: '12px', border: '1px solid #eceef2', fontSize: '13px' }}
                    />
                    <Area
                      type="monotone"
                      dataKey="revenue"
                      stroke="#3b93ff"
                      strokeWidth={2.5}
                      fill="url(#revenueGrad)"
                    />
                  </AreaChart>
                </ResponsiveContainer>
              ) : (
                <div className="flex h-full items-center justify-center text-sm text-ash-400">
                  No revenue data yet. Create invoices to see trends.
                </div>
              )}
            </div>
          </SectionCard>
        </div>

        {/* Quick Info */}
        <SectionCard title="Quick Info" description="System overview">
          <div className="space-y-4">
            <InfoRow icon={Users} label="Your Role" value={roleName} />
            <InfoRow icon={MapPinned} label="Scope" value={user?.scopeType || 'System'} />
            <InfoRow icon={HeartPulse} label="Center" value={user?.centerName || 'All Centers'} />
            <div className="rounded-xl bg-brand-50 border border-brand-100 p-4 mt-4">
              <p className="text-xs font-bold text-brand-700 uppercase tracking-wider">Platform Status</p>
              <p className="mt-1 text-sm text-brand-600">All systems operational</p>
            </div>
          </div>
        </SectionCard>
      </div>
    </>
  );
}

function InfoRow({ icon: Icon, label, value }) {
  return (
    <div className="flex items-center gap-3">
      <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-ash-100">
        <Icon size={15} className="text-ash-500" />
      </div>
      <div>
        <p className="text-[11px] text-ash-500 uppercase tracking-wider font-semibold">{label}</p>
        <p className="text-sm font-medium text-ash-800">{value}</p>
      </div>
    </div>
  );
}

function getGreeting() {
  const hour = new Date().getHours();
  if (hour < 12) return 'Good morning';
  if (hour < 17) return 'Good afternoon';
  return 'Good evening';
}
