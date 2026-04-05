import { useEffect, useMemo, useState } from 'react';
import { dashboardAPI } from '../services/api';

function formatDateTime(value) {
  if (!value) return 'N/A';
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? 'N/A' : date.toLocaleString();
}

export default function DashboardPage() {
  const [overview, setOverview] = useState(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState(null);

  const loadOverview = async (background = false) => {
    if (background) {
      setRefreshing(true);
    } else {
      setLoading(true);
    }

    setError(null);

    try {
      const response = await dashboardAPI.getOverview();
      setOverview(response.data);
    } catch (err) {
      setError('Unable to load telemetry data. Ensure backend is running on port 8082.');
      console.error('loadOverview failed:', err);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    loadOverview();
    const timer = setInterval(() => loadOverview(true), 15000);
    return () => clearInterval(timer);
  }, []);

  const quotaPercent = useMemo(() => {
    const limit = Number(overview?.quota?.limit24h || 0);
    const used = Number(overview?.quota?.used24h || 0);
    if (limit <= 0) return 0;
    return Math.min(100, Math.round((used / limit) * 100));
  }, [overview]);

  if (loading) {
    return (
      <div className="page">
        <div className="panel panel-pad loading">
          <div className="dot-loader" aria-hidden="true">
            <span />
            <span />
            <span />
          </div>
          <p style={{ marginTop: '0.7rem' }}>Initializing telemetry board...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="page">
      <section className="panel panel-pad panel-strong">
        <div className="btn-row" style={{ justifyContent: 'space-between' }}>
          <div>
            <span className="badge">Telemetry</span>
            <h1 className="page-lead" style={{ marginTop: '0.55rem', fontSize: '2rem' }}>
              Live crawl operations dashboard
            </h1>
            <p className="page-sub" style={{ marginTop: '0.45rem' }}>
              Monitor quota pressure, active runs, and source trends across your crawling network.
            </p>
          </div>

          <div className="stack" style={{ minWidth: '230px' }}>
            <span className="mini-chip">Updated: {formatDateTime(overview?.generatedAt)}</span>
            <button className="btn btn-primary" type="button" onClick={() => loadOverview(true)} disabled={refreshing}>
              {refreshing ? 'Refreshing...' : 'Refresh Now'}
            </button>
          </div>
        </div>

        <div className="metric-grid">
          <article className="panel panel-pad">
            <p className="label">Total Jobs</p>
            <p className="metric-value">{overview?.jobs?.totalJobs ?? 0}</p>
          </article>
          <article className="panel panel-pad">
            <p className="label">New in 24h</p>
            <p className="metric-value">{overview?.jobs?.newJobsLast24h ?? 0}</p>
          </article>
          <article className="panel panel-pad">
            <p className="label">Remaining Quota</p>
            <p className="metric-value">{overview?.quota?.remaining24h ?? 0}</p>
          </article>
          <article className="panel panel-pad">
            <p className="label">Active Runs (10m)</p>
            <p className="metric-value">{overview?.crawl?.activeRunsLast10Minutes ?? 0}</p>
          </article>
        </div>

        <article className="panel panel-pad" style={{ marginTop: '0.9rem' }}>
          <div className="btn-row" style={{ justifyContent: 'space-between' }}>
            <span className="label">24h Quota Usage</span>
            <span className="mini-chip">
              {overview?.quota?.used24h ?? 0} / {overview?.quota?.limit24h ?? 0}
            </span>
          </div>
          <div className="progress-track">
            <div className="progress-fill" style={{ width: `${quotaPercent}%` }} />
          </div>
          <p className="page-sub" style={{ marginTop: '0.45rem', fontSize: '0.84rem' }}>
            Last consumption event: {formatDateTime(overview?.quota?.lastConsumedAt)}
          </p>
        </article>
      </section>

      {error && <div className="error-strip">{error}</div>}

      <section className="dashboard-grid">
        <div className="stack">
          <article className="panel panel-pad">
            <h2 style={{ fontSize: '1rem' }}>Latest Crawl Runs</h2>
            {overview?.crawl?.latestRuns?.length ? (
              <div style={{ overflowX: 'auto', marginTop: '0.65rem' }}>
                <table className="simple-table">
                  <thead>
                    <tr>
                      <th>Run ID</th>
                      <th>Processed</th>
                      <th>Limit</th>
                      <th>Remaining</th>
                      <th>Updated</th>
                    </tr>
                  </thead>
                  <tbody>
                    {overview.crawl.latestRuns.map((run) => (
                      <tr key={run.runId}>
                        <td className="mono">{(run.runId || '').slice(0, 10)}...</td>
                        <td>{run.processedCount}</td>
                        <td>{run.runLimit}</td>
                        <td>{run.remaining}</td>
                        <td>{formatDateTime(run.updatedAt)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <p className="page-sub" style={{ marginTop: '0.55rem' }}>
                No crawl run records yet.
              </p>
            )}
          </article>

          <article className="panel panel-pad">
            <h2 style={{ fontSize: '1rem' }}>Recent Jobs Captured</h2>
            {overview?.jobs?.recentJobs?.length ? (
              <div className="data-list" style={{ marginTop: '0.7rem' }}>
                {overview.jobs.recentJobs.map((job) => (
                  <div className="panel panel-pad" key={job.id} style={{ borderRadius: '14px' }}>
                    <div className="btn-row" style={{ justifyContent: 'space-between' }}>
                      <strong style={{ fontSize: '0.92rem' }}>{job.title}</strong>
                      <span className="badge">{job.source}</span>
                    </div>
                    <p className="page-sub" style={{ marginTop: '0.35rem', fontSize: '0.86rem' }}>
                      {job.company} | {job.location}
                    </p>
                    <p className="page-sub" style={{ marginTop: '0.3rem', fontSize: '0.8rem' }}>
                      Added: {formatDateTime(job.createdAt)}
                    </p>
                  </div>
                ))}
              </div>
            ) : (
              <p className="page-sub" style={{ marginTop: '0.55rem' }}>
                No jobs captured yet.
              </p>
            )}
          </article>
        </div>

        <div className="stack">
          <article className="panel panel-pad">
            <h2 style={{ fontSize: '1rem' }}>Top Titles (24h)</h2>
            {overview?.jobs?.topTitlesLast24h?.length ? (
              <div className="data-list" style={{ marginTop: '0.7rem' }}>
                {overview.jobs.topTitlesLast24h.map((item) => (
                  <div className="data-row" key={item.title}>
                    <span>{item.title}</span>
                    <span className="badge">{item.count}</span>
                  </div>
                ))}
              </div>
            ) : (
              <p className="page-sub" style={{ marginTop: '0.55rem' }}>
                No title trends available.
              </p>
            )}
          </article>

          <article className="panel panel-pad">
            <h2 style={{ fontSize: '1rem' }}>Top Sources (24h)</h2>
            {overview?.jobs?.topSourcesLast24h?.length ? (
              <div className="data-list" style={{ marginTop: '0.7rem' }}>
                {overview.jobs.topSourcesLast24h.map((item) => (
                  <div className="data-row" key={item.source}>
                    <span>{item.source}</span>
                    <span className="badge">{item.count}</span>
                  </div>
                ))}
              </div>
            ) : (
              <p className="page-sub" style={{ marginTop: '0.55rem' }}>
                No source trends available.
              </p>
            )}
          </article>
        </div>
      </section>
    </div>
  );
}
