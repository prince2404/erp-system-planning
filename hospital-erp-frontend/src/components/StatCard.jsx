import { TrendingUp, TrendingDown, Minus } from 'lucide-react';

const tones = {
  blue: { bg: 'bg-brand-50', icon: 'text-brand-500', border: 'border-brand-100' },
  green: { bg: 'bg-emerald-50', icon: 'text-emerald-500', border: 'border-emerald-100' },
  amber: { bg: 'bg-amber-50', icon: 'text-amber-500', border: 'border-amber-100' },
  red: { bg: 'bg-danger-50', icon: 'text-danger-500', border: 'border-danger-100' },
  purple: { bg: 'bg-purple-50', icon: 'text-purple-500', border: 'border-purple-100' },
  cyan: { bg: 'bg-cyan-50', icon: 'text-cyan-500', border: 'border-cyan-100' }
};

export default function StatCard({ title, value, subtitle, icon: Icon, tone = 'blue', trend, trendLabel }) {
  const t = tones[tone] || tones.blue;
  const displayValue = value === undefined || value === null ? '—' : value;
  const TrendIcon = trend === 'up' ? TrendingUp : trend === 'down' ? TrendingDown : Minus;

  return (
    <div className="card-hover p-5 animate-slide-up">
      <div className="flex items-start justify-between">
        <div className="min-w-0 flex-1">
          <p className="text-xs font-semibold uppercase tracking-wider text-ash-500">{title}</p>
          <p className="mt-2 text-2xl font-bold text-ash-900">{displayValue}</p>
          {subtitle && <p className="mt-1 text-xs text-ash-500">{subtitle}</p>}
          {trend && (
            <div className={`mt-2 inline-flex items-center gap-1 text-xs font-semibold ${trend === 'up' ? 'text-emerald-600' : trend === 'down' ? 'text-danger-600' : 'text-ash-500'}`}>
              <TrendIcon size={14} />
              {trendLabel}
            </div>
          )}
        </div>
        {Icon && (
          <div className={`flex h-10 w-10 shrink-0 items-center justify-center rounded-xl ${t.bg} ${t.border} border`}>
            <Icon size={20} className={t.icon} />
          </div>
        )}
      </div>
    </div>
  );
}
