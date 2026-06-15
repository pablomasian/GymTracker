import React from 'react';
import { render, screen, waitFor, act } from '@testing-library/react';
import '@testing-library/jest-dom';
import { AuthProvider, useAuth } from '../../context/AuthContext';
import { userService } from '../../backend';

jest.mock('../../backend/userService', () => ({
    login: jest.fn(),
    signUp: jest.fn(),
    tryLoginFromServiceToken: jest.fn(),
}));

const TestConsumer = () => {
    const { user, login, register, authLoading } = useAuth();
    if (authLoading) return <div>Loading...</div>;
    return (
        <div>
            <div data-testid="user-name">{user ? user.username : 'Guest'}</div>
            <button onClick={() => login('test', 'pass')}>Login</button>
            <button onClick={() => register({ username: 'new' })}>Register</button>
        </div>
    );
};

describe('AuthContext', () => {
    afterEach(() => jest.clearAllMocks());

    test('initializes and checks token', async () => {
        userService.tryLoginFromServiceToken.mockImplementation((onSuccess) => {
            onSuccess({ user: { username: 'ExistingUser' }, serviceToken: 'abc' });
        });

        render(
            <AuthProvider>
                <TestConsumer />
            </AuthProvider>
        );

        await waitFor(() => expect(screen.getByTestId('user-name')).toHaveTextContent('ExistingUser'));
    });

    test('login updates user state', async () => {
        userService.tryLoginFromServiceToken.mockImplementation((_, onError) => onError());
        userService.login.mockImplementation((u, p, onSuccess) => {
            onSuccess({ user: { username: 'LoggedUser' }, serviceToken: 'xyz' });
        });

        render(
            <AuthProvider>
                <TestConsumer />
            </AuthProvider>
        );

        await waitFor(() => expect(screen.getByTestId('user-name')).toHaveTextContent('Guest'));

        const btn = screen.getByText('Login');
        await act(async () => {
            btn.click();
        });

        await waitFor(() => expect(screen.getByTestId('user-name')).toHaveTextContent('LoggedUser'));
    });
});