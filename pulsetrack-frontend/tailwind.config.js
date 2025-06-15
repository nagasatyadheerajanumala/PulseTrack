/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{js,ts,jsx,tsx}"
    ],
    theme: {
        extend: {
            animation: {
                fadeIn: 'fadeIn 0.8s ease-out forwards',
                gradientBlur: 'gradientBlur 8s ease infinite',
            },
            keyframes: {
                fadeIn: {
                    '0%': { opacity: 0, transform: 'translateY(20px)' },
                    '100%': { opacity: 1, transform: 'translateY(0)' },
                },
                gradientBlur: {
                    '0%, 100%': { transform: 'translateY(0) rotate(0deg)' },
                    '50%': { transform: 'translateY(-10px) rotate(10deg)' },
                },
            },
        },
    },
    plugins: [],
}