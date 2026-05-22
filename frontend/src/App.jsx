import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import PrivateRoute from "./components/PrivateRoute";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import CoordinatorDashboard from "./pages/CoordinatorDashboard";
import ProjectsPage from "./pages/ProjectsPage";
import CreateProjectPage from "./pages/CreateProjectPage";
import EditProjectPage from "./pages/EditProjectPage";
import RegisterVolunteerPage from "./pages/RegisterVolunteerPage";
import UserProfilePage from "./pages/UserProfilePage";
import VolunteerUpdatePage from "./pages/VolunteerUpdatePage";
import ManagerDashboard from "./pages/ManagerDashboard";
import ManagerProjectsPage from "./pages/ManagerProjectsPage";
import ManagerProjectDetailPage from "./pages/ManagerProjectDetailPage";

function App() {
	return (
		<AuthProvider>
			<BrowserRouter>
				<Routes>
					<Route path="/login" element={<LoginPage />} />
					<Route path="/register" element={<RegisterPage />} />
					<Route path="/" element={<PrivateRoute allowedRoles={["COORDINATOR"]}><CoordinatorDashboard /></PrivateRoute>} />
					<Route path="/projects" element={<PrivateRoute allowedRoles={["COORDINATOR"]}><ProjectsPage /></PrivateRoute>} />
					<Route path="/projects/new" element={<PrivateRoute allowedRoles={["COORDINATOR"]}><CreateProjectPage /></PrivateRoute>} />
					<Route path="/projects/:id/edit" element={<PrivateRoute allowedRoles={["COORDINATOR"]}><EditProjectPage /></PrivateRoute>} />
					<Route path="/manager" element={<PrivateRoute allowedRoles={["MANAGER"]}><ManagerDashboard /></PrivateRoute>} />
					<Route path="/manager/registerVolunteer" element={<PrivateRoute allowedRoles={["MANAGER"]}><RegisterVolunteerPage /></PrivateRoute>} />
					<Route path="/profile" element={<PrivateRoute><UserProfilePage /></PrivateRoute>}/>
					<Route path="/volunteer/update" element={<PrivateRoute><VolunteerUpdatePage /></PrivateRoute>}/>
					<Route path="/volunteer" />
					<Route path="*" element={<Navigate to="/login" replace />} />
					<Route path="/manager" element={<PrivateRoute allowedRoles={["MANAGER"]}><ManagerDashboard /></PrivateRoute>} />
					<Route path="/manager/projects" element={<PrivateRoute allowedRoles={["MANAGER"]}><ManagerProjectsPage /></PrivateRoute>} />
					<Route path="/manager/projects/:id" element={<PrivateRoute allowedRoles={["MANAGER"]}><ManagerProjectDetailPage /></PrivateRoute>} />
				</Routes>
			</BrowserRouter>
		</AuthProvider>
	);
}

export default App;
