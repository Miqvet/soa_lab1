import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { quasar, transformAssetUrls } from '@quasar/vite-plugin'
import fs from 'fs'
import path from 'path'

export default defineConfig({
  plugins: [
    vue({
      template: { transformAssetUrls }
    }),
    quasar({
      //sassVariables: 'src/styles/quasar-variables.scss'
    })
  ],
  server: {
    port: 8081,
    https: {
      key: fs.readFileSync(path.resolve(__dirname, 'localhost.key')),
      cert: fs.readFileSync(path.resolve(__dirname, 'localhost.crt'))
    },
    proxy: {
      '/api': {
        target: 'https://localhost:8443', // Убедитесь, что это HTTPS
        changeOrigin: true,
        secure: false, // важно для самоподписанных сертификатов
        ws: true
      }
    }
  }
})
