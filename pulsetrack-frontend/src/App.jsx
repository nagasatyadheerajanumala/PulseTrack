import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar.jsx';
import Home from './pages/Home';
import Dashboard from './pages/Dashboard';
import Login from './pages/Login';
import Register from './pages/Register';
import AddMonitor from './pages/AddMonitor';
import StatusPages from './pages/StatusPages';
import StatusPageDetails from './pages/StatusPageDetails';
import CreateStatusPage from "./pages/CreateStatusPage.jsx";
import MonitorAnalytics from "./pages/MonitorAnalytics.jsx";
import LandingPage from "./pages/LandingPage";
import About from "./pages/About";
import Contact from "./pages/Contact";
import Features from "./pages/Features";
import Layout from "./components/Layout";

function App() {
    return (
        <div className="min-h-screen bg-gray-900 text-white">
            <Router>
                <Navbar />
                <div className="p-6 max-w-7xl mx-auto">
                    <Routes>
                        <Route path="/" element={<LandingPage />} />
                        <Route path="/dashboard" element={<Dashboard />} />
                        <Route path="/login" element={<Login />} />
                        <Route path="/register" element={<Register />} />
                        <Route path="/add-monitor" element={<AddMonitor />} />
                        <Route path="/status-pages" element={<StatusPages />} />
                        <Route path="/status-pages/public/:publicKey" element={<StatusPageDetails />} />
                        <Route path="/create-status-page" element={<CreateStatusPage />} />
                        <Route path ="/monitors/:id/analytics" element={<MonitorAnalytics />} />
                        <Route path="/about" element={<About />} />
                        <Route path="/contact" element={<Contact />} />
                        <Route path="/features" element={<Features />} />
                    </Routes>
                </div>
            </Router>
        </div>
    );
}

export default App;