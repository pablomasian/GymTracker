// Página de gestión de ejercicios pendientes (admin)
// src/pages/AdminExerciseQueue.jsx
import { useEffect, useState } from "react";
import { exerciseService } from "../backend/exerciseService";

export default function AdminExerciseQueue() {
  const [exercises, setExercises] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchPending = () => {
    exerciseService.getPending(
      (data) => {
        setExercises(Array.isArray(data) ? data : []);
        setLoading(false);
      },
      (err) => {
        setError(err || "Failed to load pending exercises");
        setLoading(false);
      }
    );
  };

  const handleAccept = (id) => {
    exerciseService.accept(
      id,
      () => setExercises(exercises.filter((e) => e.id !== id)),
      (err) => alert(err || "Error accepting exercise")
    );
  };

  const handleDismiss = (id) => {
    exerciseService.dismiss(
      id,
      () => setExercises(exercises.filter((e) => e.id !== id)),
      (err) => alert(err || "Error dismissing exercise")
    );
  };

  const handleBlock = (id) => {
    exerciseService.block(
      id,
      () => {
        // Marcar el ejercicio como bloqueado en lugar de eliminarlo
        setExercises(exercises.map((e) => 
          e.id === id ? { ...e, blocked: true } : e
        ));
      },
      (err) => alert(err || "Error blocking exercise")
    );
  };

  useEffect(() => {
    fetchPending();
  }, []);

  if (loading) return <div className="container" style={{ padding: '2rem' }}>Loading pending exercises...</div>;
  if (error) return <div className="container" style={{ padding: '2rem', color: 'red' }}>{error}</div>;

  return (
    <div className="container" style={{ padding: '1.25rem', maxWidth: 900, margin: '0 auto' }}>
      <h1 style={{ fontSize: '1.75rem', fontWeight: 'bold', marginBottom: 16 }}>Pending Exercises</h1>

      {exercises.length === 0 ? (
        <p style={{ color: '#555' }}>No pending exercises 🎉</p>
      ) : (
        <div className="grid" style={{ gap: 12 }}>
          {exercises.map((ex, i) => (
            <div key={i} className="card-small" style={{ 
              display: 'flex', 
              flexDirection: 'column', 
              gap: 6, 
              padding: 12,
              opacity: ex.blocked ? 0.6 : 1,
              border: ex.blocked ? '2px solid #dc3545' : undefined
            }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <strong>{i + 1}. {ex.name}</strong>
                {ex.blocked && (
                  <span style={{ 
                    backgroundColor: '#dc3545', 
                    color: 'white', 
                    padding: '2px 8px', 
                    borderRadius: 4, 
                    fontSize: 12,
                    fontWeight: 'bold'
                  }}>
                    BLOCKED
                  </span>
                )}
              </div>
              {ex.muscles && <div style={{ color: '#555', fontSize: 13 }}>Muscles: {ex.muscles}</div>}
              {ex.equipment && <div style={{ fontSize: 12, color: '#666' }}>Equipment: {ex.equipment}</div>}
              {ex.description && (
                <div style={{ fontSize: 12, color: '#666', fontStyle: 'italic', marginTop: 4 }}>
                  {ex.description}
                </div>
              )}

              <div className="grid" style={{ gap: 6, gridTemplateColumns: '1fr 1fr 1fr', marginTop: 8 }}>
                <button 
                  className="btn btn-green" 
                  onClick={() => handleAccept(ex.id)}
                  disabled={ex.blocked}
                >
                  Accept
                </button>
                <button 
                  className="btn btn-red" 
                  onClick={() => handleDismiss(ex.id)}
                  disabled={ex.blocked}
                >
                  Dismiss
                </button>
                <button 
                  className="btn btn-secondary" 
                  onClick={() => handleBlock(ex.id)}
                  disabled={ex.blocked}
                >
                  {ex.blocked ? 'Blocked' : 'Block'}
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
