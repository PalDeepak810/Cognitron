import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Header from './components/Header';
import LandingPage from './pages/LandingPage';
import JobsPage from './pages/JobsPage';
import DashboardPage from './pages/DashboardPage';
import ObservatoryPage from './pages/ObservatoryPage';
import ObservatoryHistoryPage from './pages/ObservatoryHistoryPage';
import ObservatoryTracePage from './pages/ObservatoryTracePage';
import SignupPage from './pages/SignupPage';

function App() {
  return (
    <Router>
      <div className="app-shell">
        <Header />
        <main className="container-xl page-enter">
          <Routes>
            <Route path="/" element={<LandingPage />} />
            <Route path="/signup" element={<SignupPage />} />
            <Route path="/jobs" element={<JobsPage />} />
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/observatory" element={<ObservatoryPage />} />
            <Route path="/observatory/history" element={<ObservatoryHistoryPage />} />
            <Route path="/observatory/trace/:id" element={<ObservatoryTracePage />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
