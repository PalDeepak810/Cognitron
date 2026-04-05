import { Link } from 'react-router-dom';

const highlights = [
  {
    title: 'Federated Crawl Mesh',
    text: 'Queue-driven workers ingest jobs from multiple portals without coupling crawl and extraction pipelines.',
  },
  {
    title: 'Signal Enrichment',
    text: 'Each listing is normalized with skills, location, salary context, and source metadata for smarter search.',
  },
  {
    title: 'Operational Telemetry',
    text: 'Live quota, active runs, and trend summaries help monitor crawl health like a production control room.',
  },
];

const sourceCards = ['Naukri', 'Indeed', 'LinkedIn'];

export default function LandingPage() {
  return (
    <div className="page">
      <section className="hero-grid">
        <article className="panel panel-pad panel-hover panel-strong">
          <span className="badge">Version 2.0 Interface</span>
          <h1 className="page-lead" style={{ marginTop: '0.9rem' }}>
            Search the job market through a futuristic intelligence layer.
          </h1>
          <div className="accent-line" />
          <p className="page-sub">
            Cognitron JobHunt fuses distributed crawling, extraction, and analytics into one high-clarity platform. You get one search surface with cross-platform reach.
          </p>
          <div className="btn-row" style={{ marginTop: '1.25rem' }}>
            <Link className="btn btn-primary" to="/jobs">
              Launch Search Grid
            </Link>
            <Link className="btn btn-ghost" to="/dashboard">
              Open Telemetry Board
            </Link>
          </div>
          <div className="hero-kpis">
            <div className="kpi-card">
              <div className="kpi-num">3+</div>
              <div className="kpi-label">Source Networks</div>
            </div>
            <div className="kpi-card">
              <div className="kpi-num">24h</div>
              <div className="kpi-label">Crawl Pulse</div>
            </div>
            <div className="kpi-card">
              <div className="kpi-num">1 UI</div>
              <div className="kpi-label">Unified Discovery</div>
            </div>
          </div>
        </article>

        <article className="panel panel-pad panel-hover">
          <h2 style={{ fontSize: '1.15rem' }}>Live Pipeline Overview</h2>
          <p className="page-sub" style={{ marginTop: '0.55rem' }}>
            Browser requests trigger URL generation, queue fan-out, processor extraction, and real-time indexing.
          </p>
          <div className="stack" style={{ marginTop: '1rem' }}>
            {[
              'Search intent converted into site-specific query URLs',
              'RabbitMQ routes jobs to processor workers',
              'Dedup and quota controls protect crawler stability',
              'Clean records surface in jobs and telemetry screens',
            ].map((step, index) => (
              <div key={step} className="panel panel-pad" style={{ borderRadius: '14px' }}>
                <span className="badge">Step {index + 1}</span>
                <p style={{ marginTop: '0.45rem' }}>{step}</p>
              </div>
            ))}
          </div>
        </article>
      </section>

      <section style={{ marginTop: '1.2rem' }}>
        <div className="grid-3">
          {highlights.map((item) => (
            <article key={item.title} className="panel panel-pad panel-hover">
              <h3 style={{ fontSize: '1rem' }}>{item.title}</h3>
              <p className="page-sub" style={{ marginTop: '0.55rem', fontSize: '0.95rem' }}>
                {item.text}
              </p>
            </article>
          ))}
        </div>
      </section>

      <section style={{ marginTop: '1.2rem' }}>
        <div className="grid-2">
          <article className="panel panel-pad panel-hover">
            <h2 style={{ fontSize: '1.05rem' }}>Connected Talent Sources</h2>
            <p className="page-sub" style={{ marginTop: '0.55rem' }}>
              Current connectors focus on the strongest job channels in the India market and can be expanded as needed.
            </p>
            <div className="btn-row" style={{ marginTop: '0.9rem' }}>
              {sourceCards.map((source) => (
                <span key={source} className="badge">
                  {source}
                </span>
              ))}
            </div>
          </article>

          <article className="panel panel-pad panel-hover">
            <h2 style={{ fontSize: '1.05rem' }}>Ready for Candidate Discovery</h2>
            <p className="page-sub" style={{ marginTop: '0.55rem' }}>
              Jump into search to discover roles, or open telemetry to track crawl behavior and freshness of the catalog.
            </p>
            <div className="btn-row" style={{ marginTop: '1rem' }}>
              <Link className="btn btn-primary" to="/jobs">
                Start Now
              </Link>
            </div>
          </article>
        </div>
      </section>
    </div>
  );
}
