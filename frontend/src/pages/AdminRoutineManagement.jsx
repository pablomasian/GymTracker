import { useEffect, useState } from "react";
import { getAllRoutinesForAdmin, blockRoutine, unblockRoutine } from "../backend/routineService";
import { useNavigate } from "react-router-dom";

export default function AdminRoutineManagement() {
  const [routines, setRoutines] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [filter, setFilter] = useState("all"); // all, blocked, active
  const navigate = useNavigate();

  const fetchAllRoutines = () => {
    setLoading(true);
    getAllRoutinesForAdmin(
      (data) => {
        setRoutines(Array.isArray(data) ? data : []);
        setLoading(false);
      },
      (err) => {
        setError(err?.globalError || "Failed to load routines");
        setLoading(false);
      }
    );
  };

  const handleBlock = (id) => {
    if (!window.confirm("Are you sure you want to block this routine? It will be hidden from all users.")) {
      return;
    }
    blockRoutine(
      id,
      () => setRoutines(routines.map((r) => r.id === id ? { ...r, blocked: true } : r)),
      (err) => alert(err?.globalError || "Error blocking routine")
    );
  };

  const handleUnblock = (id) => {
    unblockRoutine(
      id,
      () => setRoutines(routines.map((r) => r.id === id ? { ...r, blocked: false } : r)),
      (err) => alert(err?.globalError || "Error unblocking routine")
    );
  };

  useEffect(() => {
    fetchAllRoutines();
  }, []);

  const filteredRoutines = routines.filter(r => {
    if (filter === "blocked") return r.blocked;
    if (filter === "active") return !r.blocked;
    return true; // all
  });

  if (loading) return <div className="container" style={{ padding: '2rem' }}>Loading all routines...</div>;
  if (error) return <div className="container" style={{ padding: '2rem', color: 'red' }}>{error}</div>;

  return (
    <div className="container" style={{ padding: '1.25rem', maxWidth: 1200, margin: '0 auto' }}>
      <h1 style={{ fontSize: '1.75rem', fontWeight: 'bold', marginBottom: 16 }}>Routine Management</h1>
      
      <div style={{ marginBottom: 16, display: 'flex', gap: 8 }}>
        <button className={`btn ${filter === 'all' ? 'btn-primary' : ''}`} onClick={() => setFilter('all')}>
          All ({routines.length})
        </button>
        <button className={`btn ${filter === 'active' ? 'btn-primary' : ''}`} onClick={() => setFilter('active')}>
          Active ({routines.filter(r => !r.blocked).length})
        </button>
        <button className={`btn ${filter === 'blocked' ? 'btn-primary' : ''}`} onClick={() => setFilter('blocked')}>
          Blocked ({routines.filter(r => r.blocked).length})
        </button>
      </div>

      {filteredRoutines.length === 0 ? (
        <p style={{ color: '#555' }}>No routines found for this filter.</p>
      ) : (
        <div className="grid" style={{ gap: 12, gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))' }}>
          {filteredRoutines.map((r) => (
            <div 
              key={r.id} 
              className="card-small" 
              style={{ 
                display: 'flex', flexDirection: 'column', gap: 6, padding: 12,
                opacity: r.blocked ? 0.6 : 1,
                border: r.blocked ? '2px solid #dc3545' : undefined
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <strong style={{cursor: 'pointer'}} onClick={() => navigate(`/routines/${r.id}`)}>{r.name}</strong>
                {r.blocked && (
                  <span style={{ backgroundColor: '#dc3545', color: 'white', padding: '2px 8px', borderRadius: 4, fontSize: 12, fontWeight: 'bold' }}>
                    BLOCKED
                  </span>
                )}
              </div>
              
              <div style={{ color: '#555', fontSize: 13 }}>By: {r.coachNombreUsuario}</div>
              <div style={{ fontSize: 12, color: '#666' }}>Exercises: {r.exerciseCount}</div>
              <div style={{ fontSize: 12, color: '#666' }}>Status: {r.estado || 'N/A'}</div>


              <div style={{ marginTop: 8 }}>
                {!r.blocked ? (
                  <button className="btn btn-red" onClick={() => handleBlock(r.id)} style={{ width: '100%' }}>
                    Block Routine
                  </button>
                ) : (
                  <button className="btn btn-secondary" onClick={() => handleUnblock(r.id)} style={{ width: '100%' }}>
                    Unblock Routine
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}