// Página de inicio de sesión: formulario para autenticar al usuario
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

export default function LoginPage() {
  const auth = useAuth();
  const nav = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [show, setShow] = useState(false);
  const [err, setErr] = useState("");
  const [busy, setBusy] = useState(false);

  async function mockLogin(u, p) {
    await new Promise(r => setTimeout(r, 500));
    const ok = (u === "admin" || u === "coach") && p === "123456";
    if (!ok) throw new Error("Invalid username or password");
  }

  async function onSubmit(e) {
    e.preventDefault();
    setErr(""); setBusy(true);
    try {
      if (!username.trim() || !password) throw new Error("Please, fill both fields.");
      if (auth && typeof auth.login === "function") {
        await auth.login(username, password);
      } else {
        await mockLogin(username, password);
      }
      nav("/my-feed");
    } catch (ex) {
      // Si el error viene del backend con globalError (usuario bloqueado u otros errores)
      if (ex && ex.globalError) {
        setErr(ex.globalError);
      } else {
        setErr(ex.message || "Invalid credentials");
      }
    } finally {
      setBusy(false);
    }
  }

  const disabled = !username.trim() || !password || busy;

  return (
    <div className="auth-page">
      <div className="slogan-hero">
        <span className="brand">GymTracker</span>
        <span className="phrase">To be the best, train with the best</span>
      </div>
      <div className="card auth-card auth-card-login">
        <div className="card-header">
          <img
            src={`${process.env.PUBLIC_URL}/assets/logo.png`}
            alt="GymTracker"
            className="logo"
          />
          <div className="card-title">Sign in</div>
          <div className="card-sub">Enter your credentials to access your account.</div>
        </div>

        <div className="card-body">
          <form onSubmit={onSubmit} className="stack" noValidate>
            {err && (
              <div className="banner" role="alert" aria-live="assertive">
                ⚠️ {err}
              </div>
            )}

            <div>
              <label className="label" htmlFor="user">Username</label>
              <input
                id="user"
                className="input"
                value={username}
                onChange={e => setUsername(e.target.value)}
                autoComplete="username"
              />
            </div>

            <div>
              <label className="label" htmlFor="pass">Password</label>
              <div className="row">
                <input
                  id="pass"
                  className="input"
                  type={show ? "text" : "password"}
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  autoComplete="current-password"
                />
                <button
                  type="button"
                  className="eye"
                  onClick={() => setShow(s => !s)}
                  aria-label={show ? "Hide password" : "Show password"}
                >
                  <EyeIcon closed={show} />
                </button>
              </div>
            </div>

            <div className="actions">
              <button className="btn btn-primary" type="submit" disabled={disabled}>
                {busy ? "Signing in…" : "Sign in"}
              </button>
            </div>

            <div className="register-link">
              <Link to="/register">Create account</Link>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
