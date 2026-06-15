import { useEffect, useState, useMemo } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { appFetch, fetchConfig } from "../backend/appFetch";

export default function FeedWorkoutDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [sets, setSets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [liked, setLiked] = useState(false);
  const [likeCount, setLikeCount] = useState(0);
  const [sessionDetails, setSessionDetails] = useState(null);
  const [collapsed, setCollapsed] = useState(false);
  const [commentText, setCommentText] = useState("");
  const [comments, setComments] = useState([]);

  useEffect(() => {
    let alive = true;

    appFetch(`/feed/session-feed-details/${id}`, fetchConfig("GET"), (data) => {
      if (alive) {
        setSessionDetails(data);
        setLiked(data.liked || false);
      }
    }, () => {});

    appFetch(`/feed/session-feed-sets/${id}`, fetchConfig("GET"), (data) => {
      if (alive) {
        setSets(data || []);
        setLoading(false);
      }
    }, (err) => {
      if (alive) {
        setError(err.globalError || "Failed to load sets");
        setLoading(false);
      }
    });

    appFetch(`/feed/likes-count/${id}`, fetchConfig("GET"), (count) => {
      if (alive) setLikeCount(count);
    }, () => {});

    appFetch(`/feed/comments-of-session/${id}`, fetchConfig("GET"), (data) => {
      if (alive) setComments(data || []);
    }, () => {});

    return () => { alive = false; };
  }, [id]);

  const setsByExercise = useMemo(() => {
    const map = {};
    for (const s of sets) {
      const key = s.exerciseId;
      if (!map[key]) map[key] = { exerciseName: s.exerciseName, imageUrl: s.imageUrl, sets: [] };
      map[key].sets.push(s);
    }
    return Object.entries(map);
  }, [sets]);

  const handleLikeToggle = () => {
    const endpoint = liked ? `/feed/unlike/${id}` : `/feed/like/${id}`;
    appFetch(endpoint, fetchConfig("POST"), () => {
      setLiked(prev => !prev);
      setLikeCount(prev => prev + (liked ? -1 : 1));
    }, () => {});
  };

  const handleSubmitComment = () => {
    if (!commentText.trim()) return;
    const params = new URLSearchParams({ text: commentText }).toString();
    const endpoint = `/feed/${id}/comment?${params}`;

    appFetch(endpoint, fetchConfig("POST"), () => {
      setCommentText("");
      appFetch(`/feed/comments-of-session/${id}`, fetchConfig("GET"), (data) => setComments(data || []), () => {});
    }, () => {});
  };

  if (loading) return <div className="container"><h2>Loading workout details...</h2></div>;

  return (
    <div className="container" style={{ padding: "1.5rem", fontFamily: "Arial, sans-serif", maxHeight: "calc(100vh - 100px)", overflowY: "auto" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1rem" }}>
        <h2 style={{ margin: 0, color: "#333" }}>Workout Details</h2>
        <div style={{ display: "flex", gap: "0.5rem", alignItems: "center" }}>
          <span style={{ fontSize: "1.2rem" }}>❤️ {likeCount}</span>
          <button onClick={handleLikeToggle} style={{ padding: "4px 10px", borderRadius: "6px", border: "1px solid #aaa", cursor: "pointer", background: liked ? "#ffe6e6" : "#fafafa", fontSize: "0.85rem" }}>
            {liked ? "💔 Unlike" : "👍 Like"}
          </button>
          <button onClick={() => setCollapsed(prev => !prev)} style={{ padding: "4px 10px", borderRadius: "6px", border: "1px solid #aaa", cursor: "pointer", background: "#fafafa", fontSize: "0.85rem" }}>
            {collapsed ? "Show" : "Hide"}
          </button>
          <button onClick={() => navigate(-1)} style={{ padding: "4px 10px", borderRadius: "6px", border: "1px solid #888", backgroundColor: "#e6e6e6", cursor: "pointer", fontSize: "0.85rem" }}>
            ← Back
          </button>
        </div>
      </div>

      {sessionDetails && (
        <p style={{ marginBottom: "1rem", color: "#555", fontSize: "1rem" }}>
          <strong>{sessionDetails.userName}</strong> has completed{" "}
          <span style={{ color: "#007bff", cursor: "pointer", textDecoration: "underline" }} onClick={() => navigate(`/routines/${sessionDetails.routineId}`)}>
            {sessionDetails.routineName}
          </span>
        </p>
      )}

      {error && (
        <div style={{ backgroundColor: "#ffe0e0", color: "#900", padding: "0.75rem 1rem", borderRadius: "8px", marginBottom: "1rem", border: "1px solid #f5c2c2" }}>
          ⚠️ {error}
        </div>
      )}

      {!collapsed && (
        sets.length === 0 ? (
          <p style={{ color: "#666", fontStyle: "italic" }}>No sets found for this workout.</p>
        ) : (
          <div className="stack" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem', paddingBottom: '2rem' }}>
            {setsByExercise.map(([exerciseId, info]) => (
              <div key={exerciseId} className="card-small" style={{ padding: '1rem', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '0.75rem' }}>
                  {(() => {
                    const n = (info.exerciseName || '').toLowerCase();
                    const banned = n.includes('plank') || n.includes('crunch') || n.includes('burpee') || n.includes('jump rope') || n.includes('jump-rope') || n.includes('jumprope');
                    if (banned) return null;
                    return (
                      <img
                        src={info.imageUrl || `${process.env.PUBLIC_URL}/noop.png`}
                        alt={info.exerciseName}
                        style={{ width: 120, height: 120, objectFit: 'cover', borderRadius: 8, border: '1px solid #ddd', flexShrink: 0, background:'#fff' }}
                        onError={(e)=>{ e.currentTarget.style.display='none'; }}
                      />
                    );
                  })()}
                  <h3 style={{ margin: 0 }}>{info.exerciseName}</h3>
                </div>
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                  <thead>
                    <tr style={{ backgroundColor: '#fafafa' }}>
                      <th style={{ padding: '0.5rem', borderBottom: '1px solid #eee', textAlign:'left' }}>Set #</th>
                      <th style={{ padding: '0.5rem', borderBottom: '1px solid #eee', textAlign:'left' }}>Reps</th>
                      <th style={{ padding: '0.5rem', borderBottom: '1px solid #eee', textAlign:'left' }}>Weight</th>
                    </tr>
                  </thead>
                  <tbody>
                    {info.sets.map((s,i)=>(
                      <tr key={s.id || i} style={{ backgroundColor: i % 2 === 0 ? '#fff' : '#f7f7f7' }}>
                        <td style={{ padding: '0.5rem', borderBottom: '1px solid #f0f0f0' }}>{s.numeroSerie}</td>
                        <td style={{ padding: '0.5rem', borderBottom: '1px solid #f0f0f0' }}>{s.repeticiones}</td>
                        <td style={{ padding: '0.5rem', borderBottom: '1px solid #f0f0f0' }}>{s.peso}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ))}
          </div>
        )
      )}

      <div className="comments-section" style={{ marginTop: '2rem' }}>
        <h3 style={{ marginBottom: '0.75rem', color: "#333" }}>Comments</h3>
        <div style={{ marginBottom: '1rem', display: 'flex', gap: '0.5rem' }}>
          <input 
            type="text" 
            value={commentText} 
            onChange={(e) => setCommentText(e.target.value)} 
            placeholder="Write a comment..." 
            style={{ flex: 1, padding: '0.5rem', borderRadius: '6px', border: '1px solid #ccc' }} 
          />
          <button onClick={handleSubmitComment} style={{ padding: '0.5rem 1rem', borderRadius: '6px', border: 'none', backgroundColor: '#007bff', color: '#fff', cursor: 'pointer' }}>
            Submit
          </button>
        </div>
        <div className="comments-list" style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
          {comments.map((c, i) => (
            <div key={i} style={{ padding: '0.5rem', borderRadius: '6px', backgroundColor: '#f5f5f5' }}>
              <strong>{c.commenter_username}</strong> commented: {c.text}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
