const API = "http://localhost:8080/api/auth";

const login = async (email, password) => {
    const res = await fetch(`${API}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
    });

    if (!res.ok) throw new Error("Login failed");

    const data = await res.json();
    localStorage.setItem("token", data.token);
};

const register = async (email, password) => {
    const res = await fetch(`${API}/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
    });

    if (!res.ok) throw new Error("Register failed");
};

const logout = () => localStorage.removeItem("token");

export default { login, register, logout };