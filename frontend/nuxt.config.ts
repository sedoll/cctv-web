export default defineNuxtConfig({
  devtools: { enabled: true },
  modules: ['@nuxtjs/tailwindcss', '@pinia/nuxt'],
  css: ['~/assets/css/main.css'],

  app: {
    head: {
      link: [
        // Google Fonts (Inter) & Material Symbols
        { rel: 'stylesheet', href: 'https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap' },
        { rel: 'stylesheet', href: 'https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap' }
      ]
    }
  },

  nitro: {
    routeRules: {
      '/api/**': { proxy: 'http://localhost:8080/api/**' }
    }
  }
})