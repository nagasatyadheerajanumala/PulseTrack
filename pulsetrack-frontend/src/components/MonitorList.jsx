// src/components/MonitorList.jsx
import { useEffect, useState } from 'react';

const MonitorList = () => {
    const [monitors, setMonitors] = useState([]);
    const [loading, setLoading] = useState(true);

    const fetchMonitors = async () => {
        try {
            const res = await fetch('http://localhost:8080/api/monitors', {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`
                }
            });
            const data = await res.json();
            setMonitors(data);
        } catch (err) {
            console.error('Failed to fetch monitors:', err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchMonitors();
    }, []);

    return (
        <div className="mt-6">
            <h3 className="text-xl font-semibold mb-4">Your Monitors</h3>
            {loading ? (
                <p>Loading monitors...</p>
            ) : (
                <table className="w-full border text-left">
                    <thead className="bg-gray-100">
                    <tr>
                        <th className="p-2">Name</th>
                        <th className="p-2">URL</th>
                        <th className="p-2">Frequency (min)</th>
                        <th className="p-2">Active</th>
                    </tr>
                    </thead>
                    <tbody>
                    {monitors.map(m => (
                        <tr key={m.id} className="border-b">
                            <td className="p-2">{m.name}</td>
                            <td className="p-2">{m.url}</td>
                            <td className="p-2">{m.checkFreq}</td>
                            <td className="p-2">
                                {m.isActive ? (
                                    <span className="text-green-600 font-medium">✔️ Yes</span>
                                ) : (
                                    <span className="text-red-600 font-medium">❌ No</span>
                                )}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default MonitorList;