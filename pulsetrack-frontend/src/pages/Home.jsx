import { Player } from "@lottiefiles/react-lottie-player";
import monitorAnimation from "../assets/monitor.json";
import { useNavigate } from "react-router-dom";

const HomePage = () => {
    const navigate = useNavigate();

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-900 text-white p-6">
            <Player
                autoplay
                loop
                src={monitorAnimation}
                style={{ height: '280px', width: '280px' }}
            />

            <h1 className="text-4xl sm:text-5xl font-extrabold mt-6 bg-gradient-to-r from-purple-400 via-blue-500 to-teal-400 bg-clip-text text-transparent animate-fadeIn">
                Track Your Uptime. Instantly.
            </h1>

            <p className="mt-4 text-gray-400 text-center max-w-xl text-lg animate-fadeIn delay-200">
                Get real-time service monitoring with beautiful dashboards, intelligent alerts, and public status pages.
            </p>

            <button
                onClick={() => navigate('/dashboard')}
                className="mt-8 px-6 py-3 bg-gradient-to-r from-indigo-500 to-purple-600 text-white font-semibold rounded shadow hover:from-indigo-600 hover:to-purple-700 transition-all duration-300"
            >
                🚀 Go to Dashboard
            </button>
        </div>
    );
};

export default HomePage;