import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { MemoryRouter } from 'react-router-dom';
import CreateExercisePage from '../../pages/CreateExercisePage';
import { createExercise } from '../../backend/exerciseService';

jest.mock('../../backend/exerciseService');

describe('CreateExercisePage', () => {
  test('renders form inputs', () => {
    render(<MemoryRouter><CreateExercisePage /></MemoryRouter>);
    expect(screen.getByLabelText(/Exercise Name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Main Muscles Worked/i)).toBeInTheDocument();
  });

  test('submits form successfully', async () => {
    createExercise.mockImplementation((data, onSuccess) => onSuccess());

    render(<MemoryRouter><CreateExercisePage /></MemoryRouter>);

    fireEvent.change(screen.getByLabelText(/Exercise Name/i), { target: { value: 'Burpee' } });
    
    const musclesInput = screen.getByPlaceholderText(/e.g., Shoulders/i); 
    fireEvent.change(musclesInput, { target: { value: 'Full Body' } });
    
    const submitBtn = screen.getByRole('button', { name: /Submit Proposal/i });
    expect(submitBtn).not.toBeDisabled();
    
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(screen.getByText(/Proposal submitted/i)).toBeInTheDocument();
    });
  });

  test('handles submission error', async () => {
    createExercise.mockImplementation((data, _, onError) => onError({ globalError: 'Duplicate exercise' }));

    render(<MemoryRouter><CreateExercisePage /></MemoryRouter>);

    fireEvent.change(screen.getByLabelText(/Exercise Name/i), { target: { value: 'Burpee' } });
    fireEvent.click(screen.getByRole('button', { name: /Submit Proposal/i }));

    await waitFor(() => {
      expect(screen.getByText(/Duplicate exercise/i)).toBeInTheDocument();
    });
  });
});