import axios from 'axios';

const normalizeBase = (value, fallback) => (value || fallback).replace(/\/$/, '');

const PROC_API_BASE_URL = normalizeBase(import.meta.env.VITE_PROC_API_BASE_URL, '/api');
const CRAWL_API_BASE_URL = normalizeBase(import.meta.env.VITE_CRAWL_API_BASE_URL, '/api/crawl');

const procClient = axios.create({
  baseURL: PROC_API_BASE_URL,
});

const crawlClient = axios.create({
  baseURL: CRAWL_API_BASE_URL,
});

export const jobAPI = {
  getAllJobs: (page = 0, size = 10) => procClient.get('/jobs', { params: { page, size } }),

  searchJobs: (title, location) => procClient.get('/jobs/search', { params: { title, location } }),

  filterJobs: (filters) => procClient.get('/jobs/filter', { params: filters }),

  getRecentJobs: (limit = 20) => procClient.get('/jobs/recent', { params: { limit } }),

  getJobById: (id) => procClient.get(`/jobs/${id}`),
};

export const dashboardAPI = {
  getOverview: () => procClient.get('/dashboard/overview'),
};

export const crawlAPI = {
  triggerJobSearch: (jobTitle, location) =>
    crawlClient.post('/jobs/search', {
      jobTitle,
      location,
    }),
};

export const subscriptionAPI = {
  subscribe: ({ email, jobTitles, locations }) =>
    procClient.post('/subscriptions', {
      email,
      jobTitles,
      locations,
      active: true,
    }),

  getByEmail: (email) => procClient.get(`/subscriptions/${encodeURIComponent(email)}`),

  unsubscribe: (email) => procClient.delete(`/subscriptions/${encodeURIComponent(email)}`),
};
