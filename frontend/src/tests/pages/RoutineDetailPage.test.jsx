import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import RoutineDetailPage from '../../pages/RoutineDetailPage';
import { appFetch } from '../../backend/appFetch';
import * as AuthContext from '../../context/AuthContext';

jest.mock('../../backend/appFetch');
jest.mock('../../context/AuthContext', () => ({
  useAuth: jest.fn(),
}));

describe('RoutineDetailPage', () => {
  const mockRoutine = {
    id: 1,
    name: 'Super Strength',
    coachId: 10,
    coachNombreUsuario: 'Coach Mike',
    visible: true
  };
  
  const mockExercises = [
    { id: 1, name: 'Squat', muscles: 'Legs', sets: 3, repetitions: 10, weight: 100 }
  ];

  beforeEach(() => {
    jest.clearAllMocks();
    AuthContext.useAuth.mockReturnValue({ user: { id: 5, role: 'USER' } });

    appFetch.mockImplementation((url, config, onSuccess) => {
      if (url === '/routines/1') {
        onSuccess(mockRoutine);
      } else if (url === '/routines/1/exercises') {
        onSuccess(mockExercises);
      } else if (url.includes('/saved-routines')) {
        onSuccess();
      }
    });
  });

  test('renders routine details and exercises', async () => {
    render(
      <MemoryRouter initialEntries={['/routines/1']}>
        <Routes>
          <Route path="/routines/:id" element={<RoutineDetailPage />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => expect(screen.getByText('Super Strength')).toBeInTheDocument());
    expect(screen.getByText(/Coach Mike/)).toBeInTheDocument();
    expect(screen.getByText(/Squat/)).toBeInTheDocument();
  });

  test('allows saving a routine', async () => {
    render(
      <MemoryRouter initialEntries={['/routines/1']}>
        <Routes>
          <Route path="/routines/:id" element={<RoutineDetailPage />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText('Super Strength'));
    
    const saveBtn = screen.getByText('Save routine');
    fireEvent.click(saveBtn);
    
    await waitFor(() => expect(screen.getByText('Unsave')).toBeInTheDocument());
  });
});