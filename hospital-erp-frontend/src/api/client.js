import axios from 'axios';
import { useAuthStore } from '../store/authStore.js';

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 30000
});

let refreshing = null;

api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config;
    const { refreshToken, updateSession, logout } = useAuthStore.getState();
    if (error.response?.status === 401 && refreshToken && !original?._retry && !original?.url?.includes('/auth/refresh')) {
      original._retry = true;
      refreshing ??= api.post('/auth/refresh', { refreshToken }).finally(() => {
        refreshing = null;
      });
      try {
        const response = await refreshing;
        const session = response.data.data;
        updateSession(session);
        original.headers.Authorization = `Bearer ${session.accessToken}`;
        return api(original);
      } catch (refreshError) {
        logout();
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);

export function unwrap(response) {
  return response.data?.data;
}
