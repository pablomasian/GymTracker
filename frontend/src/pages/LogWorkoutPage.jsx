import { useEffect, useState } from "react";
import { useParams, useNavigate, useLocation } from "react-router-dom";
import { appFetch, fetchConfig } from "../backend/appFetch";
import workoutService from "../backend/workoutService";

export default function LogWorkoutPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const sessionId = location.state?.sessionId;

  const [routine, setRoutine] = useState(null);
  const [exercises, setExercises] = useState([]);
  const [logs, setLogs] = useState({});
  const [duration, setDuration] = useState("60");

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    let alive = true;
    async function loadData() {
      try {
        appFetch(`/routines/${id}`, fetchConfig('GET'), (dto) => {
          if (alive) setRoutine(dto);
        });
        appFetch(`/routines/${id}/exercises`, fetchConfig('GET'), (list) => {
          if (alive) {
            const exercisesList = Array.isArray(list) ? list : [];
            setExercises(exercisesList);
            const initialLogs = {};
            for (const ex of exercisesList) {
              const isCardio = ex.exerciseType === 'CARDIO';
              const numSets = ex.sets ?? 1;

              if (isCardio) {
                // Para cardio: distance (km) y duration (min)
                const distance = ex.targetDistance ?? '';
                const duration = ex.targetDuration ?? '';
                const sets = Array.from({ length: numSets }, () => ({
                  distance,
                  duration,
                  isCardio: true
                }));
                initialLogs[ex.exerciseId] = sets;
              } else {
                // Para fuerza: reps y weight
                const reps = ex.repetitions ?? '';
                const weight = ex.weight ?? '';
                const sets = Array.from({ length: numSets }, () => ({
                  weight,
                  reps,
                  isCardio: false
                }));
                initialLogs[ex.exerciseId] = sets;
              }
            }
            setLogs(initialLogs);
          }
        });
      } catch (e) {
        if (alive) setError('Failed to load routine data.');
      } finally {
        if (alive) setLoading(false);
      }
    }
    loadData();
    return () => { alive = false; };
  }, [id]);

  const handleLogChange = (exerciseId, setIndex, field, value) => {
    const updatedLogs = { ...logs };
    updatedLogs[exerciseId][setIndex][field] = value;
    setLogs(updatedLogs);
  };

  const addSet = (exerciseId) => {
    const updatedLogs = { ...logs };
    const ex = exercises.find(e => e.exerciseId === exerciseId);
    const isCardio = ex?.exerciseType === 'CARDIO';

    if (isCardio) {
      updatedLogs[exerciseId].push({ distance: '', duration: '', isCardio: true });
    } else {
      updatedLogs[exerciseId].push({ reps: '', weight: '', isCardio: false });
    }
    setLogs(updatedLogs);
  };

  const removeSet = (exerciseId, setIndex) => {
    const updatedLogs = { ...logs };
    if (updatedLogs[exerciseId].length > 1) {
      updatedLogs[exerciseId].splice(setIndex, 1);
      setLogs(updatedLogs);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);

    // Crear fecha en formato ISO local (sin conversión a UTC)
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');
    const localDateTime = `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;

    const payload = {
      routineId: Number(id),
      date: localDateTime,
      durationMinutes: Number(duration) || 60,
      sets: [],
    };

    for (const exerciseId in logs) {
      logs[exerciseId].forEach((set, index) => {
        if (set.isCardio) {
          // Para cardio: enviar distance y duration
          if (set.distance || set.duration) {
            payload.sets.push({
              exerciseId: Number(exerciseId),
              setNumber: index + 1,
              reps: 0,
              weight: null,
              distance: set.distance ? Number(set.distance) : null,
              duration: set.duration ? Number(set.duration) : null,
            });
          }
        } else {
          // Para fuerza: enviar reps y weight
          if (set.reps) {
            payload.sets.push({
              exerciseId: Number(exerciseId),
              setNumber: index + 1,
              reps: Number(set.reps) || 0,
              weight: set.weight ? Number(set.weight) : null,
            });
          }
        }
      });
    }

    if (payload.sets.length === 0) {
      setError("Please log at least one set.");
      setSubmitting(false);
      return;
    }

    if (!sessionId) {
      // Si no hay sessionId, asumimos que es un log a posteriori
      workoutService.logWorkout(
        payload,
        () => {
          navigate('/my-workouts');
        },
        (errorPayload) => {
          setError(errorPayload.globalError || 'An error occurred while logging the workout.');
          setSubmitting(false);
        }
      );
    } else {
      // Si hay sessionId, es porque se finaliza una sesión en tiempo real
      workoutService.finishWorkout(
        sessionId,
        payload,
        () => {
          navigate('/my-workouts');
        },
        (errorPayload) => {
          setError(errorPayload.globalError || 'An error occurred while finishing the workout.');
          setSubmitting(false);
        }
      );
    }
  };

  if (loading) return <div className="container"><h2>Loading workout...</h2></div>;

  return (
    <div className="container" style={{ padding: '1.25rem' }}>
      <h2>Log Workout: {routine?.name}</h2>
      <form onSubmit={handleSubmit}>
        {error && <div className="banner">⚠️ {error}</div>}

        <div className="card-small" style={{ marginBottom: '1.5rem' }}>
          <label htmlFor="duration" className="label">Total Duration (minutes)</label>
          <input
            id="duration"
            type="number"
            className="input"
            value={duration}
            onChange={(e) => setDuration(e.target.value)}
            min="1"
            required
          />
        </div>

        <div className="stack" style={{ gap: '1.5rem' }}>
          {exercises.map(ex => (
            <div
              key={ex.exerciseId}
              className="card-small"
              style={{ opacity: ex.blocked ? 0.7 : 1, border: ex.blocked ? '2px solid #dc3545' : undefined }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '0.75rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                  {(() => {

                    return (
                      <img
                        src={ex.imageUrl || `${process.env.PUBLIC_URL}/noop.png`}
                        alt={ex.name}
                        style={{ width: 92, height: 92, objectFit: 'cover', borderRadius: 8, border: '1px solid #eee', background: '#fff' }}
                        onError={(e) => {
                          // Blank out image completely per user request
                          e.currentTarget.style.display = 'none';
                        }}
                      />
                    );
                  })()}
                  <strong>{ex.name}</strong>
                </div>
                {ex.blocked && (
                  <span style={{ backgroundColor: '#dc3545', color: 'white', padding: '2px 8px', borderRadius: 4, fontSize: 11, fontWeight: 'bold' }}>
                    BLOCKED
                  </span>
                )}
              </div>
              <div style={{ color: '#555' }}>{ex.muscles}</div>
              {ex.blocked && (
                <div style={{ fontSize: 12, color: '#dc3545', marginTop: 6, fontStyle: 'italic' }}>
                  ⚠️ This exercise was blocked by admin. You can still log this workout.
                </div>
              )}
              <hr style={{ margin: '1rem 0' }} />

              <div className="stack" style={{ gap: '0.75rem' }}>
                {logs[ex.exerciseId]?.map((set, i) => (
                  <div key={i} style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                    <span style={{ fontWeight: 'bold' }}>{ex.exerciseType === 'CARDIO' ? 'Entry' : 'Set'} {i + 1}</span>
                    {ex.exerciseType === 'CARDIO' ? (
                      <>
                        <input type="number" className="input" placeholder="Distance (km)" value={set.distance ?? ''}
                          onChange={(e) => handleLogChange(ex.exerciseId, i, 'distance', e.target.value)} min="0" step="0.1" />
                        <input type="number" className="input" placeholder="Duration (min)" value={set.duration ?? ''}
                          onChange={(e) => handleLogChange(ex.exerciseId, i, 'duration', e.target.value)} min="0" />
                      </>
                    ) : (
                      <>
                        <input type="number" className="input" placeholder="Reps" value={set.reps ?? ''}
                          onChange={(e) => handleLogChange(ex.exerciseId, i, 'reps', e.target.value)} min="0" />
                        <input type="number" className="input" placeholder="Weight (kg)" value={set.weight ?? ''}
                          onChange={(e) => handleLogChange(ex.exerciseId, i, 'weight', e.target.value)} min="0" step="0.25" />
                      </>
                    )}
                    <button type="button" className="btn btn-black" onClick={() => removeSet(ex.exerciseId, i)}
                      disabled={logs[ex.exerciseId].length <= 1}>
                      Delete
                    </button>
                  </div>
                ))}
              </div>

              <button type="button" className="btn btn-green" onClick={() => addSet(ex.exerciseId)}
                style={{ marginTop: '1rem', width: 'auto', padding: '.4rem .8rem' }}>
                + Add Set
              </button>
            </div>
          ))}
        </div>

        <div className="actions" style={{ marginTop: '2rem', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
          <button type="button" className="btn btn-black" onClick={() => navigate(-1)} disabled={submitting}>
            Cancel
          </button>
          <button type="submit" className="btn btn-green" disabled={submitting}>
            {submitting ? 'Saving...' : 'Finish Workout'}
          </button>
        </div>
      </form>
    </div>
  );
}