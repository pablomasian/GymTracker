import React from 'react';

const cardStyle = {
    background: '#fff',
    borderRadius: '12px',
    padding: '16px',
    textAlign: 'center',
    boxShadow: '0 4px 12px rgba(0,0,0,0.05)',
    border: '1px solid var(--border, #e6e6e6)',
};

const valueStyle = {
    fontSize: '2rem',
    fontWeight: 'bold',
    color: 'var(--primary, #ff6b6b)',
    lineHeight: 1.2,
};

const labelStyle = {
    fontSize: '0.9rem',
    color: 'var(--muted, #666)',
    marginTop: '4px',
};

const StatCard = ({ value, label, valueStyle: customValueStyle }) => (
    <div style={cardStyle}>
        <div style={{...valueStyle, ...customValueStyle}}>{value}</div>
        <div style={labelStyle}>{label}</div>
    </div>
);

export default StatCard;