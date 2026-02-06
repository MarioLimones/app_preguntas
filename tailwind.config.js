module.exports = {
  content: [
    "./src/main/resources/templates/**/*.html",
    "./src/main/resources/static/js/**/*.js"
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          50: "#ecfeff",
          100: "#cffafe",
          200: "#a5f3fc",
          300: "#67e8f9",
          400: "#22d3ee",
          500: "#06b6d4",
          600: "#0891b2",
          700: "#0e7490",
          800: "#155e75",
          900: "#164e63"
        }
      },
      fontFamily: {
        sans: ["Segoe UI", "Calibri", "Arial", "system-ui", "sans-serif"],
        display: ["Segoe UI", "Calibri", "Arial", "system-ui", "sans-serif"]
      },
      boxShadow: {
        soft: "0 30px 70px -50px rgba(15, 23, 42, 0.65)"
      }
    }
  },
  plugins: []
};
