export default function HistoryFilterBar({ filters, onChange, onApply }) {
  const set = (key, value) => onChange({ ...filters, [key]: value });

  return (
    <div className="panel panel-pad" style={{ marginBottom: '0.8rem' }}>
      <div className="input-grid">
        <div>
          <label className="label">Domain</label>
          <input
            className="field"
            value={filters.domain}
            onChange={(e) => set('domain', e.target.value)}
            placeholder="linkedin.com"
          />
        </div>
        <div>
          <label className="label">Status</label>
          <select className="field" value={filters.status} onChange={(e) => set('status', e.target.value)}>
            <option value="all">all</option>
            <option value="PROCESSING">PROCESSING</option>
            <option value="COMPLETED">COMPLETED</option>
            <option value="FAILED">FAILED</option>
          </select>
        </div>
        <div>
          <label className="label">From (ISO)</label>
          <input
            className="field"
            value={filters.fromDate}
            onChange={(e) => set('fromDate', e.target.value)}
            placeholder="2026-04-30T00:00:00"
          />
        </div>
      </div>
      <div className="input-grid" style={{ marginTop: '0.6rem' }}>
        <div>
          <label className="label">To (ISO)</label>
          <input
            className="field"
            value={filters.toDate}
            onChange={(e) => set('toDate', e.target.value)}
            placeholder="2026-04-30T23:59:59"
          />
        </div>
      </div>
      <div className="btn-row" style={{ marginTop: '0.75rem' }}>
        <button className="btn btn-primary" type="button" onClick={onApply}>
          Apply Filters
        </button>
      </div>
    </div>
  );
}
