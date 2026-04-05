import { useEffect, useState } from 'react';
import FilterPanel from '../components/FilterPanel';
import JobList from '../components/JobList';
import SearchBar from '../components/SearchBar';
import SubscriptionPanel from '../components/SubscriptionPanel';
import { crawlAPI, jobAPI } from '../services/api';

const parseSalaryInputToLpa = (value) => {
  if (!value || !value.trim()) return null;
  const raw = value.trim();
  const normalized = raw.replace(/,/g, '');
  const match = normalized.match(/\d+(?:\.\d+)?/);
  if (!match) return null;

  const num = Number(match[0]);
  if (Number.isNaN(num)) return null;

  if (/lpa|lac|lakh/i.test(raw)) return num;
  if (num >= 100000) return num / 100000;
  return num;
};

const parseJobSalaryRangeToLpa = (salaryText) => {
  if (!salaryText || !salaryText.trim()) return null;

  const cleaned = salaryText.replace(/,/g, '');
  const matches = cleaned.match(/\d+(?:\.\d+)?/g);
  if (!matches || matches.length === 0) return null;

  const values = matches
    .map((token) => Number(token))
    .filter((n) => !Number.isNaN(n))
    .map((n) => {
      if (/lpa|lac|lakh/i.test(cleaned)) return n;
      if (n >= 100000) return n / 100000;
      return n;
    });

  if (values.length === 0) return null;
  return {
    min: Math.min(...values),
    max: Math.max(...values),
  };
};

const applyClientSalaryRange = (jobs, minSalaryText, maxSalaryText) => {
  const minLpa = parseSalaryInputToLpa(minSalaryText);
  const maxLpa = parseSalaryInputToLpa(maxSalaryText);

  if (minLpa == null && maxLpa == null) return jobs;

  return jobs.filter((job) => {
    const range = parseJobSalaryRangeToLpa(job.salary || '');
    if (!range) return false;

    const satisfiesMin = minLpa == null || range.max >= minLpa;
    const satisfiesMax = maxLpa == null || range.min <= maxLpa;
    return satisfiesMin && satisfiesMax;
  });
};

export default function JobsPage() {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [crawlLoading, setCrawlLoading] = useState(false);
  const [error, setError] = useState(null);
  const [notice, setNotice] = useState('');
  const [statusLabel, setStatusLabel] = useState('Bootstrapping');

  useEffect(() => {
    loadRecentJobs();
  }, []);

  const loadRecentJobs = async () => {
    setLoading(true);
    setError(null);
    setNotice('');
    setStatusLabel('Loading recent jobs');

    try {
      const response = await jobAPI.getRecentJobs(20);
      setJobs(response.data || []);
      setStatusLabel('Recent jobs stream');
    } catch (err) {
      setError('Unable to load jobs. Ensure backend is running on port 8082.');
      setStatusLabel('Connection issue');
      console.error('loadRecentJobs failed:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async ({ jobTitle, location }) => {
    setLoading(true);
    setError(null);
    setNotice('');
    setStatusLabel(`Searching for ${jobTitle}`);

    try {
      const response = await jobAPI.searchJobs(jobTitle, location);
      setJobs(response.data || []);
      setStatusLabel('Search completed');
    } catch (err) {
      setError('Search request failed. Please retry.');
      setStatusLabel('Search failed');
      console.error('handleSearch failed:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleFreshCrawl = async ({ jobTitle, location, invalid }) => {
    if (invalid) {
      setError('Fresh crawl requires both job title and location.');
      setStatusLabel('Fresh crawl validation failed');
      return;
    }

    setCrawlLoading(true);
    setError(null);
    setNotice('');
    setStatusLabel(`Queueing crawl for ${jobTitle} in ${location}`);

    try {
      const response = await crawlAPI.triggerJobSearch(jobTitle, location);
      setNotice(response.data || 'Fresh crawl request queued.');
      setStatusLabel('Fresh crawl queued');

      setTimeout(() => {
        loadRecentJobs();
      }, 3000);
    } catch (err) {
      setError('Fresh crawl trigger failed. Verify crawl service on port 8081 and RabbitMQ.');
      setStatusLabel('Fresh crawl failed');
      console.error('handleFreshCrawl failed:', err);
    } finally {
      setCrawlLoading(false);
    }
  };

  const handleFilter = async (filters) => {
    setLoading(true);
    setError(null);
    setNotice('');
    setStatusLabel('Applying filter rail');

    try {
      const serverFilters = {
        location: filters.location,
        skills: filters.skills,
        company: filters.company,
      };

      const response = await jobAPI.filterJobs(serverFilters);
      const serverJobs = response.data || [];
      const refinedJobs = applyClientSalaryRange(serverJobs, filters.minSalary, filters.maxSalary);

      setJobs(refinedJobs);
      if (refinedJobs.length !== serverJobs.length && (filters.minSalary || filters.maxSalary)) {
        setNotice('Salary range refinement applied on frontend for precise filtering.');
      }
      setStatusLabel('Filter applied');
    } catch (err) {
      setError('Filter request failed. Please check your criteria and try again.');
      setStatusLabel('Filter failed');
      console.error('handleFilter failed:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page">
      <section className="panel panel-pad panel-strong">
        <span className="badge">Search Grid</span>
        <h1 className="page-lead" style={{ marginTop: '0.65rem' }}>
          Futuristic role discovery workspace
        </h1>
        <p className="page-sub">
          Run keyword scans, trigger fresh crawling, tune filters, and inspect consolidated job signals.
        </p>

        <div className="btn-row" style={{ marginTop: '0.85rem' }}>
          <span className="mini-chip">Status: {statusLabel}</span>
          <button type="button" className="btn btn-ghost" onClick={loadRecentJobs} disabled={loading || crawlLoading}>
            Reload Recent
          </button>
        </div>
      </section>

      <section style={{ marginTop: '1rem' }}>
        <SearchBar
          onSearch={handleSearch}
          onFreshCrawl={handleFreshCrawl}
          loading={loading}
          crawlLoading={crawlLoading}
        />
      </section>

      {notice && <div className="info-strip">{notice}</div>}
      {error && <div className="error-strip">{error}</div>}

      <section className="jobs-layout">
        <FilterPanel onFilter={handleFilter} loading={loading || crawlLoading} />
        <div className="stack">
          <JobList jobs={jobs} loading={loading} />
          <SubscriptionPanel onStatusUpdate={setStatusLabel} />
        </div>
      </section>
    </div>
  );
}
