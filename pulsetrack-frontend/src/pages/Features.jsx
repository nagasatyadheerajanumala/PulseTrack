import React from "react";

const features = [
    {
        title: "Real-Time Uptime Monitoring",
        description:
            "Get instant feedback on your website’s availability with frequent checks and status updates.",
        icon: "📡",
    },
    {
        title: "Custom Status Pages",
        description:
            "Generate public URLs to share live uptime analytics with customers or team members.",
        icon: "🌐",
    },
    {
        title: "Detailed Analytics",
        description:
            "Track uptime %, average response time, and HTTP status codes with rich graphs and charts.",
        icon: "📊",
    },
    {
        title: "Smart Alerts",
        description:
            "Receive alerts via Email or Slack when a monitor fails or recovers — stay informed in real time.",
        icon: "🔔",
    },
    {
        title: "Embeddable Uptime Badges",
        description:
            "Display a beautiful SVG badge showing your uptime, perfect for readmes and dashboards.",
        icon: "🏷️",
    },
    {
        title: "Public + Private Monitoring",
        description:
            "Track internal APIs or public websites — PulseTrack supports both with secure access.",
        icon: "🔐",
    },
];

const Features = () => {
    return (
        <div className="bg-gray-900 text-white min-h-screen px-6 py-16">
            <div className="max-w-5xl mx-auto text-center mb-12">
                <h1 className="text-4xl font-bold mb-4">Features that Power PulseTrack</h1>
                <p className="text-gray-400 text-lg">
                    Everything you need to monitor uptime, response time, and reliability — with simplicity.
                </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 max-w-6xl mx-auto">
                {features.map((f, i) => (
                    <div
                        key={i}
                        className="bg-gray-800 rounded-lg p-6 shadow hover:shadow-xl transition"
                        data-aos="fade-up"
                        data-aos-delay={i * 100}
                    >
                        <div className="text-4xl mb-4">{f.icon}</div>
                        <h3 className="text-xl font-semibold mb-2">{f.title}</h3>
                        <p className="text-gray-300 text-sm">{f.description}</p>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Features;