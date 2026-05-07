import { ObservatoryWebSocketProvider } from '../context/ObservatoryWebSocketProvider';
import useObservatory from '../hooks/useObservatory';
import { Link } from 'react-router-dom';
import ThroughputMiniChart from '../components/ThroughputMiniChart';
import QueueDepthTrend from '../components/QueueDepthTrend';
import ConnectionDiagnostics from '../components/ConnectionDiagnostics';

function formatTime(value) {
  if (!value) return 'N/A';
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? 'N/A' : date.toLocaleTimeString();
}

function ObservatoryBody() {
  const {
    connected,
    wsState,
    connectionAttempts,
    lastConnectedAt,
    lastDisconnectedAt,
    lastErrorMessage,
    queueStats,
    activeCrawls,
    recentlyCompleted,
    throughput,
    error,
    refreshFromApi,
  } = useObservatory();

  return (
    <div className="page">
      <section className="panel panel-pad panel-strong">
        <div className="btn-row" style={{ justifyContent: 'space-between' }}>
          <div>
            <span className="badge">Observatory</span>
            <h1 className="page-lead" style={{ marginTop: '0.55rem', fontSize: '2rem' }}>
              Live Queue and Crawl Monitor
            </h1>
            <p className="page-sub" style={{ marginTop: '0.4rem' }}>
              Streaming queue pressure, crawl lifecycle, and throughput samples.
            </p>
          </div>
          <div className="stack" style={{ minWidth: '210px' }}>
            <span className={`mini-chip ${connected ? '' : 'chip-muted'}`}>
              {connected ? 'WS Connected' : wsState === 'reconnecting' ? 'Reconnecting' : 'Polling Fallback'}
            </span>
            <button className="btn btn-primary" type="button" onClick={refreshFromApi}>
              Refresh Snapshot
            </button>
            <Link className="btn btn-ghost" to="/observatory/history">
              Open History
            </Link>
          </div>
        </div>
      </section>

      {error && <div className="error-strip">{error}</div>}

      <section className="dashboard-grid" style={{ marginTop: '0.9rem' }}>
        <ConnectionDiagnostics
          wsState={wsState}
          connected={connected}
          connectionAttempts={connectionAttempts}
          lastConnectedAt={lastConnectedAt}
          lastDisconnectedAt={lastDisconnectedAt}
          lastErrorMessage={lastErrorMessage}
        />
      </section>

      <section className="dashboard-grid" style={{ marginTop: '0.9rem' }}>
        <article className="panel panel-pad">
          <h2 style={{ fontSize: '1rem' }}>Queues</h2>
          {!queueStats.length ? (
            <p className="page-sub" style={{ marginTop: '0.55rem' }}>
              No queue data yet.
            </p>
          ) : (
            <div className="data-list" style={{ marginTop: '0.7rem' }}>
              {queueStats.map((q) => (
                <div className="data-row" key={q.name}>
                  <span>{q.name}</span>
                  <span className="badge">
                    msgs {q.messageCount} | c {q.consumerCount} | r {q.messagesPerSecond.toFixed(1)}/s
                  </span>
                </div>
              ))}
            </div>
          )}
        </article>

        <article className="panel panel-pad">
          <h2 style={{ fontSize: '1rem' }}>Queue Depth Trend</h2>
          <QueueDepthTrend queueStats={queueStats} />
        </article>
      </section>

      <section className="dashboard-grid" style={{ marginTop: '0.9rem' }}>
        <article className="panel panel-pad">
          <h2 style={{ fontSize: '1rem' }}>Active Crawls</h2>
          {!activeCrawls.length ? (
            <p className="page-sub" style={{ marginTop: '0.55rem' }}>
              No active crawls.
            </p>
          ) : (
            <div className="data-list" style={{ marginTop: '0.7rem' }}>
              {activeCrawls.slice(0, 12).map((c) => (
                <div className="data-row" key={`${c.id}-${c.url}`}>
                  <span>{c.url}</span>
                  <span className="badge">{c.status || 'PROCESSING'}</span>
                </div>
              ))}
            </div>
          )}
        </article>
      </section>

      <section className="dashboard-grid" style={{ marginTop: '0.9rem' }}>
        <article className="panel panel-pad">
          <h2 style={{ fontSize: '1rem' }}>Recently Completed</h2>
          {!recentlyCompleted.length ? (
            <p className="page-sub" style={{ marginTop: '0.55rem' }}>
              No completed events yet.
            </p>
          ) : (
            <div className="data-list" style={{ marginTop: '0.7rem' }}>
              {recentlyCompleted.slice(0, 10).map((c) => (
                <div className="data-row" key={`${c.id}-${c.url}`}>
                  <span>{c.url}</span>
                  <span className="badge">{c.processingTimeMs || 0}ms</span>
                </div>
              ))}
            </div>
          )}
        </article>

        <article className="panel panel-pad">
          <h2 style={{ fontSize: '1rem' }}>Throughput Trend</h2>
          <ThroughputMiniChart points={throughput} />
        </article>
      </section>

      <section className="dashboard-grid" style={{ marginTop: '0.9rem' }}>
        <article className="panel panel-pad">
          <h2 style={{ fontSize: '1rem' }}>Throughput Samples</h2>
          {!throughput.length ? (
            <p className="page-sub" style={{ marginTop: '0.55rem' }}>
              No throughput samples yet.
            </p>
          ) : (
            <div className="data-list" style={{ marginTop: '0.7rem' }}>
              {throughput.slice(-10).map((p, idx) => (
                <div className="data-row" key={`${p.timestamp}-${p.queueName}-${idx}`}>
                  <span>
                    {formatTime(p.timestamp)} | {p.queueName}
                  </span>
                  <span className="badge">{Number(p.messagesPerSecond || 0).toFixed(1)}/s</span>
                </div>
              ))}
            </div>
          )}
        </article>
      </section>
    </div>
  );
}

export default function ObservatoryPage() {
  return (
    <ObservatoryWebSocketProvider>
      <ObservatoryBody />
    </ObservatoryWebSocketProvider>
  );
}
