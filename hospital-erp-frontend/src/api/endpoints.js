import { api, unwrap } from './client.js';

export const endpoints = {
  login: (payload) => api.post('/auth/login', payload).then(unwrap),
  bootstrap: (payload) => api.post('/auth/bootstrap-super-admin', payload).then(unwrap),
  logout: (refreshToken) => api.post('/auth/logout', { refreshToken }).then(unwrap),
  get: (url, params) => api.get(url, { params }).then(unwrap),
  post: (url, payload) => api.post(url, payload).then(unwrap),
  put: (url, payload) => api.put(url, payload).then(unwrap),
  del: (url) => api.delete(url).then(unwrap),
  upload: (url, file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post(url, formData, { headers: { 'Content-Type': 'multipart/form-data' } }).then(unwrap);
  }
};
