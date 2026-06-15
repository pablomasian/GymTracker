import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function CoachRoute({ children }) {
  const { user, authLoading } = useAuth() || {};
  if (authLoading) return <div style={{padding:40}}>Loading session…</div>;
  if (!user) return <Navigate to="/login" replace />;
  if (user.role !== "COACH" && user.role !== "ADMIN") return <Navigate to="/routines" replace />;
  return children;
}
