import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const Dashboard = () => {
    const [monitors, setMonitors] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    const fetchMonitors = async () => {
        try {
            const token = localStorage.getItem("token");
            const res = await fetch("http://localhost:8080/api/monitors", {
                headers: { Authorization: `Bearer ${token}` },
            });

            if (!res.ok) {
                const errorText = await res.text();
                console.error(`Failed to fetch monitors (${res.status}):`, errorText);
                setMonitors([]);
                return;
            }

            const data = await res.json();
            setMonitors(data);
        } catch (err) {
            console.error("Network error while fetching monitors:", err);
            setMonitors([]);
        } finally {
            setLoading(false);
        }
    };

    const toggleMonitor = async (id) => {
        try {
            const token = localStorage.getItem("token");
            const res = await fetch(`http://localhost:8080/api/monitors/${id}/toggle`, {
                method: "PUT",
                headers: { Authorization: `Bearer ${token}` },
            });

            const result = await res.json(); // Get { active: true/false }

            if (!res.ok) {
                console.error("Failed to toggle monitor:", result);
                return;
            }

            setMonitors((prev) =>
                prev.map((monitor) =>
                    monitor.id === id ? { ...monitor, isActive: result.active } : monitor
                )
            );
        } catch (err) {
            console.error("Error toggling monitor:", err);
        }
    };

    const deleteMonitor = async (id) => {
        try {
            const token = localStorage.getItem("token");
            await fetch(`http://localhost:8080/api/monitors/${id}`, {
                method: "DELETE",
                headers: { Authorization: `Bearer ${token}` },
            });
            fetchMonitors();
        } catch (err) {
            console.error("Failed to delete monitor:", err);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem("token");
        navigate("/login");
    };

    useEffect(() => {
        fetchMonitors();
    }, []);

    return (
        <div className="min-h-screen bg-gray-900 text-white px-6 py-8">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold">Dashboard</h1>
                <button
                    onClick={handleLogout}
                    className="bg-red-600 hover:bg-red-700 px-4 py-2 rounded"
                >
                    Logout
                </button>
            </div>

            <p className="text-gray-400 mb-6">Manage your monitors and track system health.</p>

            <div className="bg-gray-800 rounded-md p-4 shadow overflow-x-auto">
                <h2 className="text-xl font-medium mb-4">Your Monitors</h2>
                {loading ? (
                    <p>Loading monitors...</p>
                ) : (
                    <table className="w-full text-sm">
                        <thead>
                        <tr className="text-left text-gray-300 border-b border-gray-700">
                            <th className="py-2">Name</th>
                            <th className="py-2">URL</th>
                            <th className="py-2">Method</th>
                            <th className="py-2">Freq (min)</th>
                            <th className="py-2">Alert Freq (min)</th>
                            <th className="py-2">Uptime %</th>
                            <th className="py-2">Active</th>
                            <th className="py-2">Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {monitors.length === 0 ? (
                            <tr>
                                <td colSpan="8" className="py-4 text-center text-gray-400">
                                    No monitors found.
                                </td>
                            </tr>
                        ) : (
                            monitors.map((monitor) => (
                                <tr key={monitor.id} className="border-b border-gray-700">
                                    <td className="py-2">{monitor.name}</td>
                                    <td className="py-2">{monitor.url}</td>
                                    <td className="py-2">{monitor.httpMethod || "GET"}</td>
                                    <td className="py-2">{monitor.checkFreq}</td>
                                    <td className="py-2">{monitor.alertFrequencyMinutes || "N/A"}</td>
                                    <td className="py-2">
                                        {typeof monitor.uptimePercent === "number"
                                            ? `${monitor.uptimePercent.toFixed(2)}%`
                                            : "N/A"}
                                    </td>
                                    <td className="py-2">
                                        {monitor.isActive ? (
                                            <span className="text-green-500">✔️ Yes</span>
                                        ) : (
                                            <span className="text-red-500">❌ No</span>
                                        )}
                                    </td>
                                    <td className="py-2 space-x-1">
                                        <button
                                            onClick={() => toggleMonitor(monitor.id)}
                                            className="px-2 py-1 text-xs bg-yellow-600 hover:bg-yellow-700 rounded"
                                        >
                                            Toggle
                                        </button>
                                        <button
                                            onClick={() => deleteMonitor(monitor.id)}
                                            className="px-2 py-1 text-xs bg-red-600 hover:bg-red-700 rounded"
                                        >
                                            Delete
                                        </button>
                                        <button
                                            onClick={() =>
                                                navigate(`/monitors/${monitor.id}/analytics`)
                                            }
                                            className="px-2 py-1 text-xs bg-indigo-600 hover:bg-indigo-700 rounded"
                                        >
                                            View Analytics
                                        </button>
                                        <button
                                            onClick={() =>
                                                navigate(`/edit-monitor/${monitor.id}`)
                                            }
                                            className="px-2 py-1 text-xs bg-green-600 hover:bg-green-700 rounded"
                                        >
                                            Edit
                                        </button>
                                    </td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                )}
            </div>

            <div className="mt-6 flex justify-end">
                <button
                    onClick={() => navigate("/add-monitor")}
                    className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
                >
                    Add Monitor
                </button>
            </div>
        </div>
    );
};

export default Dashboard;