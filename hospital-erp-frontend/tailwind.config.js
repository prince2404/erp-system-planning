/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Inter', 'ui-sans-serif', 'system-ui', '-apple-system', 'sans-serif']
      },
      colors: {
        brand: {
          50: '#eef7ff',
          100: '#d9edff',
          200: '#bce0ff',
          300: '#8eceff',
          400: '#59b2ff',
          500: '#3b93ff',
          600: '#1a6ff5',
          700: '#1459e1',
          800: '#1749b6',
          900: '#19408f',
          950: '#142957'
        },
        ash: {
          50: '#f6f7f9',
          100: '#eceef2',
          200: '#d5d9e2',
          300: '#b0b8c9',
          400: '#8592ab',
          500: '#667591',
          600: '#515e78',
          700: '#434d62',
          800: '#3a4253',
          900: '#343a47',
          950: '#23272f'
        },
        emerald: {
          50: '#edfcf2',
          100: '#d4f7df',
          200: '#abeec4',
          300: '#74dfa2',
          400: '#3bca7c',
          500: '#17b062',
          600: '#0b8e4e',
          700: '#097141',
          800: '#0a5935',
          900: '#09492d',
          950: '#042919'
        },
        danger: {
          50: '#fef2f2',
          100: '#ffe1e1',
          200: '#ffc9c9',
          300: '#fea3a3',
          400: '#fb6e6e',
          500: '#f34141',
          600: '#e02424',
          700: '#bc1a1a',
          800: '#9c1919',
          900: '#811c1c',
          950: '#460909'
        },
        amber: {
          50: '#fefce8',
          100: '#fef9c3',
          200: '#fef08a',
          300: '#fde047',
          400: '#facc15',
          500: '#eab308',
          600: '#ca8a04',
          700: '#a16207',
          800: '#854d0e',
          900: '#713f12',
          950: '#422006'
        }
      },
      boxShadow: {
        soft: '0 2px 15px -3px rgba(0,0,0,0.07), 0 10px 20px -2px rgba(0,0,0,0.04)',
        card: '0 0 0 1px rgba(0,0,0,0.04), 0 1px 3px rgba(0,0,0,0.06)',
        glow: '0 0 20px rgba(59,147,255,0.15)',
        'inner-glow': 'inset 0 1px 0 0 rgba(255,255,255,0.05)'
      },
      backgroundImage: {
        'gradient-radial': 'radial-gradient(var(--tw-gradient-stops))',
        'glass': 'linear-gradient(135deg, rgba(255,255,255,0.1), rgba(255,255,255,0.05))'
      },
      animation: {
        'fade-in': 'fadeIn 0.3s ease-out',
        'slide-in': 'slideIn 0.3s ease-out',
        'slide-up': 'slideUp 0.25s ease-out',
        'scale-in': 'scaleIn 0.2s ease-out',
        'pulse-soft': 'pulseSoft 2s ease-in-out infinite',
        'counter': 'counter 0.6s ease-out'
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' }
        },
        slideIn: {
          '0%': { transform: 'translateX(-16px)', opacity: '0' },
          '100%': { transform: 'translateX(0)', opacity: '1' }
        },
        slideUp: {
          '0%': { transform: 'translateY(8px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' }
        },
        scaleIn: {
          '0%': { transform: 'scale(0.95)', opacity: '0' },
          '100%': { transform: 'scale(1)', opacity: '1' }
        },
        pulseSoft: {
          '0%, 100%': { opacity: '1' },
          '50%': { opacity: '0.7' }
        }
      }
    }
  },
  plugins: []
};
