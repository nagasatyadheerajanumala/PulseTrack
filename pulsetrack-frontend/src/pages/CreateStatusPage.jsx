// src/pages/CreateStatusPage.jsx
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const CreateStatusPage = () => {
    const [name, setName] = useState("");
    const [monitors, setMonitors] = useState([]);
    const [selected, setSelected] = useState([]);
    const navigate = useNavigate();

    const token = localStorage.getItem("token");

    useEffect(() => {
        fetch("http://localhost:8080/api/monitors", {
            headers: { Authorization: `Bearer ${token}` },
        })
            .then((res) => res.json())
            .then(setMonitors);
    }, []);

    const handleCheckbox = (id) => {
        setSelected((prev) =>
            prev.includes(id) ? prev.filter((m) => m !== id) : [...prev, id]
        );
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const res = await fetch("http://localhost:8080/api/status-pages", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify({
                name: name,
                monitorIds: selected, // ✅ send selected monitor IDs directly
            }),
        });

        if (!res.ok) {
            alert("Failed to create status page");
            return;
        }

        // Optional: show confirmation
        navigate("/status-pages");
    };

    return (
        <div className="max-w-xl mx-auto mt-10 p-6 bg-gray-800 rounded shadow text-white">
            <h2 className="text-xl font-bold mb-4">Create Status Page</h2>
            <form onSubmit={handleSubmit} className="space-y-4">
                <input
                    type="text"
                    className="w-full p-2 rounded bg-gray-700"
                    placeholder="Status Page Name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    required
                />

                <div>
                    <h3 className="font-medium mb-2">Select Monitors</h3>
                    <div className="space-y-2 max-h-48 overflow-y-auto">
                        {monitors.map((m) => (
                            <label key={m.id} className="block">
                                <input
                                    type="checkbox"
                                    className="mr-2"
                                    checked={selected.includes(m.id)}
                                    onChange={() => handleCheckbox(m.id)}
                                />
                                {m.name} - {m.url}
                            </label>
                        ))}
                    </div>
                </div>

                <button
                    type="submit"
                    className="bg-blue-600 hover:bg-blue-700 px-4 py-2 rounded"
                >
                    Create
                </button>
            </form>
        </div>
    );
};

export default CreateStatusPage;