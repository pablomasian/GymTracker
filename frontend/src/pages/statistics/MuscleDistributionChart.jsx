import React from 'react';
import { Doughnut } from 'react-chartjs-2';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';

ChartJS.register(ArcElement, Tooltip, Legend);

const chartContainerStyle = {
    background: '#fff',
    borderRadius: '12px',
    padding: '16px',
    boxShadow: '0 4px 12px rgba(0,0,0,0.05)',
    border: '1px solid var(--border, #e6e6e6)',
};

const MuscleDistributionChart = ({ data }) => {
    const labels = Object.keys(data).map(s => s.charAt(0).toUpperCase() + s.slice(1));
    const values = Object.values(data);

    const chartData = {
        labels: labels,
        datasets: [{
            label: 'Sets per Muscle Group',
            data: values,
            backgroundColor: [
                'rgba(255, 99, 132, 0.7)',
                'rgba(54, 162, 235, 0.7)',
                'rgba(255, 206, 86, 0.7)',
                'rgba(75, 192, 192, 0.7)',
                'rgba(153, 102, 255, 0.7)',
                'rgba(255, 159, 64, 0.7)',
            ],
            borderColor: '#fff',
            borderWidth: 2,
        }],
    };

    const options = {
        responsive: true,
        plugins: {
            legend: {
                position: 'top',
            },
            title: {
                display: true,
                text: 'Muscle Distribution (by Sets)',
            },
        },
    };

    return (
        <div style={chartContainerStyle}>
            <Doughnut data={chartData} options={options} />
        </div>
    );
};

export default MuscleDistributionChart;