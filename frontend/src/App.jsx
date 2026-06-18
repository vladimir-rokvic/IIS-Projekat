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
import ReturnDocumentsPage from "./pages/CampaignCoordinatorDocuments";
import VolunteerDashboard from "./pages/VolunteerDashboard";
import ProjectDeniedPage from "./pages/ProjectDeniedPage";
import ProjectAcceptedPage from "./pages/ProjectAcceptedPage";
import BeneficiaryHomePage from "./pages/BeneficiaryHomePage";
import BeneficiaryMyAccountPage from "./pages/BeneficiaryMyAccountPage";
import BeneficiaryDocumentsPage from "./pages/BeneficiaryDocumentsPage";

import CreateTaskPage from "./pages/CreateTaskPage";
import VolunteerSelectPage from "./pages/VolunteerSelectPage";
import VolunteerDetailsPage from "./pages/VolunteerDetailsPage";
import TasksPage from "./pages/TasksPage";

import BeneficiaryRegisterPage from "./pages/BeneficiaryRegisterPage";
import DonorProjectsPage from "./pages/DonorProjectsPage";
import DonorProjectDetailPage from "./pages/DonorProjectDetailPage";
import DonorProjectFullDetailPage from "./pages/DonorProjectFullDetailPage";

import TaskDetailsPage from "./pages/TaskDetailsPage";
import TaskDetailsEdit from "./pages/TaskDetailsEdit";

import NewDistributionLocation from "./pages/DistributionLocationCreationPage";
import DistributionLocationsPage from "./pages/ManagerDistributionLocationsPage";
import CreatePackage from "./pages/CreatePackage";
import NewAidDistributionPage from "./pages/NewAidDistributionPage";
import DistributionDetailsPage from "./pages/DistributionDetailsPage";

import ProjectInformationPage from "./pages/ProjectInformationPage";
import PhaseFormPage from "./pages/PhaseFormPage";
import PhaseTaskFormPage from "./pages/PhaseTaskFormPage";
import DistributionsPage from "./pages/DistributionsPage";

import CreateRegiment from "./pages/CreateRegiment";
import SelectCertificatePage from "./pages/SelectCertificatePage";
import BeneficiariesListPage from "./pages/BeneficiariesListPage";

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
					<Route path="/campaign-coordinator" element={<PrivateRoute allowedRoles={["CAMPAIGN_COORDINATOR"]}><CampaignCoordinatorDashboard /></PrivateRoute>} />
					<Route path="/campaign-coordinator/home" element={<PrivateRoute allowedRoles={["CAMPAIGN_COORDINATOR"]}><CampaignCoordinatorHomePage /></PrivateRoute>} />
					<Route path="/campaign-coordinator/campaigns" element={<PrivateRoute allowedRoles={["CAMPAIGN_COORDINATOR"]}><CampaignCoordinatorAllCampaignsPage /></PrivateRoute>} />
					<Route path="/campaign-coordinator/create-campaign" element={<PrivateRoute allowedRoles={["CAMPAIGN_COORDINATOR"]}><CampaignCoordinatorCreateCampaignPage /></PrivateRoute>} />
					<Route path="/campaign-coordinator/statistics" element={<PrivateRoute allowedRoles={["CAMPAIGN_COORDINATOR"]}><CampaignCoordinatorStatisticsPage /></PrivateRoute>} />
					<Route path="/campaign-coordinator/return-documents" element={<PrivateRoute allowedRoles={["CAMPAIGN_COORDINATOR"]}><ReturnDocumentsPage /></PrivateRoute>} />

					<Route path="/profile" element={<PrivateRoute><UserProfilePage /></PrivateRoute>}/>
					<Route path="/volunteer/update" element={<PrivateRoute><VolunteerUpdatePage /></PrivateRoute>}/>

					<Route path="/donor" element={<PrivateRoute allowedRoles={["DONOR"]}><DonorDashboardPage /></PrivateRoute>} />
					<Route path="/donor/home" element={<PrivateRoute allowedRoles={["DONOR"]}><DonorHomePage /></PrivateRoute>} />
					<Route path="/donor/campaigns" element={<PrivateRoute allowedRoles={["DONOR"]}><DonorCampaignsPage /></PrivateRoute>} />
					<Route path="/donor/profile" element={<PrivateRoute allowedRoles={["DONOR"]}><DonorProfilePage /></PrivateRoute>} />
					<Route path="/donor/projects" element={<PrivateRoute allowedRoles={["DONOR"]}><DonorProjectsPage /></PrivateRoute>} />
					<Route path="/donor/projects/:id" element={<PrivateRoute allowedRoles={["DONOR"]}><DonorProjectDetailPage /></PrivateRoute>} />
					<Route path="/donor/projects/:id/details" element={<PrivateRoute allowedRoles={["DONOR"]}><DonorProjectFullDetailPage /></PrivateRoute>} />

					<Route path="/beneficiary" element={<PrivateRoute allowedRoles={["BENEFICIARY"]}><BeneficiaryHomePage /></PrivateRoute>} />
					<Route path="/beneficiary/profile" element={<PrivateRoute allowedRoles={["BENEFICIARY"]}><BeneficiaryMyAccountPage /></PrivateRoute>} />
					<Route path="/beneficiary/register" element={<BeneficiaryRegisterPage />}/>
					<Route path="/beneficiary/documents" element={<PrivateRoute allowedRoles={["BENEFICIARY"]}><BeneficiaryDocumentsPage /></PrivateRoute>} />

					<Route path="/manager/distributionlocation" element={<PrivateRoute allowedRoles={["MANAGER"]}><NewDistributionLocation /></PrivateRoute>}/>
					<Route path="/manager/distribution/:id/package/create" element={<PrivateRoute allowedRoles={["MANAGER"]}><CreatePackage /></PrivateRoute>}/>
					<Route path="/manager/distributionlocations" element={<PrivateRoute allowedRoles={["MANAGER"]}><DistributionLocationsPage /></PrivateRoute>}/>
					<Route path="/manager/distribution/new" element={<PrivateRoute allowedRoles={["MANAGER"]}><NewAidDistributionPage /></PrivateRoute>}/>
					<Route path="/manager/distribution/:id" element={<PrivateRoute allowedRoles={["MANAGER"]}><DistributionDetailsPage /></PrivateRoute>}/>
					<Route path="/manager/distributions" element={<PrivateRoute allowedRoles={["MANAGER"]}><DistributionsPage /></PrivateRoute>}/>
					<Route path="/manager/beneficiaries" element={<PrivateRoute allowedRoles={["MANAGER"]}><BeneficiariesListPage /></PrivateRoute>}/>


					<Route path="/volunteer" element={<PrivateRoute allowedRoles={["VOLUNTEER"]}><VolunteerDashboard /></PrivateRoute>}/>
					<Route path="/manager/projects" element={<PrivateRoute allowedRoles={["MANAGER"]}><ManagerProjectsPage /></PrivateRoute>} />
					<Route path="/manager/projects/:id" element={<PrivateRoute allowedRoles={["MANAGER"]}><ManagerProjectDetailPage /></PrivateRoute>} />

					<Route path="/coord/createTask" element={<PrivateRoute allowedRoles={["COORDINATOR"]}><CreateTaskPage /></PrivateRoute>}/>
					<Route path="/manager/createRegiment" element={<PrivateRoute allowedRoles={["MANAGER"]}><CreateRegiment /></PrivateRoute>}/>
					<Route path="/coord/createTask/addVolunteer" element={<PrivateRoute allowedRoles={["COORDINATOR", "MANAGER"]}><VolunteerSelectPage /></PrivateRoute>}/>
					<Route path="/volunteer/details/:id" element={<PrivateRoute allowedRoles={["COORDINATOR", "MANAGER"]}><VolunteerDetailsPage /></PrivateRoute>}/>
					<Route path="/tasks" element={<PrivateRoute allowedRoles={["COORDINATOR"]}><TasksPage /></PrivateRoute>}/>
					<Route path="/selectCertificate" element={<PrivateRoute allowedRoles={["MANAGER"]}><SelectCertificatePage /></PrivateRoute>}/>
					<Route path="/coord/tasks/:id" element={<PrivateRoute allowedRoles={["COORDINATOR", "VOLUNTEER"]}><TaskDetailsPage /></PrivateRoute>}/>
					<Route path="/coord/tasksEdit/:id" element={<PrivateRoute allowedRoles={["COORDINATOR"]}><TaskDetailsEdit /></PrivateRoute>}/>

					<Route path="/projects/:id/info" element={<PrivateRoute allowedRoles={["COORDINATOR"]}><ProjectInformationPage /></PrivateRoute>} />
					<Route path="/projects/:id/phases/new" element={<PrivateRoute allowedRoles={["COORDINATOR"]}><PhaseFormPage /></PrivateRoute>} />
					<Route path="/projects/:id/phases/:phaseId/edit" element={<PrivateRoute allowedRoles={["COORDINATOR"]}><PhaseFormPage /></PrivateRoute>} />
					<Route path="/projects/:id/phases/:phaseId/tasks/new" element={<PrivateRoute allowedRoles={["COORDINATOR"]}><PhaseTaskFormPage /></PrivateRoute>} />
					<Route path="/projects/:id/phases/:phaseId/tasks/:taskId/edit" element={<PrivateRoute allowedRoles={["COORDINATOR"]}><PhaseTaskFormPage /></PrivateRoute>} />

					<Route path="*" element={<Navigate to="/login" replace />} />
				</Routes>
			</BrowserRouter>
		</AuthProvider>
	);
}

export default App;
