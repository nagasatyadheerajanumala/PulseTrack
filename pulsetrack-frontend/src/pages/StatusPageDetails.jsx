import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import axios from "axios";
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    BarElement,
    Title,
    Tooltip,
    Legend,
    TimeScale,
} from "chart.js";
import { Line, Bar } from "react-chartjs-2";
import "chartjs-adapter-date-fns";

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    BarElement,
    Title,
    Tooltip,
    Legend,
    TimeScale
);

const StatusPageDetails = () => {
    const { publicKey } = useParams();
    const [statusPage, setStatusPage] = useState(null);
    const [loading, setLoading] = useState(true);
    const [logs, setLogs] = useState({});
    const [chartType, setChartType] = useState({});
    const [badgeError, setBadgeError] = useState(false);

    useEffect(() => {
        const fetchStatusPage = async () => {
            try {
                const res = await axios.get(`http://localhost:8080/api/status-pages/public/${publicKey}`);
                setStatusPage(res.data);
            } catch (err) {
                console.error("Failed to load status page:", err);
            } finally {
                setLoading(false);
            }
        };
        fetchStatusPage();
    }, [publicKey]);

    useEffect(() => {
        if (!statusPage) return;

        const fetchLogs = async () => {
            const updatedLogs = {};
            for (const monitor of statusPage.monitors) {
                const res = await axios.get(
                    `http://localhost:8080/api/status-pages/public/${publicKey}/monitor/${monitor.id}/logs`
                );
                updatedLogs[monitor.id] = res.data;
            }
            setLogs(updatedLogs);
        };

        fetchLogs();
        const interval = setInterval(fetchLogs, 30000);
        return () => clearInterval(interval);
    }, [statusPage, publicKey]);

    const exportCSV = (monitorId) => {
        const data = logs[monitorId];
        if (!data || data.length === 0) return;

        const csvRows = ["timestamp,statusCode,responseTime"];
        data.forEach((log) => {
            csvRows.push(`${log.timestamp},${log.statusCode},${log.responseTime}`);
        });

        const blob = new Blob([csvRows.join("\n")], { type: "text/csv" });
        const url = URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.download = `monitor-${monitorId}-logs.csv`;
        link.click();
        URL.revokeObjectURL(url);
    };

    const copyToClipboard = async (text) => {
        try {
            await navigator.clipboard.writeText(text);
            alert("Copied to clipboard!");
        } catch (err) {
            alert("Failed to copy text.");
        }
    };

    if (loading) return <p className="p-8 text-white">Loading status page...</p>;
    if (!statusPage) return <p className="p-8 text-red-400">Status page not found</p>;

    const badgeUrl = `http://localhost:8080/api/status-pages/public/${publicKey}/badge`;

    return (
        <div className="p-8 bg-gray-900 text-white min-h-screen">
            <h1 className="text-3xl font-bold mb-4">{statusPage.name}</h1>
            <p className="mb-6 text-gray-400">Public key: {publicKey}</p>

            {/* Embed Badge */}
            <div className="mb-10 bg-gray-800 p-4 rounded">
                <h2 className="text-xl font-semibold mb-2">Embed Uptime Badge</h2>

                {!badgeError && (
                    <img
                        src={badgeUrl}
                        alt="Uptime Badge"
                        className="mb-2"
                        style={{ border: "1px solid white", width: "fit-content" }}
                        onError={() => setBadgeError(true)}
                    />
                )}

                <p className="text-gray-400 text-sm italic mb-3">
                    If the badge doesn't appear, ensure the image is served as <code>image/svg+xml</code> and CORS is allowed.
                </p>

                <div className="text-sm bg-gray-900 p-3 rounded mt-3">
                    {/* HTML */}
                    <div className="flex items-center mb-1">
                        <span className="mr-2 font-semibold">HTML:</span>
                        <button
                            onClick={() =>
                                copyToClipboard(`<img src="${badgeUrl}" alt="Uptime Badge"/>`)
                            }
                            className="text-xs bg-gray-700 hover:bg-gray-600 px-2 py-1 rounded"
                        >
                            Copy
                        </button>
                    </div>
                    <pre className="overflow-x-auto bg-black text-white p-2 rounded text-xs mb-3">
                        <code>{`<img src="${badgeUrl}" alt="Uptime Badge"/>`}</code>
                    </pre>

                    {/* Markdown */}
                    <div className="flex items-center mb-1">
                        <span className="mr-2 font-semibold">Markdown:</span>
                        <button
                            onClick={() =>
                                copyToClipboard(`![Uptime Badge](${badgeUrl})`)
                            }
                            className="text-xs bg-gray-700 hover:bg-gray-600 px-2 py-1 rounded"
                        >
                            Copy
                        </button>
                    </div>
                    <pre className="overflow-x-auto bg-black text-white p-2 rounded text-xs">
                        <code>{`![Uptime Badge](${badgeUrl})`}</code>
                    </pre>
                </div>
            </div>

            {/* Monitors */}
            {statusPage.monitors.length === 0 ? (
                <p>No monitors available.</p>
            ) : (
                statusPage.monitors.map((monitor) => {
                    const monitorLogs = logs[monitor.id] || [];
                    const currentChart = chartType[monitor.id] || "line";

                    const chartData = {
                        labels: monitorLogs.map((log) => new Date(log.timestamp)),
                        datasets: [
                            {
                                label: "Response Time (ms)",
                                data: monitorLogs.map((log) => log.responseTime),
                                borderColor: "rgba(75, 192, 192, 1)",
                                backgroundColor: "rgba(75, 192, 192, 0.2)",
                                yAxisID: 'y',
                            },
                            {
                                label: "Status Code",
                                data: monitorLogs.map((log) => log.statusCode),
                                type: "line",
                                borderColor: "rgba(255, 99, 132, 1)",
                                backgroundColor: "rgba(255, 99, 132, 0.2)",
                                yAxisID: 'y1',
                            },
                        ],
                    };

                    const chartOptions = {
                        responsive: true,
                        plugins: {
                            legend: { position: "top" },
                            title: { display: true, text: "Monitor Logs (Response Time & Status Code)" },
                        },
                        scales: {
                            y: {
                                type: "linear",
                                position: "left",
                                title: { display: true, text: "Response Time (ms)" },
                            },
                            y1: {
                                type: "linear",
                                position: "right",
                                title: { display: true, text: "Status Code" },
                                grid: { drawOnChartArea: false },
                                suggestedMin: 100,
                                suggestedMax: 600,
                                ticks: { stepSize: 100 },
                            },
                        },
                    };

                    return (
                        <div key={monitor.id} className="mb-12">
                            <div className="bg-gray-800 rounded p-4 mb-4">
                                <h2 className="text-xl font-bold">{monitor.name}</h2>
                                <p><strong>URL:</strong> {monitor.url}</p>
                                <p><strong>Status:</strong>{" "}
                                    {monitor.lastStatusCode === 200
                                        ? <span className="text-green-400">✅ Up</span>
                                        : <span className="text-red-400">❌ Down</span>}
                                </p>
                                <p><strong>Uptime %:</strong> {monitor.uptimePercent != null ? monitor.uptimePercent.toFixed(2) + "%" : "N/A"}</p>
                                <p><strong>Avg Response Time:</strong> {monitor.averageResponseTime ?? "N/A"} ms</p>
                                <p><strong>Last Checked:</strong> {monitor.lastChecked ? new Date(monitor.lastChecked).toLocaleString() : "N/A"}</p>
                                <p><strong>Status Code:</strong> {monitor.lastStatusCode}</p>
                                <p><strong>Check Freq:</strong> {monitor.checkFreq} min</p>

                                <div className="mt-4 flex space-x-4">
                                    <button
                                        onClick={() => exportCSV(monitor.id)}
                                        className="bg-blue-600 hover:bg-blue-700 px-4 py-1 rounded"
                                    >
                                        Export CSV
                                    </button>
                                    <button
                                        onClick={() =>
                                            setChartType((prev) => ({
                                                ...prev,
                                                [monitor.id]: currentChart === "line" ? "bar" : "line"
                                            }))
                                        }
                                        className="bg-gray-700 hover:bg-gray-600 px-4 py-1 rounded"
                                    >
                                        Toggle {currentChart === "line" ? "Bar" : "Line"} Chart
                                    </button>
                                </div>
                            </div>

                            <div className="bg-gray-800 p-4 rounded">
                                {currentChart === "line" ? (
                                    <Line data={chartData} options={chartOptions} />
                                ) : (
                                    <Bar data={chartData} options={chartOptions} />
                                )}
                            </div>
                        </div>
                    );
                })
            )}
        </div>
    );
};

export default StatusPageDetails;