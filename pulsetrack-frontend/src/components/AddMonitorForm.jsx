// src/components/AddMonitorForm.jsx
import { useState } from 'react';

const AddMonitorForm = ({ onMonitorAdded }) => {
    const [name, setName] = useState('');
    const [url, setUrl] = useState('');
    const [checkFreq, setCheckFreq] = useState(5);

    const handleSubmit = async (e) => {
        e.preventDefault();
        const token = localStorage.getItem("token");

        const res = await fetch('http://localhost:8080/api/monitors', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify({ name, url, checkFreq }),
        });

        if (res.ok) {
            setName('');
            setUrl('');
            setCheckFreq(5);
            onMonitorAdded(); // refresh list
        } else {
            alert('Failed to add monitor');
        }
    };

    return (
        <form onSubmit={handleSubmit} className="mb-6 bg-gray-800 p-4 rounded-md shadow">
            <h3 className="text-lg font-semibold text-white mb-4">Add Monitor</h3>
            <div className="flex flex-col gap-2">
                <input
                    type="text"
                    placeholder="Monitor Name"
                    className="p-2 rounded bg-gray-700 text-white"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    required
                />
                <input
                    type="url"
                    placeholder="https://example.com"
                    className="p-2 rounded bg-gray-700 text-white"
                    value={url}
                    onChange={(e) => setUrl(e.target.value)}
                    required
                />
                <input
                    type="number"
                    min={1}
                    className="p-2 rounded bg-gray-700 text-white"
                    value={checkFreq}
                    onChange={(e) => setCheckFreq(e.target.value)}
                    placeholder="Frequency (min)"
                    required
                />
                <button className="bg-blue-600 hover:bg-blue-700 text-white p-2 rounded mt-2" type="submit">
                    Add Monitor
                </button>
            </div>
        </form>
    );
};

export default AddMonitorForm;