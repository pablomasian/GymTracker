import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import LogWorkoutPage from '../../pages/LogWorkoutPage';
import { appFetch } from '../../backend/appFetch';
import workoutService from '../../backend/workoutService';

jest.mock('../../backend/appFetch');
jest.mock('../../backend/workoutService');

describe('LogWorkoutPage', () => {
  const mockRoutine = { id: 1, name: 'Chest Day' };
  const mockExercises = [
    { exerciseId: 10, name: 'Bench Press', muscles: 'Chest', sets: 1, repetitions: 10 }
  ];

  beforeEach(() => {
    appFetch.mockImplementation((url, config, onSuccess) => {
        if (url.includes('/routines/1/exercises')) onSuccess(mockExercises);
        else if (url.includes('/routines/1')) onSuccess(mockRoutine);
    });
  });

  afterEach(() => jest.clearAllMocks());

  test('renders routine info and exercises', async () => {
    render(
      <MemoryRouter initialEntries={['/log-workout/1']}>
        <Routes>
          <Route path="/log-workout/:id" element={<LogWorkoutPage />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => expect(screen.getByText(/Log Workout: Chest Day/i)).toBeInTheDocument());
    expect(screen.getByText('Bench Press')).toBeInTheDocument();
  });

  test('allows adding and removing sets', async () => {
    render(
      <MemoryRouter initialEntries={['/log-workout/1']}>
        <Routes>
            <Route path="/log-workout/:id" element={<LogWorkoutPage />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => expect(screen.getByText(/Set 1/i)).toBeInTheDocument());

    const addBtn = screen.getByText('+ Add Set');
    fireEvent.click(addBtn);
    expect(screen.getByText(/Set 2/i)).toBeInTheDocument();

    const deleteBtns = screen.getAllByText('Delete');
    fireEvent.click(deleteBtns[1]); 
    expect(screen.queryByText(/Set 2/i)).not.toBeInTheDocument();
  });

  test('submits workout successfully', async () => {
    workoutService.logWorkout.mockImplementation((payload, onSuccess) => onSuccess());

    render(
      <MemoryRouter initialEntries={['/log-workout/1']}>
         <Routes>
            <Route path="/log-workout/:id" element={<LogWorkoutPage />} />
            <Route path="/my-workouts" element={<div>My Workouts Page</div>} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => screen.getByText('Bench Press'));

    const inputs = screen.getAllByPlaceholderText('Reps');
    fireEvent.change(inputs[0], { target: { value: '12' } });

    const finishBtn = screen.getByText('Finish Workout');
    fireEvent.click(finishBtn);

    await waitFor(() => {
      expect(workoutService.logWorkout).toHaveBeenCalled();
    });
  });
});