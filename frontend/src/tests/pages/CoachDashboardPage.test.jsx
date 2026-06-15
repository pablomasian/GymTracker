import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { MemoryRouter } from 'react-router-dom';
import CoachDashboardPage from '../../pages/CoachDashboardPage';
import workoutService from '../../backend/workoutService';

jest.mock('../../backend/workoutService');

describe('CoachDashboardPage', () => {
  test('renders loading state', () => {
    workoutService.getCoachDashboard.mockReturnValue(new Promise(() => {}));
    render(<MemoryRouter><CoachDashboardPage /></MemoryRouter>);
    expect(screen.getByText(/Loading Dashboard.../i)).toBeInTheDocument();
  });

  test('renders workouts list successfully', async () => {
    const mockData = [
      { id: 100, userId: 1, userName: 'Alice', routineId: 5, routineName: 'Leg Day', fecha: '2023-01-01T10:00:00' }
    ];
    
    workoutService.getCoachDashboard.mockImplementation((onSuccess) => {
        onSuccess(mockData);
        return Promise.resolve();
    });

    render(<MemoryRouter><CoachDashboardPage /></MemoryRouter>);

    await waitFor(() => {
      expect(screen.getByText('Alice')).toBeInTheDocument();
      expect(screen.getByText('Leg Day')).toBeInTheDocument();
    });
  });

  test('handles error state', async () => {
    workoutService.getCoachDashboard.mockImplementation((_, onError) => {
        onError({ globalError: 'Failed to fetch' });
        return Promise.resolve();
    });

    render(<MemoryRouter><CoachDashboardPage /></MemoryRouter>);

    await waitFor(() => {
      expect(screen.getByText(/Failed to fetch/i)).toBeInTheDocument();
    });
  });
});