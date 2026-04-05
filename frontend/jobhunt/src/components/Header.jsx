import { NavLink } from 'react-router-dom';

const navItems = [
  { to: '/', label: 'Portal' },
  { to: '/jobs', label: 'Search Grid' },
  { to: '/dashboard', label: 'Telemetry' },
];

export default function Header() {
  return (
    <header className="top-nav-wrap">
      <div className="container-xl top-nav">
        <NavLink to="/" className="brand" aria-label="JobHunt home">
          <span className="brand-title">COGNITRON JOBHUNT</span>
          <span className="brand-sub">Future-ready hiring intelligence cockpit</span>
        </NavLink>

        <nav className="nav-links" aria-label="Primary navigation">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.to === '/'}
              className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
      </div>
    </header>
  );
}
