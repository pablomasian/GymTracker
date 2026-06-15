import { useEffect, useState } from 'react';
import { appFetch, fetchConfig } from '../backend/appFetch';
import { useAuth } from '../context/AuthContext';

export function useNotificationCount() {
  const { user } = useAuth() || {};
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!user) {
      setUnreadCount(0);
      setLoading(false);
      return;
    }

    let alive = true;

    let regularCount = 0;
    let followerCount = 0;
    let followedCoachCount = 0;
    let likeCount = 0;
    let commentCount = 0;

    const updateTotal = () => {
      if (alive) {
        setUnreadCount(
          regularCount +
          followerCount +
          followedCoachCount +
          likeCount +
          commentCount
        );
      }
    };

    // Always fetch general notifications unread count (includes leaderboard notifications)
    appFetch(
      '/notifications/unread-count',
      fetchConfig('GET'),
      (data) => {
        regularCount = data?.count || 0;
        updateTotal();
      },
      () => {
        regularCount = 0;
        updateTotal();
      }
    );

    if (user.role === 'COACH' || user.role === 'ADMIN') {
      appFetch(
        '/notifications/followers/unread-count',
        fetchConfig('GET'),
        (data) => {
          followerCount = data?.count || 0;
          updateTotal();
        },
        () => {
          followerCount = 0;
          updateTotal();
        }
      );
    }

    appFetch(
      '/notifications/followed-coach/unread-count',
      fetchConfig('GET'),
      (data) => {
        followedCoachCount = data?.count || 0;
        updateTotal();
      },
      () => {
        followedCoachCount = 0;
        updateTotal();
      }
    );

    appFetch(
      '/notifications/likes-unread-count',
      fetchConfig('GET'),
      (data) => {
        likeCount = data?.count || 0;
        updateTotal();
      },
      () => {
        likeCount = 0;
        updateTotal();
      }
    );

    appFetch(
      '/notifications/comments-unread-count',
      fetchConfig('GET'),
      (data) => {
        commentCount = data?.count || 0;
        updateTotal();
      },
      () => {
        commentCount = 0;
        updateTotal();
      }
    );

    const timer = setTimeout(() => {
      if (alive) setLoading(false);
    }, 500);

    return () => {
      alive = false;
      clearTimeout(timer);
    };
  }, [user]);

  return { unreadCount, loading };
}
