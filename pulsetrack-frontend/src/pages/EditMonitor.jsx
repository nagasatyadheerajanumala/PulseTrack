import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";

const EditMonitor = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [monitor, setMonitor] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchMonitor = async () => {
            try {
                const token = localStorage.getItem("token");
                const res = await fetch(`http://localhost:8080/api/monitors/${id}`, {
                    headers: { Authorization: `Bearer ${token}` },
                });

                if (!res.ok) {
                    console.error("Failed to fetch monitor details");
                    return;
                }

                const data = await res.json();
                setMonitor(data);
            } catch (err) {
                console.error("Error fetching monitor:", err);
            } finally {
                setLoading(false);
            }
        };

        fetchMonitor();
    }, [id]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setMonitor((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const token = localStorage.getItem("token");

        const res = await fetch(`http://localhost:8080/api/monitors/${id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify(monitor),
        });

        if (res.ok) {
            navigate("/dashboard");
        } else {
            const errorText = await res.text();
            console.error("Update failed:", errorText);
        }
    };

    if (loading) return <p className="text-white">Loading monitor...</p>;
    if (!monitor) return <p className="text-red-500">Monitor not found or error occurred.</p>;

    return (
        <div className="text-white px-6 py-8">
            <h2 className="text-2xl font-bold mb-4">Edit Monitor</h2>
            <form onSubmit={handleSubmit} className="space-y-4">
                <input
                    name="name"
                    value={monitor.name}
                    onChange={handleChange}
                    placeholder="Name"
                    className="w-full bg-gray-800 p-2 rounded"
                />
                <input
                    name="url"
                    value={monitor.url}
                    onChange={handleChange}
                    placeholder="URL"
                    className="w-full bg-gray-800 p-2 rounded"
                />
                <input
                    name="httpMethod"
                    value={monitor.httpMethod || "GET"}
                    onChange={handleChange}
                    placeholder="HTTP Method"
                    className="w-full bg-gray-800 p-2 rounded"
                />
                <input
                    name="checkFreq"
                    value={monitor.checkFreq}
                    onChange={handleChange}
                    placeholder="Check Frequency (min)"
                    type="number"
                    className="w-full bg-gray-800 p-2 rounded"
                />
                <input
                    name="alertFrequencyMinutes"
                    value={monitor.alertFrequencyMinutes}
                    onChange={handleChange}
                    placeholder="Alert Frequency (min)"
                    type="number"
                    className="w-full bg-gray-800 p-2 rounded"
                />
                <textarea
                    name="requestBody"
                    value={monitor.requestBody || ""}
                    onChange={handleChange}
                    placeholder="Request Body (Optional)"
                    className="w-full bg-gray-800 p-2 rounded"
                />
                <button
                    type="submit"
                    className="bg-blue-600 hover:bg-blue-700 px-4 py-2 rounded"
                >
                    Save Changes
                </button>
            </form>
        </div>
    );
};

export default EditMonitor;