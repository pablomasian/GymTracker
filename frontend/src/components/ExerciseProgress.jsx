import React, { useState, useEffect } from 'react';
import { Line } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend } from 'chart.js';
import workoutService from '../backend/workoutService';

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend);

export default function ExerciseProgress({ exerciseId, exerciseName }) {
    const [progressData, setProgressData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        if (!exerciseId) {
            setLoading(false);
            return;
        }

        workoutService.getExerciseProgress(
            exerciseId,
            (data) => {
                setProgressData(data);
                setLoading(false);
            },
            (err) => {
                setError('Could not load exercise progress.');
                setLoading(false);
            }
        );
    }, [exerciseId]);

    if (loading) {
        return <div style={{ padding: '1rem', textAlign: 'center' }}>Loading progress...</div>;
    }

    if (error) {
        return <div className="banner">⚠️ {error}</div>;
    }

    if (!progressData || progressData.length === 0) {
        return (
            <div style={{ 
                background: '#fff', 
                borderRadius: '12px', 
                padding: '16px', 
                textAlign: 'center',
                border: '1px solid var(--border, #e6e6e6)'
            }}>
                <div style={{ color: 'var(--muted, #666)' }}>
                    No data available for this exercise yet. Start tracking your workouts!
                </div>
            </div>
        );
    }

    // Formatear datos para la gráfica
    const chartData = {
        labels: progressData.map(item => {
            const date = new Date(item.fecha);
            return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
        }),
        datasets: [
            {
                label: 'Max Weight (kg)',
                data: progressData.map(item => item.maxWeight),
                borderColor: 'rgba(255, 95, 109, 1)',
                backgroundColor: 'rgba(255, 95, 109, 0.2)',
                borderWidth: 2,
                fill: true,
                tension: 0.4,
                pointRadius: 4,
                pointHoverRadius: 6,
            }
        ],
    };

    const chartOptions = {
        responsive: true,
        maintainAspectRatio: true,
        plugins: {
            legend: {
                position: 'top',
            },
            title: {
                display: true,
                text: `${exerciseName || 'Exercise'} - Weight Progress`,
                font: {
                    size: 16,
                    weight: 'bold'
                }
            },
            tooltip: {
                callbacks: {
                    afterLabel: (context) => {
                        const index = context.dataIndex;
                        const item = progressData[index];
                        return [
                            `Total Sets: ${item.totalSets}`,
                            `Total Reps: ${item.totalReps}`
                        ];
                    }
                }
            }
        },
        scales: {
            y: {
                beginAtZero: true,
                title: {
                    display: true,
                    text: 'Weight (kg)'
                }
            },
            x: {
                title: {
                    display: true,
                    text: 'Date'
                }
            }
        }
    };

    return (
        <div style={{ 
            background: '#fff', 
            borderRadius: '12px', 
            padding: '20px', 
            border: '1px solid var(--border, #e6e6e6)',
            marginTop: '16px'
        }}>
            <Line data={chartData} options={chartOptions} />
            
            <div style={{ 
                marginTop: '20px', 
                padding: '12px', 
                background: '#f8f9fa', 
                borderRadius: '8px',
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))',
                gap: '12px'
            }}>
                <div style={{ textAlign: 'center' }}>
                    <div style={{ fontSize: '0.875rem', color: '#666' }}>Sessions</div>
                    <div style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#333' }}>
                        {progressData.length}
                    </div>
                </div>
                <div style={{ textAlign: 'center' }}>
                    <div style={{ fontSize: '0.875rem', color: '#666' }}>Max Weight</div>
                    <div style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#333' }}>
                        {Math.max(...progressData.map(item => item.maxWeight))} kg
                    </div>
                </div>
                <div style={{ textAlign: 'center' }}>
                    <div style={{ fontSize: '0.875rem', color: '#666' }}>Total Reps</div>
                    <div style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#333' }}>
                        {progressData.reduce((sum, item) => sum + item.totalReps, 0)}
                    </div>
                </div>
                <div style={{ textAlign: 'center' }}>
                    <div style={{ fontSize: '0.875rem', color: '#666' }}>Total Sets</div>
                    <div style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#333' }}>
                        {progressData.reduce((sum, item) => sum + item.totalSets, 0)}
                    </div>
                </div>
            </div>
        </div>
    );
}
