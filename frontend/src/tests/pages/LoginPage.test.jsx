import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { MemoryRouter } from 'react-router-dom';
import LoginPage from '../../pages/LoginPage';
import * as AuthContext from '../../context/AuthContext';

jest.mock('../../context/AuthContext', () => ({
  useAuth: jest.fn(),
}));

describe('LoginPage', () => {
  const mockLogin = jest.fn();

  beforeEach(() => {
    AuthContext.useAuth.mockReturnValue({
      login: mockLogin,
    });
  });

  afterEach(() => jest.clearAllMocks());

  test('renders login form', () => {
    render(<MemoryRouter><LoginPage /></MemoryRouter>);
    expect(screen.getByLabelText(/Username/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Password/i, { selector: 'input' })).toBeInTheDocument();
  });

  test('calls login function with credentials', async () => {
    render(<MemoryRouter><LoginPage /></MemoryRouter>);

    fireEvent.change(screen.getByLabelText(/Username/i), { target: { value: 'testuser' } });
    fireEvent.change(screen.getByLabelText(/Password/i, { selector: 'input' }), { target: { value: '123456' } });
    
    const btn = screen.getByRole('button', { name: /Sign in/i });
    fireEvent.click(btn);

    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledWith('testuser', '123456');
    });
  });

  test('displays error message on failure', async () => {
    mockLogin.mockRejectedValue(new Error('Invalid credentials'));
    render(<MemoryRouter><LoginPage /></MemoryRouter>);

    fireEvent.change(screen.getByLabelText(/Username/i), { target: { value: 'wrong' } });
    fireEvent.change(screen.getByLabelText(/Password/i, { selector: 'input' }), { target: { value: 'wrong' } });
    
    fireEvent.click(screen.getByRole('button', { name: /Sign in/i }));

    await waitFor(() => {
      expect(screen.getByText(/Invalid credentials/i)).toBeInTheDocument();
    });
  });
});