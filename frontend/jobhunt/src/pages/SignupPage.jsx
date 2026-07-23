import { Link } from 'react-router-dom';
import './SignupPage.css';

export default function SignupPage() {
  const handleSubmit = (event) => {
    event.preventDefault();
    // Placeholder submit behavior
  };

  return (
    <div className="signup-shell">
      <div className="signup-background">
        <div className="signup-glow signup-glow-left" />
        <div className="signup-glow signup-glow-right" />
      </div>

      <main className="signup-grid container">
        <section className="signup-panel signup-panel-left">
          <div className="signup-brand">
            <span className="material-symbols-outlined signup-brand-icon">analytics</span>
            <div>
              <p className="signup-brand-label">CrawlOps</p>
              <h1 className="signup-title">Start monitoring pipelines in <span>seconds</span>.</h1>
            </div>
          </div>

          <p className="signup-copy">
            The enterprise-grade job aggregation engine. Join 2,000+ recruitment professionals scaling their
            data strategy with automated crawling and real-time validation.
          </p>

          <div className="signup-stats">
            <div className="signup-stat floating-element" style={{ animationDelay: '0s' }}>
              <div className="stat-head">
                <span className="material-symbols-outlined stat-icon">speed</span>
                <span className="stat-label">Latency</span>
              </div>
              <p className="stat-value">240ms</p>
            </div>
            <div className="signup-stat floating-element" style={{ animationDelay: '1s' }}>
              <div className="stat-head">
                <span className="material-symbols-outlined stat-icon">check_circle</span>
                <span className="stat-label">Integrity</span>
              </div>
              <p className="stat-value stat-value-success">99.9%</p>
            </div>
          </div>

          <div className="signup-preview">
            <img
              className="preview-image"
              src="https://lh3.googleusercontent.com/aida-public/AB6AXuDaV95C7HUT5WLTT-TluXqAauNIPm2oBq8jSFG6g5WjIb1H-WuYwDsYJBHhypIiGsedIvzjHtZCgrrmideSHEQbWouXrsEAl3nG17cENx-ShVPXKCfp0-DEU-nMRIshoXiBBuC9qHdQHZnkxE2KQgUmImQxygCe7xEa0o_TnY-HrjX3DS4vtrdESZYl7UFH9bJDX8iJN-E0opnd0vfuel4-J8RoMXVeXhdLtDNQHV6n9yeJEwL2Vak5Vg"
              alt="Dashboard screenshot showing real-time pipeline metrics and status cards."
            />
            <div className="preview-overlay">
              <div className="preview-status">
                <div className="preview-status-icon">
                  <span className="material-symbols-outlined">bolt</span>
                </div>
                <div>
                  <p className="preview-label">Real-time Stream</p>
                  <p className="preview-subtitle">Active pipelines in North America</p>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section className="signup-panel signup-panel-right">
          <div className="signup-form-shell">
            <div className="signup-form-header">
              <h2>Create your account</h2>
              <p>Get started with a 14-day free trial. No credit card required.</p>
            </div>

            <form className="signup-form" onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="name">FULL NAME</label>
                <input id="name" type="text" placeholder="Alex Rivera" />
              </div>

              <div className="form-group">
                <label htmlFor="email">WORK EMAIL</label>
                <input id="email" type="email" placeholder="alex@company.com" />
              </div>

              <div className="form-group">
                <label htmlFor="company">
                  <span>COMPANY NAME</span>
                  <span className="optional">Optional</span>
                </label>
                <input id="company" type="text" placeholder="Acme Analytics" />
              </div>

              <div className="form-group form-group-password">
                <label htmlFor="password">PASSWORD</label>
                <input id="password" type="password" placeholder="••••••••••••" />
                <button type="button" className="password-toggle" aria-label="Toggle password visibility">
                  <span className="material-symbols-outlined">visibility</span>
                </button>
              </div>

              <div className="form-terms">
                <label className="checkbox-label">
                  <input type="checkbox" />
                  <span>I agree to the <a href="#">Terms of Service</a> and <a href="#">Privacy Policy</a>.</span>
                </label>
              </div>

              <button type="submit" className="signup-button">
                <span>Create Account</span>
                <span className="material-symbols-outlined">arrow_forward</span>
              </button>
            </form>

            <div className="signup-divider">
              <span>OR SIGN UP WITH</span>
            </div>

            <div className="signup-social-grid">
              <button className="social-button social-button-light">
                <img
                  className="social-icon"
                  src="https://lh3.googleusercontent.com/aida-public/AB6AXuD8_H7oN1TVqGviBPXljYllUMOstPaEaE7Y_Xne7XBaRouugZzqIJ46zcXQD4RG4sGiW7R_l4rCAPg0Mr9ECdBIch8l6E1JUqsgUQ-lahwRYfI-cBhZP4WnMgRiofVg5lJ19PzgN9wWAk5dbiiXJK7KDnRGOVLFMjvNj9tk6-kgBhxUpTbGNz3jqkNsivvGHI8X9HQMOgOAwYUGa2M2yJekEOGkF-AmV3ZSve8fJr6h7XPtGonwnMq9qQ"
                  alt="Google logo"
                />
                <span>Google</span>
              </button>
              <button className="social-button social-button-dark">
                <svg viewBox="0 0 24 24" className="social-icon">
                  <path d="M12 .297c-6.63 0-12 5.373-12 12 0 5.303 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61C4.422 18.07 3.633 17.7 3.633 17.7c-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.43.372.823 1.102.823 2.222 0 1.606-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 22.092 24 17.592 24 12.297c0-6.627-5.373-12-12-12" />
                </svg>
                <span>GitHub</span>
              </button>
            </div>

            <p className="signup-login-copy">
              Already have an account? <Link className="signup-login-link" to="/">Log in</Link>
            </p>
          </div>
        </section>
      </main>

      <div className="signup-badge">
        <span className="material-symbols-outlined">verified_user</span>
        <span>SOC2 Type II Compliant</span>
      </div>
    </div>
  );
}
