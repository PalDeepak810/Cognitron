function fmt(value) {
  if (!value) return 'N/A';
  const d = new Date(value);
  return Number.isNaN(d.getTime()) ? 'N/A' : d.toLocaleString();
}

function badgeClass(state) {
  if (state === 'connected') return 'diag-badge diag-ok';
  if (state === 'reconnecting') return 'diag-badge diag-warn';
  return 'diag-badge diag-bad';
}

export default function ConnectionDiagnostics({
  wsState,
  connected,
  connectionAttempts,
  lastConnectedAt,
  lastDisconnectedAt,
  lastErrorMessage,
}) {
  return (
    <section className="panel panel-pad">
      <div className="btn-row" style={{ justifyContent: 'space-between' }}>
        <h2 style={{ fontSize: '1rem' }}>Connection Diagnostics</h2>
        <span className={badgeClass(wsState)}>{connected ? 'connected' : wsState}</span>
      </div>
      <div className="diag-grid">
        <div className="diag-item">
          <span className="diag-key">Attempts</span>
          <span className="diag-val">{connectionAttempts}</span>
        </div>
        <div className="diag-item">
          <span className="diag-key">Last Connected</span>
          <span className="diag-val">{fmt(lastConnectedAt)}</span>
        </div>
        <div className="diag-item">
          <span className="diag-key">Last Disconnected</span>
          <span className="diag-val">{fmt(lastDisconnectedAt)}</span>
        </div>
        <div className="diag-item">
          <span className="diag-key">Last Error</span>
          <span className="diag-val">{lastErrorMessage || 'None'}</span>
        </div>
      </div>
    </section>
  );
}
