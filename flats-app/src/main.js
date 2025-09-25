import { createApp } from 'vue'
import App from './App.vue'
import { Quasar, Notify, Dialog } from 'quasar' // Добавляем импорт плагинов
import quasarLang from 'quasar/lang/ru'
import quasarIconSet from 'quasar/icon-set/material-icons'

// Импорты стилей
import '@quasar/extras/material-icons/material-icons.css'
import 'quasar/src/css/index.sass'

import router from './router'

const app = createApp(App)

app.use(Quasar, {
  lang: quasarLang,
  iconSet: quasarIconSet,
  plugins: {
    Notify, // Добавляем плагин уведомлений
    Dialog  // Добавляем плагин диалогов (если используете)
  }
})

app.use(router)
app.mount('#app')
