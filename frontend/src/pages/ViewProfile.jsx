import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { useEffect, useMemo, useState, useRef } from "react";
import {
  updateProfile as updateProfileApi,
  uploadAvatar as uploadAvatarApi,
  updateFitnessData,
} from "../backend/userService";
import { config } from "../config/constants";
import { appFetch, fetchConfig } from "../backend/appFetch";
import UserStatistics from "./UserStatistics";
import WrappedCarousel from "../components/WrappedCarousel";

export default function ViewProfile() {
  const { user, updateUser } = useAuth();
  const nav = useNavigate();
  const [avatarFile, setAvatarFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [badges, setBadges] = useState([]);
  const [showWrapped, setShowWrapped] = useState(false);
  const [hasWrappedAvailable, setHasWrappedAvailable] = useState(null); // null = unknown, true = available, false = not available
  const fileInputRef = useRef(null);

  // FETCH BADGES
  useEffect(() => {
    if (!user?.id) return;

    const fetchBadges = async () => {
      try {
        await appFetch(
          `/users/badges`,
          fetchConfig("GET"),
          (data) => {
            console.log("Fetched badges:", data); // <-- aquí logueas los datos recibidos
            setBadges(data);
          },
          (err) => console.error("Error fetching badges:", err)
        );
      } catch (err) {
        console.error("Error fetching badges:", err);
      }
    };

    fetchBadges();
  }, [user?.id]);

  // Check if Wrapped is available for this user (backend returns 204 when not in window)
  useEffect(() => {
    if (!user?.id) return;
    setHasWrappedAvailable(null);
    try {
      appFetch(
        `/wrapped/current`,
        fetchConfig('GET'),
        (data) => {
          // Only available if we got actual data (not 204 which calls onSuccess with no args)
          setHasWrappedAvailable(data != null);
        },
        (err) => {
          // On error (4xx with payload) -> not available
          setHasWrappedAvailable(false);
        }
      );
    } catch (e) {
      setHasWrappedAvailable(false);
    }
  }, [user?.id]);

  // Función para renderizar icono de badge usando Material Symbols (icono weight)
  const renderBadgeIcon = (badge) => {
    const baseStyle = { fontSize: 24, marginLeft: 4 };
    switch (badge.type) {
      case "HUNDRED":
        return (
          <span
            className="material-symbols-outlined"
            title={badge.description}
            style={{ ...baseStyle, color: "#fbc02d" }}
          >
            weight
          </span>
        );
      case "VOLUME_KING":
        return (
          <span
            className="material-symbols-outlined"
            title={badge.description}
            style={{ ...baseStyle, color: "#ff7043" }}
          >
            bolt
          </span>
        );
      case "EARLY_BIRD":
        return (
          <span
            className="material-symbols-outlined"
            title={badge.description}
            style={{ ...baseStyle, color: "#42a5f5" }}
          >
            sunrise
          </span>
        );
      case "FIFTY_WORKOUTS":
        return (
          <span
            className="material-symbols-outlined"
            title={badge.description}
            style={{ ...baseStyle, color: "#66bb6a" }}
          >
            verified
          </span>
        );
      case "CONSISTENCY_CHAMPION":
        return (
          <span
            className="material-symbols-outlined"
            title={badge.description}
            style={{ ...baseStyle, color: "#ab47bc" }}
          >
            calendar_month
          </span>
        );
      default:
        return null;
    }
  };

  const FALLBACK_DATAURI = useMemo(() => {
    const svg = encodeURIComponent(`
      <svg xmlns="http://www.w3.org/2000/svg" width="120" height="120">
        <defs><linearGradient id="g" x1="0" x2="1" y1="0" y2="1">
          <stop stop-color="#ffe3dc" offset="0"/>
          <stop stop-color="#ffd2cb" offset="1"/>
        </linearGradient></defs>
        <rect width="100%" height="100%" fill="url(#g)"/>
        <g fill="#fff">
          <circle cx="60" cy="48" r="22"/>
          <rect x="20" y="76" width="80" height="32" rx="16"/>
        </g>
      </svg>
    `);
    return `data:image/svg+xml;charset=utf-8,${svg}`;
  }, []);

  const toAbsolute = (path) => {
    if (!path) return null;
    if (/^https?:/i.test(path)) return path;
    const base = config.BASE_PATH.replace(/\/?api\/?$/i, "");
    return path.startsWith("/") ? base + path : base + "/" + path;
  };

  const candidates = useMemo(() => {
    const list = [];
    if (user?.avatarUrl) list.push(toAbsolute(user.avatarUrl));
    else if (user?.username)
      list.push(toAbsolute(`/uploads/${user.username}/avatar.png?t=${Date.now()}`));
    list.push(FALLBACK_DATAURI);
    return list;
  }, [user, FALLBACK_DATAURI]);

  const [idx, setIdx] = useState(0);
  const baseAvatar = candidates[idx] || FALLBACK_DATAURI;
  const src = previewUrl || baseAvatar;
  useEffect(() => setIdx(0), [user]);
  const handleError = () => setIdx((i) => Math.min(i + 1, candidates.length - 1));
  const handleBack = () => nav("/routines");
  const handleDashboard = () => nav("/coach/dashboard");

  const [form, setForm] = useState({
    nombreUsuario: user?.nombreUsuario || "",
    email: user?.email || "",
    altura: user?.altura || "",
    peso: user?.peso || "",
    edad: user?.edad || "",
    premium: user?.premium || false,
  });
  const [saving, setSaving] = useState(false);
  const [msg, setMsg] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [togglingPremium, setTogglingPremium] = useState(false);

  useEffect(() => {
    setForm({
      nombreUsuario: user?.nombreUsuario || "",
      email: user?.email || "",
      altura: user?.altura || "",
      peso: user?.peso || "",
      edad: user?.edad || "",
      premium: user?.premium || false,
    });
  }, [user]);

  useEffect(() => () => previewUrl && URL.revokeObjectURL(previewUrl), [previewUrl]);

  const onChange = (e) => setForm((f) => ({ ...f, [e.target.name]: e.target.value }));

  const isDirty = useMemo(() => {
    return (
      (user?.nombreUsuario || "").trim() !== (form.nombreUsuario || "").trim() ||
      (user?.email || "") !== form.email ||
      (user?.altura || "") !== form.altura ||
      (user?.peso || "") !== form.peso ||
      (user?.edad || "") !== form.edad ||
      (!!avatarFile) ||
      (user?.role === "COACH" && (user?.premium || false) !== form.premium)
    );
  }, [form, user, avatarFile]);

  const bmi = useMemo(() => {
    const h = Number(user?.altura);
    const w = Number(user?.peso);
    if (!h || !w) return null;
    return w / Math.pow(h / 100, 2);
  }, [user?.altura, user?.peso]);

  // Función segura de validación de emails
  const isValidEmail = (email) => {
    if (!email) return false;
    const parts = email.split("@");
    if (parts.length !== 2) return false;
    const [local, domain] = parts;
    if (!local || !domain) return false;
    if (!domain.includes(".")) return false;
    return true;
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setMsg("");
    if (!form.nombreUsuario?.trim()) { setMsg("Name is required"); return; }
    if (form.email && !isValidEmail(form.email)) { setMsg("Invalid email"); return; }

    setSaving(true);
    try {
      // Update profile (name, email)
      const profilePayload = { id: user.id, nombreUsuario: form.nombreUsuario.trim(), email: form.email?.trim() || null };
      const updatedProfile = await new Promise((resolve, reject) =>
        updateProfileApi(profilePayload, (upd) => { updateUser((prev) => ({ ...prev, ...upd })); resolve(upd); }, (err) => reject(new Error(err?.globalError || "Could not update profile")))
      );

      // Avatar
      if (avatarFile) {
        await new Promise((resolve, reject) => {
          uploadAvatarApi(updatedProfile.username, avatarFile, (u) => {
            const withBust = u?.avatarUrl ? { ...u, avatarUrl: `${u.avatarUrl}?t=${Date.now()}` } : u;
            updateUser((prev) => ({ ...prev, ...withBust }));
            resolve();
          }, () => reject(new Error("Avatar upload failed")));
        });
      }

      // Fitness
      await new Promise((resolve, reject) => {
        updateFitnessData(
          user.id,
          {
            altura: form.altura || null,
            peso: form.peso || null,
            edad: form.edad || null,
          },
          () => {
            updateUser(prev => ({
              ...prev,
              altura: form.altura || null,
              peso: form.peso || null,
              edad: form.edad || null,
              ...(prev.role === "COACH" ? { premium: form.premium } : {})
            }));
            resolve(); // <--- Muy importante
          },
          () => reject(new Error("Could not update fitness data"))
        );
      });

      setMsg("Profile updated successfully");
      if (previewUrl) { URL.revokeObjectURL(previewUrl); setPreviewUrl(null); }
      setAvatarFile(null);
      setShowForm(false);

    } catch (err) { setMsg(err.message || "Error saving"); }
    finally { setSaving(false); }
  };

  const handlePickAvatar = () => fileInputRef.current?.click();
  const handleAvatarChange = (e) => {
    const f = e.target.files?.[0] || null;
    setAvatarFile(f);
    if (previewUrl) URL.revokeObjectURL(previewUrl);
    setPreviewUrl(f ? URL.createObjectURL(f) : null);
  };

  const handleTogglePremium = async () => {
    setTogglingPremium(true);
    setMsg("");
    try {
      const response = await fetch(`/api/users/${user.id}/toggle-premium`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
      });
      if (!response.ok) {
        throw new Error('Failed to toggle premium status');
      }
      const updated = await response.json();
      updateUser(updated);
      setMsg('Premium status updated successfully');
    } catch (err) {
      setMsg(err.message || 'Error toggling premium status');
    } finally {
      setTogglingPremium(false);
    }
  };

  return (
    <div style={{ minHeight: "100dvh", display: "flex", flexDirection: "column", alignItems: "center", padding: "24px 12px", boxSizing: 'border-box' }}>
      {/* Wrapped Carousel Modal */}
      {showWrapped && <WrappedCarousel onClose={() => setShowWrapped(false)} />}

      <div className="card" style={{ width: "min(420px, 92vw)", borderRadius: 16, border: "1px solid var(--border, #e6e6e6)", boxShadow: "0 12px 30px rgba(0,0,0,.06)", background: "#fff", padding: 20, textAlign: "center", position: "relative" }}>


        {/* Avatar */}
        <div style={{ marginBottom: 14, position: "relative", display: "inline-block" }}>
          <img src={src} alt={`${user?.username || "user"} avatar`} onError={handleError} style={{ width: 112, height: 112, borderRadius: "50%", objectFit: "cover", border: "1px solid var(--border, #e6e6e6)", boxShadow: "0 4px 14px rgba(0,0,0,.08)", background: "#fafafa", cursor: showForm ? "pointer" : "default" }} onClick={() => showForm && handlePickAvatar()} />
          {showForm && <>
            <button type="button" onClick={handlePickAvatar} title="Change avatar" style={{ position: "absolute", right: -6, bottom: -6, border: "1px solid #e6e6e6", background: "#fff", color: "#333", borderRadius: 16, fontSize: 12, padding: "6px 10px", boxShadow: "0 2px 6px rgba(0,0,0,.08)", cursor: "pointer" }}>Change</button>
            <input ref={fileInputRef} type="file" accept="image/*" onChange={handleAvatarChange} style={{ display: "none" }} />
          </>}
        </div>

        <h2 style={{ margin: "6px 0 2px 0" }}>Profile</h2>
        <div style={{ display: "inline-flex", alignItems: "center", gap: 6, padding: "4px 10px", borderRadius: 999, fontSize: 12, fontWeight: 600, border: "1px solid var(--border, #e6e6e6)", color: "var(--primary, #ff6b6b)", background: "color-mix(in srgb, var(--primary, #ff6b6b) 8%, #fff)", marginBottom: 12 }}>
          <span>{user?.role || "USER"}</span>
          {badges.map(badge => (
            <span key={badge.id}>
              {renderBadgeIcon(badge)}
            </span>
          ))}
        </div>

        {/* Info / Form */}
        <div style={{ textAlign: "left", display: "grid", gap: 10, background: "#fafafa", border: "1px solid var(--border, #e6e6e6)", borderRadius: 12, padding: 14 }}>
          {msg && <div role="status" style={{ background: msg.toLowerCase().includes("updated") ? "#e8f5e9" : "#ffebee", color: msg.toLowerCase().includes("updated") ? "#2e7d32" : "#c62828", border: `1px solid ${msg.toLowerCase().includes("updated") ? "#c8e6c9" : "#ffcdd2"}`, borderRadius: 8, padding: "8px 10px", fontSize: 13, marginBottom: 6 }}>{msg}</div>}

          {!showForm && (
            <>
              <div><div style={{ color: "#777", fontSize: 13 }}>Username</div><div style={{ fontWeight: 600 }}>{user?.username}</div></div>
              <div style={{ height: 1, background: "linear-gradient(90deg,#0000,#0002,#0000)", margin: "2px 0" }} />
              <div><div style={{ color: "#777", fontSize: 13 }}>Name</div><div style={{ fontWeight: 600 }}>{user?.nombreUsuario || `${user?.firstName || ""} ${user?.lastName || ""}`}</div></div>
              <div style={{ height: 1, background: "linear-gradient(90deg,#0000,#0002,#0000)", margin: "2px 0" }} />
              <div><div style={{ color: "#777", fontSize: 13 }}>Email</div><div style={{ fontWeight: 600 }}>{user?.email || "—"}</div></div>
              <div style={{ height: 1, background: "linear-gradient(90deg,#0000,#0002,#0000)", margin: "2px 0" }} />
              <div><div style={{ color: "#777", fontSize: 13 }}>Role</div><div style={{ fontWeight: 600 }}>{user?.role}</div></div>

              <div style={{ height: 1, background: "linear-gradient(90deg,#0000,#0002,#0000)", margin: "2px 0" }} />
              <div><div style={{ color: "#777", fontSize: 13 }}>Height</div><div style={{ fontWeight: 600 }}>{user?.altura || "—"} cm</div></div>
              <div><div style={{ color: "#777", fontSize: 13 }}>Weight</div><div style={{ fontWeight: 600 }}>{user?.peso || "—"} kg</div></div>
              <div><div style={{ color: "#777", fontSize: 13 }}>Age</div><div style={{ fontWeight: 600 }}>{user?.edad || "—"} years</div></div>
              <div style={{ height: 1, background: "linear-gradient(90deg,#0000,#0002,#0000)", margin: "2px 0" }} />
              <div>
                <div style={{ color: "#777", fontSize: 13 }}>IMC</div>
                <div style={{ fontWeight: 600 }}>{bmi ? bmi.toFixed(1) : "—"}</div>
              </div>

              {user?.role === "COACH" && (
                <>
                  <div style={{ height: 1, background: "linear-gradient(90deg,#0000,#0002,#0000)", margin: "2px 0" }} />
                  <div><div style={{ color: "#777", fontSize: 13 }}>Premium Status</div><div style={{ fontWeight: 600 }}>{user?.premium ? "Premium" : "Basic"}</div></div>
                </>
              )}
            </>
          )}

          {showForm && (
            <form onSubmit={onSubmit} style={{ display: "grid", gap: 10 }}>
              <label>
                <div style={{ color: "#555", fontSize: 13, fontWeight: 600, letterSpacing: .3 }}>Name</div>
                <input type="text" name="nombreUsuario" value={form.nombreUsuario} onChange={onChange} maxLength={60} className="form-control" placeholder="Your name" style={{ marginTop: 4, background: "linear-gradient(145deg,#ffffff,#f4f4f4)", border: "1px solid #d7d7d7", borderRadius: 10, padding: "10px 12px", fontSize: 14 }} />
              </label>
              <label>
                <div style={{ color: "#555", fontSize: 13, fontWeight: 600, letterSpacing: .3 }}>Email (optional)</div>
                <input type="email" name="email" value={form.email} onChange={onChange} maxLength={120} className="form-control" placeholder="you@example.com" style={{ marginTop: 4, background: "linear-gradient(145deg,#ffffff,#f4f4f4)", border: "1px solid #d7d7d7", borderRadius: 10, padding: "10px 12px", fontSize: 14 }} />
              </label>
              <label>
                <div style={{ color: "#555", fontSize: 13, fontWeight: 600, letterSpacing: .3 }}>Height (cm)</div>
                <input type="number" name="altura" value={form.altura} onChange={onChange} className="form-control" placeholder="Height" style={{ marginTop: 4, background: "linear-gradient(145deg,#ffffff,#f4f4f4)", border: "1px solid #d7d7d7", borderRadius: 10, padding: "10px 12px", fontSize: 14 }} />
              </label>
              <label>
                <div style={{ color: "#555", fontSize: 13, fontWeight: 600, letterSpacing: .3 }}>Weight (kg)</div>
                <input type="number" name="peso" value={form.peso} onChange={onChange} className="form-control" placeholder="Weight" style={{ marginTop: 4, background: "linear-gradient(145deg,#ffffff,#f4f4f4)", border: "1px solid #d7d7d7", borderRadius: 10, padding: "10px 12px", fontSize: 14 }} />
              </label>
              <label>
                <div style={{ color: "#555", fontSize: 13, fontWeight: 600, letterSpacing: .3 }}>Age</div>
                <input type="number" name="edad" value={form.edad} onChange={onChange} className="form-control" placeholder="Age" style={{ marginTop: 4, background: "linear-gradient(145deg,#ffffff,#f4f4f4)", border: "1px solid #d7d7d7", borderRadius: 10, padding: "10px 12px", fontSize: 14 }} />
              </label>

              {user?.role === "COACH" && (
                <label style={{ display: 'flex', alignItems: 'center', gap: 8, cursor: 'pointer' }}>
                  <input type="checkbox" checked={form.premium} onChange={(e) => setForm(f => ({ ...f, premium: e.target.checked }))} style={{ cursor: 'pointer' }} />
                  <span style={{ fontSize: 14, fontWeight: 600, color: '#555' }}>Premium</span>
                </label>
              )}

              <div style={{ display: "flex", gap: 8, alignItems: "center", marginTop: 4 }}>
                <button type="submit" className="btn btn-green" disabled={!isDirty || saving}>{saving ? "Saving…" : "Save"}</button>
                {!isDirty && !saving && <span style={{ color: "#777", fontSize: 12 }}>Nothing to save</span>}
              </div>
            </form>
          )}
        </div>

        {/* Action buttons */}
        <div style={{ marginTop: 14, display: "flex", gap: 8, justifyContent: "center", flexWrap: 'wrap' }}>

          <button className="btn btn-secondary btn-sm" onClick={handleBack}>Back to Routines</button>
          {(user?.role === 'COACH' || user?.role === 'ADMIN') && <button type="button" className="btn btn-primary btn-sm" onClick={handleDashboard}>View Dashboard</button>}

          {/* My Wrapped button - only show when available */}
          {hasWrappedAvailable === true && (
            <button
              type="button"
              className="btn btn-sm"
              onClick={() => setShowWrapped(true)}
              style={{
                background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                color: 'white',
                border: 'none',
              }}
            >
              🎉 My Wrapped {new Date().getFullYear()}
            </button>
          )}

          <button
            type="button"
            className="btn btn-blue btn-sm"
            onClick={() => nav(`/profile/${user?.id}/following`)}
          >
            Following
          </button>

          {(user?.role === 'COACH' || user?.role === 'ADMIN') && (
            <button
              type="button"
              className="btn btn-gray btn-sm"
              onClick={() => nav(`/profile/${user?.id}/followers`)}
              style={{ marginTop: 4 }}
            >
              Followers
            </button>
          )}

          <button type="button" className="btn btn-black btn-sm" onClick={() => {
            if (showForm) {
              setForm({
                nombreUsuario: user?.nombreUsuario || "",
                email: user?.email || "",
                altura: user?.altura || "",
                peso: user?.peso || "",
                edad: user?.edad || "",
                premium: user?.premium || false
              });
              if (previewUrl) { URL.revokeObjectURL(previewUrl); setPreviewUrl(null); }
              setAvatarFile(null);
            }
            setShowForm(!showForm);
          }}>{showForm ? "Cancel" : "Edit"}</button>
        </div>
      </div>


      {/* Statistics */}
      <div style={{ width: 'min(900px, 92vw)', marginTop: '2.5rem' }}>
        <h2 style={{ textAlign: 'center', color: '#444', marginBottom: '1.5rem' }}>Last Month's Statistics</h2>
        <UserStatistics />
      </div>
    </div>
  );
}
