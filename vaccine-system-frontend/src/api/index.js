import axios from 'axios';

const API_BASE_URL = 'http://localhost:8004/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    console.error('API Error:', error);
    return Promise.reject(error);
  }
);

export const vaccineApi = {
  getAll: () => api.get('/vaccines'),
  getById: (id) => api.get(`/vaccines/${id}`),
  getAllBatches: () => api.get('/vaccines/batches'),
  getBatchById: (id) => api.get(`/vaccines/batches/${id}`),
  getBatchesByVaccine: (vaccineId) => api.get(`/vaccines/${vaccineId}/batches`),
  getAvailableBatches: (vaccineId) => api.get(`/vaccines/${vaccineId}/available-batches`),
  isBatchValid: (id) => api.get(`/vaccines/batches/${id}/valid`),
};

export const appointmentApi = {
  create: (data) => api.post('/appointments', data),
  getById: (id) => api.get(`/appointments/${id}`),
  getByUser: (userId) => api.get(`/appointments/user/${userId}`),
  checkIn: (id) => api.post(`/appointments/${id}/check-in`),
  cancel: (id) => api.post(`/appointments/${id}/cancel`),
};

export const vaccinationApi = {
  start: (appointmentId) => api.post(`/vaccinations/start/${appointmentId}`),
  complete: (recordId, vaccinationSite) => 
    api.post(`/vaccinations/complete/${recordId}?vaccinationSite=${encodeURIComponent(vaccinationSite || '上臂三角肌')}`),
  completeObservation: (recordId) => 
    api.post(`/vaccinations/complete-observation/${recordId}`),
  getById: (id) => api.get(`/vaccinations/${id}`),
  getByUser: (userId) => api.get(`/vaccinations/user/${userId}`),
  getObserving: () => api.get('/vaccinations/observing'),
};

export const adverseReactionApi = {
  report: (data) => api.post('/adverse-reactions/report', data),
  getAll: () => api.get('/adverse-reactions'),
  getById: (id) => api.get(`/adverse-reactions/${id}`),
  getByUser: (userId) => api.get(`/adverse-reactions/user/${userId}`),
  getByBatch: (batchId) => api.get(`/adverse-reactions/batch/${batchId}`),
  update: (id, params) => api.put(`/adverse-reactions/${id}`, null, { params }),
};

export const recallApi = {
  create: (data) => api.post('/recalls', data),
  getAll: () => api.get('/recalls'),
  getById: (id) => api.get(`/recalls/${id}`),
  markNotified: (id) => api.post(`/recalls/${id}/mark-notified`),
  getAffectedUsers: (batchId) => api.get(`/recalls/affected-users/${batchId}`),
};

export const reportApi = {
  generate: (date, vaccinationPointId) => 
    api.post(`/reports/generate?date=${date}&vaccinationPointId=${vaccinationPointId}`),
  getAll: () => api.get('/reports'),
  getById: (id) => api.get(`/reports/${id}`),
  getDashboard: (vaccinationPointId) => 
    api.get(`/reports/dashboard?vaccinationPointId=${vaccinationPointId || 1}`),
};

export const userApi = {
  getAll: () => api.get('/users'),
  getById: (id) => api.get(`/users/${id}`),
  create: (data) => api.post('/users', data),
  update: (id, data) => api.put(`/users/${id}`, data),
};

export const vaccinationPointApi = {
  getAll: () => api.get('/vaccination-points'),
  getById: (id) => api.get(`/vaccination-points/${id}`),
};

export const doctorApi = {
  getAll: () => api.get('/doctors'),
  getById: (id) => api.get(`/doctors/${id}`),
  getByPoint: (pointId) => api.get(`/doctors/point/${pointId}`),
};

export default api;
