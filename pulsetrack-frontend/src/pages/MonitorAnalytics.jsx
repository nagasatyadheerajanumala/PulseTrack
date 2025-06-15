import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
    Chart as ChartJS,
    ArcElement,
    Tooltip,
    Legend,
    CategoryScale,
    LinearScale,
    BarElement,
    Title
} from "chart.js";
import { Bar, Pie } from "react-chartjs-2";

ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, Title);

const MonitorAnalytics = () => {
    const { id } = useParams();
    const [analytics, setAnalytics] = useState(null);
    const [range, setRange] = useState("24h");
    const [page, setPage] = useState(1);
    const [chartType, setChartType] = useState("pie");
    const pageSize = 10;

    useEffect(() => {
        fetchAnalytics(range);
    }, [range]);

    const fetchAnalytics = async (selectedRange) => {
        const token = localStorage.getItem("token");
        try {
            const res = await fetch(`http://localhost:8080/api/monitors/${id}/analytics?range=${selectedRange}`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            if (!res.ok) throw new Error("Failed to fetch analytics");
            const data = await res.json();
            setAnalytics(data);
            setPage(1);
        } catch (err) {
            console.error("Error loading analytics:", err);
        }
    };

    const exportToCSV = () => {
        const logs = analytics?.recentChecks || [];
        const csvRows = [
            ["Timestamp", "Status Code", "Response Time (ms)"],
            ...logs.map(log => [
                new Date(log.timestamp).toLocaleString(),
                log.statusCode,
                log.responseTime
            ])
        ];

        const blob = new Blob([csvRows.map(r => r.join(",")).join("\n")], { type: "text/csv;charset=utf-8;" });
        const url = URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.download = `monitor_${id}_analytics.csv`;
        link.click();
        URL.revokeObjectURL(url);
    };

    if (!analytics) {
        return (
            <div className="flex items-center justify-center h-screen text-white">
                <span className="text-lg animate-pulse">Loading analytics...</span>
            </div>
        );
    }

    const recentChecks = analytics.recentChecks || [];
    const paginatedLogs = recentChecks.slice((page - 1) * pageSize, page * pageSize);

    const statusCodeCounts = {};
    recentChecks.forEach(log => {
        const code = log.statusCode ?? "Unknown";
        statusCodeCounts[code] = (statusCodeCounts[code] || 0) + 1;
    });

    const chartData = {
        labels: Object.keys(statusCodeCounts),
        datasets: [
            {
                label: "Status Code Count",
                data: Object.values(statusCodeCounts),
                backgroundColor: [
                    "#10B981", "#EF4444", "#3B82F6", "#FBBF24", "#A78BFA", "#F87171"
                ]
            }
        ]
    };

    return (
        <div className="pt-24 px-6 text-white min-h-screen bg-gray-900">
            <div className="flex justify-between items-center mb-4">
                <h2 className="text-2xl font-bold">Analytics for: {analytics.name}</h2>
                <select
                    className="bg-gray-800 text-white px-2 py-1 rounded"
                    value={range}
                    onChange={(e) => setRange(e.target.value)}
                >
                    <option value="1h">Last 1 Hour</option>
                    <option value="24h">Last 24 Hours</option>
                    <option value="7d">Last 7 Days</option>
                    <option value="30d">Last 30 Days</option>
                </select>
            </div>

            <div className="mb-6 space-y-1">
                <p><strong>URL:</strong> {analytics.url}</p>
                <p>
                    <strong>Uptime:</strong>{" "}
                    {typeof analytics.uptimePercent === "number"
                        ? `${analytics.uptimePercent.toFixed(2)}%`
                        : "N/A"}
                </p>
                <p><strong>Avg. Response Time:</strong> {analytics.averageResponseTime ?? "N/A"} ms</p>
                <p><strong>Last Checked:</strong> {analytics.lastChecked ? new Date(analytics.lastChecked).toLocaleString() : "N/A"}</p>
                <p><strong>Last Status Code:</strong> {analytics.lastStatusCode ?? "N/A"}</p>
            </div>

            <div className="my-8 max-w-md">
                <div className="flex justify-between items-center mb-2">
                    <h3 className="text-lg font-semibold">Status Code Distribution</h3>
                    <button
                        className="text-sm text-blue-400 underline"
                        onClick={() => setChartType(chartType === "pie" ? "bar" : "pie")}
                    >
                        {chartType === "pie" ? "Switch to Bar" : "Switch to Pie"}
                    </button>
                </div>

                {chartData.labels.length > 0 ? (
                    chartType === "pie" ? (
                        <Pie data={chartData} />
                    ) : (
                        <Bar
                            data={{
                                ...chartData,
                                datasets: [{
                                    ...chartData.datasets[0],
                                    backgroundColor: "#3B82F6"
                                }]
                            }}
                            options={{
                                scales: {
                                    y: { beginAtZero: true }
                                }
                            }}
                        />
                    )
                ) : (
                    <p className="text-gray-400">No chart data available.</p>
                )}
            </div>

            {/* Recent Checks Table */}
            <div className="mt-10">
                <h3 className="text-lg font-semibold mb-2">Recent Checks</h3>
                <div className="overflow-x-auto">
                    <table className="min-w-full text-sm bg-gray-800 rounded">
                        <thead className="bg-gray-700 text-gray-300">
                        <tr>
                            <th className="p-2 text-left">Timestamp</th>
                            <th className="p-2 text-left">Status Code</th>
                            <th className="p-2 text-left">Response Time (ms)</th>
                        </tr>
                        </thead>
                        <tbody>
                        {paginatedLogs.length > 0 ? (
                            paginatedLogs.map((log, idx) => (
                                <tr key={`${log.timestamp}-${idx}`} className="border-b border-gray-700">
                                    <td className="p-2">{log.timestamp ? new Date(log.timestamp).toLocaleString() : "N/A"}</td>
                                    <td className="p-2">{log.statusCode ?? "N/A"}</td>
                                    <td className="p-2">{log.responseTime ?? "N/A"}</td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="3" className="p-2 text-center text-gray-400">
                                    No recent data available.
                                </td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>

                {/* Pagination + CSV Export */}
                {recentChecks.length > pageSize && (
                    <div className="mt-4 flex justify-center items-center space-x-4">
                        <button
                            onClick={() => setPage(p => Math.max(p - 1, 1))}
                            disabled={page === 1}
                            className="px-3 py-1 bg-gray-700 hover:bg-gray-600 rounded disabled:opacity-50"
                        >
                            Prev
                        </button>
                        <span>Page {page}</span>
                        <button
                            onClick={() => setPage(p => p * pageSize < recentChecks.length ? p + 1 : p)}
                            disabled={page * pageSize >= recentChecks.length}
                            className="px-3 py-1 bg-gray-700 hover:bg-gray-600 rounded disabled:opacity-50"
                        >
                            Next
                        </button>
                        <button
                            onClick={exportToCSV}
                            className="ml-4 px-3 py-1 bg-green-600 hover:bg-green-700 rounded text-sm"
                        >
                            Export CSV
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
};

export default MonitorAnalytics;