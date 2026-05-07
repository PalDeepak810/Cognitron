import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import HistoryFilterBar from '../components/HistoryFilterBar';
import { observatoryAPI } from '../services/api';

function fmt(value) {
  if (!value) return 'N/A';
  const d = new Date(value);
  return Number.isNaN(d.getTime()) ? 'N/A' : d.toLocaleString();
}

export default function ObservatoryHistoryPage() {
  const [page, setPage] = useState(1);
  const [limit] = useState(25);
  const [filters, setFilters] = useState({
    domain: '',
    status: 'all',
    fromDate: '',
    toDate: '',
  });
  const [data, setData] = useState({ total: 0, items: [] });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const load = async (nextPage = page, nextFilters = filters) => {
    setLoading(true);
    setError(null);
    try {
      const params = {
        page: nextPage,
        limit,
        domain: nextFilters.domain || undefined,
        status: nextFilters.status || undefined,
        fromDate: nextFilters.fromDate || undefined,
        toDate: nextFilters.toDate || undefined,
      };
      const res = await observatoryAPI.getCrawlHistory(params);
      setData(res.data || { total: 0, items: [] });
    } catch (err) {
      setError('Unable to load crawl history.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load(1, filters);
    setPage(1);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const totalPages = Math.max(1, Math.ceil((data.total || 0) / limit));

  return (
    <div className="page">
      <section className="panel panel-pad panel-strong">
        <div className="btn-row" style={{ justifyContent: 'space-between' }}>
          <div>
            <span className="badge">Observatory</span>
            <h1 className="page-lead" style={{ marginTop: '0.55rem', fontSize: '1.9rem' }}>
              Crawl History
            </h1>
          </div>
          <div className="btn-row">
            <Link className="btn btn-ghost" to="/observatory">
              Back to Live
            </Link>
          </div>
        </div>
      </section>

      <HistoryFilterBar
        filters={filters}
        onChange={setFilters}
        onApply={() => {
          setPage(1);
          load(1, filters);
        }}
      />

      {error && <div className="error-strip">{error}</div>}

      <section className="panel panel-pad">
        {loading ? (
          <p className="page-sub">Loading history...</p>
        ) : !data.items?.length ? (
          <p className="page-sub">No history rows found.</p>
        ) : (
          <div style={{ overflowX: 'auto' }}>
            <table className="simple-table">
              <thead>
                <tr>
                  <th>URL</th>
                  <th>Domain</th>
                  <th>Status</th>
                  <th>Depth</th>
                  <th>Links</th>
                  <th>Time</th>
                  <th>Created</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {data.items.map((item) => (
                  <tr key={item.id}>
                    <td>{item.url}</td>
                    <td>{item.domain}</td>
                    <td>{item.status}</td>
                    <td>{item.depth}</td>
                    <td>{item.linksDiscovered ?? 0}</td>
                    <td>{item.processingTimeMs ?? 0}ms</td>
                    <td>{fmt(item.createdAt)}</td>
                    <td>
                      <Link className="badge" to={`/observatory/trace/${item.id}`}>
                        View Path
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        <div className="btn-row" style={{ marginTop: '0.8rem', justifyContent: 'space-between' }}>
          <span className="mini-chip">
            Page {page} / {totalPages} | Total {data.total || 0}
          </span>
          <div className="btn-row">
            <button
              className="btn btn-ghost"
              type="button"
              disabled={page <= 1 || loading}
              onClick={() => {
                const next = page - 1;
                setPage(next);
                load(next, filters);
              }}
            >
              Previous
            </button>
            <button
              className="btn btn-primary"
              type="button"
              disabled={page >= totalPages || loading}
              onClick={() => {
                const next = page + 1;
                setPage(next);
                load(next, filters);
              }}
            >
              Next
            </button>
          </div>
        </div>
      </section>
    </div>
  );
}
