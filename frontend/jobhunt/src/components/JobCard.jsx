function sourceClass(source = '') {
  const normalized = source.toLowerCase();
  if (normalized.includes('naukri')) return 'badge' ;
  if (normalized.includes('linkedin')) return 'badge';
  if (normalized.includes('indeed')) return 'badge';
  return 'badge';
}

function formatDate(value) {
  if (!value) return 'Unknown';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return 'Unknown';
  return date.toLocaleDateString();
}

export default function JobCard({ job }) {
  const skills = job.skills ? job.skills.split(',').map((skill) => skill.trim()).filter(Boolean).slice(0, 6) : [];

  return (
    <article className="panel panel-pad panel-hover">
      <div className="job-card-head">
        <div>
          <h3 style={{ fontSize: '1rem' }}>{job.title || 'Untitled Role'}</h3>
          <p className="page-sub" style={{ marginTop: '0.32rem', fontSize: '0.9rem' }}>
            {job.company || 'Unknown Company'}
          </p>
        </div>
        <span className={sourceClass(job.source)}>{job.source || 'Unknown'}</span>
      </div>

      <div className="job-meta">
        {job.location && <span className="mini-chip">Location: {job.location}</span>}
        {job.salary && <span className="mini-chip">Salary: {job.salary}</span>}
        {job.experience && <span className="mini-chip">Experience: {job.experience}</span>}
      </div>

      {!!skills.length && (
        <div className="skill-wrap">
          {skills.map((skill) => (
            <span className="skill-pill" key={skill}>
              {skill}
            </span>
          ))}
        </div>
      )}

      <p className="page-sub" style={{ marginTop: '0.72rem', fontSize: '0.88rem' }}>
        {(job.description || 'No description available.').slice(0, 180)}
        {(job.description || '').length > 180 ? '...' : ''}
      </p>

      <div className="btn-row" style={{ justifyContent: 'space-between', marginTop: '0.9rem' }}>
        <span className="mini-chip">Posted: {formatDate(job.postedDate || job.createdAt)}</span>
        {job.applicationLink ? (
          <a className="btn btn-ghost" href={job.applicationLink} target="_blank" rel="noreferrer noopener">
            Open Listing
          </a>
        ) : (
          <span className="mini-chip">No URL</span>
        )}
      </div>
    </article>
  );
}
