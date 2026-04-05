import { useState } from 'react';

export default function SearchBar({ onSearch, onFreshCrawl, loading, crawlLoading }) {
  const [jobTitle, setJobTitle] = useState('');
  const [location, setLocation] = useState('');

  const busy = loading || crawlLoading;

  const handleSearch = (event) => {
    event.preventDefault();
    if (!jobTitle.trim()) return;
    onSearch({ jobTitle: jobTitle.trim(), location: location.trim() });
  };

  const handleFreshCrawl = () => {
    if (!jobTitle.trim() || !location.trim()) {
      onFreshCrawl({ jobTitle: jobTitle.trim(), location: location.trim(), invalid: true });
      return;
    }
    onFreshCrawl({ jobTitle: jobTitle.trim(), location: location.trim(), invalid: false });
  };

  return (
    <form onSubmit={handleSearch} className="panel panel-pad panel-strong">
      <div className="btn-row" style={{ justifyContent: 'space-between', marginBottom: '0.75rem' }}>
        <h2 style={{ fontSize: '1rem' }}>Neural Search Console</h2>
        <span className="badge">Live query + crawl trigger</span>
      </div>

      <div className="input-grid" style={{ gridTemplateColumns: '1.35fr 1fr 0.9fr 1fr' }}>
        <div>
          <label className="label" htmlFor="jobTitle">
            Job Title
          </label>
          <input
            id="jobTitle"
            value={jobTitle}
            onChange={(event) => setJobTitle(event.target.value)}
            className="field"
            placeholder="Backend Engineer"
            required
          />
        </div>

        <div>
          <label className="label" htmlFor="location">
            Location
          </label>
          <input
            id="location"
            value={location}
            onChange={(event) => setLocation(event.target.value)}
            className="field"
            placeholder="Bangalore"
          />
        </div>

        <div style={{ display: 'flex', alignItems: 'end' }}>
          <button className="btn btn-primary" style={{ width: '100%' }} disabled={busy} type="submit">
            {loading ? 'Searching...' : 'Scan'}
          </button>
        </div>

        <div style={{ display: 'flex', alignItems: 'end' }}>
          <button className="btn btn-ghost" style={{ width: '100%' }} disabled={busy} type="button" onClick={handleFreshCrawl}>
            {crawlLoading ? 'Queueing...' : 'Fresh Crawl'}
          </button>
        </div>
      </div>

      <p className="page-sub" style={{ marginTop: '0.7rem', fontSize: '0.84rem' }}>
        Fresh crawl needs both job title and location, then background workers fetch new jobs from sources.
      </p>
    </form>
  );
}
