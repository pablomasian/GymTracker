import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import AdminRoute from '../components/AdminRoute';
import CoachRoute from '../components/CoachRoute';
import ProtectedRoute from '../components/ProtectedRoute';
import * as Auth from '../context/AuthContext';

describe('AdminRoute component', () => {
  afterEach(() => jest.restoreAllMocks());

  test('shows loading when authLoading is true', () => {
    jest.spyOn(Auth, 'useAuth').mockReturnValue({ authLoading: true });
    render(
      <MemoryRouter>
        <AdminRoute>
          <div>admin area</div>
        </AdminRoute>
      </MemoryRouter>
    );
    expect(screen.getByText(/Loading session/i)).toBeTruthy();
  });

  test('renders children for admin user', () => {
    jest.spyOn(Auth, 'useAuth').mockReturnValue({ user: { role: 'ADMIN' } });
    render(
      <MemoryRouter>
        <AdminRoute>
          <div>admin area</div>
        </AdminRoute>
      </MemoryRouter>
    );
    expect(screen.getByText(/admin area/i)).toBeTruthy();
  });

  test('does not render children for non-admin user', () => {
    jest.spyOn(Auth, 'useAuth').mockReturnValue({ user: { role: 'USER' } });
    render(
      <MemoryRouter>
        <AdminRoute>
          <div>admin area</div>
        </AdminRoute>
      </MemoryRouter>
    );
    expect(screen.queryByText(/admin area/i)).toBeNull();
  });
});

describe('CoachRoute component', () => {
  afterEach(() => jest.restoreAllMocks());

  test('shows loading when authLoading true', () => {
    jest.spyOn(Auth, 'useAuth').mockReturnValue({ authLoading: true });
    render(
      <MemoryRouter>
        <CoachRoute>
          <div>coach area</div>
        </CoachRoute>
      </MemoryRouter>
    );
    expect(screen.getByText(/Loading session/i)).toBeTruthy();
  });

  test('renders children for coach or admin user', () => {
    jest.spyOn(Auth, 'useAuth').mockReturnValue({ user: { role: 'COACH' } });
    const { rerender } = render(
      <MemoryRouter>
        <CoachRoute>
          <div>coach area</div>
        </CoachRoute>
      </MemoryRouter>
    );
    expect(screen.getByText(/coach area/i)).toBeTruthy();

    // admin also allowed
    jest.spyOn(Auth, 'useAuth').mockReturnValue({ user: { role: 'ADMIN' } });
    rerender(
      <MemoryRouter>
        <CoachRoute>
          <div>coach area</div>
        </CoachRoute>
      </MemoryRouter>
    );
    expect(screen.getByText(/coach area/i)).toBeTruthy();
  });

  test('does not render children for normal user', () => {
    jest.spyOn(Auth, 'useAuth').mockReturnValue({ user: { role: 'USER' } });
    render(
      <MemoryRouter>
        <CoachRoute>
          <div>coach area</div>
        </CoachRoute>
      </MemoryRouter>
    );
    expect(screen.queryByText(/coach area/i)).toBeNull();
  });
});

describe('ProtectedRoute component', () => {
  afterEach(() => jest.restoreAllMocks());

  test('renders outlet when user is present', () => {
    jest.spyOn(Auth, 'useAuth').mockReturnValue({ user: { id: 1 } });
    render(
      <MemoryRouter initialEntries={["/protected"]}>
        <Routes>
          <Route path="/protected" element={<ProtectedRoute />}>
            <Route index element={<div>secret</div>} />
          </Route>
        </Routes>
      </MemoryRouter>
    );
    expect(screen.getByText(/secret/i)).toBeTruthy();
  });

  test('does not render outlet when user is absent', () => {
    jest.spyOn(Auth, 'useAuth').mockReturnValue({ user: null });
    render(
      <MemoryRouter initialEntries={["/protected"]}>
        <Routes>
          <Route path="/protected" element={<ProtectedRoute />}>
            <Route index element={<div>secret</div>} />
          </Route>
        </Routes>
      </MemoryRouter>
    );
    expect(screen.queryByText(/secret/i)).toBeNull();
  });
});
