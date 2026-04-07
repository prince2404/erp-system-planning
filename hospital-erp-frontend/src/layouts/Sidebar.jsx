import { NavLink } from 'react-router-dom';
import { navigation } from '../lib/navigation.js';

export default function Sidebar() {
  return (
    <aside className="hidden min-h-screen w-72 shrink-0 border-r border-slate-200 bg-white px-4 py-5 lg:block">
      <div className="mb-8 rounded-2xl bg-blue-600 p-4 text-white shadow-soft">
        <p className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-100">Hospital ERP</p>
        <h1 className="mt-2 text-lg font-semibold">Apana Swastha Kendra</h1>
      </div>
      <nav className="space-y-1">
        {navigation.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.to === '/'}
              className={({ isActive }) =>
                `flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition ${
                  isActive ? 'bg-blue-50 text-blue-700' : 'text-slate-600 hover:bg-slate-50 hover:text-slate-950'
                }`
              }
            >
              <Icon size={18} />
              {item.label}
            </NavLink>
          );
        })}
      </nav>
    </aside>
  );
}
