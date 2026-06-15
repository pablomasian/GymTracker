// Página de rutinas: listado público y propias con filtros y acciones
import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { config } from "../config/constants";
import { getMyRoutines, deleteRoutine } from "../backend/routineService";

// Estilos personalizados para checkboxes
const checkboxStyles = `
  .custom-checkbox {
    appearance: none;
    width: 18px;
    height: 18px;
    min-width: 18px;
    min-height: 18px;
    max-width: 18px;
    max-height: 18px;
    border: 2px solid #ccc;
    border-radius: 3px;
    cursor: pointer;
    position: rrelative;
    transition: all 0.2s;
    margin-right: 8px;
    flex-shrink: 0;
  }
  
  .custom-checkbox:checked {
    background-color: #ff6b6b;
    border-color: #ff6b6b;
  }
  
  .custom-checkbox:checked::after {
    content: '✓';
    position: absolute;
    color: white;
    font-size: 12px;
    font-weight: bold;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    line-height: 1;
  }
  
  .custom-checkbox:hover {
    border-color: #ff6b6b;
  }
`;


const DEFAULT_IMAGES = [
  "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1579758629938-03608ccdbaba?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1599058917212-d750089bc07e?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?q=80&w=1200&auto=format&fit=crop",

  "https://images.unsplash.com/photo-1554284126-aa88f22d8b74?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1546483875-ad9014c88eba?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1593079831268-3381b0db4a77?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1576678927484-cc907957088c?q=80&w=1200&auto=format&fit=crop",

  "https://images.unsplash.com/photo-1508215885820-4585e56135c8?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1507398941214-572c25f4b1dc?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1605296867304-46d5465a13f1?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1549576490-b0b4831ef60a?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1583454110551-21f2fa2afe61?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1600026453346-a44501602a02?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1571902943202-507ec2618e8f?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1581009137042-c552e485697a?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1549576490-b0b4831ef60a?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1590487988256-9ed24133863e?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1532384748853-8f54a0f1bdb3?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1517963879433-6ad2b056d712?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1594381898411-846e7d193883?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1558611848-73f7eb4001a1?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1483721310020-03333e577078?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1518611012118-696072aa579a?q=80&w=1200&auto=format&fit=crop",
  "https://images.unsplash.com/photo-1556817411-31ae72fa3ea0?q=80&w=1200&auto=format&fit=crop"

];


function getRandomImage() {
  return DEFAULT_IMAGES[Math.floor(Math.random() * DEFAULT_IMAGES.length)];
}

function CoverImage({ src, alt }) {
  const [url, setUrl] = useState(() => src || getRandomImage());

  useEffect(() => {
    if (src) {
      setUrl(src);
    }
  }, [src]);

  return (
    <img
      src={url}
      alt={alt}
      onError={() => setUrl(getRandomImage())}
      style={{
        width: "100%",
        height: 160,
        objectFit: "cover",
        display: "block",
        borderTopLeftRadius: 12,
        borderTopRightRadius: 12,
      }}
    />
  );
}

//export default CoverImage;


function RoutineCard({ r, onClick, isOwner, onEdit, onDelete }) {
  const { user } = useAuth() || {};
  const count = r.exerciseCount ?? 0;
  const coachName = r.coachNombreUsuario || r.coach || (r.userId ? ('#' + r.userId) : '—');

  return (
    <div
      className="card-small"
      style={{
        padding: 0,
        overflow: "hidden",
        borderRadius: 12,
        textAlign: "left",
        display: "flex",
        flexDirection: "column",
      }}
    >
      <button
        onClick={onClick}
        style={{
          background: "none",
          border: "none",
          padding: 0,
          width: "100%",
          cursor: "pointer",
          flexGrow: 1,
        }}
      >
        <CoverImage src={r.cover} alt={r.name || r.title} />
        <div style={{ padding: "12px 14px" }}>
          <div style={{ display: "flex", justifyContent: "space-between", gap: 8 }}>
            <strong style={{ fontSize: "1.02rem", lineHeight: 1.2, textAlign: "left" }}>
              {r.name || r.title}
            </strong>
            <span className="badge">{count} ex.</span>
          </div>
          <div style={{ color: "#555", marginTop: 6, textAlign: "left" }}>by {coachName}</div>
        </div>
      </button>

      {(isOwner || user?.role === "ADMIN") && (
        <div
          style={{
            display: "flex",
            gap: "8px",
            padding: "12px 14px",
            borderTop: "1px solid var(--border)",
            marginTop: "auto",
          }}
        >
          <button
            className="btn"
            onClick={onEdit}
            style={{ width: "100%", padding: "8px", fontSize: "14px" }}
          >
            Edit
          </button>
          <button
            className="btn btn-black"
            onClick={onDelete}
            style={{ width: "100%", padding: "8px", fontSize: "14px" }}
          >
            Delete
          </button>
        </div>
      )}
    </div>
  );
}


export default function RoutinesPage() {
  const [items, setItems] = useState([]);
  const [busy, setBusy] = useState(true);
  const [err, setErr] = useState("");
  const [q, setQ] = useState("");
  const [view, setView] = useState("all");
  const [selectedEquipment, setSelectedEquipment] = useState([]);
  const [showEquipmentDropdown, setShowEquipmentDropdown] = useState(false);
  const [selectedMuscles, setSelectedMuscles] = useState([]);
  const [showMuscleDropdown, setShowMuscleDropdown] = useState(false);


  const { user } = useAuth() || {};
  const nav = useNavigate();

  // Cerrar el desplegable de equipamiento al hacer clic fuera
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (!event.target.closest('.multi-select-dropdown')) {
        setShowEquipmentDropdown(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (!e.target.closest('.multi-select-dropdown')) {
        setShowMuscleDropdown(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);


  useEffect(() => {
    let alive = true;
    const ac = new AbortController();

    const loadAllRoutines = async () => {
      try {
        const base = `${config.BASE_PATH}/routines`;
        const params = new URLSearchParams();
        selectedEquipment.forEach(eq => {
          params.append('equipment', eq);
        });
        const url = params.toString()
          ? `${base}?${params.toString()}`
          : `${base}/display_all`;
        const res = await fetch(url, { signal: ac.signal });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const data = await res.json();
        if (alive) setItems(Array.isArray(data) ? data : []);
      } catch (e) {
        if (alive) {
          setItems([]);
          setErr("Backend not available: could not load routines.");
        }
      } finally {
        if (alive) setBusy(false);
      }
    };

    const loadMyRoutines = () => {
      getMyRoutines(
        (data) => {
          if (alive) setItems(Array.isArray(data) ? data : []);
        },
        (error) => {
          if (alive)
            setErr(error?.globalError || "Could not fetch your routines.");
        }
      ).finally(() => {
        if (alive) setBusy(false);
      });
    };

    setBusy(true);
    setErr("");
    setItems([]);

    if (view === "all") {
      loadAllRoutines();
    } else if (
      view === "my" &&
      (user?.role === "COACH" || user?.role === "ADMIN")
    ) {
      loadMyRoutines();
    } else {
      setBusy(false);
    }

    return () => {
      alive = false;
      ac.abort();
    };
  }, [view, user, selectedEquipment]);

  useEffect(() => {
    let alive = true;
    const ac = new AbortController();

    const loadAllRoutines = async () => {
      try {
        const base = `${config.BASE_PATH}/routines`;
        const params = new URLSearchParams();

        selectedMuscles.forEach(m => params.append('muscles', m));

        const url = params.toString()
          ? `${base}?${params.toString()}`
          : `${base}/display_all`;

        const res = await fetch(url, { signal: ac.signal });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const data = await res.json();
        if (alive) setItems(Array.isArray(data) ? data : []);
      } catch (e) {
        if (alive) {
          setItems([]);
          setErr('Backend not available: could not load routines.');
        }
      } finally {
        if (alive) setBusy(false);
      }
    };

    setBusy(true);
    setErr('');
    setItems([]);
    loadAllRoutines();

    return () => { alive = false; ac.abort(); };
  }, [selectedMuscles]);


  const filtered = useMemo(() => {
    const t = q.trim().toLowerCase();
    let result = items;

    // Filtrado por texto de búsqueda (en cliente)
    if (t) {
      result = result.filter(r => {
        const title = (r.name || r.title || '').toLowerCase();
        const coach = (r.coachNombreUsuario || r.coach || '').toLowerCase();
        return title.includes(t) || coach.includes(t);
      });
    }

    return result;
  }, [q, items]);

  const handleDelete = (id) => {
    if (window.confirm("Are you sure you want to delete this routine? This action cannot be undone.")) {
      setBusy(true);
      deleteRoutine(
        id,
        () => {
          setItems(currentItems => currentItems.filter(item => item.id !== id));
        },
        (error) => {
          setErr(error?.globalError || "Failed to delete routine.");
        }
      ).finally(() => {
        setBusy(false);
      });
    }
  };

  const equipmentOptions = ["Barbell", "Dumbbell", "Machine", "Bodyweight", "Cable", "Resistance Band", "Kettlebell"];
  const muscleOptions = ["Chest", "Triceps", "Biceps", "Back", "Legs", "Glutes", "Shoulders", "Core"];

  const toggleEquipment = (eq) => {
    setSelectedEquipment(prev =>
      prev.includes(eq) ? prev.filter(e => e !== eq) : [...prev, eq]
    );
  };

  const toggleMuscle = (muscle) => {
    setSelectedMuscles(prev =>
      prev.includes(muscle) ? prev.filter(m => m !== muscle) : [...prev, muscle]
    );
  };



  return (
    <div className="container" style={{ padding: "1.25rem" }}>
      <style>{checkboxStyles}</style>
      <div
        style={{
          display: "flex",
          alignItems: "center",
          gap: 12,
          justifyContent: "space-between",
          flexWrap: "wrap",
          marginBottom: 24,
        }}
      >
        <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
          <img
            src={`${process.env.PUBLIC_URL}/assets/logo.png`}
            alt="GymTracker"
            style={{ width: 44, height: 44 }}
          />
          <h2 style={{ margin: 0 }}>
            {view === "my" ? "My Workout Routines" : "All Workout Routines"}
          </h2>
        </div>

        <div style={{ display: "flex", gap: 10, alignItems: "center", flexWrap: "nowrap", flex: 1 }}>
          <input
            className="input"
            placeholder="Search..."
            value={q}
            onChange={(e) => setQ(e.target.value)}
            style={{
              flex: 8,
              minWidth: "200px",
              padding: ".5rem 1rem",
              height: "50px"
            }}
          />

          <div className="multi-select-dropdown" style={{ position: 'relative', minWidth: 120 }}>
            <button
              className="input"
              onClick={() => setShowMuscleDropdown(!showMuscleDropdown)}
              style={{
                width: '100%', height: 50, padding: 8,
                textAlign: 'left', display: 'flex', justifyContent: 'space-between', alignItems: 'center'
              }}
            >
              <span>
                {selectedMuscles.length === 0 ? 'All muscles' : `${selectedMuscles.length} selected`}
              </span>
              <span>▼</span>
            </button>

            {showMuscleDropdown && (
              <div style={{
                position: 'absolute', top: 52, left: 0, right: 0,
                background: 'white', border: '1px solid #ccc', borderRadius: 4,
                boxShadow: '0 4px 6px rgba(0,0,0,0.1)', zIndex: 1000,
                maxHeight: 300, overflowY: 'auto'
              }}>
                {muscleOptions.map(muscle => (
                  <label key={muscle}
                    style={{
                      display: 'flex', alignItems: 'center', padding: '8px 12px', cursor: 'pointer',
                      background: selectedMuscles.includes(muscle) ? '#f0f0f0' : 'white'
                    }}
                    onMouseEnter={(e) => e.currentTarget.style.background = '#f5f5f5'}
                    onMouseLeave={(e) => e.currentTarget.style.background = selectedMuscles.includes(muscle) ? '#f0f0f0' : 'white'}
                  >
                    <input
                      type="checkbox"
                      className="custom-checkbox"
                      checked={selectedMuscles.includes(muscle)}
                      onChange={() => toggleMuscle(muscle)}
                    />
                    {muscle}
                  </label>
                ))}
                {selectedMuscles.length > 0 && (
                  <button onClick={() => setSelectedMuscles([])} className="btn" style={{ width: '100%', margin: '8px 0', padding: 6, fontSize: '.85rem' }}>
                    Clear all
                  </button>
                )}
              </div>
            )}
          </div>

          {/* Multi-select Equipment Filter */}
          <div className="multi-select-dropdown" style={{ position: 'relative', flex: 1, minWidth: "120px" }}>
            <button
              className="input"
              onClick={() => setShowEquipmentDropdown(!showEquipmentDropdown)}
              style={{
                width: '100%',
                height: "50px",
                padding: "8px",
                textAlign: 'left',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                cursor: 'pointer'
              }}
            >
              <span>
                {selectedEquipment.length === 0
                  ? 'All equipment'
                  : `${selectedEquipment.length} selected`}
              </span>
              <span>▼</span>
            </button>
            {showEquipmentDropdown && (
              <div style={{
                position: 'absolute',
                top: '52px',
                left: 0,
                right: 0,
                background: 'white',
                border: '1px solid #ccc',
                borderRadius: '4px',
                boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
                zIndex: 1000,
                maxHeight: '300px',
                overflowY: 'auto'
              }}>
                {equipmentOptions.map(eq => (
                  <label
                    key={eq}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      padding: '8px 12px',
                      cursor: 'pointer',
                      background: selectedEquipment.includes(eq) ? '#f0f0f0' : 'white',
                      transition: 'background 0.2s'
                    }}
                    onMouseEnter={(e) => e.currentTarget.style.background = '#f5f5f5'}
                    onMouseLeave={(e) => e.currentTarget.style.background = selectedEquipment.includes(eq) ? '#f0f0f0' : 'white'}
                  >
                    <input
                      type="checkbox"
                      className="custom-checkbox"
                      checked={selectedEquipment.includes(eq)}
                      onChange={() => toggleEquipment(eq)}
                    />
                    {eq}
                  </label>
                ))}
                {selectedEquipment.length > 0 && (
                  <button
                    onClick={() => setSelectedEquipment([])}
                    className="btn"
                    style={{
                      width: '100%',
                      margin: '8px 0',
                      padding: '6px',
                      fontSize: '0.85rem'
                    }}
                  >
                    Clear all
                  </button>
                )}
              </div>
            )}
          </div>

          {(user?.role === "COACH" || user?.role === "ADMIN") && (
            <>
              <button
                onClick={() => setView("all")}
                className={`btn ${view === "all" ? "btn-primary" : ""}`}
                style={{ flex: 1, minWidth: "80px", padding: ".5rem 1rem", height: "50px" }}
              >
                All Routines
              </button>
              <button
                onClick={() => setView("my")}
                className={`btn ${view === "my" ? "btn-primary" : ""}`}
                style={{ flex: 1, minWidth: "80px", padding: ".5rem 1rem", height: "50px" }}
              >
                My Routines
              </button>
              <button
                className="btn btn-primary"
                onClick={() => nav("/routines/new")}
                style={{
                  flex: 1,
                  minWidth: "80px",
                  padding: ".5rem 1rem",
                  whiteSpace: "nowrap",
                  height: "50px",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  gap: "6px",
                }}
              >
                <span style={{ fontSize: "1.2rem", lineHeight: 0, position: "relative", top: "-1px" }}>➕</span>
                <span style={{ fontWeight: 600 }}>Create</span>
              </button>

            </>
          )}
        </div>


      </div>

      {err && (
        <div
          className="banner"
          style={{ marginTop: 12 }}
          role="status"
          aria-live="polite"
        >
          {err}
        </div>
      )}

      {busy && (
        <div
          className="grid routines"
          style={{
            marginTop: "1rem",
            gridTemplateColumns: "repeat(auto-fill, minmax(280px, 1fr))",
          }}
        >
          {Array.from({ length: 6 }).map((_, i) => (
            <div
              key={i}
              className="card-small"
              style={{
                opacity: 0.6,
                padding: 0,
                borderRadius: 12,
                overflow: "hidden",
              }}
            >
              <div style={{ height: 160, background: "#eee" }} />
              <div style={{ padding: "12px 14px" }}>
                <div
                  style={{
                    height: 16,
                    background: "#eee",
                    borderRadius: 6,
                    marginBottom: 8,
                  }}
                />
                <div
                  style={{
                    height: 12,
                    background: "#f0f0f0",
                    borderRadius: 6,
                    width: "60%",
                  }}
                />
              </div>
            </div>
          ))}
        </div>
      )}

      {!busy && filtered.length > 0 && (
        <div
          className="grid routines"
          style={{
            marginTop: "1rem",
            gridTemplateColumns: "repeat(auto-fill, minmax(280px, 1fr))",
          }}
        >
          {filtered.map((r) => (
            <RoutineCard
              key={r.id}
              r={r}
              onClick={() => nav(`/routines/${r.id}`)}
              isOwner={view === "my"}
              onEdit={() => nav(`/routines/edit/${r.id}`)}
              onDelete={() => handleDelete(r.id)}
            />
          ))}
        </div>
      )}

      {!busy && filtered.length === 0 && (
        <div className="card-small" style={{ marginTop: "1rem" }}>
          <strong>No routines found</strong>
          <div style={{ color: "#555", marginTop: 6 }}>
            {view === "my"
              ? "You have not created any routines yet."
              : "Try another search or check back later."}
          </div>
        </div>
      )}
    </div>
  );
}