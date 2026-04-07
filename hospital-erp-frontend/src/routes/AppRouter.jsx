import { Navigate, Route, Routes } from 'react-router-dom';
import DashboardLayout from '../layouts/DashboardLayout.jsx';
import AppointmentsPage from '../pages/Appointments/AppointmentsPage.jsx';
import BillingPage from '../pages/Billing/BillingPage.jsx';
import CentersPage from '../pages/Centers/CentersPage.jsx';
import DashboardPage from '../pages/Dashboard/DashboardPage.jsx';
import DoctorsPage from '../pages/Doctors/DoctorsPage.jsx';
import HrPage from '../pages/HR/HrPage.jsx';
import IpdPage from '../pages/IPD/IpdPage.jsx';
import LoginPage from '../pages/Login/LoginPage.jsx';
import OpdPage from '../pages/OPD/OpdPage.jsx';
import PatientsPage from '../pages/Patients/PatientsPage.jsx';
import PharmacyPage from '../pages/Pharmacy/PharmacyPage.jsx';
import ReportsPage from '../pages/Reports/ReportsPage.jsx';
import UsersPage from '../pages/Users/UsersPage.jsx';
import WalletPage from '../pages/Wallet/WalletPage.jsx';
import ProtectedRoute from './ProtectedRoute.jsx';

export default function AppRouter() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route element={<ProtectedRoute />}>
        <Route element={<DashboardLayout />}>
          <Route index element={<DashboardPage />} />
          <Route path="/users" element={<UsersPage />} />
          <Route path="/centers" element={<CentersPage />} />
          <Route path="/patients" element={<PatientsPage />} />
          <Route path="/opd" element={<OpdPage />} />
          <Route path="/ipd" element={<IpdPage />} />
          <Route path="/doctors" element={<DoctorsPage />} />
          <Route path="/appointments" element={<AppointmentsPage />} />
          <Route path="/pharmacy" element={<PharmacyPage />} />
          <Route path="/billing" element={<BillingPage />} />
          <Route path="/hr" element={<HrPage />} />
          <Route path="/wallet" element={<WalletPage />} />
          <Route path="/reports" element={<ReportsPage />} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
