import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { MemoryRouter } from 'react-router-dom';
import RoutineEditorPage from '../../pages/RoutineEditorPage';
import { appFetch, fetchConfig } from '../../backend/appFetch';
import * as AuthContext from '../../context/AuthContext';

jest.mock('../../backend/appFetch', () => {
  return {
    appFetch: jest.fn(),
    fetchConfig: jest.fn().mockReturnValue({ method: 'POST' }),
    config: { BASE_PATH: '' }
  };
});

jest.mock('../../context/AuthContext', () => ({
  useAuth: jest.fn(),
}));

describe('RoutineEditorPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    AuthContext.useAuth.mockReturnValue({ user: { id: 10, role: 'COACH' } });

    global.fetch = jest.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve([
        { id: 100, name: 'Bench Press', muscle: 'Chest' },
        { id: 101, name: 'Squat', muscle: 'Legs' }
      ])
    });

    fetchConfig.mockReturnValue({ method: 'POST' });
  });

  test('renders form and loads catalog', async () => {
    render(<MemoryRouter><RoutineEditorPage /></MemoryRouter>);

    expect(screen.getByText('Create Routine', { selector: '.card-title' })).toBeInTheDocument();

    await waitFor(() => expect(screen.getByText('Bench Press')).toBeInTheDocument());
  });

  test('adds exercise and submits routine', async () => {
    appFetch.mockImplementation((url, config, onSuccess) => {
      if (onSuccess) onSuccess({ id: 99 });
    });

    render(<MemoryRouter><RoutineEditorPage /></MemoryRouter>);

    fireEvent.change(screen.getByLabelText('Name'), { target: { value: 'My New Routine' } });

    await waitFor(() => screen.getByText('Bench Press'));

    const addBtns = screen.getAllByRole('button', { name: 'Add' });
    fireEvent.click(addBtns[0]);

    await waitFor(() => {
      const elements = screen.getAllByText(/Bench Press/);
      expect(elements.length).toBeGreaterThan(1);
    });

    const createBtn = screen.getByRole('button', { name: 'Create Routine' });

    expect(createBtn).not.toBeDisabled();
    fireEvent.click(createBtn);

    await waitFor(() => {
      expect(appFetch).toHaveBeenCalledWith(
        '/routines',
        expect.objectContaining({ method: 'POST' }),
        expect.any(Function),
        expect.any(Function)
      );
      expect(screen.getByText(/Your routine has been created successfully!/i)).toBeInTheDocument();
    });
  });
});