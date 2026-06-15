// Panel del entrenador con resumen de actividad de usuarios
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import workoutService from "../backend/workoutService";

export default function CoachDashboardPage() {
  const navigate = useNavigate();
  const [workouts, setWorkouts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let alive = true;
    setLoading(true);

    workoutService.getCoachDashboard(
      (data) => {
        if (alive) {
          setWorkouts(Array.isArray(data) ? data : []);
        }
      },
      (err) => {
        if (alive) {
          setError(err.globalError || "Failed to load dashboard data.");
        }
      }
    ).finally(() => {
      if (alive) {
        setLoading(false);
      }
    });

    return () => { alive = false; };
  }, []);

  if (loading) return <div className="container" style={{ padding: '1.25rem' }}><h2>Loading Dashboard...</h2></div>;

  return (
    <div className="container" style={{ padding: "1.25rem" }}>
      <h2>Completed Workouts by Users</h2>
      <div className="card-sub">Users who have completed one of your routines.</div>
      
      {error && <div className="banner" style={{marginTop: '1rem'}}>⚠️ {error}</div>}

      <div className="stack" style={{ gap: "1.5rem", marginTop: '1rem' }}>
        {workouts.length === 0 && !loading && (
          <div className="card-small">No workouts completed by users yet.</div>
        )}
        
        {workouts.map((session) => (
          <div key={session.id} className="card-small">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                    <div><strong>User:</strong> {session.userName || session.userId}</div>
                    <div style={{ color: "#555" }}><strong>Routine:</strong> {session.routineName || session.routineId}</div>
                    <div style={{ color: "#555" }}><strong>Date:</strong> {new Date(session.fecha).toLocaleString()}</div>
                </div>
                <button
                    type="button"
                    className="btn"
                    style={{ width: 'auto', padding: '0.5rem 1rem' }}
                    onClick={() => navigate(`/workouts/${session.id}`)}
                    >
                    View Details
                </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}