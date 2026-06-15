import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { MemoryRouter } from 'react-router-dom';
import WithSidebar from '../components/WithSidebar'; 
import * as AuthContext from '../context/AuthContext'; 

jest.mock('../context/AuthContext', () => ({ useAuth: jest.fn() }));
jest.mock('../hooks/useNotificationCount', () => ({ useNotificationCount: () => ({ unreadCount: 0 }) }));

describe('WithSidebar', () => {
    test('renders children and sidebar', () => {
        AuthContext.useAuth.mockReturnValue({ user: { username: 'User' } });
        
        render(
            <MemoryRouter>
                <WithSidebar>
                    <h1>Content</h1>
                </WithSidebar>
            </MemoryRouter>
        );

        expect(screen.getByText('Content')).toBeInTheDocument();
        expect(screen.getByText('GymTracker')).toBeInTheDocument(); 
    });
});