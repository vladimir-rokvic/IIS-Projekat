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
import DonorDashboardPage from "./pages/DonorDashboardPage";
import DonorHomePage from "./pages/DonorHomePage";
import DonorCampaignsPage from "./pages/DonorCampaignsPage";
import DonorProfilePage from "./pages/DonorProfilePage";
import CampaignCoordinatorDashboard from "./pages/CampaignCoordinatorDashboard";
import CampaignCoordinatorHomePage from "./pages/CampaignCoordinatorHomePage";
import CampaignCoordinatorAllCampaignsPage from "./pages/CampaignCoordinatorAllCampaignsPage";
import CampaignCoordinatorCreateCampaignPage from "./pages/CampaignCoordinatorCreateCampaignPage";
import CampaignCoordinatorStatisticsPage from "./pages/CampaignCoordinatorStatisticsPage";
import VolunteerDashboard from "./pages/VolunteerDashboard";
import ProjectDeniedPage from "./pages/ProjectDeniedPage";
import ProjectAcceptedPage from "./pages/ProjectAcceptedPage";
import BeneficiaryHomePage from "./pages/BeneficiaryHomePage";
import BeneficiaryMyAccountPage from "./pages/BeneficiaryMyAccountPage";
import BeneficiaryRegisterPage from "./pages/BeneficiaryRegisterPage";
import DonorProjectsPage from "./pages/DonorProjectsPage";
import DonorProjectDetailPage from "./pages/DonorProjectDetailPage";
import DonorProjectFullDetailPage from "./pages/DonorProjectFullDetailPage";
import NewDistributionLocation from "./pages/DistributionLocationPage";

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
					<Route path="/projects/:id/denied" element={<PrivateRoute allowedRoles={["COORDINATOR"]}><ProjectDeniedPage /></PrivateRoute>} />
					<Route path="/projects/:id/accepted" element={<PrivateRoute allowedRoles={["COORDINATOR"]}><ProjectAcceptedPage /></PrivateRoute>} />
					<Route path="/manager" element={<PrivateRoute allowedRoles={["MANAGER"]}><ManagerDashboard /></PrivateRoute>} />
					<Route path="/manager/registerVolunteer" element={<PrivateRoute allowedRoles={["MANAGER"]}><RegisterVolunteerPage /></PrivateRoute>} />
					<Route path="/campaign-coordinator" element={<PrivateRoute allowedRoles={["CAMPAIGN_COORDINATOR"]}><CampaignCoordinatorDashboard /></PrivateRoute>}>
						<Route index element={<CampaignCoordinatorHomePage />} />
						<Route path="all-campaigns" element={<CampaignCoordinatorAllCampaignsPage />} />
						<Route path="create-campaign" element={<CampaignCoordinatorCreateCampaignPage />} />
						<Route path="statistics" element={<CampaignCoordinatorStatisticsPage />} />
					</Route>
					<Route path="/profile" element={<PrivateRoute><UserProfilePage /></PrivateRoute>}/>
					<Route path="/volunteer/update" element={<PrivateRoute><VolunteerUpdatePage /></PrivateRoute>}/>

					<Route path="/donor" element={<PrivateRoute allowedRoles={["DONOR"]}><DonorDashboardPage /></PrivateRoute>}>
						<Route index element={<DonorHomePage />} />
						<Route path="campaigns" element={<DonorCampaignsPage />} />
						<Route path="profile" element={<DonorProfilePage />} />
						<Route path="projects" element={<DonorProjectsPage />} />
						<Route path="projects/:id" element={<DonorProjectDetailPage />} />
						<Route path="projects/:id/details" element={<DonorProjectFullDetailPage />} />
					</Route>

					<Route path="/beneficiary" element={<PrivateRoute allowedRoles={["BENEFICIARY"]}><BeneficiaryHomePage /></PrivateRoute>} />
					<Route path="/beneficiary/profile" element={<PrivateRoute allowedRoles={["BENEFICIARY"]}><BeneficiaryMyAccountPage /></PrivateRoute>} />
					<Route path="/beneficiary/register" element={<BeneficiaryRegisterPage />}/>
					<Route path="/manager/distributionlocation" element={<NewDistributionLocation />}/>


					<Route path="/volunteer" element={<PrivateRoute allowedRoles={["VOLUNTEER"]}><VolunteerDashboard /></PrivateRoute>}/>
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
