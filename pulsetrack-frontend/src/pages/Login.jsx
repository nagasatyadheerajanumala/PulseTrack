import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const res = await fetch('http://localhost:8080/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password }),
            });

            const token = await res.text(); // expecting plain text

            if (res.ok) {
                const cleanToken = token.replace(/"/g, '');
                localStorage.setItem('token', cleanToken);

                // ✅ Notify other components (e.g., Navbar)
                window.dispatchEvent(new Event("login"));

                navigate('/dashboard');
            } else {
                alert('Login failed. Check your credentials.');
            }
        } catch (err) {
            console.error('Login error:', err);
            alert('Something went wrong. Try again later.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-md mx-auto mt-20 p-6 bg-gray-900 rounded shadow">
            <h2 className="text-2xl font-semibold mb-4 text-white">Login</h2>

            <form onSubmit={handleSubmit} className="space-y-4">
                <input
                    type="email"
                    placeholder="Email"
                    className="w-full px-4 py-2 bg-gray-800 text-white rounded"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />
                <input
                    type="password"
                    placeholder="Password"
                    className="w-full px-4 py-2 bg-gray-800 text-white rounded"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
                <button
                    type="submit"
                    disabled={loading}
                    className="w-full bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
                >
                    {loading ? 'Logging in...' : 'Login'}
                </button>
            </form>

            <p className="mt-4 text-sm text-gray-400">
                Don't have an account?{' '}
                <Link to="/register" className="text-blue-400 hover:underline">
                    Register
                </Link>
            </p>
        </div>
    );
};

export default Login;