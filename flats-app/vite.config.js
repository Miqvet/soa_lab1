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
      '/flats-api': {
        target: 'https://localhost:8080',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/flats-api/, '/itmo-soa-lab2-1.0/api')
      },
      '/agency-api': {
        target: 'https://localhost:8084',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/agency-api/, '/api')
      }
    }
  }
})
