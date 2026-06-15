import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import UserStatistics from '../../pages/UserStatistics';
import * as workoutService from '../../backend/workoutService';

jest.mock('../../backend/workoutService');
jest.mock('../../pages/statistics/StatCard', () => {
  return function MockStatCard({ value, label }) {
    return <div data-testid="stat-card">{label}: {value}</div>;
  };
});
jest.mock('../../pages/statistics/MuscleDistributionChart', () => {
  return function MockMuscleChart() {
    return <div data-testid="muscle-chart">Muscle Distribution Chart</div>;
  };
});
jest.mock('../../pages/statistics/TopExercisesTable', () => {
  return function MockTopExercises() {
    return <div data-testid="top-exercises">Top Exercises Table</div>;
  };
});
jest.mock('../../components/ExerciseProgress', () => {
  return function MockExerciseProgress() {
    return <div data-testid="exercise-progress">Exercise Progress</div>;
  };
});

describe('UserStatistics - Leaderboards', () => {
  const mockStats = {
    totalWorkouts: 10,
    averageDurationMinutes: 60,
    workoutFrequency: 3.5,
    mostFrequentRoutine: 'Push Day',
    totalSets: 100,
    totalReps: 500,
    totalWeightLifted: 50000,
    workoutsPerWeek: [2, 3, 2, 3],
    muscleDistribution: { chest: 0.3, back: 0.3, legs: 0.4 },
    topExercises: [
      { exerciseName: 'Bench Press', totalSets: 20, totalReps: 100 }
    ]
  };

  const mockExercisesWithWeight = [
    { exerciseId: 1, exerciseName: 'Bench Press', totalReps: 100, totalSets: 20 },
    { exerciseId: 2, exerciseName: 'Squat', totalReps: 150, totalSets: 30 }
  ];

  const mockRoutinesWithWeight = [
    { routineId: 1, routineName: 'Push Day', totalWeight: 25000 },
    { routineId: 2, routineName: 'Leg Day', totalWeight: 15000 }
  ];

  const mockExerciseRanking = [
    { userId: 1, displayName: 'Current User', value: '100' },
    { userId: 2, displayName: 'Friend User', value: '95' },
    { userId: 3, displayName: 'Another User', value: '80' }
  ];

  const mockRoutineRanking = [
    { userId: 1, displayName: 'Current User', value: '250' },
    { userId: 2, displayName: 'Friend User', value: '200' },
    { userId: 3, displayName: 'Another User', value: '150' }
  ];

  beforeEach(() => {
    jest.clearAllMocks();
    workoutService.default.getUserStatistics.mockImplementation((onSuccess) => {
      onSuccess(mockStats);
    });
    workoutService.default.getExercisesWithWeight.mockImplementation((onSuccess) => {
      onSuccess(mockExercisesWithWeight);
    });
    workoutService.default.getRoutinesWithWeight.mockImplementation((onSuccess) => {
      onSuccess(mockRoutinesWithWeight);
    });
  });

  test('renders leaderboard sections', async () => {
    render(<UserStatistics />);
    
    await waitFor(() => {
      expect(screen.getByText('Exercise ranking (max weight)')).toBeInTheDocument();
      expect(screen.getByText('Routine ranking (total weight)')).toBeInTheDocument();
    });
  });

  test('displays exercise buttons for leaderboard', async () => {
    render(<UserStatistics />);
    
    await waitFor(() => {
      expect(screen.getAllByText('Bench Press').length).toBeGreaterThan(0);
      expect(screen.getAllByText('Squat').length).toBeGreaterThan(0);
    });
  });

  test('displays routine buttons for leaderboard', async () => {
    render(<UserStatistics />);
    
    await waitFor(() => {
      expect(screen.getByText('Push Day')).toBeInTheDocument();
      expect(screen.getByText('Leg Day')).toBeInTheDocument();
    });
  });

  test('shows message when no exercises with weight', async () => {
    workoutService.default.getExercisesWithWeight.mockImplementation((onSuccess) => {
      onSuccess([]);
    });

    render(<UserStatistics />);

    await waitFor(() => {
      expect(screen.getByText('Log weight in your exercises to see rankings.')).toBeInTheDocument();
    });
  });

  test('shows message when no routines with weight', async () => {
    workoutService.default.getRoutinesWithWeight.mockImplementation((onSuccess) => {
      onSuccess([]);
    });

    render(<UserStatistics />);

    await waitFor(() => {
      expect(screen.getByText('Complete routines with weight to see rankings.')).toBeInTheDocument();
    });
  });

  test('displays exercise info in buttons', async () => {
    render(<UserStatistics />);

    await waitFor(() => {
      const exerciseInfos = screen.getAllByText(/reps · .* sets/);
      expect(exerciseInfos.length).toBeGreaterThan(0);
    });
  });

  test('displays routine total weight in buttons', async () => {
    render(<UserStatistics />);

    await waitFor(() => {
      expect(screen.getByText(/25.00 t/)).toBeInTheDocument();
      expect(screen.getByText(/15.00 t/)).toBeInTheDocument();
    });
  });
});
