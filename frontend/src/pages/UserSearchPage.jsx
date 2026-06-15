import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { searchUsers } from "../backend/userService";

export default function UserSearchPage() {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const nav = useNavigate();

  async function handleSearch(e) {
    const value = e.target.value;
    setQuery(value);
    if (value.trim().length < 2) {
      setResults([]);
      return;
    }
    setLoading(true);

    searchUsers(
      value,
      (users) => {
        setResults(users);
        setLoading(false);
      },
      () => {
        setResults([]);
        setLoading(false);
      }
    );
  }

  const handleClickUser = (user) => {
    if (!user || !user.id) return;
    // Redirigir según rol
    if (user.role === "COACH") {
      nav(`/coach/${user.id}`); // ruta para ViewCoachProfile
    } else {
      nav(`/user/${user.id}`); // ruta para ViewUserProfile
    }
  };

  return (
    <div className="page" style={{ padding: "24px", maxWidth: "700px", margin: "0 auto" }}>
      <h1 className="page-title">Search user</h1>
      <p style={{ color: "#777", marginBottom: "20px" }}>
        Type part of a username to find people.
      </p>

      <input
        className="input"
        placeholder="Enter a username…"
        value={query}
        onChange={handleSearch}
        style={{ maxWidth: "400px" }}
      />

      {loading && <div style={{ marginTop: "20px", color: "#555" }}>Searching...</div>}

      <div style={{ marginTop: "20px", display: "flex", flexDirection: "column", gap: "12px" }}>
        {results.map((u) => (
          <div
            key={u.id}
            className="gt-card"
            onClick={() => handleClickUser(u)}
            style={{
              display: "flex",
              alignItems: "center",
              gap: "15px",
              padding: "12px 16px",
              borderRadius: "12px",
              background: "var(--color-bg-card, #f8f8f9)",
              border: "1px solid var(--color-border, #ddd)",
              cursor: "pointer",
              transition: "0.2s",
            }}
          >
            {u.avatarUrl ? (
              <img
                src={u.avatarUrl}
                alt="avatar"
                style={{
                  width: "52px",
                  height: "52px",
                  borderRadius: "14px",
                  objectFit: "cover",
                  background: "#eee",
                  flexShrink: 0,
                }}
                onError={(e) => {
                  e.target.style.display = "none";
                  e.target.nextSibling.style.display = "grid";
                }}
              />
            ) : null}
            <div className="avatar-search-fallback" style={{ display: u.avatarUrl ? "none" : "grid" }}>
              {u.username?.[0]?.toUpperCase() ?? "U"}
            </div>

            <div style={{ display: "flex", flexDirection: "column" }}>
              <strong style={{ fontSize: "16px" }}>@{u.username}</strong>
              <span style={{ fontSize: "14px", color: "#555" }}>{u.nombreUsuario}</span>
            </div>
          </div>
        ))}
      </div>

      {!loading && query.length >= 2 && results.length === 0 && (
        <p style={{ marginTop: "20px", color: "#777" }}>No users found.</p>
      )}
    </div>
  );
}
