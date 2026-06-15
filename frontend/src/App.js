import { Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import RoutinesPage from "./pages/RoutinesPage";
import RoutineEditorPage from "./pages/RoutineEditorPage";
import RoutineDetailPage from "./pages/RoutineDetailPage";
import ViewProfile from "./pages/ViewProfile";
import LogWorkoutPage from "./pages/LogWorkoutPage";
import UserWorkoutsPage from "./pages/UserWorkoutsPage";
import CoachRoute from "./components/CoachRoute";
import WithSidebar from "./components/WithSidebar";
import WorkoutDetailPage from "./pages/WorkoutDetailPage";
import CreateExercisePage from "./pages/CreateExercisePage";
import SavedRoutinesPage from "./pages/SavedRoutinesPage";
import AdminExerciseQueue from "./pages/AdminExerciseQueue";
import AdminExerciseManagement from "./pages/AdminExerciseManagement";
import AdminUserManagement from "./pages/AdminUserManagement";
import NotificationsPage from "./pages/NotificationsPage";
import AdminRoute from "./components/AdminRoute";
import CoachDashboardPage from "./pages/CoachDashboardPage";
import ViewCoachProfile from "./pages/ViewCoachProfile";
import AdminRoutineQueue from "./pages/AdminRoutineQueue";
import AdminRoutineManagement from "./pages/AdminRoutineManagement";
import UserFeedPage from "./pages/UserFeedPage.jsx";
import FollowsPage from "./pages/Following";
import FollowersPage from "./pages/Followers";
import UserSearchPage from "./pages/UserSearchPage";
import ViewUserProfile from "./pages/ViewUserProfile";
import SessionFeed from "./pages/SessionFeed";




export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      {/* Rutas Públicas y de Usuario */}
      <Route path="/routines" element={<WithSidebar><RoutinesPage /></WithSidebar>} />
      <Route path="/routines/:id" element={<WithSidebar><RoutineDetailPage /></WithSidebar>} />
      <Route path="/log-workout/:id" element={<WithSidebar><LogWorkoutPage /></WithSidebar>} />
      <Route path="/view-profile" element={<WithSidebar><ViewProfile /></WithSidebar>} />
      <Route path="/saved-routines" element={<WithSidebar><SavedRoutinesPage /></WithSidebar>} />
      <Route path="/notifications" element={<WithSidebar><NotificationsPage /></WithSidebar>} />
      <Route path="/workouts/:id" element={<WithSidebar><WorkoutDetailPage /></WithSidebar>} />
      <Route path="/my-workouts" element={<WithSidebar><UserWorkoutsPage /></WithSidebar>} />
      <Route path="/my-feed" element={<WithSidebar><UserFeedPage /></WithSidebar>} />
      <Route path="/coach/:id" element={<WithSidebar><ViewCoachProfile /></WithSidebar>} />
      <Route path="/profile/:id/following" element={<WithSidebar><FollowsPage /></WithSidebar>} />
      <Route path="/profile/:id/followers" element={<WithSidebar><FollowersPage /></WithSidebar>} />
      <Route path="/users/search" element={<WithSidebar><UserSearchPage /></WithSidebar>} />
      <Route path="/user/:id" element={<WithSidebar><ViewUserProfile /></WithSidebar>} />
      <Route path="/session-feed/:id" element={<WithSidebar><SessionFeed /></WithSidebar>} />

      {/* Rutas para Coach/Admin */}
      <Route path="/routines/new" element={<WithSidebar><CoachRoute><RoutineEditorPage /></CoachRoute></WithSidebar>} />
      <Route path="/routines/edit/:id" element={<WithSidebar><CoachRoute><RoutineEditorPage /></CoachRoute></WithSidebar>} />
      <Route path="/exercises/new" element={<WithSidebar><CoachRoute><CreateExercisePage /></CoachRoute></WithSidebar>} />
      <Route path="/coach/dashboard" element={<WithSidebar><CoachRoute><CoachDashboardPage /></CoachRoute></WithSidebar>} />

      {/* Rutas Exclusivas de Admin */}
      <Route path="/exercises/pending" element={<WithSidebar><AdminRoute><AdminExerciseQueue /></AdminRoute></WithSidebar>} />
      <Route path="/exercises/manage" element={<WithSidebar><AdminRoute><AdminExerciseManagement /></AdminRoute></WithSidebar>} />
      <Route path="/routines/pending" element={<WithSidebar><AdminRoute><AdminRoutineQueue /></AdminRoute></WithSidebar>} />
      <Route path="/routines/manage" element={<WithSidebar><AdminRoute><AdminRoutineManagement /></AdminRoute></WithSidebar>} />
      <Route path="/users/manage" element={<WithSidebar><AdminRoute><AdminUserManagement /></AdminRoute></WithSidebar>} />

      {/* Redirecciones por defecto */}
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}