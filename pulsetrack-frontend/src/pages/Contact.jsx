// src/pages/Contact.jsx
import { useEffect } from "react";
import AOS from "aos";

const Contact = () => {
    useEffect(() => {
        AOS.init({ duration: 800, once: true });
    }, []);

    return (
        <div className="bg-gray-900 text-white min-h-screen px-6 py-12">
            <div className="max-w-2xl mx-auto">
                <h1 data-aos="fade-down" className="text-4xl font-bold mb-6 text-center">Contact Us</h1>
                <p data-aos="fade-up" className="text-gray-300 mb-8 text-center">
                    Questions, feedback, or partnership ideas? We’d love to hear from you.
                </p>

                <form
                    data-aos="fade-up"
                    className="bg-gray-800 p-6 rounded shadow space-y-4"
                    onSubmit={(e) => {
                        e.preventDefault();
                        alert("Form submission coming soon! For now, email us at hello@pulsetrack.io");
                    }}
                >
                    <input
                        type="text"
                        placeholder="Your Name"
                        className="w-full px-4 py-2 rounded bg-gray-700 text-white"
                        required
                    />
                    <input
                        type="email"
                        placeholder="Your Email"
                        className="w-full px-4 py-2 rounded bg-gray-700 text-white"
                        required
                    />
                    <textarea
                        rows="5"
                        placeholder="Your Message"
                        className="w-full px-4 py-2 rounded bg-gray-700 text-white"
                        required
                    />
                    <button
                        type="submit"
                        className="bg-indigo-600 hover:bg-indigo-700 px-6 py-2 rounded font-semibold"
                    >
                        Send Message
                    </button>
                </form>
            </div>
        </div>
    );
};

export default Contact;