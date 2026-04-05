import { useState } from 'react';
import { subscriptionAPI } from '../services/api';

const initialForm = {
  email: '',
  jobTitles: '',
  locations: '',
};

export default function SubscriptionPanel({ onStatusUpdate }) {
  const [form, setForm] = useState(initialForm);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const setStatus = (text) => {
    if (onStatusUpdate) onStatusUpdate(text);
  };

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const validateEmail = () => {
    if (!form.email.trim()) {
      setError('Email is required for subscriptions.');
      return false;
    }
    return true;
  };

  const handleSubscribe = async () => {
    if (!validateEmail()) return;

    setLoading(true);
    setError('');
    setMessage('');
    setStatus('Saving subscription');

    try {
      await subscriptionAPI.subscribe({
        email: form.email.trim(),
        jobTitles: form.jobTitles.trim(),
        locations: form.locations.trim(),
      });
      setMessage('Subscription saved. Daily alerts will be sent to your email.');
      setStatus('Subscription active');
    } catch (err) {
      setError('Failed to save subscription.');
      setStatus('Subscription failed');
      console.error('subscribe failed:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCheck = async () => {
    if (!validateEmail()) return;

    setLoading(true);
    setError('');
    setMessage('');
    setStatus('Checking subscription');

    try {
      const response = await subscriptionAPI.getByEmail(form.email.trim());
      const data = response.data || {};
      setForm((prev) => ({
        ...prev,
        jobTitles: data.jobTitles || prev.jobTitles,
        locations: data.locations || prev.locations,
      }));
      setMessage(data.active ? 'Subscription is active.' : 'Subscription exists but currently inactive.');
      setStatus('Subscription loaded');
    } catch (err) {
      setError('Subscription not found for this email.');
      setStatus('No subscription found');
      console.error('check subscription failed:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleUnsubscribe = async () => {
    if (!validateEmail()) return;

    setLoading(true);
    setError('');
    setMessage('');
    setStatus('Disabling subscription');

    try {
      await subscriptionAPI.unsubscribe(form.email.trim());
      setMessage('Subscription disabled successfully.');
      setStatus('Subscription disabled');
    } catch (err) {
      setError('Failed to unsubscribe this email.');
      setStatus('Unsubscribe failed');
      console.error('unsubscribe failed:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="panel panel-pad">
      <div className="btn-row" style={{ justifyContent: 'space-between' }}>
        <h3 style={{ fontSize: '0.96rem' }}>Job Alert Subscription</h3>
        <span className="badge">Email digest</span>
      </div>

      <div style={{ marginTop: '0.8rem', display: 'grid', gap: '0.65rem' }}>
        <div>
          <label className="label" htmlFor="subscription-email">
            Email
          </label>
          <input
            id="subscription-email"
            name="email"
            type="email"
            className="field"
            value={form.email}
            placeholder="you@example.com"
            onChange={handleChange}
            disabled={loading}
          />
        </div>

        <div>
          <label className="label" htmlFor="subscription-titles">
            Preferred Titles
          </label>
          <input
            id="subscription-titles"
            name="jobTitles"
            className="field"
            value={form.jobTitles}
            placeholder="Backend Engineer, Java Developer"
            onChange={handleChange}
            disabled={loading}
          />
        </div>

        <div>
          <label className="label" htmlFor="subscription-locations">
            Preferred Locations
          </label>
          <input
            id="subscription-locations"
            name="locations"
            className="field"
            value={form.locations}
            placeholder="Bangalore, Pune"
            onChange={handleChange}
            disabled={loading}
          />
        </div>
      </div>

      <div className="btn-row" style={{ marginTop: '0.85rem' }}>
        <button className="btn btn-primary" type="button" onClick={handleSubscribe} disabled={loading}>
          Save
        </button>
        <button className="btn btn-ghost" type="button" onClick={handleCheck} disabled={loading}>
          Check
        </button>
        <button className="btn btn-ghost" type="button" onClick={handleUnsubscribe} disabled={loading}>
          Unsubscribe
        </button>
      </div>

      {message && <div className="info-strip">{message}</div>}
      {error && <div className="error-strip">{error}</div>}
    </section>
  );
}
