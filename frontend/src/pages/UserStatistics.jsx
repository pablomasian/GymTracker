import React, { useState, useEffect } from 'react';
import { Bar } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, ArcElement, Title, Tooltip, Legend } from 'chart.js';
import workoutService from '../backend/workoutService';

import StatCard from './statistics/StatCard';
import MuscleDistributionChart from './statistics/MuscleDistributionChart';
import TopExercisesTable from './statistics/TopExercisesTable';
import ExerciseProgress from '../components/ExerciseProgress';

ChartJS.register(CategoryScale, LinearScale, BarElement, ArcElement, Title, Tooltip, Legend);

export default function UserStatistics() {
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [selectedExercise, setSelectedExercise] = useState(null);
    const [exercisesWithWeight, setExercisesWithWeight] = useState([]);
    const [routinesWithWeight, setRoutinesWithWeight] = useState([]);
    const [selectedRoutine, setSelectedRoutine] = useState(null);
    const [exerciseRanking, setExerciseRanking] = useState([]);
    const [routineRanking, setRoutineRanking] = useState([]);
    const [rankingError, setRankingError] = useState('');

    useEffect(() => {
        workoutService.getUserStatistics(
            (data) => {
                setStats(data);
                setLoading(false);
                
                // Solo cargar ejercicios con peso si hay datos de estadísticas
                if (data && data.totalWorkouts > 0) {
                    workoutService.getExercisesWithWeight(
                        (exerciseData) => {
                            setExercisesWithWeight(exerciseData || []);
                        },
                        (err) => {
                            console.error('Could not load exercises with weight:', err);
                            setExercisesWithWeight([]);
                        }
                    );

                    workoutService.getRoutinesWithWeight(
                        (routineData) => {
                            setRoutinesWithWeight(routineData || []);
                        },
                        (err) => {
                            console.error('Could not load routines with weight:', err);
                            setRoutinesWithWeight([]);
                        }
                    );
                }
            },
            (err) => {
                setError('Could not load statistics.');
                setLoading(false);
            }
        );
    }, []);

    useEffect(() => {
        if (selectedExercise) {
            workoutService.getExerciseRanking(
                selectedExercise.exerciseId,
                (data) => {
                    setExerciseRanking(data || []);
                    setRankingError('');
                },
                () => setRankingError('Could not load exercise ranking')
            );
        } else {
            setExerciseRanking([]);
        }
    }, [selectedExercise]);

    useEffect(() => {
        if (selectedRoutine) {
            workoutService.getRoutineRanking(
                selectedRoutine.routineId,
                (data) => {
                    setRoutineRanking(data || []);
                    setRankingError('');
                },
                () => setRankingError('Could not load routine ranking')
            );
        } else {
            setRoutineRanking([]);
        }
    }, [selectedRoutine]);

    if (loading) {
        return <div style={{ padding: '2rem', textAlign: 'center' }}>Loading statistics...</div>;
    }

    if (error) {
        return <div className="banner">⚠️ {error}</div>;
    }

    if (!stats || stats.totalWorkouts === 0) {
        return (
            <div style={{ background: '#fff', borderRadius: '12px', padding: '16px', textAlign: 'center', border: '1px solid var(--border, #e6e6e6)'}}>
                <div style={{ color: 'var(--muted, #666)' }}>No workout data available for the last month.</div>
            </div>
        );
    }

    const weeklyChartData = {
        labels: ['3 Weeks Ago', '2 Weeks Ago', 'Last Week', 'This Week'],
        datasets: [{
            label: 'Workouts per Week',
            data: stats.workoutsPerWeek,
            backgroundColor: 'rgba(255, 95, 109, 0.6)',
            borderColor: 'rgba(255, 95, 109, 1)',
            borderWidth: 1,
        }],
    };
    
    const weeklyChartOptions = {
        responsive: true,
        plugins: {
            legend: { position: 'top' },
            title: { display: true, text: 'Weekly Activity' },
        },
        scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } }
    };

    return (
        <div style={{ display: 'grid', gap: '24px' }}>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))', gap: '16px' }}>
                <StatCard value={stats.totalWorkouts} label="Workouts" />
                <StatCard value={stats.averageDurationMinutes.toFixed(0)} label="Avg. Mins / Workout" />
                <StatCard value={stats.workoutFrequency.toFixed(1)} label="Days / Week" />
                <StatCard 
                    value={stats.mostFrequentRoutine} 
                    label="Favorite Routine" 
                    valueStyle={{ fontSize: '1.2rem', textTransform: 'capitalize' }}
                />
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))', gap: '16px' }}>
                <StatCard value={stats.totalSets} label="Total Sets" />
                <StatCard value={stats.totalReps} label="Total Reps" />
                 <StatCard 
                    value={(stats.totalWeightLifted / 1000).toFixed(2)} 
                    label="Tons Lifted" 
                    valueStyle={{ fontSize: '1.75rem' }}
                />
            </div>
            
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '20px', alignItems: 'start' }}>
                {stats.muscleDistribution && Object.keys(stats.muscleDistribution).length > 0 && (
                    <MuscleDistributionChart data={stats.muscleDistribution} />
                )}

                {stats.topExercises && Array.isArray(stats.topExercises) && stats.topExercises.length > 0 && (
                     <TopExercisesTable data={stats.topExercises} />
                )}
            </div>

            <div style={{ background: '#fff', borderRadius: '12px', padding: '16px', border: '1px solid var(--border, #e6e6e6)' }}>
                <Bar options={weeklyChartOptions} data={weeklyChartData} />
            </div>

            {/* Exercise Progress Section */}
            <div style={{ background: '#fff', borderRadius: '12px', padding: '20px', border: '1px solid var(--border, #e6e6e6)' }}>
                <h3 style={{ margin: '0 0 16px 0', color: '#333' }}>Exercise Progress Tracker</h3>
                <p style={{ color: '#666', marginBottom: '16px', fontSize: '0.9rem' }}>
                    Select an exercise to view your weight progression over time
                </p>
                
                {exercisesWithWeight && exercisesWithWeight.length > 0 ? (
                    <div style={{ marginBottom: '16px' }}>
                        <label style={{ display: 'block', marginBottom: '12px', fontWeight: '500', color: '#444' }}>
                            Choose Exercise:
                        </label>
                        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '12px' }}>
                            {exercisesWithWeight.map(exercise => (
                                <button
                                    key={exercise.exerciseId}
                                    type="button"
                                    onClick={() => setSelectedExercise(exercise)}
                                    className="btn"
                                    style={{
                                        padding: '12px 16px',
                                        textAlign: 'left',
                                        backgroundColor: selectedExercise?.exerciseId === exercise.exerciseId ? '#ff5f6d' : '#fff',
                                        color: selectedExercise?.exerciseId === exercise.exerciseId ? '#fff' : '#333',
                                        border: selectedExercise?.exerciseId === exercise.exerciseId ? '2px solid #ff5f6d' : '2px solid #e6e6e6',
                                        borderRadius: '8px',
                                        cursor: 'pointer',
                                        transition: 'all 0.2s',
                                        fontSize: '0.9rem'
                                    }}
                                    onMouseEnter={(e) => {
                                        if (selectedExercise?.exerciseId !== exercise.exerciseId) {
                                            e.currentTarget.style.borderColor = '#ff5f6d';
                                            e.currentTarget.style.backgroundColor = '#fff5f6';
                                        }
                                    }}
                                    onMouseLeave={(e) => {
                                        if (selectedExercise?.exerciseId !== exercise.exerciseId) {
                                            e.currentTarget.style.borderColor = '#e6e6e6';
                                            e.currentTarget.style.backgroundColor = '#fff';
                                        }
                                    }}
                                >
                                    <div style={{ fontWeight: '600', marginBottom: '4px' }}>{exercise.exerciseName}</div>
                                    <div style={{ fontSize: '0.75rem', opacity: 0.8 }}>
                                        {exercise.totalReps} reps · {exercise.totalSets} sets
                                    </div>
                                </button>
                            ))}
                        </div>
                    </div>
                ) : (
                    <div style={{ 
                        padding: '20px', 
                        textAlign: 'center', 
                        color: '#999',
                        background: '#f8f9fa',
                        borderRadius: '8px',
                        marginBottom: '16px'
                    }}>
                        No exercises with weight data found. Log workouts with weight to see progress!
                    </div>
                )}

                {selectedExercise && (
                    <ExerciseProgress 
                        exerciseId={selectedExercise.exerciseId} 
                        exerciseName={selectedExercise.exerciseName}
                    />
                )}

                {!selectedExercise && (
                    <div style={{ 
                        padding: '40px 20px', 
                        textAlign: 'center', 
                        color: '#999',
                        background: '#f8f9fa',
                        borderRadius: '8px'
                    }}>
                        <svg 
                            width="64" 
                            height="64" 
                            viewBox="0 0 24 24" 
                            fill="none" 
                            stroke="currentColor" 
                            strokeWidth="2"
                            style={{ margin: '0 auto 16px', opacity: 0.5 }}
                        >
                            <path d="M3 3v18h18"/>
                            <path d="M18 17V9"/>
                            <path d="M13 17V5"/>
                            <path d="M8 17v-3"/>
                        </svg>
                        <p style={{ margin: 0, fontSize: '1rem' }}>
                            Select an exercise above to view your progress
                        </p>
                    </div>
                )}
            </div>

            {/* Rankings section */}
            <div style={{ display: 'grid', gap: '20px', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))' }}>
                <div style={{ background: '#fff', borderRadius: '12px', padding: '20px', border: '1px solid var(--border, #e6e6e6)' }}>
                    <h3 style={{ margin: '0 0 12px 0' }}>Exercise ranking (max weight)</h3>
                    <p style={{ color: '#666', marginBottom: '12px', fontSize: '0.9rem' }}>You and the people you follow.</p>

                    {exercisesWithWeight && exercisesWithWeight.length > 0 ? (
                        <div style={{ marginBottom: '12px', display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '10px' }}>
                            {exercisesWithWeight.map(exercise => (
                                <button
                                    key={exercise.exerciseId}
                                    type="button"
                                    onClick={() => setSelectedExercise(exercise)}
                                    className="btn"
                                    style={{
                                        padding: '10px 12px',
                                        textAlign: 'left',
                                        backgroundColor: selectedExercise?.exerciseId === exercise.exerciseId ? '#ff5f6d' : '#fff',
                                        color: selectedExercise?.exerciseId === exercise.exerciseId ? '#fff' : '#333',
                                        border: selectedExercise?.exerciseId === exercise.exerciseId ? '2px solid #ff5f6d' : '2px solid #e6e6e6',
                                        borderRadius: '8px',
                                        cursor: 'pointer',
                                        transition: 'all 0.2s',
                                        fontSize: '0.9rem'
                                    }}
                                >
                                    <div style={{ fontWeight: '600' }}>{exercise.exerciseName}</div>
                                    <div style={{ fontSize: '0.75rem', opacity: 0.8 }}>{exercise.totalReps} reps · {exercise.totalSets} sets</div>
                                </button>
                            ))}
                        </div>
                    ) : (
                        <div style={{ padding: '12px', background: '#f8f9fa', borderRadius: '8px', color: '#999' }}>
                            Log weight in your exercises to see rankings.
                        </div>
                    )}

                    {selectedExercise && exerciseRanking && exerciseRanking.length > 0 && (
                        <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '12px' }}>
                            <thead>
                                <tr style={{ textAlign: 'left', borderBottom: '1px solid #e6e6e6' }}>
                                    <th style={{ padding: '8px' }}>Pos</th>
                                    <th style={{ padding: '8px' }}>User</th>
                                    <th style={{ padding: '8px' }}>Max weight</th>
                                </tr>
                            </thead>
                            <tbody>
                                {exerciseRanking.map((row, idx) => (
                                    <tr key={row.userId} style={{ borderBottom: '1px solid #f1f1f1' }}>
                                        <td style={{ padding: '8px', fontWeight: idx === 0 ? 700 : 500 }}>{idx + 1}</td>
                                        <td style={{ padding: '8px' }}>{row.displayName}</td>
                                        <td style={{ padding: '8px' }}>{row.value?.toString()} kg</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    )}

                    {rankingError && <div style={{ color: '#c00', marginTop: '8px' }}>{rankingError}</div>}
                </div>

                <div style={{ background: '#fff', borderRadius: '12px', padding: '20px', border: '1px solid var(--border, #e6e6e6)' }}>
                    <h3 style={{ margin: '0 0 12px 0' }}>Routine ranking (total weight)</h3>
                    <p style={{ color: '#666', marginBottom: '12px', fontSize: '0.9rem' }}>Sum of weight (weight × reps).</p>

                    {routinesWithWeight && routinesWithWeight.length > 0 ? (
                        <div style={{ marginBottom: '12px', display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(220px, 1fr))', gap: '10px' }}>
                            {routinesWithWeight.map(routine => (
                                <button
                                    key={routine.routineId}
                                    type="button"
                                    onClick={() => setSelectedRoutine(routine)}
                                    className="btn"
                                    style={{
                                        padding: '10px 12px',
                                        textAlign: 'left',
                                        backgroundColor: selectedRoutine?.routineId === routine.routineId ? '#ff5f6d' : '#fff',
                                        color: selectedRoutine?.routineId === routine.routineId ? '#fff' : '#333',
                                        border: selectedRoutine?.routineId === routine.routineId ? '2px solid #ff5f6d' : '2px solid #e6e6e6',
                                        borderRadius: '8px',
                                        cursor: 'pointer',
                                        transition: 'all 0.2s',
                                        fontSize: '0.9rem'
                                    }}
                                >
                                    <div style={{ fontWeight: '600' }}>{routine.routineName}</div>
                                    <div style={{ fontSize: '0.75rem', opacity: 0.8 }}>{(routine.totalWeight / 1000).toFixed(2)} t</div>
                                </button>
                            ))}
                        </div>
                    ) : (
                        <div style={{ padding: '12px', background: '#f8f9fa', borderRadius: '8px', color: '#999' }}>
                            Complete routines with weight to see rankings.
                        </div>
                    )}

                    {selectedRoutine && routineRanking && routineRanking.length > 0 && (
                        <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '12px' }}>
                            <thead>
                                <tr style={{ textAlign: 'left', borderBottom: '1px solid #e6e6e6' }}>
                                    <th style={{ padding: '8px' }}>Pos</th>
                                    <th style={{ padding: '8px' }}>User</th>
                                    <th style={{ padding: '8px' }}>Total weight</th>
                                </tr>
                            </thead>
                            <tbody>
                                {routineRanking.map((row, idx) => (
                                    <tr key={row.userId} style={{ borderBottom: '1px solid #f1f1f1' }}>
                                        <td style={{ padding: '8px', fontWeight: idx === 0 ? 700 : 500 }}>{idx + 1}</td>
                                        <td style={{ padding: '8px' }}>{row.displayName}</td>
                                        <td style={{ padding: '8px' }}>{row.value?.toString()} kg</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    )}

                    {rankingError && <div style={{ color: '#c00', marginTop: '8px' }}>{rankingError}</div>}
                </div>
            </div>
        </div>
    );
}