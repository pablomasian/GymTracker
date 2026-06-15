import { useState, useEffect } from 'react';
import { userService } from '../backend';
import '../styles/AdminManagement.css';

export default function AdminUserManagement() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filter, setFilter] = useState('all'); // 'all', 'active', 'blocked'
  const [confirmDialog, setConfirmDialog] = useState({ show: false, user: null, action: null });

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await userService.listAll();
      // Filtrar admins ya que no se pueden bloquear
      const nonAdminUsers = data.filter(user => user.role !== 'ADMIN');
      setUsers(nonAdminUsers);
    } catch (err) {
      setError('Error loading users: ' + (err.message || 'Unknown error'));
      console.error('Error loading users:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleBlock = async (userId) => {
    try {
      await userService.block(userId);
      await loadUsers();
      setConfirmDialog({ show: false, user: null, action: null });
    } catch (err) {
      setError('Error blocking user: ' + (err.message || 'Unknown error'));
      console.error('Error blocking user:', err);
    }
  };

  const handleUnblock = async (userId) => {
    try {
      await userService.unblock(userId);
      await loadUsers();
      setConfirmDialog({ show: false, user: null, action: null });
    } catch (err) {
      setError('Error unblocking user: ' + (err.message || 'Unknown error'));
      console.error('Error unblocking user:', err);
    }
  };

  const openConfirmDialog = (user, action) => {
    setConfirmDialog({ show: true, user, action });
  };

  const closeConfirmDialog = () => {
    setConfirmDialog({ show: false, user: null, action: null });
  };

  const confirmAction = () => {
    if (confirmDialog.action === 'block') {
      handleBlock(confirmDialog.user.id);
    } else if (confirmDialog.action === 'unblock') {
      handleUnblock(confirmDialog.user.id);
    }
  };

  const filteredUsers = users.filter(user => {
    if (filter === 'active') return !user.blocked;
    if (filter === 'blocked') return user.blocked;
    return true; // 'all'
  });

  if (loading) {
    return <div className="admin-container"><p>Loading users...</p></div>;
  }

  return (
    <div className="admin-container">
      <header className="admin-header">
        <h1>User Management</h1>
        <p className="admin-subtitle">Manage user accounts and access</p>
      </header>

      {error && (
        <div className="alert alert-error">
          {error}
          <button onClick={() => setError(null)} className="alert-close">×</button>
        </div>
      )}

      <div className="admin-filters">
        <button
          className={`filter-btn ${filter === 'all' ? 'active' : ''}`}
          onClick={() => setFilter('all')}
        >
          All Users ({users.length})
        </button>
        <button
          className={`filter-btn ${filter === 'active' ? 'active' : ''}`}
          onClick={() => setFilter('active')}
        >
          Active ({users.filter(u => !u.blocked).length})
        </button>
        <button
          className={`filter-btn ${filter === 'blocked' ? 'active' : ''}`}
          onClick={() => setFilter('blocked')}
        >
          Blocked ({users.filter(u => u.blocked).length})
        </button>
      </div>

      <div className="users-grid">
        {filteredUsers.length === 0 ? (
          <p className="no-results">No users found with current filter.</p>
        ) : (
          filteredUsers.map(user => (
            <div key={user.id} className={`user-card ${user.blocked ? 'blocked' : ''}`}>
              <div className="user-card-header">
                <div className="user-info">
                  <h3>{user.nombreUsuario || user.username}</h3>
                  <p className="user-username">@{user.username}</p>
                  <span className={`user-role-badge ${user.role?.toLowerCase()}`}>
                    {user.role}
                  </span>
                </div>
                {user.blocked && (
                  <span className="blocked-badge">BLOCKED</span>
                )}
              </div>

              <div className="user-card-body">
                <p><strong>Name:</strong> {user.firstName} {user.lastName}</p>
                {user.email && <p><strong>Email:</strong> {user.email}</p>}
              </div>

              <div className="user-card-actions">
                {user.blocked ? (
                  <button
                    className="btn btn-success"
                    onClick={() => openConfirmDialog(user, 'unblock')}
                  >
                    Unblock User
                  </button>
                ) : (
                  <button
                    className="btn btn-danger"
                    onClick={() => openConfirmDialog(user, 'block')}
                  >
                    Block User
                  </button>
                )}
              </div>
            </div>
          ))
        )}
      </div>

      {confirmDialog.show && (
        <div className="modal-overlay" onClick={closeConfirmDialog}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h3>Confirm {confirmDialog.action === 'block' ? 'Block' : 'Unblock'}</h3>
            <p>
              Are you sure you want to {confirmDialog.action} user{' '}
              <strong>{confirmDialog.user?.nombreUsuario || confirmDialog.user?.username}</strong>?
            </p>
            {confirmDialog.action === 'block' && (
              <p className="warning-text">
                This user will not be able to login until unblocked.
              </p>
            )}
            <div className="modal-actions">
              <button className="btn" onClick={closeConfirmDialog}>
                Cancel
              </button>
              <button
                className={`btn ${confirmDialog.action === 'block' ? 'btn-danger' : 'btn-success'}`}
                onClick={confirmAction}
              >
                Confirm
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
