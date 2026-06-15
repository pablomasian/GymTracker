import { useState, useEffect } from 'react';
import { createPortal } from 'react-dom';
import { useNavigate } from 'react-router-dom';
import { appFetch, fetchConfig } from '../backend/appFetch';
import './WrappedCarousel.css';

export default function WrappedCarousel({ onClose }) {
    const [wrapped, setWrapped] = useState(null);
    const [loading, setLoading] = useState(true);
    const [currentSlide, setCurrentSlide] = useState(0);
    const [isAnimating, setIsAnimating] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        appFetch(
            '/wrapped/current',
            fetchConfig('GET'),
            (data) => {
                setWrapped(data);
                setLoading(false);
            },
            (err) => {
                console.error('Error loading wrapped:', err);
                setLoading(false);
            }
        );
    }, []);

    const nextSlide = () => {
        if (isAnimating) return;
        setIsAnimating(true);
        setTimeout(() => {
            setCurrentSlide((prev) => (prev + 1) % slides.length);
            setIsAnimating(false);
        }, 300);
    };

    const prevSlide = () => {
        if (isAnimating) return;
        setIsAnimating(true);
        setTimeout(() => {
            setCurrentSlide((prev) => (prev - 1 + slides.length) % slides.length);
            setIsAnimating(false);
        }, 300);
    };

    if (loading) {
        return (
            <div className="wrapped-container">
                <div className="wrapped-loading">
                    <div className="wrapped-spinner"></div>
                    <p>Loading your summary...</p>
                </div>
            </div>
        );
    }

    if (!wrapped) return null;

    const slides = [
        // Slide 1: Intro
        {
            id: 'intro',
            gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
            content: (
                <div className="slide-content intro">
                    <div className="year-badge">{wrapped.year}</div>
                    <h1>Your Year at the Gym</h1>
                    <p>Discover your achievements 💪</p>
                    <div className="intro-icon">🏋️‍♂️</div>
                </div>
            )
        },
        // Slide 2: Total Workouts
        {
            id: 'workouts',
            gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
            content: (
                <div className="slide-content">
                    <h2>This year you trained</h2>
                    <div className="big-number">{wrapped.totalWorkouts}</div>
                    <p>times 🔥</p>
                    {wrapped.bestMonth && (
                        <div className="sub-stat">
                            Your best month was <strong>{wrapped.bestMonth}</strong> with {wrapped.bestMonthWorkouts} workouts
                        </div>
                    )}
                </div>
            )
        },
        // Slide 3: Weight Lifted
        {
            id: 'weight',
            gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
            content: (
                <div className="slide-content">
                    <h2>You lifted a total of</h2>
                    <div className="big-number">
                        {wrapped.totalWeightLifted?.toLocaleString() || 0}
                        <span className="unit">kg</span>
                    </div>
                    <p className="comparison">{wrapped.weightComparison}</p>
                </div>
            )
        },
        // Slide 4: Friends Ranking (only if in top 3)
        ...(wrapped.friendsRanking ? [{
            id: 'ranking',
            gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
            content: (
                <div className="slide-content">
                    <h2>Among your friends</h2>
                    <div className="ranking-badge">
                        {wrapped.friendsRanking === 1 ? '🥇' : wrapped.friendsRanking === 2 ? '🥈' : '🥉'}
                    </div>
                    <div className="big-number">#{wrapped.friendsRanking}</div>
                    <p>in workouts</p>
                    <div className="sub-stat">
                        Out of {wrapped.totalFriends} friends
                    </div>
                </div>
            )
        }] : []),
        // Slide 5: Top Exercises
        {
            id: 'exercises',
            gradient: 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
            content: (
                <div className="slide-content dark-text">
                    <h2>Your favorite exercises</h2>
                    <div className="top-exercises">
                        {wrapped.topExercises?.map((ex, idx) => (
                            <div key={ex.exerciseId} className="exercise-item">
                                <span className="exercise-rank">{idx + 1}</span>
                                {ex.imageUrl && (
                                    <img src={ex.imageUrl} alt={ex.exerciseName} className="exercise-img" />
                                )}
                                <div className="exercise-info">
                                    <span className="exercise-name">{ex.exerciseName}</span>
                                    <span className="exercise-count">{ex.count} times</span>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )
        },
        // Slide 6: Top Muscle Group
        {
            id: 'muscle',
            gradient: 'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)',
            content: (
                <div className="slide-content dark-text">
                    <h2>Your most trained muscle</h2>
                    <div className="muscle-icon">💪</div>
                    <div className="big-text">{wrapped.topMuscleGroup}</div>
                    <p>{wrapped.topMuscleGroupCount} times trained</p>
                </div>
            )
        },
        // Slide 7: Favorite Coach
        ...(wrapped.favoriteCoachName ? [{
            id: 'coach',
            gradient: 'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)',
            content: (
                <div className="slide-content">
                    <h2>Your favorite coach</h2>
                    <div className="coach-icon">🏆</div>
                    <div className="big-text">{wrapped.favoriteCoachName}</div>
                    <p>You did {wrapped.routinesFromFavoriteCoach} of their routines</p>
                    <button
                        className="view-coach-btn"
                        onClick={() => navigate(`/coach/${wrapped.favoriteCoachId}`)}
                    >
                        View Profile
                    </button>
                </div>
            )
        }] : []),
        // Slide 8: Social Stats
        {
            id: 'social',
            gradient: 'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)',
            content: (
                <div className="slide-content dark-text">
                    <h2>Your social activity</h2>
                    <div className="social-stats">
                        <div className="social-item">
                            <span className="social-icon">❤️</span>
                            <span className="social-number">{wrapped.likesGiven}</span>
                            <span className="social-label">likes given</span>
                        </div>
                        <div className="social-item">
                            <span className="social-icon">💖</span>
                            <span className="social-number">{wrapped.likesReceived}</span>
                            <span className="social-label">likes received</span>
                        </div>
                        <div className="social-item">
                            <span className="social-icon">💬</span>
                            <span className="social-number">{wrapped.commentsReceived}</span>
                            <span className="social-label">comments</span>
                        </div>
                    </div>
                </div>
            )
        },
        // Slide 9: Top Interaction User
        ...(wrapped.topInteractionUserName ? [{
            id: 'bestfriend',
            gradient: 'linear-gradient(135deg, #d299c2 0%, #fef9d7 100%)',
            content: (
                <div className="slide-content dark-text">
                    <h2>Your gym buddy</h2>
                    <div className="buddy-icon">🤝</div>
                    <div className="big-text">{wrapped.topInteractionUserName}</div>
                    <p>{wrapped.topInteractionCount} interactions</p>
                </div>
            )
        }] : []),
        // Slide 10: Streak
        {
            id: 'streak',
            gradient: 'linear-gradient(135deg, #f6d365 0%, #fda085 100%)',
            content: (
                <div className="slide-content dark-text">
                    <h2>Your current streak</h2>
                    <div className="streak-fire">🔥</div>
                    <div className="big-number">{wrapped.currentStreak}</div>
                    <p>consecutive days</p>
                </div>
            )
        },
        // Slide 11: Outro
        {
            id: 'outro',
            gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
            content: (
                <div className="slide-content intro">
                    <h1>Keep it up in {wrapped.year + 1}!</h1>
                    <div className="outro-icons">
                        <span>💪</span>
                        <span>🏋️‍♂️</span>
                        <span>🔥</span>
                    </div>
                    <button className="close-btn" onClick={onClose}>
                        Close
                    </button>
                </div>
            )
        }
    ];

    return createPortal(
        <div className="wrapped-container" onClick={(e) => e.target === e.currentTarget && onClose()}>
            <div
                className={`wrapped-slide ${isAnimating ? 'animating' : ''}`}
                style={{ background: slides[currentSlide].gradient }}
            >
                <button className="close-x" onClick={onClose}>✕</button>

                {slides[currentSlide].content}

                <div className="slide-navigation">
                    <button
                        className="nav-btn prev"
                        onClick={prevSlide}
                        disabled={currentSlide === 0}
                    >
                        ←
                    </button>
                    <div className="slide-dots">
                        {slides.map((_, idx) => (
                            <span
                                key={idx}
                                className={`dot ${idx === currentSlide ? 'active' : ''}`}
                                onClick={() => setCurrentSlide(idx)}
                            />
                        ))}
                    </div>
                    <button
                        className="nav-btn next"
                        onClick={nextSlide}
                        disabled={currentSlide === slides.length - 1}
                    >
                        →
                    </button>
                </div>
            </div>
        </div>,
        document.body
    );
}
