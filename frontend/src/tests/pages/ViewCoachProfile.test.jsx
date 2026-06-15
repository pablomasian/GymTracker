import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import ViewCoachProfile from '../../pages/ViewCoachProfile';
import { appFetch } from '../../backend/appFetch';
import * as AuthContext from '../../context/AuthContext';

jest.mock('../../backend/appFetch');
jest.mock('../../context/AuthContext', () => ({ useAuth: jest.fn() }));

describe('ViewCoachProfile', () => {
    const mockCoach = { id: 5, username: 'coachX', nombreUsuario: 'Coach Xavier' };
    const mockRoutines = [{ id: 1, name: 'Hardcore Abs', exerciseCount: 5 }];

    beforeEach(() => {
        AuthContext.useAuth.mockReturnValue({ user: { id: 1 } });
        appFetch.mockImplementation((url, config, onSuccess) => {
            if (url.includes('coach-profile/5/following')) onSuccess(false);
            else if (url.includes('coach-profile/5')) onSuccess(mockCoach);
            else if (url.includes('routines/display_by_coach')) onSuccess(mockRoutines);
        });
    });

    test('renders coach info and routines', async () => {
        render(
            <MemoryRouter initialEntries={['/coach/5']}>
                <Routes>
                    <Route path="/coach/:id" element={<ViewCoachProfile />} />
                </Routes>
            </MemoryRouter>
        );

        await waitFor(() => expect(screen.getByText('Coach Xavier')).toBeInTheDocument());
        expect(screen.getByText('Hardcore Abs')).toBeInTheDocument();
        expect(screen.getByText('Follow')).toBeInTheDocument();
    });
});