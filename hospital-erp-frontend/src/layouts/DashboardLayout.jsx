import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar.jsx';
import Topbar from './Topbar.jsx';

export default function DashboardLayout() {
  return (
    <div className="min-h-screen bg-clinical-bg text-clinical-text">
      <div className="flex">
        <Sidebar />
        <div className="min-w-0 flex-1">
          <Topbar />
          <main className="px-4 py-6 lg:px-8">
            <Outlet />
          </main>
        </div>
      </div>
    </div>
  );
}
