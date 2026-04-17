import { LogOut, Menu, Bell, Search } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { endpoints } from '../api/endpoints.js';
import { useAuthStore } from '../store/authStore.js';
import { useUiStore } from '../store/uiStore.js';
import { RoleBadge } from '../components/Badge.jsx';

export default function Topbar() {
  const navigate = useNavigate();
  const { user, refreshToken, logout } = useAuthStore();
  const openSidebar = useUiStore(s => s.openSidebar);

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
    <header className="sticky top-0 z-30 border-b border-ash-200/80 bg-white/80 backdrop-blur-xl">
      <div className="flex h-16 items-center justify-between gap-4 px-4 lg:px-6">
        {/* Left */}
        <div className="flex items-center gap-3">
          <button
            className="flex h-9 w-9 items-center justify-center rounded-xl border border-ash-200 text-ash-500 hover:bg-ash-50 lg:hidden"
            onClick={openSidebar}
            type="button"
          >
            <Menu size={18} />
          </button>
          <div className="hidden sm:block">
            <p className="text-sm font-semibold text-ash-900">
              Welcome back, <span className="text-brand-600">{user?.name?.split(' ')[0] || 'User'}</span>
            </p>
          </div>
        </div>

        {/* Right */}
        <div className="flex items-center gap-2">
          <RoleBadge role={user?.role} />

          <button className="btn-icon text-ash-400 hover:bg-ash-100 hover:text-ash-600 relative" title="Notifications">
            <Bell size={18} />
            <span className="absolute right-1.5 top-1.5 h-2 w-2 rounded-full bg-brand-500" />
          </button>

          <Link
            className="btn-secondary btn-sm hidden sm:inline-flex"
            to="/profile"
          >
            Profile
          </Link>

          <button
            className="btn-ghost btn-sm text-ash-500 hover:text-danger-600"
            onClick={handleLogout}
            type="button"
          >
            <LogOut size={16} />
            <span className="hidden sm:inline">Logout</span>
          </button>
        </div>
      </div>
    </header>
  );
}
