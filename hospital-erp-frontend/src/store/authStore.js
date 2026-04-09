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
      updateSession: ({ accessToken, refreshToken, user, permissions }) =>
        set((state) => ({
          accessToken: accessToken ?? state.accessToken,
          refreshToken: refreshToken ?? state.refreshToken,
          user: user ?? state.user,
          permissions: permissions ?? state.permissions
        })),
      updateUser: (user) => set({ user }),
      logout: () => set({ accessToken: null, refreshToken: null, user: null, permissions: [] }),
      isAuthenticated: () => Boolean(get().accessToken)
    }),
    {
      name: 'hospital-erp-auth'
    }
  )
);
