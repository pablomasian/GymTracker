// Página de rutinas guardadas por el usuario
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { appFetch, fetchConfig } from '../backend/appFetch';

export default function SavedRoutinesPage() {
  const navigate = useNavigate();
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let alive = true;
    setLoading(true);
    appFetch('/saved-routines', fetchConfig('GET'),
      (res) => { if (alive) setList(Array.isArray(res) ? res : []); },
      () => { if (alive) setList([]); }
    );
    const t = setTimeout(() => { if (alive) setLoading(false); }, 200);
    return () => { alive = false; clearTimeout(t); };
  }, []);

  if (loading) return <div className='container'><h2>Loading…</h2></div>;

  return (
    <div className='container' style={{ padding: 16 }}>
      <h2>Saved routines</h2>
      {list.length === 0 && (
        <div className="empty-state-modern">
          <div className="empty-icon">⭐</div>
          <h3 className="empty-title">No saved routines yet</h3>
          <p className="empty-subtitle">
            Browse routines and save your favorites to access them quickly here!
          </p>
        </div>
      )}
      {list.length > 0 && (
        <div className='grid' style={{ gap: 12 }}>
          {list.map(r => (
            <div key={r.id} className='card-small' style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <strong>{r.name}</strong>
                <div style={{ color: '#555' }}>Coach: {r.coachName || (r.coachId ? `#${r.coachId}` : 'Unknown')}</div>
              </div>
              <button className='btn' onClick={() => navigate(`/routines/${r.routineId}`)}>Open</button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
