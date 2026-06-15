import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useState } from "react";
import { useNotificationCount } from "../hooks/useNotificationCount";

export default function Sidebar() {
  const { user, logout } = useAuth() || {};
  const nav = useNavigate();
  const [collapsed, setCollapsed] = useState(false);
  const { unreadCount} = useNotificationCount();

  function handleLogout() {
    logout && logout();
    nav('/login');
  }

  return (
    <>
      <aside className={`gt-sidebar ${collapsed ? 'collapsed' : ''}`}>
        <div className="sb-head" onClick={() => nav('/routines')} style={{ cursor: 'pointer' }}>
          <img src={`${process.env.PUBLIC_URL}/assets/logo.png`} alt="GymTracker" className="sb-logo" />
          <span className="sb-title">GymTracker</span>
          <button className="sb-toggle" onClick={(e) => { e.stopPropagation(); setCollapsed(c => !c); }} aria-label={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}>
            {collapsed ? '»' : '«'}
          </button>
        </div>
        <nav className="sb-nav" aria-label="Main navigation">
          <NavLink to="/my-feed" className={({ isActive }) => isActive ? 'active' : ''}> Feed</NavLink>
          <NavLink to="/routines" className={({ isActive }) => isActive ? 'active' : ''}> Routines</NavLink>
          <NavLink to="/saved-routines" className={({ isActive }) => isActive ? 'active' : ''}> Saved routines</NavLink>
          {(user?.role === 'COACH' || user?.role === "ADMIN") && (
            <NavLink to="/routines/new" className={({ isActive }) => isActive ? 'active' : ''}> Create routine</NavLink>
          )}
          {(user?.role === 'COACH' || user?.role === "ADMIN") && (
            <NavLink to="/exercises/new" className={({ isActive }) => isActive ? 'active' : ''}> Propose New Exercise</NavLink>
          )}
          
          <NavLink to="/notifications" className={({ isActive }) => isActive ? 'active' : ''}>
            <span>Notifications</span>
            {unreadCount > 0 && <span className="notification-badge">{unreadCount}</span>}
          </NavLink>
          
          <NavLink to="/view-profile" className={({ isActive }) => isActive ? 'active' : ''}> Profile</NavLink>
          <NavLink to="/my-workouts" className={({ isActive }) => isActive ? 'active' : ''}> My Workouts</NavLink>
          <NavLink to="/users/search" className={({ isActive }) => (isActive ? "active" : "")}> Search Users</NavLink>

          {user?.role === 'ADMIN' && (
            <>
              <NavLink to="/exercises/pending" className={({ isActive }) => isActive ? 'active admin-link' : 'admin-link'}>
                Pending Exercises
              </NavLink>
              <NavLink to="/exercises/manage" className={({ isActive }) => isActive ? 'active admin-link' : 'admin-link'}>
                Manage Exercises
              </NavLink>
              <NavLink to="/routines/manage" className={({ isActive }) => isActive ? 'active admin-link' : 'admin-link'}>
                Manage Routines
              </NavLink>
              <NavLink to="/users/manage" className={({ isActive }) => isActive ? 'active admin-link' : 'admin-link'}>
                Manage Users
              </NavLink>
            </>
          )}
        </nav>

        <div className="sb-footer">
          {user && (
            <>
              <div className="sb-user" title={user.username}>
                <img
                  src={`/uploads/${user.username}/avatar.png`}
                  alt={`${user.username} avatar`}
                  onError={(e) => {
                    e.target.style.display = "none";
                    e.target.nextSibling.style.display = "grid";
                  }}
                  className="sb-avatar-img"
                />
                <div className="avatar">{user.username?.[0]?.toUpperCase() || "U"}</div>
                <div className="u-meta">
                  <strong className="u-name">{user.username}</strong>
                  <span className="u-role">{user.role?.toLowerCase()}</span>
                </div>
              </div>

              <button className="btn sb-logout" onClick={handleLogout}>Logout</button>
            </>
          )}
          {!user && (
            <div style={{ display: 'grid', gap: 8 }}>
              <NavLink to="/login" className="btn" style={{ textAlign: 'center' }}>Login</NavLink>
              <NavLink to="/register" className="btn btn-primary" style={{ textAlign: 'center' }}>Register</NavLink>
            </div>
          )}
        </div>
      </aside>
      {collapsed && (
        <button className="sb-floating-reopen" onClick={() => setCollapsed(false)} aria-label="Mostrar sidebar">»</button>
      )}
    </>
  );
}