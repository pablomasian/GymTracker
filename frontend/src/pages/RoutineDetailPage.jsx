// Página de detalle de rutina: muestra ejercicios y acciones (guardar, publicar, log)
import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { appFetch, fetchConfig } from '../backend/appFetch';
import { publishRoutine, hideRoutine } from '../backend/routineService';

export default function RoutineDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth() || {};
  const [routine, setRoutine] = useState(null);
  const [exercises, setExercises] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [saved, setSaved] = useState(false);

  useEffect(() => {
    let alive = true;
    setLoading(true); setError(null);
    appFetch(`/routines/${id}`, fetchConfig('GET'), (dto) => {
      if (!alive) return;
      setRoutine(dto);
    }, (errPayload) => {
      if (!alive) return;
      setError(errPayload?.globalError || 'Unable to load routine');
    });
    appFetch(`/routines/${id}/exercises`, fetchConfig('GET'), (list) => {
      if (!alive) return;
      setExercises(Array.isArray(list) ? list : []);
    }, () => { if (alive) setExercises([]); });
    const timer = setTimeout(() => { if (alive) setLoading(false); }, 400);
    return () => { alive = false; clearTimeout(timer); };
  }, [id, user]);

  function handleLogWorkout() {
    appFetch(
      `/workouts/start/${id}`,
      fetchConfig('POST'),
      (session) => {
        navigate(`/log-workout/${id}`, { state: { sessionId: session.id } });
      },
      (error) => {
        console.error("Error starting workout:", error);
        alert("Could not start workout session. Please try again.");
      }
    );
  }

  const canEdit = (user && user.role === "COACH" && user.id === routine?.coachId) || user?.role === "ADMIN";

  if (loading) return <div className='container' style={{ padding: '2rem' }}><h2>Loading routine details...</h2></div>;
  if (error) return <div className='container' style={{ padding: '2rem' }}><h2>Error</h2><div className="banner">{error}</div></div>;
  if (!routine) return null;

  return (
    <div
      className="container"
      style={{
        padding: '1.25rem',
        maxWidth: 900,
        margin: '0 auto',
      }}
    >
      <div
        className="card"
        style={{
          marginTop: 16,
          width: '100%',
          position: 'relative',
        }}
      >
        {routine.coachId && (
          <button
            className="btn btn-sm btn-secondary"
            style={{
              position: 'absolute',
              top: 12,
              right: 12,
              padding: '4px 8px',
              fontSize: 12,
              width: 'auto',
              minWidth: 0,
              display: 'inline-block',
            }}
            onClick={() => navigate(`/coach/${routine.coachId}`)}
          >
            View Coach
          </button>
        )}

        <div className="card-header" style={{ textAlign: 'center' }}>
          <h2 style={{ margin: 0 }}>{routine.name}</h2>
          <div style={{ color: '#555' }}>
            Coach: {routine.coachNombreUsuario || (typeof routine.coachId !== 'undefined' ? `#${routine.coachId}` : 'Unknown')}
          </div>
          <div style={{ color: '#777', fontSize: 14, marginTop: 4 }}>
            Exercises: {routine.exerciseCount ?? exercises.length}
          </div>
        </div>

        <div className="card-body">
          {exercises.length === 0 && (
            <div>No exercises have been added to this routine yet.</div>
          )}
          {exercises.length > 0 && (
            <div className="grid" style={{ gap: 12 }}>
              {exercises.map((ex, i) => (
                <div
                  key={ex.id || i}
                  className="card-small"
                  style={{
                    display: 'flex',
                    flexDirection: 'column',
                    gap: 6,
                    opacity: ex.blocked ? 0.5 : 1,
                    border: ex.blocked ? '2px solid #dc3545' : undefined,
                    position: 'relative'
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: '0.75rem' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', flex: 1 }}>
                      {(() => {

                        return (
                          <img
                            src={ex.imageUrl || `${process.env.PUBLIC_URL}/noop.png`}
                            alt={ex.name || 'exercise'}
                            style={{
                              width: 80,
                              height: 80,
                              objectFit: 'cover',
                              borderRadius: 8,
                              border: '1px solid #ddd',
                              flexShrink: 0,
                              background: '#fff'
                            }}
                            onError={(e) => {
                              // Blank out image completely per user request
                              e.currentTarget.style.display = 'none';
                            }}
                          />
                        );
                      })()}
                      <div style={{ flex: 1 }}>
                        <strong>{i + 1}. {ex.name || ex.exerciseName}</strong>
                        <div style={{ color: '#555', fontSize: 14 }}>{ex.muscles || ex.muscleGroup}</div>
                      </div>
                    </div>
                    {ex.blocked && (
                      <span style={{
                        backgroundColor: '#dc3545',
                        color: 'white',
                        padding: '2px 8px',
                        borderRadius: 4,
                        fontSize: 11,
                        fontWeight: 'bold'
                      }}>BLOCKED</span>
                    )}
                  </div>
                  {ex.exerciseType === 'CARDIO' ? (
                    <div style={{ fontSize: 14 }}>
                      Sets: {ex.sets}
                      {(ex.targetDistance !== undefined && ex.targetDistance !== null) ? ` · ${ex.targetDistance} km` : ''}
                      {(ex.targetDuration !== undefined && ex.targetDuration !== null) ? ` · ${ex.targetDuration} min` : ''}
                    </div>
                  ) : (
                    <div style={{ fontSize: 14 }}>Sets x Reps: {ex.sets} x {ex.repetitions}{(ex.weight !== undefined && ex.weight !== null) ? ` · ${ex.weight} kg` : ''}</div>
                  )}
                  {ex.equipment && <div style={{ fontSize: 12, color: '#666' }}>Equipment: {ex.equipment}</div>}
                  {ex.description && <div style={{ fontSize: 12, color: '#666', marginTop: 4, fontStyle: 'italic' }}>{ex.description}</div>}
                  {ex.blocked && (
                    <div style={{
                      fontSize: 12,
                      color: '#dc3545',
                      marginTop: 4,
                      fontWeight: 'bold',
                      fontStyle: 'italic'
                    }}>
                      ⚠️ This exercise has been blocked by an administrator
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>

        <div style={{ marginTop: 12, padding: '0 16px 16px' }}>
          <div className="grid" style={{ gap: 10, gridTemplateColumns: '1fr 1fr' }}>
            <button className="btn" onClick={() => navigate('/routines')}>← Back to routines</button>
            <button className="btn" onClick={handleLogWorkout}>Log workout</button>
            {(!saved) && (
              <button className="btn btn-primary" onClick={() => { appFetch(`/saved-routines/${id}`, fetchConfig('POST'), () => setSaved(true)); }}>Save routine</button>
            )}
            {(saved) && (
              <button className="btn" onClick={() => { appFetch(`/saved-routines/${id}`, fetchConfig('DELETE'), () => setSaved(false)); }}>Unsave</button>
            )}
            {canEdit && <button className="btn" onClick={() => navigate(`/routines/edit/${id}`)}>Edit</button>}
            {canEdit && (
              routine?.visible ? (
                <button className="btn" onClick={() => {
                  hideRoutine(id, (dto) => setRoutine(prev => ({ ...prev, visible: dto.visible })), () => { });
                }}>Hide</button>
              ) : (
                <button className="btn btn-primary" onClick={() => {
                  publishRoutine(id, (dto) => setRoutine(prev => ({ ...prev, visible: dto.visible })), () => { });
                }}>Publish</button>
              )
            )}
          </div>
        </div>
      </div>
    </div>
  );
}