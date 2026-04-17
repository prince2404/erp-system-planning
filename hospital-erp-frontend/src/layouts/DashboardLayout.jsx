import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar.jsx';
import Topbar from './Topbar.jsx';
import ToastContainer from '../components/Toast.jsx';

export default function DashboardLayout() {
  return (
    <div className="flex min-h-screen bg-ash-50">
      <Sidebar />
      <div className="flex min-w-0 flex-1 flex-col">
        <Topbar />
        <main className="flex-1 px-4 py-6 lg:px-8">
          <Outlet />
        </main>
      </div>
      <ToastContainer />
    </div>
  );
}
