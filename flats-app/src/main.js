import { createApp } from 'vue'
import App from './App.vue'
import { Quasar } from 'quasar'
import quasarLang from 'quasar/lang/ru'
import quasarIconSet from 'quasar/icon-set/material-icons'

import '@quasar/extras/material-icons/material-icons.css'
import 'quasar/src/css/index.sass'

import router from './router'

const app = createApp(App)

app.use(Quasar, {
  lang: quasarLang,
  iconSet: quasarIconSet,
  plugins: {},
})

app.use(router)
app.mount('#app')
