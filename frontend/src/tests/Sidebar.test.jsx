import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import { MemoryRouter } from 'react-router-dom';
import Sidebar from '../components/Sidebar';
import * as AuthContext from '../context/AuthContext';
import * as NotificationHook from '../hooks/useNotificationCount';

jest.mock('../context/AuthContext');
jest.mock('../hooks/useNotificationCount');

describe('Sidebar Component', () => {
  test('renders user info and logout', () => {
    AuthContext.useAuth.mockReturnValue({ 
      user: { username: 'TestUser', role: 'USER' },
      logout: jest.fn()
    });
    NotificationHook.useNotificationCount.mockReturnValue({ unreadCount: 5 });

    render(<MemoryRouter><Sidebar /></MemoryRouter>);

    expect(screen.getByText('TestUser')).toBeInTheDocument();
    expect(screen.getByText('5')).toBeInTheDocument(); 
    expect(screen.getByText('Logout')).toBeInTheDocument();
  });

  test('renders admin links when user is admin', () => {
    AuthContext.useAuth.mockReturnValue({ 
      user: { username: 'Admin', role: 'ADMIN' }
    });
    NotificationHook.useNotificationCount.mockReturnValue({ unreadCount: 0 });

    render(<MemoryRouter><Sidebar /></MemoryRouter>);

    expect(screen.getByText(/Manage Users/i)).toBeInTheDocument();
    expect(screen.getByText(/Manage Exercises/i)).toBeInTheDocument();
  });

  test('toggles collapse', () => {
    AuthContext.useAuth.mockReturnValue({ user: { username: 'U' } });
    NotificationHook.useNotificationCount.mockReturnValue({ unreadCount: 0 });

    render(<MemoryRouter><Sidebar /></MemoryRouter>);
    
    const collapseBtn = screen.getByLabelText('Collapse sidebar'); 
    fireEvent.click(collapseBtn);
    
    
    expect(screen.getByLabelText('Mostrar sidebar')).toBeInTheDocument();
  });
});