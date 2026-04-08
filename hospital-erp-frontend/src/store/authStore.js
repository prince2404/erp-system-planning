import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export const useAuthStore = create(
  persist(
    (set, get) => ({
      accessToken: null,
      refreshToken: null,
      user: null,
      permissions: [],
      setSession: ({ accessToken, refreshToken, user, permissions = [] }) =>
        set({ accessToken, refreshToken, user, permissions }),
      updateTokens: (accessToken, refreshToken) =>
        set((state) => ({ accessToken, refreshToken: refreshToken ?? state.refreshToken })),
      logout: () => set({ accessToken: null, refreshToken: null, user: null, permissions: [] }),
      isAuthenticated: () => Boolean(get().accessToken)
    }),
    {
      name: 'hospital-erp-auth'
    }
  )
);
