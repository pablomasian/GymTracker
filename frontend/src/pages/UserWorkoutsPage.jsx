// Página con el histórico de sesiones del usuario
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { appFetch, fetchConfig, getServiceToken } from "../backend/appFetch";

const workoutService = {
  getUserWorkouts: (onSuccess, onErrors) => {
    const token = getServiceToken();
    if (!token) {
      onErrors && onErrors({ globalError: "No authentication token found" });
      return;
    }

    appFetch(
      "/workouts/user-sessions",
      fetchConfig("GET", null, token),
      onSuccess,
      onErrors
    );
  },
};

export default function UserWorkoutsPage() {
  const navigate = useNavigate();

  const [workouts, setWorkouts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [viewMode, setViewMode] = useState("list"); // "list" o "calendar"
  const [currentMonth, setCurrentMonth] = useState(new Date());

  function formatDuration(start, end) {
    if (!start || !end) return "In progress";
    const startDate = new Date(start);
    const endDate = new Date(end);
    const diffMs = endDate - startDate;
    const diffMinutes = Math.floor(diffMs / 60000);
    const diffSeconds = Math.floor((diffMs % 60000) / 1000);
    return `${diffMinutes} min ${diffSeconds}s`;
  }


  useEffect(() => {
    let alive = true;

    workoutService.getUserWorkouts(
      (data) => {
        if (alive) setWorkouts(Array.isArray(data) ? data : []);
        if (alive) setLoading(false);
      },
      (err) => {
        if (alive) {
          setError(err.globalError || "Failed to load workouts.");
          setLoading(false);
        }
      }
    );

    return () => {
      alive = false;
    };
  }, []);

  if (loading) return <div className="container"><h2>Loading workouts...</h2></div>;

  // Agrupar workouts por día para la vista de calendario
  const workoutsByDate = workouts.reduce((acc, workout) => {
    // Extraer la fecha local sin conversión UTC para evitar cambios de día
    const workoutDate = new Date(workout.fecha);
    const year = workoutDate.getFullYear();
    const month = String(workoutDate.getMonth() + 1).padStart(2, '0');
    const day = String(workoutDate.getDate()).padStart(2, '0');
    const date = `${year}-${month}-${day}`; // YYYY-MM-DD en zona horaria local
    if (!acc[date]) acc[date] = [];
    acc[date].push(workout);
    return acc;
  }, {});

  // Generar días del mes actual para el calendario
  const getDaysInMonth = (date) => {
    const year = date.getFullYear();
    const month = date.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const daysInMonth = lastDay.getDate();
    const startDayOfWeek = firstDay.getDay(); // 0 = Sunday

    const days = [];
    // Añadir días vacíos al inicio para alinear el calendario
    for (let i = 0; i < startDayOfWeek; i++) {
      days.push(null);
    }
    // Añadir los días del mes
    for (let day = 1; day <= daysInMonth; day++) {
      days.push(new Date(year, month, day));
    }
    return days;
  };

  const monthDays = getDaysInMonth(currentMonth);
  const monthName = currentMonth.toLocaleDateString('es-ES', { month: 'long', year: 'numeric' });

  const goToPreviousMonth = () => {
    setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() - 1, 1));
  };

  const goToNextMonth = () => {
    setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 1));
  };

  return (
    <div className="container" style={{ padding: "1.25rem" }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
        <h2>My Workouts</h2>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button
            className={`btn ${viewMode === 'list' ? 'btn-primary' : ''}`}
            onClick={() => setViewMode('list')}
          >
            List View
          </button>
          <button
            className={`btn ${viewMode === 'calendar' ? 'btn-primary' : ''}`}
            onClick={() => setViewMode('calendar')}
          >
            Calendar View
          </button>
        </div>
      </div>

      {error && <div className="banner">⚠️ {error}</div>}

      {viewMode === 'list' && (
        <div className="stack" style={{ gap: "1.5rem" }}>
          {workouts.map((session) => (
            <div key={session.id} className="card-small">
              <strong>Routine: {session.routineName || session.routineId}</strong>
              <div style={{ color: "#555" }}>User: {session.userName || session.userId}</div>
              <div style={{ color: "#555" }}>Date: {new Date(session.fecha).toLocaleString()}</div>
              <div style={{ color: "#555" }}>Duration: {formatDuration(session.startTime, session.endTime)}</div>
              <button
                type="button"
                className="btn"
                style={{ marginTop: "1rem" }}
                onClick={() => navigate(`/workouts/${session.id}`)}
              >
                View Details
              </button>
            </div>
          ))}
          {workouts.length === 0 && (
            <div className="empty-state-modern">
              <div className="empty-icon">🏋️</div>
              <h3 className="empty-title">No workouts yet</h3>
              <p className="empty-subtitle">
                Start your first workout session to track your progress and build your history!
              </p>
            </div>
          )}
        </div>
      )}

      {viewMode === 'calendar' && (
        <div>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
            <button className="btn" onClick={goToPreviousMonth}>← Previous</button>
            <h3 style={{ margin: 0, textTransform: 'capitalize' }}>{monthName}</h3>
            <button className="btn" onClick={goToNextMonth}>Next →</button>
          </div>

          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(7, 1fr)',
            gap: '0.5rem',
            marginBottom: '0.5rem'
          }}>
            {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].map(day => (
              <div key={day} style={{
                fontWeight: 'bold',
                textAlign: 'center',
                padding: '0.5rem',
                borderBottom: '2px solid #ddd'
              }}>
                {day}
              </div>
            ))}
          </div>

          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(7, 1fr)',
            gap: '0.5rem'
          }}>
            {monthDays.map((day, index) => {
              if (!day) {
                return <div key={`empty-${index}`} style={{ minHeight: '80px' }} />;
              }

              // Generar dateKey en formato local para que coincida con workoutsByDate
              const year = day.getFullYear();
              const month = String(day.getMonth() + 1).padStart(2, '0');
              const dayNum = String(day.getDate()).padStart(2, '0');
              const dateKey = `${year}-${month}-${dayNum}`;
              const dayWorkouts = workoutsByDate[dateKey] || [];
              const isToday = day.toDateString() === new Date().toDateString();

              return (
                <div
                  key={dateKey}
                  style={{
                    border: isToday ? '2px solid #007bff' : '1px solid #ddd',
                    borderRadius: '4px',
                    padding: '0.5rem',
                    minHeight: '80px',
                    backgroundColor: dayWorkouts.length > 0 ? '#f0f9ff' : '#fff',
                    cursor: dayWorkouts.length > 0 ? 'pointer' : 'default'
                  }}
                  onClick={() => {
                    if (dayWorkouts.length === 1) {
                      navigate(`/workouts/${dayWorkouts[0].id}`);
                    } else if (dayWorkouts.length > 1) {
                      // Si hay múltiples entrenamientos, navegar al primero
                      navigate(`/workouts/${dayWorkouts[0].id}`);
                    }
                  }}
                >
                  <div style={{ fontWeight: 'bold', marginBottom: '0.25rem' }}>
                    {day.getDate()}
                  </div>
                  {dayWorkouts.map((workout) => (
                    <div
                      key={workout.id}
                      style={{
                        fontSize: '0.75rem',
                        backgroundColor: '#007bff',
                        color: 'white',
                        padding: '2px 4px',
                        borderRadius: '3px',
                        marginBottom: '2px',
                        overflow: 'hidden',
                        textOverflow: 'ellipsis',
                        whiteSpace: 'nowrap'
                      }}
                      title={workout.routineName || `Routine ${workout.routineId}`}
                    >
                      {workout.routineName || `Routine ${workout.routineId}`}
                    </div>
                  ))}
                </div>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
}
