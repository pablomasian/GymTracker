import React from 'react';

const tableContainerStyle = {
    background: '#fff',
    borderRadius: '12px',
    padding: '16px',
    boxShadow: '0 4px 12px rgba(0,0,0,0.05)',
    border: '1px solid var(--border, #e6e6e6)',
};

const TopExercisesTable = ({ data }) => {
    return (
        <div style={tableContainerStyle}>
            <h4 style={{ margin: '0 0 12px 0', textAlign: 'center' }}>Top 5 Exercises (by Sets)</h4>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                    <tr style={{ borderBottom: '2px solid var(--border, #eee)' }}>
                        <th style={{ textAlign: 'left', padding: '8px' }}>Exercise</th>
                        <th style={{ textAlign: 'right', padding: '8px' }}>Total Sets</th>
                    </tr>
                </thead>
                <tbody>
                    {data.map((item, index) => (
                        <tr key={index} style={{ borderBottom: '1px solid #f5f5f5' }}>
                            <td style={{ padding: '8px', textTransform: 'capitalize' }}>{item.exerciseName}</td>
                            <td style={{ padding: '8px', textAlign: 'right', fontWeight: 'bold' }}>{item.totalSets}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default TopExercisesTable;