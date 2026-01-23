/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./app/**/*.{vue,js,ts}",
    "./components/**/*.{vue,js,ts}",
    "./pages/**/*.{vue,js,ts}",
    "./layouts/**/*.{vue,js,ts}",
    "./plugins/**/*.{js,ts}",
    "./nuxt.config.{js,ts}",
    "./app.vue",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        // 디자인에 정의된 커스텀 컬러
        "primary": "#137fec",
        "primary-hover": "#0f6bd0",
        "background-light": "#f6f7f8",
        "background-dark": "#101922",
        "card-light": "#ffffff",
        "card-dark": "#1c2632",
        "text-main-light": "#0d141b",
        "text-main-dark": "#e7edf3",
        "text-secondary-light": "#4c739a",
        "text-secondary-dark": "#94a3b8",
        "border-light": "#e7edf3",
        "border-dark": "#2d3748",
      },
      fontFamily: {
        "display": ["Inter", "sans-serif"],
        "sans": ["Inter", "sans-serif", "Noto Sans KR"], // 기본 폰트 설정
      },
    },
  },
  plugins: [
    require('@tailwindcss/forms'), // 폼 스타일 플러그인 (없으면 npm install -D @tailwindcss/forms 필요)
  ],
}