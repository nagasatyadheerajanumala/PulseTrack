import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const AddMonitor = () => {
    const [name, setName] = useState('');
    const [url, setUrl] = useState('');
    const [checkFreq, setCheckFreq] = useState(5);
    const [alertFrequencyMinutes, setAlertFrequencyMinutes] = useState(15);
    const [httpMethod, setHttpMethod] = useState('GET');
    const [headers, setHeaders] = useState('');
    const [requestBody, setRequestBody] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        const token = localStorage.getItem("token");

        let parsedHeaders = {};
        try {
            parsedHeaders = headers ? JSON.parse(headers) : {};
        } catch (err) {
            alert("Invalid headers JSON");
            return;
        }

        const payload = {
            name,
            url,
            checkFreq,
            alertFrequencyMinutes,
            httpMethod,
            headers: parsedHeaders,
            requestBody,
        };

        const res = await fetch('http://localhost:8080/api/monitors', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify(payload),
        });

        if (res.ok) {
            navigate('/dashboard');
        } else {
            alert('Failed to add monitor');
        }
    };

    return (
        <div className="max-w-2xl mx-auto mt-20 p-6 bg-gray-900 rounded shadow text-white">
            <h2 className="text-2xl font-semibold mb-4">Add New Monitor</h2>
            <form onSubmit={handleSubmit} className="space-y-4">
                <input
                    type="text"
                    placeholder="Monitor Name"
                    className="w-full px-4 py-2 bg-gray-800 text-white rounded"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    required
                />
                <input
                    type="url"
                    placeholder="https://example.com"
                    className="w-full px-4 py-2 bg-gray-800 text-white rounded"
                    value={url}
                    onChange={(e) => setUrl(e.target.value)}
                    required
                />
                <select
                    className="w-full px-4 py-2 bg-gray-800 text-white rounded"
                    value={httpMethod}
                    onChange={(e) => setHttpMethod(e.target.value)}
                >
                    <option value="GET">GET</option>
                    <option value="POST">POST</option>
                    <option value="PUT">PUT</option>
                    <option value="DELETE">DELETE</option>
                </select>
                <input
                    type="number"
                    min={1}
                    placeholder="Check Frequency (minutes)"
                    className="w-full px-4 py-2 bg-gray-800 text-white rounded"
                    value={checkFreq}
                    onChange={(e) => setCheckFreq(e.target.value)}
                    required
                />
                <input
                    type="number"
                    min={1}
                    placeholder="Alert Frequency (minutes)"
                    className="w-full px-4 py-2 bg-gray-800 text-white rounded"
                    value={alertFrequencyMinutes}
                    onChange={(e) => setAlertFrequencyMinutes(e.target.value)}
                    required
                />
                <textarea
                    placeholder='Headers (JSON) — e.g., {"Authorization": "Bearer abc"}'
                    className="w-full px-4 py-2 bg-gray-800 text-white rounded"
                    value={headers}
                    onChange={(e) => setHeaders(e.target.value)}
                    rows={3}
                />
                <textarea
                    placeholder="Request Body (raw JSON for POST/PUT)"
                    className="w-full px-4 py-2 bg-gray-800 text-white rounded"
                    value={requestBody}
                    onChange={(e) => setRequestBody(e.target.value)}
                    rows={4}
                />
                <div className="flex justify-between">
                    <button
                        type="button"
                        onClick={() => navigate('/dashboard')}
                        className="bg-gray-600 hover:bg-gray-700 text-white px-4 py-2 rounded"
                    >
                        Cancel
                    </button>
                    <button
                        type="submit"
                        className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
                    >
                        Add Monitor
                    </button>
                </div>
            </form>
        </div>
    );
};

export default AddMonitor;