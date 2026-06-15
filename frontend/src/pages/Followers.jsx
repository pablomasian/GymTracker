import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Followers() {
    const nav = useNavigate();
    const { user } = useAuth();
    const [followersList, setFollowersList] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        console.log('Current user:', user);
        console.log('User serviceToken:', user?.serviceToken);
        if (!user) {
            setLoading(false);
            return;
        }

        const fetchFollowers = async () => {
            try {
                console.log('Fetching followers list for user:', user.id);

                const response = await fetch(`/api/users/${user.id}/followers-list`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    credentials: 'include'
                });

                console.log('Response status:', response.status);

                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(`HTTP ${response.status}: ${errorText}`);
                }

                const data = await response.json();
                console.log('Received data:', data);
                setFollowersList(data);
            } catch (err) {
                console.error('Fetch error:', err);
                setError(`Error loading followers list: ${err.message}`);
            } finally {
                setLoading(false);
            }
        };

        fetchFollowers();
    }, [user]);

    if (loading) return <div style={{ padding: '2rem', textAlign: 'center' }}>Loading...</div>;

    if (error) return (
        <div style={{ padding: '24px', maxWidth: '480px', margin: '0 auto', textAlign: 'center' }}>
            <div style={{ color: 'red', marginBottom: '16px' }}>{error}</div>
            <button
                onClick={() => nav('/view-profile')}
                className="btn btn-secondary btn-sm"
            >
                Back to profile
            </button>
        </div>
    );

    return (
        <div style={{ padding: '24px', maxWidth: '480px', margin: '0 auto' }}>
            <div style={{ marginBottom: '24px', textAlign: 'center' }}>
                <h2 style={{ margin: 0, marginBottom: '12px' }}>Followers</h2>
                <button
                    onClick={() => nav('/view-profile')}
                    className="btn btn-secondary btn-sm"
                    style={{ padding: '6px 16px', fontSize: '12px', height: '32px', lineHeight: '1.2', minWidth: '120px' }}
                >
                    Back to profile
                </button>
            </div>

            {followersList.length === 0 ? (
                <div
                    style={{
                        background: '#fff',
                        borderRadius: '12px',
                        padding: '16px',
                        textAlign: 'center',
                        border: '1px solid var(--border, #e6e6e6)',
                        color: 'var(--muted, #666)'
                    }}
                >
                    No followers yet.
                </div>
            ) : (
                <div style={{ display: 'grid', gap: '12px' }}>
                    {followersList.map(u => (
                        <div
                            key={u.id}
                            style={{
                                display: 'flex',
                                alignItems: 'center',
                                gap: '12px',
                                background: '#fff',
                                borderRadius: '12px',
                                padding: '10px 14px',
                                border: '1px solid var(--border, #e6e6e6)'
                            }}
                        >
                            <img
                                src={u.avatarUrl || '/fallback-avatar.png'}
                                alt={u.nombreUsuario || u.username}
                                style={{ width: 48, height: 48, borderRadius: '50%', objectFit: 'cover', border: '1px solid #ddd' }}
                            />
                            <div>
                                <div style={{ fontWeight: 600 }}>{u.nombreUsuario || u.username}</div>
                                <div style={{ fontSize: 12, color: '#666' }}>{u.role || 'USER'}</div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}