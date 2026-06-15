import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { MemoryRouter } from 'react-router-dom';
import UserWorkoutsPage from '../../pages/UserWorkoutsPage';
import { appFetch, getServiceToken } from '../../backend/appFetch';

jest.mock('../../backend/appFetch');

describe('UserWorkoutsPage', () => {
  const mockWorkouts = [
    { id: 1, routineName: 'Morning Run', fecha: new Date().toISOString(), startTime: new Date(), endTime: new Date() }
  ];

  beforeEach(() => {
    getServiceToken.mockReturnValue('fake-token');
    
    appFetch.mockImplementation((url, config, onSuccess) => onSuccess(mockWorkouts));
  });

  test('renders list view by default', async () => {
    render(<MemoryRouter><UserWorkoutsPage /></MemoryRouter>);
    
    await waitFor(() => expect(screen.getByText(/Morning Run/)).toBeInTheDocument());
    expect(screen.getByText('List View')).toHaveClass('btn-primary');
  });

  test('switches to calendar view', async () => {
    render(<MemoryRouter><UserWorkoutsPage /></MemoryRouter>);
    
    await waitFor(() => expect(screen.getByText(/Morning Run/)).toBeInTheDocument());

    const calBtn = screen.getByText('Calendar View');
    fireEvent.click(calBtn);

    expect(screen.getByText('Sun')).toBeInTheDocument(); 
  });
});