import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const Register = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        const res = await fetch('http://localhost:8080/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password }),
        });

        if (res.ok) {
            navigate('/login');
        } else {
            alert('Registration failed');
        }
    };

    return (
        <div className="max-w-md mx-auto mt-20 p-6 bg-gray-900 rounded shadow">
            <h2 className="text-2xl font-semibold mb-4 text-white">Register</h2>
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
                <button type="submit" className="w-full bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded">
                    Register
                </button>
            </form>
        </div>
    );
};

export default Register;