import { defineNuxtConfig } from 'nuxt/config'
import obfuscator from 'vite-plugin-javascript-obfuscator'

export default defineNuxtConfig({
  compatibilityDate: '2026-02-11', //
  devtools: { enabled: true },
  modules: ['@nuxtjs/tailwindcss', '@pinia/nuxt'],
  css: ['~/assets/css/main.css'],

  app: {
    head: {
      link: [
        { rel: 'stylesheet', href: 'https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap' },
        { rel: 'stylesheet', href: 'https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap' }
      ]
    }
  },

  // 1. PostCSS 설정 (CSS 클래스명 난독화)
  postcss: {
    plugins: {
      'postcss-obfuscator': {
        // 운영 모드에서만 활성화
        enable: process.env.NODE_ENV === 'production',
        length: 6,
        baseName: 'v-',
        // UI가 깨지지 않도록 제외할 클래스들
        ignoreClasses: [
          'material-symbols-outlined',
          '__nuxt',
          '__layout',
          'video-wrapper',
          'cctv-video'
        ],
      },
    },
  },

  // 2. Vite 설정 (JS 로직 및 문자열 난독화)
  vite: {
    plugins: [
      // 운영 모드일 때만 플러그인 실행
      ...(process.env.NODE_ENV === 'production' ? [
        obfuscator({
          include: [/\.(js|ts|vue)$/],
          options: {
            compact: true,
            controlFlowFlattening: true, // 코드 흐름 꼬기
            controlFlowFlatteningThreshold: 0.75,
            numbersToExpressions: true, // 숫자를 수식으로 변환
            stringArray: true, // 문자열을 별도 배열로 추출
            stringArrayEncoding: ['base64'], // 문자열 암호화
            stringArrayThreshold: 0.75,
            unicodeEscapeSequence: true
          }
        })
      ] : [])
    ]
  },

  routeRules: {
    '/api/**': { proxy: 'http://localhost:8080/api/**' }
  }
})