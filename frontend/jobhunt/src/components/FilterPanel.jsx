import { useState } from 'react';

const initialFilters = {
  location: '',
  skills: '',
  company: '',
  minSalary: '',
  maxSalary: '',
};

export default function FilterPanel({ onFilter, loading }) {
  const [filters, setFilters] = useState(initialFilters);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFilters((prev) => ({ ...prev, [name]: value }));
  };

  const handleApply = (event) => {
    event.preventDefault();
    onFilter(filters);
  };

  const handleReset = () => {
    setFilters(initialFilters);
    onFilter(initialFilters);
  };

  return (
    <aside className="panel panel-pad">
      <div className="btn-row" style={{ justifyContent: 'space-between' }}>
        <h3 style={{ fontSize: '0.95rem' }}>Filter Rail</h3>
        <span className="badge">Precision</span>
      </div>

      <form onSubmit={handleApply} style={{ marginTop: '0.75rem' }}>
        <div style={{ display: 'grid', gap: '0.65rem' }}>
          <div>
            <label className="label" htmlFor="filter-location">
              Location
            </label>
            <input
              id="filter-location"
              className="field"
              name="location"
              placeholder="City"
              value={filters.location}
              onChange={handleChange}
            />
          </div>

          <div>
            <label className="label" htmlFor="filter-skills">
              Skills
            </label>
            <input
              id="filter-skills"
              className="field"
              name="skills"
              placeholder="Java, Spring"
              value={filters.skills}
              onChange={handleChange}
            />
          </div>

          <div>
            <label className="label" htmlFor="filter-company">
              Company
            </label>
            <input
              id="filter-company"
              className="field"
              name="company"
              placeholder="Google"
              value={filters.company}
              onChange={handleChange}
            />
          </div>

          <div>
            <label className="label" htmlFor="filter-min-salary">
              Min Salary
            </label>
            <input
              id="filter-min-salary"
              className="field"
              name="minSalary"
              placeholder="6 LPA"
              value={filters.minSalary}
              onChange={handleChange}
            />
          </div>

          <div>
            <label className="label" htmlFor="filter-max-salary">
              Max Salary
            </label>
            <input
              id="filter-max-salary"
              className="field"
              name="maxSalary"
              placeholder="20 LPA"
              value={filters.maxSalary}
              onChange={handleChange}
            />
          </div>
        </div>

        <div className="btn-row" style={{ marginTop: '0.85rem' }}>
          <button className="btn btn-primary" type="submit" disabled={loading}>
            Apply
          </button>
          <button className="btn btn-ghost" type="button" onClick={handleReset} disabled={loading}>
            Reset
          </button>
        </div>
      </form>
    </aside>
  );
}
