import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

const StatusPages = () => {
    const [statusPages, setStatusPages] = useState([]);
    const token = localStorage.getItem("token");

    const fetchStatusPages = async () => {
        try {
            const res = await fetch("http://localhost:8080/api/status-pages", {
                headers: { Authorization: `Bearer ${token}` },
            });
            const data = await res.json();
            setStatusPages(data);
        } catch (err) {
            console.error("Failed to load status pages:", err);
        }
    };

    useEffect(() => {
        fetchStatusPages();
    }, []);

    const handleDelete = async (id) => {
        const confirmDelete = window.confirm("Are you sure you want to delete this status page?");
        if (!confirmDelete) return;

        try {
            const res = await fetch(`http://localhost:8080/api/status-pages/${id}`, {
                method: "DELETE",
                headers: { Authorization: `Bearer ${token}` },
            });

            if (!res.ok) throw new Error("Failed to delete status page");

            fetchStatusPages(); // Refresh the list
        } catch (err) {
            console.error("Error deleting status page:", err);
            alert("Could not delete status page.");
        }
    };

    return (
        <div className="max-w-3xl mx-auto mt-10 p-6 bg-gray-800 text-white rounded shadow">
            <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-bold">Status Pages</h2>
                <Link
                    to="/create-status-page"
                    className="bg-blue-600 hover:bg-blue-700 px-4 py-2 rounded"
                >
                    + New
                </Link>
            </div>

            {statusPages.length === 0 ? (
                <p className="text-gray-400">No status pages yet.</p>
            ) : (
                <ul className="space-y-4">
                    {statusPages.map((page) => (
                        <li key={page.id} className="bg-gray-700 p-4 rounded">
                            <div className="flex justify-between items-center">
                                <div>
                                    <p className="text-lg font-semibold">{page.name}</p>
                                    <p className="text-sm text-gray-400">
                                        Public URL:{" "}
                                        <a
                                            href={`/status-pages/public/${page.publicKey}`}
                                            className="text-blue-400 underline"
                                            target="_blank"
                                            rel="noopener noreferrer"
                                        >
                                            /status-pages/public/{page.publicKey}
                                        </a>
                                    </p>
                                </div>
                                <button
                                    onClick={() => handleDelete(page.id)}
                                    className="ml-4 bg-red-600 hover:bg-red-700 text-sm px-3 py-1 rounded"
                                >
                                    Delete
                                </button>
                            </div>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default StatusPages;