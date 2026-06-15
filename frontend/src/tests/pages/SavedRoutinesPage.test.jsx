import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { MemoryRouter } from 'react-router-dom';
import SavedRoutinesPage from '../../pages/SavedRoutinesPage';
import { appFetch } from '../../backend/appFetch';

jest.mock('../../backend/appFetch');

describe('SavedRoutinesPage', () => {
  test('renders saved routines', async () => {
    const mockSaved = [
      { id: 1, routineId: 10, name: 'My Favorite Routine', coachName: 'BestCoach' }
    ];

    appFetch.mockImplementation((url, config, onSuccess) => onSuccess(mockSaved));

    render(<MemoryRouter><SavedRoutinesPage /></MemoryRouter>);

    await waitFor(() => expect(screen.getByText('My Favorite Routine')).toBeInTheDocument());
    expect(screen.getByText(/BestCoach/)).toBeInTheDocument();
  });

  test('renders empty state', async () => {
    appFetch.mockImplementation((url, config, onSuccess) => onSuccess([]));

    render(<MemoryRouter><SavedRoutinesPage /></MemoryRouter>);

    await waitFor(() => expect(screen.getByText('No saved routines yet')).toBeInTheDocument());
  });
});