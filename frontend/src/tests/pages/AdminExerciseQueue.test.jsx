import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import { MemoryRouter } from 'react-router-dom';
import AdminExerciseQueue from '../../pages/AdminExerciseQueue';
import { exerciseService } from '../../backend/exerciseService';

jest.mock('../../backend/exerciseService');

describe('AdminExerciseQueue Page', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  test('renders loading state initially', () => {
    exerciseService.getPending.mockImplementation(() => {}); 
    render(<AdminExerciseQueue />);
    expect(screen.getByText(/Loading pending exercises.../i)).toBeInTheDocument();
  });

  test('renders empty state when no exercises', async () => {
    exerciseService.getPending.mockImplementation((onSuccess) => onSuccess([]));
    render(<AdminExerciseQueue />);
    
    await waitFor(() => {
      expect(screen.getByText(/No pending exercises/i)).toBeInTheDocument();
    });
  });

  test('renders exercises and handles actions (Accept, Dismiss, Block)', async () => {
    const mockExercises = [
      { id: 1, name: 'Unique Push Up', muscles: 'Chest', equipment: 'None', description: 'Basic movement' }
    ];

    exerciseService.getPending.mockImplementation((onSuccess) => onSuccess(mockExercises));
    exerciseService.accept.mockImplementation((id, onSuccess) => onSuccess());
    exerciseService.dismiss.mockImplementation((id, onSuccess) => onSuccess());
    exerciseService.block.mockImplementation((id, onSuccess) => onSuccess());

    render(<AdminExerciseQueue />);

    await waitFor(() => {
      expect(screen.getByText(/Unique Push Up/i)).toBeInTheDocument();
    });

    const acceptBtn = screen.getByRole('button', { name: /Accept/i });
    fireEvent.click(acceptBtn);
    expect(exerciseService.accept).toHaveBeenCalledWith(1, expect.any(Function), expect.any(Function));

  });
  
  test('handles Dismiss action', async () => {
     const mockExercises = [{ id: 1, name: 'Unique Push Up', muscles: 'Chest', equipment: 'None', description: 'Desc' }];
     exerciseService.getPending.mockImplementation((onSuccess) => onSuccess(mockExercises));
     exerciseService.dismiss.mockImplementation((id, onSuccess) => onSuccess());

     render(<AdminExerciseQueue />);
     await waitFor(() => expect(screen.getByText(/Unique Push Up/i)).toBeInTheDocument());

     const dismissBtn = screen.getByRole('button', { name: /Dismiss/i });
     fireEvent.click(dismissBtn);
     expect(exerciseService.dismiss).toHaveBeenCalledWith(1, expect.any(Function), expect.any(Function));
  });

  test('handles Block action', async () => {
     const mockExercises = [{ id: 1, name: 'Unique Push Up', muscles: 'Chest', equipment: 'None', description: 'Desc' }];
     exerciseService.getPending.mockImplementation((onSuccess) => onSuccess(mockExercises));
     exerciseService.block.mockImplementation((id, onSuccess) => onSuccess());

     render(<AdminExerciseQueue />);
     await waitFor(() => expect(screen.getByText(/Unique Push Up/i)).toBeInTheDocument());

     const blockBtn = screen.getByRole('button', { name: /Block/i });
     fireEvent.click(blockBtn);
     expect(exerciseService.block).toHaveBeenCalledWith(1, expect.any(Function), expect.any(Function));
  });

  test('handles fetch error', async () => {
    exerciseService.getPending.mockImplementation((_, onError) => onError('Network Error'));
    render(<AdminExerciseQueue />);
    
    await waitFor(() => {
      expect(screen.getByText('Network Error')).toBeInTheDocument();
    });
  });
});