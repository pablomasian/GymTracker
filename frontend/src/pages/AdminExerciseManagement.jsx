// Página de gestión de todos los ejercicios (admin)
// src/pages/AdminExerciseManagement.jsx
import { useEffect, useState } from "react";
import { appFetch, fetchConfig } from "../backend/appFetch";
import { exerciseService } from "../backend/exerciseService";

export default function AdminExerciseManagement() {
  const [exercises, setExercises] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [filter, setFilter] = useState("all"); // all, blocked, active

  const fetchAllExercises = () => {
    setLoading(true);
    // Llamar al endpoint que lista TODOS los ejercicios (incluyendo bloqueados para admin)
    appFetch("/exercises/all", fetchConfig("GET"), 
      (data) => {
        setExercises(Array.isArray(data) ? data : []);
        setLoading(false);
      },
      (err) => {
        // Si no existe /all, usar el endpoint normal
        appFetch("/exercises", fetchConfig("GET"), 
          (data) => {
            setExercises(Array.isArray(data) ? data : []);
            setLoading(false);
          },
          (err2) => {
            setError(err2 || "Failed to load exercises");
            setLoading(false);
          }
        );
      }
    );
  };

  const handleBlock = (id) => {
    if (!window.confirm("Are you sure you want to block this exercise? It will affect all routines using it.")) {
      return;
    }
    
    exerciseService.block(
      id,
      () => {
        // Marcar el ejercicio como bloqueado
        setExercises(exercises.map((e) => 
          e.id === id ? { ...e, blocked: true } : e
        ));
      },
      (err) => alert(err || "Error blocking exercise")
    );
  };

  const handleUnblock = (id) => {
    // Endpoint para desbloquear (si quieres implementarlo)
    alert("Unblock feature not yet implemented");
  };

  useEffect(() => {
    fetchAllExercises();
  }, []);

  const filteredExercises = exercises.filter(ex => {
    if (filter === "blocked") return ex.blocked;
    if (filter === "active") return !ex.blocked;
    return true; // all
  });

  if (loading) return <div className="container" style={{ padding: '2rem' }}>Loading exercises...</div>;
  if (error) return <div className="container" style={{ padding: '2rem', color: 'red' }}>{error}</div>;

  return (
    <div className="container" style={{ padding: '1.25rem', maxWidth: 1200, margin: '0 auto' }}>
      <h1 style={{ fontSize: '1.75rem', fontWeight: 'bold', marginBottom: 16 }}>Exercise Management</h1>
      
      <div style={{ marginBottom: 16, display: 'flex', gap: 8 }}>
        <button 
          className={`btn ${filter === 'all' ? 'btn-primary' : ''}`}
          onClick={() => setFilter('all')}
        >
          All ({exercises.length})
        </button>
        <button 
          className={`btn ${filter === 'active' ? 'btn-primary' : ''}`}
          onClick={() => setFilter('active')}
        >
          Active ({exercises.filter(e => !e.blocked).length})
        </button>
        <button 
          className={`btn ${filter === 'blocked' ? 'btn-primary' : ''}`}
          onClick={() => setFilter('blocked')}
        >
          Blocked ({exercises.filter(e => e.blocked).length})
        </button>
      </div>

      {filteredExercises.length === 0 ? (
        <p style={{ color: '#555' }}>No exercises found for this filter.</p>
      ) : (
        <div className="grid" style={{ gap: 12, gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))' }}>
          {filteredExercises.map((ex) => (
            <div 
              key={ex.id} 
              className="card-small" 
              style={{ 
                display: 'flex', 
                flexDirection: 'column', 
                gap: 6, 
                padding: 12,
                opacity: ex.blocked ? 0.6 : 1,
                border: ex.blocked ? '2px solid #dc3545' : undefined
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <strong>{ex.name}</strong>
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

              <div style={{ marginTop: 8 }}>
                {!ex.blocked ? (
                  <button 
                    className="btn btn-red" 
                    onClick={() => handleBlock(ex.id)}
                    style={{ width: '100%' }}
                  >
                    Block Exercise
                  </button>
                ) : (
                  <button 
                    className="btn btn-secondary" 
                    onClick={() => handleUnblock(ex.id)}
                    style={{ width: '100%' }}
                    disabled
                  >
                    Unblock (Coming Soon)
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
