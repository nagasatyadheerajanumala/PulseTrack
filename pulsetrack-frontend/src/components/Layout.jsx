// src/components/Layout.jsx
import Navbar from "./Navbar";

const Layout = ({ children }) => {
    return (
        <div className="bg-gray-900 text-white min-h-screen">
            <Navbar />
            <div className="pt-20 px-4"> {/* ✅ consistent top padding */}
                {children}
            </div>
        </div>
    );
};

export default Layout;