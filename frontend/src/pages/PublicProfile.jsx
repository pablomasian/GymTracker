import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { appFetch, fetchConfig } from "../backend/appFetch";
import { config } from "../config/constants";
import { useAuth } from "../context/AuthContext";

export default function PublicProfile({ id, isCoach }) {
  const nav = useNavigate();
  const { user: loggedUser } = useAuth() || {};

  const [profile, setProfile] = useState(null);
  const [list, setList] = useState([]);
  const [busy, setBusy] = useState(true);
  const [idx, setIdx] = useState(0);

  const [followStatus, setFollowStatus] = useState({
    loading: false,
    success: false,
    error: null,
  });

  const FALLBACK_DATAURI = useMemo(() => {
    const svg = encodeURIComponent(`
      <svg xmlns="http://www.w3.org/2000/svg" width="120" height="120">
        <rect width="100%" height="100%" fill="#eee"/>
        <circle cx="60" cy="48" r="22" fill="#fff"/>
        <rect x="20" y="76" width="80" height="32" rx="16" fill="#fff"/>
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


  useEffect(() => {
    setBusy(true);

    const url = isCoach
      ? `/users/coach-profile/${id}`
      : `/users/public-profile/${id}`;

    let alive = true;

    appFetch(
      url,
      fetchConfig("GET"),
      (data) => {
        if (!alive) return;
        setProfile(data);
        setBusy(false);
      },
      () => {
        if (!alive) return;
        setProfile(null);
        setBusy(false);
      }
    );

    return () => {
      alive = false;
    };
  }, [id, isCoach]);


  useEffect(() => {
    if (!id) return;

    setBusy(true);
    let alive = true;

    const url = isCoach
      ? `/routines/display_by_coach?coach_id=${id}`
      : `/users/public-profile/${id}/performed-routines`;

    appFetch(
      url,
      fetchConfig("GET"),
      (data) => {
        if (!alive) return;
        setList(Array.isArray(data) ? data : []);
        setBusy(false);
      },
      () => {
        if (!alive) return;
        setList([]);
        setBusy(false);
      }
    );

    return () => {
      alive = false;
    };
  }, [id, isCoach]);

  useEffect(() => {
    if (!loggedUser || !id) return;

    let alive = true;

    const url = `/users/${id}/following?user_id=${loggedUser.id}`;

    appFetch(
      url,
      fetchConfig("GET"),
      (alreadyFollowing) => {
        if (!alive) return;
        setFollowStatus({
          loading: false,
          success: !!alreadyFollowing,
          error: null,
        });
      },
      () => {}
    );

    return () => {
      alive = false;
    };
  }, [id, loggedUser]);

  const handleFollowToggle = () => {
    if (!loggedUser) {
      alert("You must be logged in to follow users");
      return;
    }

    setFollowStatus({
      loading: true,
      success: followStatus.success,
      error: null,
    });

    const method = followStatus.success ? "DELETE" : "PUT";

    const url = `/users/${id}/follow?user_id=${loggedUser.id}`;

    appFetch(
      url,
      fetchConfig(method),
      () =>
        setFollowStatus({
          loading: false,
          success: !followStatus.success,
          error: null,
        }),
      (err) =>
        setFollowStatus({
          loading: false,
          success: followStatus.success,
          error: err?.globalError || "Failed to update follow status",
        })
    );
  };

  const candidates = useMemo(() => {
    const list = [];
    if (profile?.avatarUrl) list.push(toAbsolute(profile.avatarUrl));
    else if (profile?.username)
      list.push(toAbsolute(`/uploads/${profile.username}/avatar.png`));
    list.push(FALLBACK_DATAURI);
    return list;
  }, [profile, FALLBACK_DATAURI]);

  const src = candidates[idx] || FALLBACK_DATAURI;
  const handleError = () =>
    setIdx((i) => Math.min(i + 1, candidates.length - 1));

  const handleBack = () =>
    isCoach ? nav("/routines") : nav("/users/search");

  if (busy && !profile) {
    return <div style={{ padding: 40 }}>Loading profile...</div>;
  }

  if (!profile) {
    return <div style={{ padding: 40 }}>Profile not found</div>;
  }

  return (
    <div style={{ display: "flex", gap: 20, padding: 20, height: "100vh" }}>
      {/* Perfil lateral */}
      <div
        style={{
          flex: 1,
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        <div
          style={{
            width: 300,
            borderRadius: 14,
            border: "1px solid #eee",
            boxShadow: "0 6px 16px rgba(0,0,0,.05)",
            background: "#fff",
            padding: 30,
            textAlign: "center",
          }}
        >
          <img
            src={src}
            alt={`${profile.username} avatar`}
            onError={handleError}
            style={{
              width: 120,
              height: 120,
              borderRadius: "50%",
              objectFit: "cover",
              border: "1px solid #eee",
              marginBottom: 16,
            }}
          />

          <h2>{isCoach ? "Coach Profile" : "User Profile"}</h2>

          <p><strong>Username:</strong> {profile.username}</p>
          <p><strong>Name:</strong> {profile.nombreUsuario || "—"}</p>
        </div>

        <div style={{ display: "flex", gap: 8, marginTop: 16, width: 300 }}>
          <button
            className="btn btn-secondary btn-sm"
            style={{ flex: 1 }}
            onClick={handleBack}
          >
            Back
          </button>

          {loggedUser && loggedUser.id !== profile.id && (
            <button
              className="btn btn-primary btn-sm"
              style={{ flex: 1 }}
              onClick={handleFollowToggle}
              disabled={followStatus.loading}
            >
              {followStatus.loading
                ? followStatus.success
                  ? "Unfollowing..."
                  : "Following..."
                : followStatus.success
                ? "Unfollow"
                : "Follow"}
            </button>
          )}
        </div>
      </div>

      {/* Lista derecha */}
      <div
        style={{
          flex: 1,
          borderRadius: 14,
          border: "1px solid #eee",
          boxShadow: "0 6px 16px rgba(0,0,0,.05)",
          background: "#fff",
          padding: 20,
          overflowY: "auto",
          maxHeight: "80vh",
        }}
      >
        <h3>{isCoach ? "Routines" : "Performed Routines"}</h3>

        {busy && <div>Loading...</div>}
        {!busy && list.length === 0 && <div>No data found</div>}

        {!busy &&
          list.map((r) => (
            <div
              key={r.id}
              className="card-small"
              onClick={() =>
                nav(`/routines/${isCoach ? r.id : r.routineId}`)
              }
            >
              <strong>{isCoach ? r.name : r.routineName}</strong>
              <div>
                {isCoach
                  ? `Exercises: ${r.exerciseCount ?? 0}`
                  : `Performed on: ${new Date(r.fecha).toLocaleDateString()}`}
              </div>
            </div>
          ))}
      </div>
    </div>
  );
}
