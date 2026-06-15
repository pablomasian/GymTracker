// Página de registro: alta de usuarios con avatar opcional y rol coach
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const EyeIcon = ({ closed }) => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden="true">
    {closed ? (
      <>
        <path d="M3 3l18 18" stroke="currentColor" strokeWidth="2" />
        <path d="M4 14s3-6 8-6 8 6 8 6" stroke="currentColor" strokeWidth="2" fill="none" />
      </>
    ) : (
      <>
        <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12Z" stroke="currentColor" strokeWidth="2" fill="none" />
        <circle cx="12" cy="12" r="3" fill="currentColor" />
      </>
    )}
  </svg>
);

export default function RegisterPage() {
  const auth = useAuth();
  const nav = useNavigate();

  const [f, setF] = useState({
    avatarFile: null, name: "", username: "", password: "", confirm: "", coach: false, premium: false
  });
  const [preview, setPreview] = useState(null);
  const [show1, setShow1] = useState(false);
  const [show2, setShow2] = useState(false);
  const [err, setErr] = useState("");
  const [busy, setBusy] = useState(false);

  const set = (k, v) => setF(s => ({ ...s, [k]: v }));

  const passwordsMatch = f.password && f.confirm && f.password === f.confirm;
  const valid =
    f.username.trim() &&
    f.name.trim() &&
    f.password.length >= 6 &&
    passwordsMatch;

  async function mockRegister() {
    await new Promise(r => setTimeout(r, 600));
  }

  async function onSubmit(e) {
    e.preventDefault();
    setErr("");
    setBusy(true);
    try {
      if (!valid) throw new Error("Please, check the form fields.");
      if (auth && typeof auth.register === "function") {
        await auth.register(f);
      } else {
        await mockRegister();
      }
      // Redirige a la página de rutinas tras registro correcto
      nav("/my-feed");
    } catch (ex) {
      setErr(ex.message || "Register error");
    } finally {
      setBusy(false);
    }
  }

  function onAvatarChange(e) {
    const file = e.target.files[0];
    if (file) {
      set("avatarFile", file);
      setPreview(URL.createObjectURL(file));
    }
  }

  return (
    <div className="auth-page">
      <div className="slogan-hero">
        <span className="brand">GymTracker</span>
        <span className="phrase">To be the best, train with the best</span>
      </div>
      <div className="card auth-card auth-card-register">
        <div className="card-header">
          <img
            src={`${process.env.PUBLIC_URL}/assets/logo.png`}
            alt="GymTracker"
            className="logo"
          />
          <div className="card-title">Create account</div>
          <div className="card-sub">Fill the fields below to register.</div>
        </div>

        <div className="card-body">
          <form onSubmit={onSubmit} className="stack" noValidate>
            {err && (
              <div className="banner" role="alert" aria-live="assertive">
                ⚠️ {err}
              </div>
            )}

            <div>
              <label className="label" htmlFor="avatar">Avatar (optional)</label>
              <input
                id="avatar"
                type="file"
                accept=".png*"
                className="input"
                onChange={onAvatarChange}
              />
              {preview && (
                <div style={{ marginTop: 8, display: "flex", gap: 12, alignItems: "center" }}>
                  <img
                    src={preview}
                    alt="Avatar preview"
                    style={{ width: 48, height: 48, borderRadius: 12, objectFit: "cover", border: "1px solid var(--color-border, #ccc)" }}
                  />
                  <span className="helper">Preview</span>
                </div>
              )}
            </div>

            <div>
              <label className="label" htmlFor="name">Full name</label>
              <input
                id="name"
                className="input"
                value={f.name}
                onChange={(e) => set("name", e.target.value)}
                autoComplete="name"
              />
            </div>

            <div>
              <label className="label" htmlFor="user">Username</label>
              <input
                id="user"
                className="input"
                value={f.username}
                onChange={(e) => set("username", e.target.value)}
                autoComplete="username"
              />
            </div>

            <div>
              <label className="label" htmlFor="pass1">Password</label>
              <div className="row">
                <input
                  id="pass1"
                  className="input"
                  type={show1 ? "text" : "password"}
                  value={f.password}
                  onChange={(e) => set("password", e.target.value)}
                  autoComplete="new-password"
                  minLength={6}
                  aria-describedby="passwordHelp"
                />
                <button
                  type="button"
                  className="eye"
                  onClick={() => setShow1(s => !s)}
                  aria-label={show1 ? "Hide password" : "Show password"}
                >
                  <EyeIcon closed={show1} />
                </button>
              </div>
              <div id="passwordHelp" className="helper">Minimum 6 characters.</div>
            </div>

            <div>
              <label className="label" htmlFor="pass2">Confirm password</label>
              <div className="row">
                <input
                  id="pass2"
                  className="input"
                  type={show2 ? "text" : "password"}
                  value={f.confirm}
                  onChange={(e) => set("confirm", e.target.value)}
                  autoComplete="new-password"
                />
                <button
                  type="button"
                  className="eye"
                  onClick={() => setShow2(s => !s)}
                  aria-label={show2 ? "Hide password" : "Show password"}
                >
                  <EyeIcon closed={show2} />
                </button>
              </div>
              {!!f.password && !!f.confirm && !passwordsMatch && (
                <div className="helper" style={{ color: "#ff5f6d" }}>Passwords do not match</div>
              )}
            </div>

            <label style={{ display: "flex", gap: 10, alignItems: "center", marginTop: 4 }}>
              <input
                type="checkbox"
                checked={f.coach}
                onChange={(e) => {
                  set("coach", e.target.checked);
                  // Reset premium when unchecking coach
                  if (!e.target.checked) {
                    set("premium", false);
                  }
                }}
              />
              Coach
            </label>

            {/* Show premium checkbox only when coach is selected */}
            {f.coach && (
              <label style={{ display: "flex", gap: 10, alignItems: "center", marginTop: 8, marginLeft: 24 }}>
                <input
                  type="checkbox"
                  checked={f.premium}
                  onChange={(e) => set("premium", e.target.checked)}
                />
                Premium
              </label>
            )}

            <div className="actions">
              <button className="btn btn-primary" type="submit" disabled={!valid || busy}>
                {busy ? "Creating…" : "Create account"}
              </button>
            </div>
          </form>
          <div className="register-link" style={{ marginTop: "1.5rem" }}>
            <Link to="/login">Already have an account? Sign in</Link>
          </div>
        </div>
      </div>
    </div>
  );
}
