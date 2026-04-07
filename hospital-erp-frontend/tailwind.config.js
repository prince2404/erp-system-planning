/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Inter', 'ui-sans-serif', 'system-ui']
      },
      colors: {
        clinical: {
          bg: '#F8FAFC',
          text: '#0F172A',
          muted: '#64748B',
          primary: '#2563EB',
          success: '#16A34A',
          warning: '#D97706',
          danger: '#DC2626',
          purple: '#7C3AED',
          cyan: '#0891B2',
          pink: '#DB2777',
          emerald: '#059669',
          indigo: '#4F46E5'
        }
      },
      boxShadow: {
        soft: '0 18px 45px rgba(15, 23, 42, 0.08)'
      }
    }
  },
  plugins: []
};
