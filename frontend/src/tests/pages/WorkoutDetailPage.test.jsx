import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import WorkoutDetailPage from '../../pages/WorkoutDetailPage';
import { appFetch, getServiceToken } from '../../backend/appFetch';

jest.mock('../../backend/appFetch');

describe('WorkoutDetailPage', () => {
  const mockSets = [
    { exerciseId: 1, exerciseName: 'Squat', numeroSerie: 1, repeticiones: 10, peso: 50 }
  ];

  beforeEach(() => {
    getServiceToken.mockReturnValue('token-dummy');
    appFetch.mockImplementation((url, config, onSuccess) => onSuccess(mockSets));
  });

  test('renders workout sets', async () => {
    render(
      <MemoryRouter initialEntries={['/workouts/100']}>
        <Routes>
            <Route path="/workouts/:id" element={<WorkoutDetailPage />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => expect(screen.getByText('Workout Details')).toBeInTheDocument());
    expect(screen.getByText('Squat')).toBeInTheDocument();
    expect(screen.getByText('50')).toBeInTheDocument(); 
  });
});