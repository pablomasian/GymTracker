import workoutService from '../../backend/workoutService';
import * as appFetchModule from '../../backend/appFetch';

// Mock appFetch and fetchConfig
jest.mock('../../backend/appFetch', () => ({
    appFetch: jest.fn(),
    fetchConfig: jest.fn((method, body) => ({ method, body }))
}));

describe('WorkoutService - Exercise Progress', () => {
    let appFetch;
    let fetchConfig;

    beforeEach(() => {
        jest.clearAllMocks();
        appFetch = appFetchModule.appFetch;
        fetchConfig = appFetchModule.fetchConfig;
        
        // Configurar fetchConfig para que devuelva un objeto de configuración
        fetchConfig.mockImplementation((method, body) => ({ method, body }));
    });

    describe('getExerciseProgress', () => {
        it('should fetch exercise progress successfully', (done) => {
            const mockProgress = [
                {
                    fecha: '2025-11-22T10:00:00',
                    maxWeight: 60.0,
                    totalReps: 10,
                    totalSets: 2
                },
                {
                    fecha: '2025-11-27T10:00:00',
                    maxWeight: 70.0,
                    totalReps: 12,
                    totalSets: 3
                },
                {
                    fecha: '2025-12-01T10:00:00',
                    maxWeight: 75.0,
                    totalReps: 15,
                    totalSets: 3
                }
            ];

            appFetch.mockImplementation((url, config, onSuccess) => {
                onSuccess(mockProgress);
            });

            workoutService.getExerciseProgress(1, (result) => {
                expect(result).toEqual(mockProgress);
                expect(result.length).toBe(3);
                expect(appFetch).toHaveBeenCalled();
                done();
            });
        });

        it('should return empty array when no progress exists', (done) => {
            appFetch.mockImplementation((url, config, onSuccess) => {
                onSuccess([]);
            });

            workoutService.getExerciseProgress(1, (result) => {
                expect(result).toEqual([]);
                expect(result.length).toBe(0);
                done();
            });
        });

        it('should handle API errors', (done) => {
            const errorMessage = 'Exercise not found';
            appFetch.mockImplementation((url, config, onSuccess, onErrors) => {
                onErrors(errorMessage);
            });

            workoutService.getExerciseProgress(99999, null, (error) => {
                expect(error).toBe(errorMessage);
                done();
            });
        });

        it('should handle network errors', (done) => {
            appFetch.mockImplementation((url, config, onSuccess, onErrors) => {
                onErrors('Network error');
            });

            workoutService.getExerciseProgress(1, null, (error) => {
                expect(error).toBe('Network error');
                done();
            });
        });
    });

    describe('getExercisesWithWeight', () => {
        it('should fetch exercises with weight successfully', (done) => {
            const mockExercises = [
                {
                    exerciseId: 1,
                    exerciseName: 'Bench Press',
                    totalSets: 15,
                    totalReps: 150
                },
                {
                    exerciseId: 2,
                    exerciseName: 'Squat',
                    totalSets: 12,
                    totalReps: 120
                },
                {
                    exerciseId: 3,
                    exerciseName: 'Deadlift',
                    totalSets: 10,
                    totalReps: 90
                }
            ];

            appFetch.mockImplementation((url, config, onSuccess) => {
                onSuccess(mockExercises);
            });

            workoutService.getExercisesWithWeight((result) => {
                expect(result).toEqual(mockExercises);
                expect(result.length).toBe(3);
                expect(appFetch).toHaveBeenCalled();
                done();
            });
        });

        it('should return empty array when no exercises with weight exist', (done) => {
            appFetch.mockImplementation((url, config, onSuccess) => {
                onSuccess([]);
            });

            workoutService.getExercisesWithWeight((result) => {
                expect(result).toEqual([]);
                expect(result.length).toBe(0);
                done();
            });
        });

        it('should handle API errors', (done) => {
            const errorMessage = 'User not found';
            appFetch.mockImplementation((url, config, onSuccess, onErrors) => {
                onErrors(errorMessage);
            });

            workoutService.getExercisesWithWeight(null, (error) => {
                expect(error).toBe(errorMessage);
                done();
            });
        });

        it('should return only exercises with weight data', (done) => {
            const mockExercises = [
                {
                    exerciseId: 1,
                    exerciseName: 'Bench Press',
                    totalSets: 15,
                    totalReps: 150
                },
                {
                    exerciseId: 2,
                    exerciseName: 'Squat',
                    totalSets: 12,
                    totalReps: 120
                }
            ];

            appFetch.mockImplementation((url, config, onSuccess) => {
                onSuccess(mockExercises);
            });

            workoutService.getExercisesWithWeight((result) => {
                // Verificar que no hay ejercicios sin peso como 'Pull-up' o 'Plank'
                expect(result.every(ex => ex.totalSets > 0)).toBe(true);
                expect(result.every(ex => ex.totalReps > 0)).toBe(true);
                done();
            });
        });

        it('should handle exercises sorted by total sets', (done) => {
            const mockExercises = [
                {
                    exerciseId: 1,
                    exerciseName: 'Bench Press',
                    totalSets: 15,
                    totalReps: 150
                },
                {
                    exerciseId: 2,
                    exerciseName: 'Squat',
                    totalSets: 12,
                    totalReps: 120
                },
                {
                    exerciseId: 3,
                    exerciseName: 'Deadlift',
                    totalSets: 10,
                    totalReps: 90
                }
            ];

            appFetch.mockImplementation((url, config, onSuccess) => {
                onSuccess(mockExercises);
            });

            workoutService.getExercisesWithWeight((result) => {
                // Verificar que están ordenados descendentemente por totalSets
                expect(result[0].totalSets).toBeGreaterThanOrEqual(result[1].totalSets);
                expect(result[1].totalSets).toBeGreaterThanOrEqual(result[2].totalSets);
                done();
            });
        });
    });
});
