// Detalle de una sesión/entrenamiento
import { useEffect, useState, useMemo } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { appFetch, fetchConfig, getServiceToken } from "../backend/appFetch";

const workoutService = {
  getWorkoutDetails: (sessionId, onSuccess, onErrors) => {
    const token = getServiceToken();
    if (!token) {
      onErrors && onErrors({ globalError: "No authentication token found" });
      return;
    }

    appFetch(`/workouts/${sessionId}`, fetchConfig("GET", null, token), onSuccess, onErrors);
  },
};

export default function WorkoutDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [sets, setSets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let alive = true;
    workoutService.getWorkoutDetails(
      id,
      (data) => {
        if (alive) {
          setSets(data || []);
          setLoading(false);
        }
      },
      (err) => {
        if (alive) {
          setError(err.globalError || "Failed to load sets");
          setLoading(false);
        }
      }
    );
    return () => { alive = false; };
  }, [id]);

  // Agrupar sets por ejercicio para mostrar imagen y sets juntos
  const setsByExercise = useMemo(() => {
    const map = {};
    for (const s of sets) {
      const key = s.exerciseId;
      if (!map[key]) map[key] = {
        exerciseName: s.exerciseName,
        imageUrl: s.imageUrl,
        exerciseType: s.exerciseType || 'STRENGTH',
        sets: []
      };
      map[key].sets.push(s);
    }
    return Object.entries(map);
  }, [sets]);

  if (loading) return <div className="container"><h2>Loading workout details...</h2></div>;

  return (
    <div className="container" style={{ padding: "1.5rem", fontFamily: "Arial, sans-serif", maxHeight: "calc(100vh - 100px)", overflowY: "auto" }}>
      <h2 style={{ marginBottom: "1rem", color: "#333" }}>Workout Details</h2>

      {error && (
        <div style={{
          backgroundColor: "#ffe0e0",
          color: "#900",
          padding: "0.75rem 1rem",
          borderRadius: "8px",
          marginBottom: "1rem",
          border: "1px solid #f5c2c2"
        }}>
          ⚠️ {error}
        </div>
      )}

      {sets.length === 0 ? (
        <p style={{ color: "#666", fontStyle: "italic" }}>No sets found for this workout.</p>
      ) : (
        <div className="stack" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem', paddingBottom: '2rem' }}>
          {setsByExercise.map(([exerciseId, info]) => {
            const isCardio = info.exerciseType === 'CARDIO';
            return (
              <div key={exerciseId} className="card-small" style={{ padding: '1rem', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '0.75rem' }}>
                  {(() => {

                    return (
                      <img
                        src={info.imageUrl || `${process.env.PUBLIC_URL}/noop.png`}
                        alt={info.exerciseName}
                        style={{ width: 120, height: 120, objectFit: 'cover', borderRadius: 8, border: '1px solid #ddd', flexShrink: 0, background: '#fff' }}
                        onError={(e) => { e.currentTarget.style.display = 'none'; }}
                      />
                    );
                  })()}
                  <div>
                    <h3 style={{ margin: 0 }}>{info.exerciseName}</h3>
                    {isCardio ? (
                      <span style={{ fontSize: 12, color: '#28a745', fontWeight: 'bold' }}>🏃 CARDIO</span>
                    ) : (
                      <span style={{ fontSize: 12, color: '#007bff', fontWeight: 'bold' }}>💪 STRENGTH</span>
                    )}
                  </div>
                </div>
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                  <thead>
                    <tr style={{ backgroundColor: '#fafafa' }}>
                      <th style={{ padding: '0.5rem', borderBottom: '1px solid #eee', textAlign: 'left' }}>
                        {isCardio ? 'Entry #' : 'Set #'}
                      </th>
                      <th style={{ padding: '0.5rem', borderBottom: '1px solid #eee', textAlign: 'left' }}>
                        {isCardio ? 'Distance (km)' : 'Reps'}
                      </th>
                      <th style={{ padding: '0.5rem', borderBottom: '1px solid #eee', textAlign: 'left' }}>
                        {isCardio ? 'Duration (min)' : 'Weight (kg)'}
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    {info.sets.map((s, i) => (
                      <tr key={s.id || i} style={{ backgroundColor: i % 2 === 0 ? '#fff' : '#f7f7f7' }}>
                        <td style={{ padding: '0.5rem', borderBottom: '1px solid #f0f0f0' }}>{s.numeroSerie}</td>
                        <td style={{ padding: '0.5rem', borderBottom: '1px solid #f0f0f0' }}>
                          {isCardio ? (s.distancia ?? '-') : s.repeticiones}
                        </td>
                        <td style={{ padding: '0.5rem', borderBottom: '1px solid #f0f0f0' }}>
                          {isCardio ? (s.duracion ?? '-') : (s.peso ?? '-')}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )
          })}
        </div>
      )}

      <button
        className="btn"
        onClick={() => navigate(-1)}
        style={{
          marginTop: "1rem",
          padding: "0.6rem 1.2rem",
          borderRadius: "6px",
          border: "none",
          backgroundColor: "#007bff",
          color: "#fff",
          cursor: "pointer",
          transition: "background-color 0.2s"
        }}
        onMouseOver={e => e.currentTarget.style.backgroundColor = "#0056b3"}
        onMouseOut={e => e.currentTarget.style.backgroundColor = "#007bff"}
      >
        Back
      </button>
    </div>
  );
}