import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { appFetch, fetchConfig } from "../backend/appFetch";
import { updateRoutine } from "../backend/routineService";
import { config } from "../config/constants";

const FALLBACK_EXERCISES = [
  { id: 101, name: "Squat", muscle: "Legs" },
  { id: 102, name: "Bench Press", muscle: "Chest" },
  { id: 103, name: "Deadlift", muscle: "Back" },
];

export default function RoutineEditorPage() {
  const { id } = useParams();
  const isEditMode = id != null;

  const nav = useNavigate();
  const { user } = useAuth() || {};

  const [title, setTitle] = useState("");
  const [selected, setSelected] = useState([]);
  const [busy, setBusy] = useState(false);
  const [err, setErr] = useState("");
  const [pageLoading, setPageLoading] = useState(isEditMode);
  const [successMessage, setSuccessMessage] = useState("");

  const [exercises, setExercises] = useState([]);
  const [loadingCat, setLoadingCat] = useState(true);
  const [q, setQ] = useState("");

  useEffect(() => {
    if (!isEditMode) {
      setPageLoading(false);
      return;
    }

    let alive = true;
    setPageLoading(true);

    const loadRoutineForEdit = async () => {
      try {
        appFetch(`/routines/${id}`, fetchConfig("GET"), (dto) => {
          if (alive) {
            setTitle(dto.name);
            if (user && (user.id !== dto.coachId && user.role !== 'ADMIN')) {
              nav('/routines', { replace: true });
            }
          }
        });
        appFetch(`/routines/${id}/exercises`, fetchConfig("GET"), (list) => {
          if (alive && Array.isArray(list)) {
            setSelected(list.map(ex => {
              const isCardio = ex.exerciseType === 'CARDIO';
              return {
                id: ex.exerciseId,
                name: ex.name,
                muscle: ex.muscles,
                sets: ex.sets,
                reps: isCardio ? 0 : ex.repetitions,
                weight: isCardio ? null : (ex.weight ?? ''),
                distance: isCardio ? (ex.targetDistance ?? '') : null,
                time: isCardio ? (ex.targetDuration ?? '') : null,
                exerciseType: ex.exerciseType || 'STRENGTH',
                blocked: ex.blocked || false
              };
            }));
          }
        });
      } catch (error) {
        if (alive) setErr("Could not load routine data for editing.");
      } finally {
        if (alive) setPageLoading(false);
      }
    };

    if (user) {
      loadRoutineForEdit();
    }

    return () => { alive = false; };
  }, [id, isEditMode, user, nav]);


  useEffect(() => {
    let alive = true;
    const ac = new AbortController();

    async function loadCatalog() {
      setLoadingCat(true);
      try {
        const res = await fetch(`${config.BASE_PATH}/exercises`, { signal: ac.signal });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const data = await res.json();
        if (alive) {
          const normalized = (Array.isArray(data) ? data : []).map(e => ({
            id: e.id, name: e.name, muscle: e.muscles || e.muscle || '',
            description: e.description, muscles: e.muscles,
            exerciseType: e.exerciseType || 'STRENGTH' // Añadir tipo de ejercicio
          }));
          setExercises(normalized);
        }
      } catch (e) {
        if (alive) setExercises(FALLBACK_EXERCISES);
      } finally {
        if (alive) setLoadingCat(false);
      }
    }
    loadCatalog();

    return () => { alive = false; ac.abort(); };
  }, []);

  const filtered = useMemo(() => {
    const t = q.trim().toLowerCase();
    if (!t) return exercises;
    return exercises.filter(ex =>
      ex.name.toLowerCase().includes(t) || (ex.muscle || "").toLowerCase().includes(t)
    );
  }, [q, exercises]);

  function addExercise(ex) {
    if (selected.some(s => s.id === ex.id)) return;
    const isCardio = ex.exerciseType === 'CARDIO';
    setSelected(s => [...s, {
      ...ex,
      sets: 1,
      reps: isCardio ? 0 : 10,
      weight: isCardio ? null : '',
      distance: isCardio ? '' : null,
      time: isCardio ? '' : null,
      exerciseType: ex.exerciseType || 'STRENGTH'
    }]);
  }
  function removeExercise(id) {
    setSelected(s => s.filter(x => x.id !== id));
  }

  function updateField(id, field, value) {
    setSelected(s => s.map(x => x.id === id ? { ...x, [field]: value } : x));
  }

  function move(id, dir) {
    setSelected(s => {
      const i = s.findIndex(x => x.id === id);
      if (i < 0) return s.slice();
      const j = dir === "up" ? i - 1 : i + 1;
      if (j < 0 || j >= s.length) return s.slice();
      const clone = s.slice();
      [clone[i], clone[j]] = [clone[j], clone[i]];
      return clone;
    });
  }

  const valid = title.trim().length >= 2 && selected.length > 0 && selected.every(e => e.sets > 0 && (e.exerciseType === 'CARDIO' || e.reps > 0));

  function onSubmit(e) {
    e.preventDefault();
    if (successMessage) return;

    setErr("");
    setBusy(true);

    const payload = {
      name: title.trim(),
      exercises: selected.map(e => {
        const isCardio = e.exerciseType === 'CARDIO';
        return {
          exerciseId: e.id,
          sets: Number(e.sets) || 0,
          repetitions: Number(e.reps) || 0,
          weight: !isCardio && e.weight !== '' ? Number(e.weight) : null,
          targetDistance: isCardio && e.distance !== '' ? Number(e.distance) : null,
          targetDuration: isCardio && e.time !== '' ? Number(e.time) : null
        };
      })
    };

    const onSuccess = () => {
      setSuccessMessage("Your routine has been created successfully!");
      setBusy(false);
    };

    const onError = (errPayload) => {
      setErr(errPayload?.globalError || "Server validation error.");
      setBusy(false);
    };

    if (isEditMode) {
      updateRoutine(id, payload, onSuccess, onError);
    } else {
      appFetch("/routines", fetchConfig("POST", payload), onSuccess, onError);
    }
  }

  if (pageLoading) {
    return <div className="page"><div className="card-title">Loading routine for editing...</div></div>;
  }

  if (successMessage) {
    return (
      <div className="page">
        <div className="card" style={{ width: "min(600px, 95vw)", margin: "0 auto", textAlign: 'center' }}>
          <div className="card-body">
            <div className="banner" style={{ background: '#e8f5e9', color: '#2e7d32', border: '1px solid #c8e6c9', marginBottom: '1rem' }}>
              ✅ {successMessage}
            </div>
            <button className="btn" style={{ width: 'auto', padding: '0.5rem 1rem' }} type="button" onClick={() => nav('/routines')}>
              Go to Routines
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="page">
      <div className="card" style={{ width: "min(1200px, 95vw)", margin: "0 auto" }}>
        <div className="card-header">
          <div style={{ display: "flex", alignItems: "center", gap: 12, justifyContent: "center" }}>
            <img
              src={`${process.env.PUBLIC_URL}/assets/logo.png`}
              alt="GymTracker"
              className="logo"
              style={{ height: 48, width: 48 }}
            />
            <div>
              <div className="card-title">{isEditMode ? 'Edit Routine' : 'Create Routine'}</div>
              <div className="card-sub">{isEditMode ? 'Modify the details and exercises of your routine.' : 'Name it and select exercises from the catalog.'}</div>
            </div>
          </div>
        </div>

        <div className="card-body">
          <form onSubmit={onSubmit} className="stack" noValidate>
            {err && <div className="banner">⚠️ {err}</div>}

            <div>
              <label className="label" htmlFor="title">Name</label>
              <input
                id="title"
                className="input"
                value={title}
                onChange={e => setTitle(e.target.value)}
                placeholder="e.g. Push Day Advanced"
                minLength={3}
                required
              />
            </div>

            <div style={{ display: "grid", gridTemplateColumns: "360px 1fr", gap: 20, alignItems: 'start' }}>
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '.35rem' }}>
                  <label className="label" htmlFor="search" style={{ marginBottom: 0 }}>Exercise catalog</label>
                  {(user?.role === 'COACH' || user?.role === 'ADMIN') && (
                    <button
                      type="button"
                      className="btn"
                      style={{ padding: '4px 10px', fontSize: '12px', width: 'auto', whiteSpace: 'nowrap' }}
                      onClick={() => nav('/exercises/new')}
                    >
                      + Propose New
                    </button>
                  )}
                </div>

                <input
                  id="search"
                  className="input"
                  value={q}
                  onChange={e => setQ(e.target.value)}
                  placeholder="Search by name or muscle…"
                />
                <div className="helper" style={{ textAlign: "left" }}>{loadingCat ? "Loading catalog…" : `${filtered.length} results`}</div>

                <div className="grid" style={{ gap: 10, marginTop: 10, maxHeight: "min(540px, 60vh)", overflowY: "auto", scrollbarGutter: "stable" }}>
                  {!loadingCat && filtered.map(ex => (
                    <div key={ex.id} className="card-small" style={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
                      <div>
                        <strong>{ex.name}</strong>
                        <div style={{ color: "#555" }}>{ex.muscle || ex.muscles}</div>
                      </div>
                      <div style={{ width: 96, minWidth: 96, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                        <button
                          type="button"
                          className="btn btn-green"
                          onClick={() => addExercise(ex)}
                          disabled={selected.some(s => s.id === ex.id)}
                        >
                          {selected.some(s => s.id === ex.id) ? "Added" : "Add"}
                        </button>

                      </div>
                    </div>
                  ))}
                </div>
              </div>

              <div>
                <label className="label">Selected exercises</label>
                {selected.length === 0 && (
                  <div className="card-small">
                    <strong>Empty</strong>
                    <div style={{ color: "#555", marginTop: 6 }}>Add exercises from the catalog.</div>
                  </div>
                )}
                {selected.length > 0 && (
                  <div className="grid" style={{ gap: 10, maxHeight: "min(600px, 70vh)", overflowY: "auto", overflowX: "auto", scrollbarGutter: "stable" }}>
                    {selected.map((ex, idx) => {
                      const isCardio = ex.exerciseType === 'CARDIO';
                      return (
                        <div
                          key={ex.id}
                          className="card-small"
                          style={{
                            display: "grid",
                            gridTemplateColumns: isCardio ? "1fr 80px 100px 100px auto auto auto" : "1fr 80px 80px 100px auto auto auto",
                            alignItems: "center",
                            gap: 10,
                            opacity: ex.blocked ? 0.6 : 1,
                            border: ex.blocked ? '2px solid #dc3545' : undefined,
                            position: 'relative'
                          }}
                        >
                          <div>
                            <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                              <strong>{idx + 1}. {ex.name}</strong>
                              {isCardio ? (
                                <span style={{ backgroundColor: '#28a745', color: 'white', padding: '2px 6px', borderRadius: 3, fontSize: 10, fontWeight: 'bold' }}>
                                  🏃 CARDIO
                                </span>
                              ) : (
                                <span style={{ backgroundColor: '#007bff', color: 'white', padding: '2px 6px', borderRadius: 3, fontSize: 10, fontWeight: 'bold' }}>
                                  💪 STRENGTH
                                </span>
                              )}
                              {ex.blocked && (
                                <span style={{
                                  backgroundColor: '#dc3545',
                                  color: 'white',
                                  padding: '2px 6px',
                                  borderRadius: 3,
                                  fontSize: 10,
                                  fontWeight: 'bold'
                                }}>
                                  BLOCKED
                                </span>
                              )}
                            </div>
                            <div style={{ color: "#555" }}>{ex.muscle}</div>
                            {ex.blocked && (
                              <div style={{ fontSize: 10, color: '#dc3545', marginTop: 2, fontStyle: 'italic' }}>
                                This exercise was blocked by admin
                              </div>
                            )}
                          </div>
                          <div>
                            <label className="label" style={{ fontSize: 11 }}>Sets</label>
                            <input
                              type="number"
                              min={1}
                              className="input"
                              value={ex.sets}
                              onChange={e => updateField(ex.id, 'sets', e.target.value)}
                              disabled={ex.blocked}
                            />
                          </div>
                          {!isCardio && (
                            <div>
                              <label className="label" style={{ fontSize: 11 }}>Reps</label>
                              <input
                                type="number"
                                min={1}
                                className="input"
                                value={ex.reps}
                                onChange={e => updateField(ex.id, 'reps', e.target.value)}
                                disabled={ex.blocked}
                              />
                            </div>
                          )}
                          {isCardio ? (
                            <>
                              <div>
                                <label className="label" style={{ fontSize: 11 }}>Distance (km)</label>
                                <input
                                  type="number"
                                  min={0}
                                  step="0.1"
                                  className="input"
                                  value={ex.distance ?? ''}
                                  onChange={e => updateField(ex.id, 'distance', e.target.value)}
                                  placeholder="km"
                                  disabled={ex.blocked}
                                />
                              </div>
                              <div>
                                <label className="label" style={{ fontSize: 11 }}>Time (min)</label>
                                <input
                                  type="number"
                                  min={0}
                                  className="input"
                                  value={ex.time ?? ''}
                                  onChange={e => updateField(ex.id, 'time', e.target.value)}
                                  placeholder="min"
                                  disabled={ex.blocked}
                                />
                              </div>
                            </>
                          ) : (
                            <div>
                              <label className="label" style={{ fontSize: 11 }}>Weight (kg)</label>
                              <input
                                type="number"
                                min={0}
                                step="0.5"
                                className="input"
                                value={ex.weight}
                                onChange={e => updateField(ex.id, 'weight', e.target.value)}
                                placeholder="optional"
                                disabled={ex.blocked}
                              />
                            </div>
                          )}
                          <button type="button" className="btn" onClick={() => move(ex.id, "up")} disabled={idx === 0 || ex.blocked} style={{ width: 44 }}>↑</button>
                          <button type="button" className="btn" onClick={() => move(ex.id, "down")} disabled={idx === selected.length - 1 || ex.blocked} style={{ width: 44 }}>↓</button>
                          <button type="button" className="btn btn-black" onClick={() => removeExercise(ex.id)} disabled={ex.blocked} style={{ width: 80 }}>Remove</button>
                        </div>
                      )
                    })}
                  </div>
                )}
              </div>
            </div>

            <div className="actions" style={{ gridTemplateColumns: "1fr 1fr", display: "grid", marginTop: '20px' }}>
              <button className="btn btn-black" type="button" onClick={() => isEditMode ? nav(`/routines/${id}`) : nav("/routines")}>
                Cancel
              </button>
              <button className="btn btn-primary" type="submit" disabled={!valid || busy}>
                {busy ? (isEditMode ? 'Saving...' : 'Creating...') : (isEditMode ? 'Save Changes' : 'Create Routine')}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}