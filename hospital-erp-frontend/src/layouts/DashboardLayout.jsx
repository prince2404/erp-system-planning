import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar.jsx';
import TopbarPhase1 from './TopbarPhase1.jsx';

export default function DashboardLayout() {
  return (
    <div className="min-h-screen bg-clinical-bg text-clinical-text">
      <div className="flex">
        <Sidebar />
        <div className="min-w-0 flex-1">
          <TopbarPhase1 />
          <main className="px-4 py-6 lg:px-8">
            <Outlet />
          </main>
        </div>
      </div>
    </div>
  );
}
