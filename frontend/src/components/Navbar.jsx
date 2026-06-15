import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Navbar(){
  const { user, logout } = useAuth();
  return (
    <div className="nav">
      <div className="nav-inner">
        <div style={{display:"flex",gap:8,alignItems:"center"}}>
          <span className="logo" />
          <strong>GymTracker</strong>
        </div>
        <div style={{display:"flex",gap:12,alignItems:"center"}}>
          {user ? (
            <>
              <Link to="/routines">Routines</Link>
              <Link to="/profile">Profile</Link>
              {user.role==="COACH" && <Link to="/coach/routines/new">+ New routine</Link>}
              <button className="btn" onClick={logout} style={{width:"auto",padding:".4rem .7rem"}}>Logout</button>
            </>
          ) : (
            <>
              <Link to="/login">Login</Link>
              <Link to="/register">Register</Link>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
