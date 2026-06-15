import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { appFetch, fetchConfig } from "../backend/appFetch";
import WrappedCarousel from "../components/WrappedCarousel";

export default function UserFeedPage() {
  const [feed, setFeed] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showWrapped, setShowWrapped] = useState(false);
  const navigate = useNavigate();

  // Mostrar el banner de Wrapped solo del 22 de diciembre al 16 de enero (igual que backend)
  const today = new Date();
  const month = today.getMonth();
  const day = today.getDate();
  const isWrappedSeason = (month === 11 && day >= 22) || (month === 0 && day <= 16);

  useEffect(() => {
    document.title = "Your Feed - GymTracker";
    loadFeed();
  }, []);

  const loadFeed = async () => {
    setLoading(true);
    setError("");

    try {
      const sessions = await fetchData("/feed/workouts");
      const normalizedSessions = sessions.map(s => ({ ...s, type: "session" }));

      setFeed(normalizedSessions);
      setLoading(false);
    } catch (e) {
      console.error("Error loading feed:", e);
      setError("Failed to load feed.");
      setLoading(false);
    }
  };

  const fetchData = async (endpoint) => {
    return new Promise((resolve, reject) => {
      appFetch(
        endpoint,
        fetchConfig("GET"),
        (data) => resolve(data || []),
        (err) => reject(err)
      );
    });
  };

  const handleLikeToggle = (session, event) => {
    event.stopPropagation(); // prevent card click navigation
    const endpoint = session.liked ? `/feed/unlike/${session.id}` : `/feed/like/${session.id}`;

    appFetch(
      endpoint,
      fetchConfig("POST"),
      () =>
        setFeed(prevFeed =>
          prevFeed.map(f =>
            f.id === session.id && f.type === "session"
              ? { ...f, liked: !f.liked }
              : f
          )
        ),
      (err) => console.error("Error toggling like:", err)
    );
  };

  return (
    <div className="container" style={{ padding: "1.25rem" }}>
      {/* Wrapped Carousel Modal */}
      {showWrapped && <WrappedCarousel onClose={() => setShowWrapped(false)} />}

      {/* Wrapped Banner - visible until December 31, ABOVE Refresh Feed */}
      {isWrappedSeason && (
        <div
          onClick={() => setShowWrapped(true)}
          style={{
            background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
            borderRadius: 16,
            padding: '1.25rem',
            marginBottom: 16,
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            color: 'white',
            boxShadow: '0 4px 15px rgba(102, 126, 234, 0.4)',
            transition: 'transform 0.2s, box-shadow 0.2s',
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.transform = 'scale(1.02)';
            e.currentTarget.style.boxShadow = '0 6px 20px rgba(102, 126, 234, 0.5)';
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.transform = 'scale(1)';
            e.currentTarget.style.boxShadow = '0 4px 15px rgba(102, 126, 234, 0.4)';
          }}
        >
          <div>
            <div style={{ fontSize: 12, opacity: 0.9, marginBottom: 4 }}>🎉 YOUR {new Date().getFullYear()} SUMMARY</div>
            <div style={{ fontSize: 18, fontWeight: 700 }}>GymTracker Wrapped</div>
            <div style={{ fontSize: 13, opacity: 0.85, marginTop: 4 }}>Discover your year's achievements</div>
          </div>
          <div style={{ fontSize: 32 }}>→</div>
        </div>
      )}

      <div style={{
        display: "flex",
        alignItems: "center",
        gap: 12,
        justifyContent: "space-between",
        flexWrap: "wrap",
        marginBottom: 24,
      }}>
        <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
          <img
            src={`${process.env.PUBLIC_URL}/assets/logo.png`}
            alt="GymTracker"
            style={{ width: 44, height: 44 }}
          />
          <h2 style={{ margin: 0 }}>Your Feed</h2>
        </div>
        <button
          className="btn btn-primary"
          onClick={loadFeed}
          style={{ padding: "8px 14px", borderRadius: 8, fontSize: "14px" }}
        >
          Refresh Feed
        </button>
      </div>

      {/* Skeleton Loading */}
      {loading && (
        <div className="feed-skeleton">
          {[1, 2, 3].map((n) => (
            <div key={n} className="skeleton-card">
              <div className="skeleton-row">
                <div className="skeleton-avatar" />
                <div style={{ flex: 1 }}>
                  <div className="skeleton-line medium" />
                  <div className="skeleton-line short" style={{ marginTop: 8 }} />
                </div>
              </div>
              <div className="skeleton-row" style={{ justifyContent: 'flex-end', gap: 8 }}>
                <div className="skeleton-btn" />
                <div className="skeleton-btn" />
              </div>
            </div>
          ))}
        </div>
      )}

      {error && <div className="banner">⚠️ {error}</div>}

      {/* Empty State */}
      {!loading && !error && feed.length === 0 && (
        <div className="empty-state-modern">
          <div className="empty-icon">📭</div>
          <h3 className="empty-title">No activity yet</h3>
          <p className="empty-subtitle">
            When your friends complete workouts, they'll appear here. Start following people to see their progress!
          </p>
        </div>
      )}

      <div className="stack" style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
        {feed.map((item, idx) => (
          <div
            key={idx}
            className="card-small"
            onClick={() => navigate(`/session-feed/${item.id}`)}
            style={{
              width: "100%",
              display: "flex",
              flexDirection: "row",
              alignItems: "center",
              justifyContent: "space-between",
              padding: "1rem 1.25rem",
              borderRadius: "12px",
              boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
              backgroundColor: "#fff",
              cursor: "pointer"
            }}
          >
            <div style={{ flex: 1 }}>
              <h4>🏋️‍♂️ {item.userName || "Unknown user"}</h4>
              <p>did <strong>{item.routineName}</strong></p>
            </div>

            <div style={{ display: "flex", alignItems: "center", gap: 10 }}>
              <button
                className={`like-btn ${item.liked ? 'liked' : ''}`}
                onClick={(e) => handleLikeToggle(item, e)}
              >
                <span className="like-icon">{item.liked ? '❤️' : '🤍'}</span>
                {item.liked ? 'Liked' : 'Like'}
              </button>

              <span style={{ fontSize: "0.85rem", color: "#777" }}>
                {new Date(item.fecha).toLocaleString([], { dateStyle: "medium", timeStyle: "short" })}
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
