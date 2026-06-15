import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { MemoryRouter } from 'react-router-dom';
import ViewProfile from '../../pages/ViewProfile';
import * as AuthContext from '../../context/AuthContext';
import * as userService from '../../backend/userService';
import * as appFetchModule from '../../backend/appFetch';

jest.mock('../../context/AuthContext');
jest.mock('../../backend/userService');
jest.mock('../../backend/appFetch');

describe('ViewProfile Page', () => {
  const mockUser = {
    id: 1,
    username: 'user1',
    nombreUsuario: 'User One',
    email: 'u@test.com',
    role: 'USER',
    peso: 80, 
    altura: 180,
    weight: 80 
  };

  const mockBadges = [
    { id: 1, type: 'HUNDRED', description: '100 Workouts!' },
    { id: 2, type: 'VOLUME_KING', description: '200 reps badge' },
    { id: 3, type: 'EARLY_BIRD', description: 'Early session badge' },
    { id: 4, type: 'FIFTY_WORKOUTS', description: '50 workouts badge' },
    { id: 5, type: 'CONSISTENCY_CHAMPION', description: '7-day streak badge' },
  ];

  const mockUpdateUser = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
    AuthContext.useAuth.mockReturnValue({ 
      user: mockUser, 
      updateUser: mockUpdateUser 
    });
    appFetchModule.appFetch.mockImplementation((url, config, onSuccess) => {
      if (url === '/users/badges') {
        onSuccess(mockBadges);
      }
    });
  });

  test('renders user info', async () => {
    render(<MemoryRouter><ViewProfile /></MemoryRouter>);
    expect(screen.getByText('User One')).toBeInTheDocument();
    expect(screen.getByText(/80 kg/)).toBeInTheDocument();
    await waitFor(() => {
      expect(screen.getByTitle('100 Workouts!')).toBeInTheDocument();
    });
  });

  test('toggles edit mode and saves changes', async () => {
    userService.updateProfile.mockImplementation((payload, onSuccess) => {
      onSuccess({ ...mockUser, nombreUsuario: payload.nombreUsuario, email: payload.email });
      return Promise.resolve();
    });

    userService.updateFitnessData.mockImplementation((id, data, onSuccess) => {
      onSuccess();
    });

    render(<MemoryRouter><ViewProfile /></MemoryRouter>);

    fireEvent.click(screen.getByText('Edit'));

    const weightInput = screen.getByPlaceholderText('Weight');
    fireEvent.change(weightInput, { target: { value: '85' } });

    const saveBtn = screen.getByText('Save');
    fireEvent.click(saveBtn);

    await waitFor(() => {
      expect(userService.updateProfile).toHaveBeenCalledTimes(1);
      expect(userService.updateFitnessData).toHaveBeenCalledTimes(1);
      expect(screen.getByText(/Profile updated successfully/i)).toBeInTheDocument();
      expect(mockUpdateUser).toHaveBeenCalled();
    });
  });

  test('renders badges correctly', async () => {
    render(<MemoryRouter><ViewProfile /></MemoryRouter>);
    await waitFor(() => {
      expect(screen.getByTitle('100 Workouts!')).toBeInTheDocument();
      expect(screen.getByTitle('200 reps badge')).toBeInTheDocument();
      expect(screen.getByTitle('Early session badge')).toBeInTheDocument();
      expect(screen.getByTitle('50 workouts badge')).toBeInTheDocument();
      expect(screen.getByTitle('7-day streak badge')).toBeInTheDocument();
    });

    expect(screen.getByTitle('100 Workouts!').textContent).toBe('weight');
    expect(screen.getByTitle('200 reps badge').textContent).toBe('bolt');
    expect(screen.getByTitle('Early session badge').textContent).toBe('sunrise');
    expect(screen.getByTitle('50 workouts badge').textContent).toBe('verified');
    expect(screen.getByTitle('7-day streak badge').textContent).toBe('calendar_month');
  });

});
