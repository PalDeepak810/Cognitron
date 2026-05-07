import { Client } from '@stomp/stompjs';
import { createContext, useContext, useEffect, useMemo, useRef, useState } from 'react';
import { observatoryAPI } from '../services/api';

const ObservatoryContext = createContext(null);

function normalizeQueueStats(payload) {
  if (!payload?.queues) {
    return [];
  }
  return payload.queues.map((queue) => ({
    name: queue.name,
    messageCount: Number(queue.messageCount || 0),
    consumerCount: Number(queue.consumerCount || 0),
    messagesPerSecond: Number(queue.messagesPerSecond || 0),
  }));
}

export function ObservatoryWebSocketProvider({ children }) {
  const [connected, setConnected] = useState(false);
  const [wsState, setWsState] = useState('connecting');
  const [connectionAttempts, setConnectionAttempts] = useState(0);
  const [lastConnectedAt, setLastConnectedAt] = useState(null);
  const [lastDisconnectedAt, setLastDisconnectedAt] = useState(null);
  const [lastErrorMessage, setLastErrorMessage] = useState(null);
  const [queueStats, setQueueStats] = useState([]);
  const [activeCrawls, setActiveCrawls] = useState([]);
  const [recentlyCompleted, setRecentlyCompleted] = useState([]);
  const [throughput, setThroughput] = useState([]);
  const [error, setError] = useState(null);
  const pollingRef = useRef(null);

  const refreshFromApi = async () => {
    try {
      const [queuesRes, crawlsRes, throughputRes] = await Promise.all([
        observatoryAPI.getQueues(),
        observatoryAPI.getActiveCrawls(),
        observatoryAPI.getThroughput('1h', '5m'),
      ]);
      setQueueStats(normalizeQueueStats(queuesRes.data));
      setActiveCrawls(crawlsRes.data?.activeCrawls || []);
      setRecentlyCompleted(crawlsRes.data?.recentlyCompleted || []);
      setThroughput(throughputRes.data?.dataPoints || []);
      setError(null);
    } catch (err) {
      setError('Unable to load observatory telemetry.');
    }
  };

  useEffect(() => {
    let destroyed = false;
    const client = new Client({
      brokerURL: observatoryAPI.getWsUrl(),
      reconnectDelay: 3000,
      beforeConnect: () => {
        if (destroyed) return;
        setConnectionAttempts((prev) => prev + 1);
        setWsState('connecting');
      },
      onConnect: () => {
        if (destroyed) return;
        setConnected(true);
        setWsState('connected');
        setLastConnectedAt(new Date().toISOString());
        setLastErrorMessage(null);
        setError(null);
        if (pollingRef.current) {
          clearInterval(pollingRef.current);
          pollingRef.current = null;
        }
        client.subscribe('/topic/observatory', (message) => {
          try {
            const payload = JSON.parse(message.body);
            const type = payload?.type;
            const data = payload?.data;
            if (type === 'QUEUE_UPDATE') {
              setQueueStats(normalizeQueueStats(data));
              return;
            }
            if (type === 'THROUGHPUT_UPDATE') {
              setThroughput(data?.dataPoints || []);
              return;
            }
            if (type === 'CRAWL_STATUS') {
              const status = data?.status;
              if (status === 'PROCESSING') {
                setActiveCrawls((prev) => [data, ...prev.filter((c) => c.id !== data.id)].slice(0, 50));
                return;
              }
              if (status === 'COMPLETED') {
                setActiveCrawls((prev) => prev.filter((c) => c.id !== data.id));
                setRecentlyCompleted((prev) => [data, ...prev.filter((c) => c.id !== data.id)].slice(0, 20));
                return;
              }
              if (status === 'FAILED') {
                setActiveCrawls((prev) => prev.filter((c) => c.id !== data.id));
              }
            }
          } catch {
            // ignore malformed frames
          }
        });
      },
      onStompError: () => {
        if (destroyed) return;
        setConnected(false);
        setWsState('degraded');
        setLastErrorMessage('STOMP broker reported an error');
        setError('WebSocket error. Falling back to polling.');
      },
      onWebSocketClose: () => {
        if (destroyed) return;
        setConnected(false);
        setWsState('reconnecting');
        setLastDisconnectedAt(new Date().toISOString());
      },
      onWebSocketError: () => {
        if (destroyed) return;
        setConnected(false);
        setWsState('degraded');
        setLastErrorMessage('WebSocket transport error');
      },
    });

    refreshFromApi();
    client.activate();

    return () => {
      destroyed = true;
      client.deactivate();
      if (pollingRef.current) {
        clearInterval(pollingRef.current);
      }
    };
  }, []);

  useEffect(() => {
    if (connected) {
      return;
    }
    refreshFromApi();
    pollingRef.current = setInterval(refreshFromApi, 5000);
    return () => {
      if (pollingRef.current) {
        clearInterval(pollingRef.current);
        pollingRef.current = null;
      }
    };
  }, [connected]);

  const value = useMemo(
    () => ({
      connected,
      wsState,
      connectionAttempts,
      lastConnectedAt,
      lastDisconnectedAt,
      lastErrorMessage,
      queueStats,
      activeCrawls,
      recentlyCompleted,
      throughput,
      error,
      refreshFromApi,
    }),
    [
      connected,
      wsState,
      connectionAttempts,
      lastConnectedAt,
      lastDisconnectedAt,
      lastErrorMessage,
      queueStats,
      activeCrawls,
      recentlyCompleted,
      throughput,
      error,
    ]
  );

  return <ObservatoryContext.Provider value={value}>{children}</ObservatoryContext.Provider>;
}

export function useObservatory() {
  const context = useContext(ObservatoryContext);
  if (!context) {
    throw new Error('useObservatory must be used within ObservatoryWebSocketProvider');
  }
  return context;
}
