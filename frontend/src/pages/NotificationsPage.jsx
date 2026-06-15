// Página de notificaciones del usuario
import { useEffect, useState } from 'react';
import { appFetch, fetchConfig } from '../backend/appFetch';

export default function NotificationsPage() {
  const [items, setItems] = useState([]);
  const [coachRoutineNotifs, setCoachRoutineNotifs] = useState([]);
  const [followerNotifications, setFollowerNotifications] = useState([]);

  // NEW:
  const [likeNotifications, setLikeNotifications] = useState([]);
  const [commentNotifications, setCommentNotifications] = useState([]);
  const [streakNotifications, setStreakNotifications] = useState([]);


  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Reusable function to mark unread as read
  const markAsRead = (notifications, base) => {
    const unread = notifications.filter(n => !n.read && !n.leido);

    return Promise.all(
      unread.map(n =>
        appFetch(`${base}/${n.id}/read`, fetchConfig('POST'))
      )
    );
  };

  // Utility fetch wrapper that returns a promise
  const fetchData = (endpoint) => {
    return new Promise((resolve, reject) => {
      appFetch(
        endpoint,
        fetchConfig('GET'),
        (data) => resolve(data || []),
        (err) => reject(err)
      );
    });
  };

  useEffect(() => {
    document.title = "Notifications - GymTracker";
    setLoading(true);

    const requests = [

      // Regular notifications
      fetchData('/notifications')
        .then(data => {
          const formatted = data.map(n => ({ ...n, createdAt: n.createdAt, type: 'regular' }));
          setItems(formatted);
          return markAsRead(formatted, `/notifications`);
        }),

      // Coach notifications
      fetchData('/notifications/followed-coach')
        .then(data => {
          const formatted = data.map(n => ({ ...n, createdAt: n.createdAt, type: 'coach' }));
          setCoachRoutineNotifs(formatted);
          return markAsRead(formatted, `/notifications/followed-coach`);
        }),

      // Follower notifications
      fetchData('/notifications/followers')
        .then(data => {
          const formatted = data.map(n => ({ ...n, createdAt: n.createdAt, type: 'follower' }));
          setFollowerNotifications(formatted);
          return markAsRead(formatted, `/notifications/followers`);
        }),


      fetchData('/notifications/my_likes')
        .then(data => {
          const formatted = data.map(n => ({ ...n, createdAt: n.createdAt, type: 'like' }));
          setLikeNotifications(formatted);
          return markAsRead(formatted, `/notifications/likes`);
        }),


      fetchData('/notifications/my_comments')
        .then(data => {
          const formatted = data.map(n => ({ ...n, createdAt: n.createdAt, type: 'comment' }));
          setCommentNotifications(formatted);
          return markAsRead(formatted, `/notifications/comments`);
        }),

      fetchData('/notifications/streak')
        .then(data => {
          const formatted = data.map(n => ({
            ...n,
            createdAt: n.fechaCreacion,
            type: 'streak'
          }));
          setStreakNotifications(formatted);
          return markAsRead(formatted, `/notifications/streak`);
        })

    ];

    Promise.all(requests)
      .then(() => setLoading(false))
      .catch(() => {
        setError('Could not load notifications');
        setLoading(false);
      });
  }, []);

  if (loading) return <div className='container'><h2>Notifications</h2><p>Loading…</p></div>;
  if (error) return <div className='container'><h2>Notifications</h2><p className='error'>{error}</p></div>;

  // Combine everything and sort by date
  const allNotifications = [
    ...items,
    ...coachRoutineNotifs,
    ...followerNotifications,
    ...likeNotifications,
    ...commentNotifications,
    ...streakNotifications
  ].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

  return (
    <div className='container'>
      <h2>Notifications</h2>
      {allNotifications.length === 0 && (
        <div className="empty-state-modern">
          <div className="empty-icon">🔔</div>
          <h3 className="empty-title">All caught up!</h3>
          <p className="empty-subtitle">
            You have no notifications. When someone interacts with your workouts, you'll see it here!
          </p>
        </div>
      )}

      <div style={{ display: 'grid', gap: 12 }}>
        {allNotifications.map(n => (
          <div
            key={`${n.type}-${n.id}`}
            className='card-small'
            style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '12px 16px' }}
          >
            <div style={{ flex: 1 }}>

              {n.type === 'regular' && (
                <>
                  <div style={{ fontWeight: n.read ? 400 : 600 }}>{n.message}</div>
                  <div style={{ color: '#666', fontSize: 12 }}>{new Date(n.createdAt).toLocaleString()}</div>
                </>
              )}

              {n.type === 'coach' && (
                <>
                  <div><strong>{n.coachName}</strong> published a routine: <em>{n.routineTitle}</em></div>
                  <div style={{ color: '#666', fontSize: 12 }}>{new Date(n.createdAt).toLocaleString()}</div>
                </>
              )}

              {n.type === 'follower' && (
                <>
                  <div><strong>{n.followerUsername}</strong> started following you</div>
                  <div style={{ color: '#888', fontSize: 12 }}>{new Date(n.createdAt).toLocaleString()}</div>
                </>
              )}

              {n.type === 'like' && (
                <>
                  <div>👍 <strong>{n.liker_username}</strong> liked your session</div>
                  <div style={{ color: '#888', fontSize: 12 }}>{new Date(n.createdAt).toLocaleString()}</div>
                </>
              )}

              {n.type === 'streak' && (
                <>
                  <div>
                    ⚡ <strong>Your {n.diasRacha}-day streak is about to end!</strong>
                  </div>
                  <div style={{ color: '#888', fontSize: 12 }}>
                    Deadline: {new Date(n.fechaLimite).toLocaleString()}
                  </div>
                </>
              )}


              {n.type === 'comment' && (
                <>
                  <div>💬 <strong>{n.commenter_username}</strong> commented: <em>{n.text}</em></div>
                  <div style={{ color: '#888', fontSize: 12 }}>{new Date(n.createdAt).toLocaleString()}</div>
                </>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
