function toPoints(values, width, height, pad = 10) {
  if (!values.length) return '';
  const max = Math.max(...values, 1);
  const min = Math.min(...values, 0);
  const range = Math.max(max - min, 1);
  return values
    .map((v, i) => {
      const x = pad + (i * (width - pad * 2)) / Math.max(values.length - 1, 1);
      const y = height - pad - ((v - min) * (height - pad * 2)) / range;
      return `${x},${y}`;
    })
    .join(' ');
}

export default function ThroughputMiniChart({ points }) {
  const sample = (points || []).slice(-30);
  const values = sample.map((p) => Number(p.messagesPerSecond || 0));
  const width = 360;
  const height = 120;
  const polyline = toPoints(values, width, height, 12);

  return (
    <div className="mini-chart-wrap">
      {!values.length ? (
        <p className="page-sub">No throughput points available.</p>
      ) : (
        <svg viewBox={`0 0 ${width} ${height}`} className="mini-chart" role="img" aria-label="Throughput trend">
          <polyline fill="none" stroke="rgba(22,97,255,0.25)" strokeWidth="10" points={polyline} />
          <polyline fill="none" stroke="rgb(22,97,255)" strokeWidth="2.5" points={polyline} />
        </svg>
      )}
    </div>
  );
}
