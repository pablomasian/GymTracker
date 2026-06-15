import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Sidebar from '../components/Sidebar';
import WithSidebar from '../components/WithSidebar';
import * as Auth from '../context/AuthContext';
import * as NotifyHook from '../hooks/useNotificationCount';

describe('Navbar', () => {
  afterEach(() => jest.restoreAllMocks());

  test('shows login/register when no user', () => {
    jest.spyOn(Auth, 'useAuth').mockReturnValue({ user: null });
    render(
      <MemoryRouter>
        <Navbar />
      </MemoryRouter>
    );
    expect(screen.getByText(/Login/i)).toBeTruthy();
    expect(screen.getByText(/Register/i)).toBeTruthy();
  });

  test('shows profile links when user present', () => {
    const logout = jest.fn();
    jest.spyOn(Auth, 'useAuth').mockReturnValue({ user: { role: 'COACH' }, logout });
    render(
      <MemoryRouter>
        <Navbar />
      </MemoryRouter>
    );
    expect(screen.getByText(/Routines/i)).toBeTruthy();
    expect(screen.getByText(/Profile/i)).toBeTruthy();
    // logout button present
    expect(screen.getByRole('button', { name: /Logout/i })).toBeTruthy();
  });
});

describe('Sidebar and WithSidebar', () => {
  afterEach(() => jest.restoreAllMocks());

  test('renders sidebar for guest (login/register shown)', () => {
    jest.spyOn(Auth, 'useAuth').mockReturnValue({ user: null });
    jest.spyOn(NotifyHook, 'useNotificationCount').mockReturnValue({ unreadCount: 0 });
    render(
      <MemoryRouter>
        <Sidebar />
      </MemoryRouter>
    );
    expect(screen.getByText(/Login/i)).toBeTruthy();
    expect(screen.getByText(/Register/i)).toBeTruthy();
  });

  test('shows user area and notification badge', () => {
    const user = { username: 'alice', role: 'USER' };
    jest.spyOn(Auth, 'useAuth').mockReturnValue({ user, logout: jest.fn() });
    jest.spyOn(NotifyHook, 'useNotificationCount').mockReturnValue({ unreadCount: 4 });
    render(
      <MemoryRouter>
        <Sidebar />
      </MemoryRouter>
    );
    expect(screen.getByText(/alice/i)).toBeTruthy();
    expect(screen.getByText(/Notifications/i)).toBeTruthy();
    expect(screen.getByText('4')).toBeTruthy();
  });

  test('WithSidebar wraps children and contains sidebar', () => {
    jest.spyOn(Auth, 'useAuth').mockReturnValue({ user: null });
    jest.spyOn(NotifyHook, 'useNotificationCount').mockReturnValue({ unreadCount: 0 });
    render(
      <MemoryRouter>
        <WithSidebar>
          <div>child content</div>
        </WithSidebar>
      </MemoryRouter>
    );
    expect(screen.getByText(/child content/i)).toBeTruthy();
    // Sidebar's nav should be present
    expect(screen.getByLabelText(/Main navigation/i)).toBeTruthy();
  });
});
