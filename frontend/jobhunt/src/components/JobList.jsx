import JobCard from './JobCard';

function LoadingState() {
  return (
    <div className="panel panel-pad loading">
      <div className="dot-loader" aria-hidden="true">
        <span />
        <span />
        <span />
      </div>
      <p style={{ marginTop: '0.7rem' }}>Synchronizing role signals...</p>
    </div>
  );
}

function EmptyState() {
  return (
    <div className="panel panel-pad empty">
      <h3 style={{ fontSize: '1rem' }}>No job packets found</h3>
      <p className="page-sub" style={{ marginTop: '0.45rem' }}>
        Adjust search terms or filters and run another scan.
      </p>
    </div>
  );
}

export default function JobList({ jobs, loading }) {
  if (loading) return <LoadingState />;
  if (!jobs || jobs.length === 0) return <EmptyState />;

  return (
    <section>
      <div className="btn-row" style={{ justifyContent: 'space-between' }}>
        <h2 style={{ fontSize: '1rem' }}>Result Stream</h2>
        <span className="badge">{jobs.length} listings</span>
      </div>
      <div className="job-cards">
        {jobs.map((job) => (
          <JobCard key={`${job.id}-${job.applicationLink || ''}`} job={job} />
        ))}
      </div>
    </section>
  );
}
