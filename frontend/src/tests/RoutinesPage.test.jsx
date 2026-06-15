import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import * as Auth from '../context/AuthContext';

// Mock routineService used inside the page
jest.mock('../backend/routineService', () => ({
  getMyRoutines: jest.fn(),
  deleteRoutine: jest.fn(),
}));

import RoutinesPage from '../pages/RoutinesPage';
import { getMyRoutines } from '../backend/routineService';

describe('RoutinesPage', () => {
  afterEach(() => {
    jest.restoreAllMocks();
    jest.clearAllMocks();
    delete global.fetch;
  });

  test('loads and displays routines from fetch (all view)', async () => {
    const fake = [
      { id: 1, name: 'Routine A', exerciseCount: 2, coachNombreUsuario: 'coach1' },
    ];

    global.fetch = jest.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(fake),
    });

    jest.spyOn(Auth, 'useAuth').mockReturnValue({ user: { id: 5, role: 'USER' } });

    render(
      <MemoryRouter>
        <RoutinesPage />
      </MemoryRouter>
    );

    // wait for the routine name to appear
    await waitFor(() => expect(screen.getByText(/Routine A/)).toBeInTheDocument());
    expect(screen.getByText(/2 ex\./)).toBeInTheDocument();
  });

  test('when coach clicks My Routines it calls getMyRoutines and shows items', async () => {
    const myFake = [{ id: 2, name: 'Coach Routine', exerciseCount: 3, coachNombreUsuario: 'me' }];
    getMyRoutines.mockImplementation((onSuccess) => {
      onSuccess(myFake);
      return { finally: (f) => f && f() };
    });

    jest.spyOn(Auth, 'useAuth').mockReturnValue({ user: { id: 7, role: 'COACH' } });

    render(
      <MemoryRouter>
        <RoutinesPage />
      </MemoryRouter>
    );

    // find My Routines button and click it
    const myBtn = await screen.findByRole('button', { name: /My Routines/i });
    fireEvent.click(myBtn);

    await waitFor(() => expect(getMyRoutines).toHaveBeenCalled());
    expect(screen.getByText(/Coach Routine/)).toBeInTheDocument();
  });
});
