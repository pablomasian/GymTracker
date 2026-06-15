import { useEffect, useState } from "react";
import { getPendingRoutines, approveRoutine, dismissRoutine } from "../backend/routineService";

export default function AdminRoutineQueue() {
  const [routines, setRoutines] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchPending = () => {
    getPendingRoutines(
      (data) => {
        setRoutines(Array.isArray(data) ? data : []);
        setLoading(false);
      },
      (err) => {
        setError(err?.globalError || "Failed to load pending routines");
        setLoading(false);
      }
    );
  };

  const handleApprove = (id) => {
    approveRoutine(
      id,
      () => setRoutines(routines.filter((r) => r.id !== id)),
      (err) => alert(err?.globalError || "Error approving routine")
    );
  };

  const handleDismiss = (id) => {
    if (!window.confirm("Are you sure you want to dismiss this routine? It will be permanently deleted.")) {
        return;
    }
    dismissRoutine(
      id,
      () => setRoutines(routines.filter((r) => r.id !== id)),
      (err) => alert(err?.globalError || "Error dismissing routine")
    );
  };

  useEffect(() => {
    fetchPending();
  }, []);

  if (loading) return <div className="container" style={{ padding: '2rem' }}>Loading pending routines...</div>;
  if (error) return <div className="container" style={{ padding: '2rem', color: 'red' }}>{error}</div>;

  return (
    <div className="container" style={{ padding: '1.25rem', maxWidth: 900, margin: '0 auto' }}>
      <h1 style={{ fontSize: '1.75rem', fontWeight: 'bold', marginBottom: 16 }}>Pending Routines</h1>

      {routines.length === 0 ? (
        <p style={{ color: '#555' }}>No pending routines to review 🎉</p>
      ) : (
        <div className="grid" style={{ gap: 12 }}>
          {routines.map((routine) => (
            <div key={routine.id} className="card-small" style={{ display: 'flex', flexDirection: 'column', gap: 6, padding: 12 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <strong>{routine.name}</strong>
                <span style={{color: '#555', fontSize: 13 }}>By: {routine.coachNombreUsuario || 'Unknown'}</span>
              </div>
              <div style={{ color: '#666', fontSize: 13 }}>Exercises: {routine.exerciseCount || 0}</div>

              <div className="grid" style={{ gap: 6, gridTemplateColumns: '1fr 1fr', marginTop: 12 }}>
                <button className="btn btn-red" onClick={() => handleDismiss(routine.id)}>
                  Dismiss
                </button>
                <button className="btn btn-green" onClick={() => handleApprove(routine.id)}>
                  Approve
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}