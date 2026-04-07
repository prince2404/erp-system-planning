import { LogOut, Menu } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { endpoints } from '../api/endpoints.js';
import { useAuthStore } from '../store/authStore.js';
import { useUiStore } from '../store/uiStore.js';

export default function Topbar() {
  const navigate = useNavigate();
  const { user, refreshToken, logout } = useAuthStore();
  const toggleSidebar = useUiStore((state) => state.toggleSidebar);

  const handleLogout = async () => {
    try {
      if (refreshToken) {
        await endpoints.logout(refreshToken);
      }
    } finally {
      logout();
      navigate('/login');
    }
  };

  return (
    <header className="sticky top-0 z-20 border-b border-slate-200 bg-white/90 px-4 py-3 backdrop-blur lg:px-8">
      <div className="flex items-center justify-between gap-4">
        <button className="rounded-xl border border-slate-200 p-2 text-slate-600 lg:hidden" onClick={toggleSidebar} type="button">
          <Menu size={18} />
        </button>
        <div>
          <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Signed in</p>
          <p className="text-sm font-semibold text-slate-950">{user?.name || 'User'} · {user?.role || 'Role'}</p>
        </div>
        <button className="inline-flex items-center gap-2 rounded-xl border border-slate-200 px-3 py-2 text-sm font-semibold text-slate-600 hover:bg-slate-50" onClick={handleLogout} type="button">
          <LogOut size={16} />
          Logout
        </button>
      </div>
    </header>
  );
}
