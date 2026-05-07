export default function QueueDepthTrend({ queueStats }) {
  const rows = (queueStats || []).map((q) => ({
    name: q.name,
    value: Number(q.messageCount || 0),
  }));
  const max = Math.max(...rows.map((r) => r.value), 1);

  return (
    <div className="queue-trend-wrap">
      {!rows.length ? (
        <p className="page-sub">No queue depth samples yet.</p>
      ) : (
        <div className="queue-bars">
          {rows.map((r) => (
            <div className="queue-bar-row" key={r.name}>
              <div className="queue-bar-label">{r.name}</div>
              <div className="queue-bar-track">
                <div
                  className="queue-bar-fill"
                  style={{ width: `${Math.max(4, Math.round((r.value / max) * 100))}%` }}
                />
              </div>
              <div className="queue-bar-value">{r.value}</div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
