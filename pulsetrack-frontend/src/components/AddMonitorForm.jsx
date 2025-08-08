import { useState } from 'react';

const AddMonitorForm = ({ onMonitorAdded }) => {
    const [name, setName] = useState('');
    const [url, setUrl] = useState('');
    const [checkFreq, setCheckFreq] = useState(5);
    const [alertFrequencyMinutes, setAlertFrequencyMinutes] = useState(15);
    const [httpMethod, setHttpMethod] = useState('GET');
    const [headers, setHeaders] = useState('');
    const [requestBody, setRequestBody] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        const token = localStorage.getItem("token");

        let parsedHeaders = {};
        try {
            parsedHeaders = headers ? JSON.parse(headers) : {};
        } catch (err) {
            alert("Invalid JSON in headers");
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
            setName('');
            setUrl('');
            setCheckFreq(5);
            setAlertFrequencyMinutes(15);
            setHttpMethod('GET');
            setHeaders('');
            setRequestBody('');
            onMonitorAdded();
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
                <select
                    value={httpMethod}
                    onChange={(e) => setHttpMethod(e.target.value)}
                    className="p-2 rounded bg-gray-700 text-white"
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
                    className="p-2 rounded bg-gray-700 text-white"
                    value={checkFreq}
                    onChange={(e) => setCheckFreq(e.target.value)}
                    required
                />
                <input
                    type="number"
                    min={1}
                    placeholder="Alert Frequency (minutes)"
                    className="p-2 rounded bg-gray-700 text-white"
                    value={alertFrequencyMinutes}
                    onChange={(e) => setAlertFrequencyMinutes(e.target.value)}
                />
                <textarea
                    placeholder='{"Authorization": "Bearer xyz"}'
                    className="p-2 rounded bg-gray-700 text-white"
                    value={headers}
                    onChange={(e) => setHeaders(e.target.value)}
                    rows={3}
                />
                <textarea
                    placeholder="Raw JSON body for POST/PUT"
                    className="p-2 rounded bg-gray-700 text-white"
                    value={requestBody}
                    onChange={(e) => setRequestBody(e.target.value)}
                    rows={3}
                />
                <button className="bg-blue-600 hover:bg-blue-700 text-white p-2 rounded mt-2" type="submit">
                    Add Monitor
                </button>
            </div>
        </form>
    );
};

export default AddMonitorForm;