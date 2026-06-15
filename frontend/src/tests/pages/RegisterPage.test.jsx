import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom'; 
import { MemoryRouter } from 'react-router-dom';
import RegisterPage from '../../pages/RegisterPage';
import * as AuthContext from '../../context/AuthContext';

jest.mock('../../context/AuthContext', () => ({
  useAuth: jest.fn(),
}));

describe('RegisterPage', () => {
  const mockRegister = jest.fn();

  beforeEach(() => {
    AuthContext.useAuth.mockReturnValue({
      register: mockRegister,
    });
  });

  test('validates password match', () => {
    render(<MemoryRouter><RegisterPage /></MemoryRouter>);

    fireEvent.change(screen.getByLabelText('Password'), { target: { value: '123456' } });
    fireEvent.change(screen.getByLabelText('Confirm password'), { target: { value: '654321' } });

    expect(screen.getByText('Passwords do not match')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Create account' })).toBeDisabled();
  });

  test('submits valid form', async () => {
    render(<MemoryRouter><RegisterPage /></MemoryRouter>);

    fireEvent.change(screen.getByLabelText('Full name'), { target: { value: 'John Doe' } });
    fireEvent.change(screen.getByLabelText('Username'), { target: { value: 'john' } });
    fireEvent.change(screen.getByLabelText('Password'), { target: { value: '123456' } });
    fireEvent.change(screen.getByLabelText('Confirm password'), { target: { value: '123456' } });

    const submitBtn = screen.getByRole('button', { name: 'Create account' });
    expect(submitBtn).not.toBeDisabled();

    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(mockRegister).toHaveBeenCalled();
    });
  });
  
  test('toggles coach checkbox', () => {
    render(<MemoryRouter><RegisterPage /></MemoryRouter>);

    const coachCheckbox = screen.getByLabelText('Coach');
    expect(coachCheckbox).not.toBeChecked();
    fireEvent.click(coachCheckbox);
    expect(coachCheckbox).toBeChecked();
    // Premium field assertion removed: feature no longer rendered here.
  });
});