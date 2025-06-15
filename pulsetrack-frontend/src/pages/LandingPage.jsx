// src/pages/LandingPage.jsx
import { useEffect } from "react";
import AOS from "aos";
import { Link } from "react-router-dom";

const LandingPage = () => {
    useEffect(() => {
        AOS.init({ duration: 800, once: true });
    }, []);

    const isLoggedIn = !!localStorage.getItem("token");

    return (
        <div className="bg-gray-900 text-white min-h-screen scroll-smooth">
            {/* Hero */}
            <section className="text-center py-24 px-6 bg-gradient-to-r from-indigo-600 to-blue-600">
                <h1 data-aos="fade-down" className="text-4xl md:text-5xl font-bold mb-4">PulseTrack</h1>
                <p data-aos="fade-up" className="text-lg md:text-xl max-w-2xl mx-auto mb-6">
                    Uptime & Log Monitoring for your websites, APIs, and services — all in one sleek dashboard.
                </p>
                <Link
                    to={isLoggedIn ? "/dashboard" : "/register"}
                    data-aos="zoom-in"
                    className="bg-white text-indigo-700 font-semibold px-6 py-3 rounded hover:bg-gray-100"
                >
                    {isLoggedIn ? "Go to Dashboard" : "Start Monitoring Free"}
                </Link>
            </section>

            {/* Features */}
            <section id="features" className="py-16 px-6 bg-gray-800">
                <h2 data-aos="fade-up" className="text-3xl font-bold text-center mb-10">Why Choose PulseTrack?</h2>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-8 max-w-6xl mx-auto">
                    {[
                        {
                            title: "Real-Time Monitoring",
                            desc: "Track uptime, response times, and status codes instantly.",
                        },
                        {
                            title: "Custom Status Pages",
                            desc: "Share real-time availability with your users via public links.",
                        },
                        {
                            title: "Smart Alerts",
                            desc: "Get notified by email or Slack when something breaks.",
                        },
                    ].map((feature, idx) => (
                        <div
                            key={idx}
                            data-aos="fade-up"
                            data-aos-delay={idx * 200}
                            className="bg-gray-700 p-6 rounded shadow"
                        >
                            <h3 className="text-xl font-semibold mb-2">{feature.title}</h3>
                            <p className="text-gray-300">{feature.desc}</p>
                        </div>
                    ))}
                </div>
            </section>

            {/* Screenshot */}
            <section id="about" className="py-16 px-6 bg-gray-900 text-center">
                <h2 data-aos="fade-up" className="text-3xl font-bold mb-6">Sleek, Developer-Friendly Dashboard</h2>
                <img
                    data-aos="zoom-in"
                    src="/demo-dashboard.png"
                    alt="PulseTrack Demo"
                    className="rounded-lg shadow-lg mx-auto max-w-4xl"
                />
            </section>

            {/* Pricing */}
            <section id="pricing" className="py-16 px-6 bg-indigo-700 text-center">
                <h2 data-aos="fade-right" className="text-3xl font-bold mb-4">Start Monitoring in Seconds</h2>
                <p className="mb-6 text-lg">
                    Create an account and set up your first monitor in under 2 minutes. Free forever for basic use.
                </p>
                <Link
                    to={isLoggedIn ? "/dashboard" : "/register"}
                    data-aos="zoom-in"
                    className="bg-white text-indigo-700 font-semibold px-6 py-3 rounded hover:bg-gray-100"
                >
                    {isLoggedIn ? "Go to Dashboard" : "Get Started Free"}
                </Link>
            </section>

            {/* Contact */}
            <section id="contact" className="py-16 px-6 bg-gray-800 text-center">
                <h2 data-aos="fade-up" className="text-3xl font-bold mb-4">Contact Us</h2>
                <p className="text-gray-400 mb-2">For support, suggestions, or feedback:</p>
                <p className="text-gray-300">Email: support@pulsetrack.io</p>
            </section>

            {/* Footer */}
            <footer className="bg-gray-900 py-6 text-center text-sm text-gray-500">
                © {new Date().getFullYear()} PulseTrack ·{" "}
                <Link to="/privacy" className="hover:text-white">Privacy</Link> ·{" "}
                <Link to="/terms" className="hover:text-white">Terms</Link>
            </footer>
        </div>
    );
};

export default LandingPage;