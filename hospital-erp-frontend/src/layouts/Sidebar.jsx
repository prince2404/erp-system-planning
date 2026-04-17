import { NavLink, useLocation } from 'react-router-dom';
import { ChevronLeft, X, HeartPulse } from 'lucide-react';
import { useAuthStore } from '../store/authStore.js';
import { useUiStore } from '../store/uiStore.js';
import { getGroupedNavigation, sectionLabels } from '../lib/navigation.js';

export default function Sidebar() {
  const user = useAuthStore(s => s.user);
  const { sidebarOpen, closeSidebar } = useUiStore();
  const location = useLocation();

  const groups = getGroupedNavigation(user?.role);

  return (
    <>
      {/* Mobile overlay */}
      {sidebarOpen && (
        <div className="fixed inset-0 z-40 bg-ash-950/50 backdrop-blur-sm lg:hidden animate-fade-in" onClick={closeSidebar} />
      )}

      <aside className={`
        fixed inset-y-0 left-0 z-50 flex w-[272px] flex-col border-r border-ash-200/80 bg-white
        transition-transform duration-300 ease-out lg:static lg:translate-x-0
        ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'}
      `}>
        {/* Brand header */}
        <div className="relative flex items-center gap-3 border-b border-ash-100 px-5 py-5">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-br from-brand-500 to-brand-700 shadow-sm">
            <HeartPulse size={20} className="text-white" />
          </div>
          <div className="min-w-0 flex-1">
            <p className="text-[11px] font-bold uppercase tracking-widest text-brand-600">ASK Platform</p>
            <h1 className="truncate text-sm font-bold text-ash-900">Apana Swastha Kendra</h1>
          </div>
          <button className="btn-icon text-ash-400 hover:bg-ash-100 lg:hidden" onClick={closeSidebar}>
            <X size={18} />
          </button>
        </div>

        {/* Navigation */}
        <nav className="flex-1 overflow-y-auto px-3 py-4">
          {Object.entries(groups).map(([sectionKey, items]) => (
            <div key={sectionKey} className="mb-4">
              <p className="mb-1.5 px-3 text-[10px] font-bold uppercase tracking-widest text-ash-400">
                {sectionLabels[sectionKey] || sectionKey}
              </p>
              <div className="space-y-0.5">
                {items.map(item => {
                  const Icon = item.icon;
                  return (
                    <NavLink
                      key={item.to}
                      to={item.to}
                      end={item.to === '/'}
                      onClick={closeSidebar}
                      className={({ isActive }) => `
                        group flex items-center gap-3 rounded-xl px-3 py-2 text-[13px] font-medium transition-all duration-150
                        ${isActive
                          ? 'bg-brand-50 text-brand-700 shadow-sm'
                          : 'text-ash-600 hover:bg-ash-50 hover:text-ash-900'}
                      `}
                    >
                      <Icon size={17} className="shrink-0" />
                      <span>{item.label}</span>
                    </NavLink>
                  );
                })}
              </div>
            </div>
          ))}
        </nav>

        {/* User footer */}
        {user && (
          <div className="border-t border-ash-100 px-4 py-3">
            <div className="flex items-center gap-3">
              <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-brand-100 text-xs font-bold text-brand-700">
                {user.name?.charAt(0)?.toUpperCase() || 'U'}
              </div>
              <div className="min-w-0 flex-1">
                <p className="truncate text-xs font-semibold text-ash-900">{user.name}</p>
                <p className="truncate text-[11px] text-ash-500">{(user.role || '').replace(/_/g, ' ')}</p>
              </div>
            </div>
          </div>
        )}
      </aside>
    </>
  );
}
