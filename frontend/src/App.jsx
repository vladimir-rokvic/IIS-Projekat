import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import PrivateRoute from "./components/PrivateRoute";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import CoordinatorDashboard from "./pages/CoordinatorDashboard";
import ProjectsPage from "./pages/ProjectsPage";
import CreateProjectPage from "./pages/CreateProjectPage";
import ManagerDashboard from "./pages/ManagerDashboard";
import EditProjectPage from "./pages/EditProjectPage";
import RegisterVolunteerPage from "./pages/RegisterVolunteerPage";

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
					<Route path="/profile" element={<PrivateRoute></PrivateRoute>}/>
					<Route path="/volunteer" />
					<Route path="*" element={<Navigate to="/login" replace />} />
				</Routes>
			</BrowserRouter>
		</AuthProvider>
	);
}

export default App;
