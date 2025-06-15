import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";

const Navbar = () => {
    const [loggedIn, setLoggedIn] = useState(!!localStorage.getItem("token"));
    const navigate = useNavigate();

    useEffect(() => {
        const updateLoginState = () => setLoggedIn(!!localStorage.getItem("token"));
        window.addEventListener("login", updateLoginState);
        window.addEventListener("logout", updateLoginState);
        return () => {
            window.removeEventListener("login", updateLoginState);
            window.removeEventListener("logout", updateLoginState);
        };
    }, []);

    const handleLogout = () => {
        localStorage.removeItem("token");
        window.dispatchEvent(new Event("logout"));
        navigate("/");
    };

    return (
        <nav className="bg-gray-900 text-white px-6 py-4 flex justify-between items-center shadow fixed w-full top-0 z-50">
            <Link to="/" className="text-2xl font-bold hover:text-blue-400">PulseTrack</Link>

            {/* Middle navigation */}
            <div className="space-x-6 text-sm hidden md:flex">
                <Link to="/features" className="hover:text-blue-400">Features</Link>
                <Link to="/pricing" className="hover:text-blue-400">Pricing</Link>
                <Link to="/about" className="hover:text-blue-400">About</Link>
                <Link to="/contact" className="hover:text-blue-400">Contact</Link>
            </div>

            {/* Right-side auth/dashboard links */}
            <div className="space-x-4">
                {loggedIn ? (
                    <>
                        <Link to="/dashboard" className="hover:text-blue-400">Dashboard</Link>
                        <Link to="/status-pages" className="hover:text-blue-400">Status Pages</Link>
                        <button
                            onClick={handleLogout}
                            className="bg-red-600 hover:bg-red-700 text-white px-4 py-1 rounded"
                        >
                            Logout
                        </button>
                    </>
                ) : (
                    <>
                        <Link
                            to="/login"
                            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-1 rounded"
                        >
                            Login
                        </Link>
                        <Link
                            to="/register"
                            className="text-white hover:text-blue-400 border border-white px-3 py-1 rounded"
                        >
                            Get Started
                        </Link>
                    </>
                )}
            </div>
        </nav>
    );
};

export default Navbar;