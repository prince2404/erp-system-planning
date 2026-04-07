import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '../store/authStore.js';

export default function RoleRoute({ allowed = [] }) {
  const user = useAuthStore((state) => state.user);
  if (!user || (allowed.length > 0 && !allowed.includes(user.role))) {
    return <Navigate to="/" replace />;
  }
  return <Outlet />;
}
