import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { observatoryAPI } from '../services/api';

function fmt(value) {
  if (!value) return 'N/A';
  const d = new Date(value);
  return Number.isNaN(d.getTime()) ? 'N/A' : d.toLocaleString();
}

export default function ObservatoryTracePage() {
  const { id } = useParams();
  const [trace, setTrace] = useState({ url: null, discoveryPath: [] });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    let alive = true;
    const load = async () => {
      setLoading(true);
      setError(null);
      try {
        const res = await observatoryAPI.getTrace(id);
        if (!alive) return;
        setTrace(res.data || { url: null, discoveryPath: [] });
      } catch (err) {
        if (!alive) return;
        setError('Unable to load trace.');
      } finally {
        if (alive) setLoading(false);
      }
    };
    load();
    return () => {
      alive = false;
    };
  }, [id]);

  return (
    <div className="page">
      <section className="panel panel-pad panel-strong">
        <div className="btn-row" style={{ justifyContent: 'space-between' }}>
          <div>
            <span className="badge">Observatory</span>
            <h1 className="page-lead" style={{ marginTop: '0.55rem', fontSize: '1.9rem' }}>
              URL Discovery Trace
            </h1>
            <p className="page-sub" style={{ marginTop: '0.35rem' }}>
              {trace.url || 'Loading target URL...'}
            </p>
          </div>
          <Link className="btn btn-ghost" to="/observatory/history">
            Back to History
          </Link>
        </div>
      </section>

      {error && <div className="error-strip">{error}</div>}
      <section className="panel panel-pad">
        {loading ? (
          <p className="page-sub">Loading trace...</p>
        ) : !trace.discoveryPath?.length ? (
          <p className="page-sub">No discovery path found.</p>
        ) : (
          <div className="data-list">
            {trace.discoveryPath.map((node, idx) => (
              <div className="panel panel-pad" key={`${node.url}-${idx}`} style={{ borderRadius: '14px' }}>
                <div className="btn-row" style={{ justifyContent: 'space-between' }}>
                  <strong>{node.url}</strong>
                  <span className="badge">Depth {node.depth}</span>
                </div>
                <p className="page-sub" style={{ marginTop: '0.35rem', fontSize: '0.85rem' }}>
                  discovered: {fmt(node.discoveredAt)}
                </p>
                <p className="page-sub" style={{ marginTop: '0.15rem', fontSize: '0.82rem' }}>
                  parent: {node.parentUrl || 'seed'}
                </p>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
