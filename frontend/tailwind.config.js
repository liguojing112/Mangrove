/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{vue,js,ts,jsx,tsx}'
  ],
  theme: {
    extend: {
      colors: {
        mangrove: {
          50:  '#f0faf4',
          100: '#d8f3e1',
          200: '#b3e7c5',
          300: '#80d5a0',
          400: '#98FB98',  // 淡绿 accent
          500: '#4caf7c',
          600: '#3CB371',  // 中绿
          700: '#2E8B57',  // 墨绿 primary
          800: '#256f47',
          900: '#1d5a39',
          950: '#0f3320',
        }
      },
      fontFamily: {
        sans: ['"Inter"', '"Noto Sans SC"', '"PingFang SC"', '"Microsoft YaHei"', 'sans-serif'],
      },
      borderRadius: {
        'xl': '0.75rem',
        '2xl': '1rem',
        '3xl': '1.5rem',
      },
      boxShadow: {
        'card': '0 1px 3px rgba(0,0,0,0.06), 0 1px 2px rgba(0,0,0,0.04)',
        'card-hover': '0 4px 12px rgba(0,0,0,0.08), 0 2px 4px rgba(0,0,0,0.04)',
      }
    },
  },
  plugins: [],
}
