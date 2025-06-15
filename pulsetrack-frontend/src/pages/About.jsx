// src/pages/About.jsx
import { useEffect } from "react";
import AOS from "aos";

const About = () => {
    useEffect(() => {
        AOS.init({ duration: 800, once: true });
    }, []);

    return (
        <div className="bg-gray-900 text-white min-h-screen px-6 py-12">
            <div className="max-w-4xl mx-auto">
                <h1 data-aos="fade-down" className="text-4xl font-bold mb-6 text-center">About PulseTrack</h1>

                <p data-aos="fade-up" className="text-lg text-gray-300 mb-6">
                    PulseTrack is built for developers, startups, freelancers, and small businesses who want simple but powerful uptime monitoring.
                </p>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div data-aos="fade-right" className="bg-gray-800 p-6 rounded shadow">
                        <h2 className="text-2xl font-semibold mb-2">Our Mission</h2>
                        <p className="text-gray-400">
                            To democratize monitoring by making uptime tracking accessible, beautiful, and developer-friendly — no bloated dashboards, just clarity.
                        </p>
                    </div>
                    <div data-aos="fade-left" className="bg-gray-800 p-6 rounded shadow">
                        <h2 className="text-2xl font-semibold mb-2">Why We Exist</h2>
                        <p className="text-gray-400">
                            Most existing tools are either too simple or too complex. PulseTrack fills the sweet spot: insightful, real-time monitoring that's fast to set up and easy to share.
                        </p>
                    </div>
                </div>

                <div data-aos="fade-up" className="mt-10 bg-indigo-700 p-6 rounded shadow text-center">
                    <h3 className="text-xl font-bold mb-2">Built with ❤️ for developers.</h3>
                    <p>Monitor your apps, impress your users, and never miss a heartbeat.</p>
                </div>
            </div>
        </div>
    );
};

export default About;