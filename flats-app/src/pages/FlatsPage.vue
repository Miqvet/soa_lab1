<template>
  <q-page class="q-pa-md">
    <div class="text-h4 q-mb-md">Управление квартирами</div>

    <!-- Дополнительные действия -->
    <div class="q-mb-md">
      <q-btn label="Добавить квартиру" @click="showAddDialog = true" color="positive" class="q-mr-sm" />
      <div class="row items-center q-gutter-sm q-mt-xs"></div>
      <!-- Кнопки для работы с уникальными значениями жилой площади -->
      <q-btn 
        label="Запустить задачу" 
        @click="launchUniqueLivingSpacesJob" 
        color="info" 
        class="q-mr-sm"
        :loading="jobLoading"
      />
      <q-btn 
        label="Получить результат" 
        @click="getUniqueLivingSpaces" 
        color="info" 
        class="q-mr-sm"
        :disable="!uniqueSpacesResult"
      />
      <q-btn 
        label="Отменить задачу" 
        @click="cancelUniqueLivingSpacesJob" 
        color="negative" 
        class="q-mr-sm"
      />
      <div class="row items-center q-gutter-sm q-mt-xs"></div>
      <q-btn label="Удалить по отделке" @click="showDeleteByFurnishDialog = true" color="negative" />

      <!-- Поиск и удаление по ID -->
      <div class="row items-center q-gutter-sm q-mt-xs">

        <q-btn 
          label="Найти по ID" 
          @click="getFlatById" 
          color="info" 
          :disable="!flatId"
        />
        <q-btn 
          label="Удалить по ID" 
          @click="deleteFlatById" 
          color="negative" 
          :disable="!flatId"
        />

        <q-input class="q-mt-lg"
          v-model="flatId"
          label="ID квартиры"
          type="number"
          outlined
          dense
          style="min-width: 150px; max-width: 200px"
          :rules="[val => val > 0 || 'ID должен быть положительным числом']"
        />
      </div>
    </div>

    <!-- Диалог добавления/редактирования квартиры -->
    <q-dialog v-model="showAddDialog" persistent>
      <q-card style="min-width: 70%; max-width: 900px">
        <q-card-section class="bg-primary text-white">
          <div class="text-h6">{{ editingFlat ? 'Редактирование' : 'Добавление' }} квартиры</div>
        </q-card-section>

        <q-card-section class="q-pt-lg scroll" style="max-height: 70vh">
          <q-form @submit="saveFlat" class="q-gutter-pa-lg">
            <!-- Основная информация -->
            <div class="row q-col-gutter-lg">
              <div class="col-12">
                <div class="text-subtitle1 text-weight-medium q-pb-sm">Основная информация</div>
                <q-separator />
              </div>
              
              <div class="col-12 col-sm-6">
                <q-input
                  v-model="flatForm.name"
                  label="Название *"
                  outlined
                  :rules="[val => !!val || 'Обязательное поле']"
                />
              </div>
              <div class="col-12 col-sm-6">
                <q-input
                  v-model="flatForm.area"
                  type="number"
                  label="Площадь *"
                  outlined
                  :rules="[
                    val => !!val || 'Обязательное поле',
                    val => val > 0 && val <= 784 || 'Площадь должна быть от 1 до 784'
                  ]"
                />
              </div>
              <div class="col-12 col-sm-6">
                <q-input
                  v-model="flatForm.number_of_rooms"
                  type="number"
                  label="Количество комнат *"
                  outlined
                  :rules="[
                    val => !!val || 'Обязательное поле',
                    val => val > 0 && val <= 8 || 'Количество комнат должно быть от 1 до 8'
                  ]"
                />
              </div>
              <div class="col-12 col-sm-6">
                <q-input
                  v-model="flatForm.living_space"
                  type="number"
                  label="Жилая площадь *"
                  outlined
                  :rules="[val => !!val || 'Обязательное поле']"
                />
              </div>
              <div class="col-12 col-sm-6">
                <q-select
                  v-model="flatForm.furnish"
                  :options="furnishOptions"
                  label="Отделка *"
                  outlined
                  emit-value
                  map-options
                  :rules="[val => !!val || 'Обязательное поле']"
                />
              </div>
              <div class="col-12 col-sm-6">
                <q-select
                  v-model="flatForm.transport"
                  :options="transportOptions"
                  label="Транспорт *"
                  outlined
                  emit-value
                  map-options
                  :rules="[val => !!val || 'Обязательное поле']"
                />
              </div>
              <div class="col-12">
                <q-checkbox
                  v-model="flatForm.balcony"
                  label="Балкон"
                  size="lg"
                />
              </div>
            </div>
            
            <!-- Координаты -->
            <div class="row q-col-gutter-lg q-mt-md">
              <div class="col-12">
                <div class="text-subtitle1 text-weight-medium q-pb-sm">Координаты</div>
                <q-separator />
              </div>
              
              <div class="col-12 col-sm-6">
                <q-input
                  v-model="flatForm.coordinates.x"
                  type="number"
                  label="Координата X *"
                  outlined
                  :rules="[
                    val => !!val || 'Обязательное поле',
                    val => val > -13 || 'Значение должно быть больше -13'
                  ]"
                />
              </div>
              <div class="col-12 col-sm-6">
                <q-input
                  v-model="flatForm.coordinates.y"
                  type="number"
                  label="Координата Y *"
                  outlined
                  :rules="[
                    val => !!val || 'Обязательное поле',
                    val => val > -733 || 'Значение должно быть больше -733'
                  ]"
                />
              </div>
            </div>
            
            <!-- Дом -->
            <div class="row q-col-gutter-lg q-mt-md">
              <div class="col-12">
                <div class="text-subtitle1 text-weight-medium q-pb-sm">Информация о доме</div>
                <q-separator />
              </div>
              
              <div class="col-12 col-sm-6">
                <q-input
                  v-model="flatForm.house.name"
                  label="Название дома *"
                  outlined
                  :rules="[val => !!val || 'Обязательное поле']"
                />
              </div>
              <div class="col-12 col-sm-6">
                <q-input
                  v-model="flatForm.house.year"
                  type="number"
                  label="Год постройки *"
                  outlined
                  :rules="[
                    val => !!val || 'Обязательное поле',
                    val => val >= 1 && val <= 370 || 'Год должен быть от 1 до 370'
                  ]"
                />
              </div>
              <div class="col-12 col-sm-6">
                <q-input
                  v-model="flatForm.house.number_of_floors"
                  type="number"
                  label="Количество этажей *"
                  outlined
                  :rules="[
                    val => !!val || 'Обязательное поле',
                    val => val >= 1 || 'Должен быть хотя бы 1 этаж'
                  ]"
                />
              </div>
              <div class="col-12 col-sm-6">
                <q-input
                  v-model="flatForm.house.number_of_lifts"
                  type="number"
                  label="Количество лифтов *"
                  outlined
                  :rules="[
                    val => !!val || 'Обязательное поле',
                    val => val >= 1 || 'Должен быть хотя бы 1 лифт'
                  ]"
                />
              </div>
            </div>
            
            <q-card-actions align="right" class="q-px-none q-pt-lg">
              <q-btn 
                flat 
                label="Отмена" 
                color="primary" 
                v-close-popup 
                class="q-mr-md"
              />
              <q-btn 
                type="submit" 
                :label="editingFlat ? 'Сохранить' : 'Добавить'" 
                color="primary" 
                :loading="saving"
              />
            </q-card-actions>
          </q-form>
        </q-card-section>
      </q-card>
    </q-dialog>


    <!-- Диалог удаления по отделке -->
    <q-dialog v-model="showDeleteByFurnishDialog">
      <q-card style="min-width: 300px">
        <q-card-section>
          <div class="text-h6">Удаление квартир по отделке</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-select
            v-model="deleteFurnish"
            :options="furnishOptions"
            label="Тип отделки"
            emit-value
            map-options
          />
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Отмена" color="primary" v-close-popup />
          <q-btn flat label="Удалить" color="negative" @click="confirmDeleteByFurnish" />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- Диалог просмотра квартиры -->
    <q-dialog v-model="showFlatViewDialog" persistent>
      <q-card style="min-width: 60%; max-width: 700px">
        <q-card-section class="bg-primary text-white">
          <div class="text-h6">Информация о квартире #{{ viewedFlat?.id }}</div>
        </q-card-section>

        <q-card-section class="q-pt-lg scroll" style="max-height: 70vh">
          <div v-if="viewedFlat" class="q-gutter-y-md">
            <!-- Основная информация -->
            <div class="row q-col-gutter-md">
              <div class="col-12">
                <div class="text-subtitle1 text-weight-medium q-pb-sm">Основная информация</div>
                <q-separator />
              </div>
              
              <div class="col-12 col-sm-6">
                <div class="text-caption text-grey-7">Название</div>
                <div class="text-body1">{{ viewedFlat.name }}</div>
              </div>
              <div class="col-12 col-sm-6">
                <div class="text-caption text-grey-7">Площадь</div>
                <div class="text-body1">{{ viewedFlat.area }} м²</div>
              </div>
              <div class="col-12 col-sm-6">
                <div class="text-caption text-grey-7">Количество комнат</div>
                <div class="text-body1">{{ viewedFlat.number_of_rooms }}</div>
              </div>
              <div class="col-12 col-sm-6">
                <div class="text-caption text-grey-7">Жилая площадь</div>
                <div class="text-body1">{{ viewedFlat.living_space }} м²</div>
              </div>
              <div class="col-12 col-sm-6">
                <div class="text-caption text-grey-7">Отделка</div>
                <div class="text-body1">{{ getFurnishLabel(viewedFlat.furnish) }}</div>
              </div>
              <div class="col-12 col-sm-6">
                <div class="text-caption text-grey-7">Транспорт</div>
                <div class="text-body1">{{ getTransportLabel(viewedFlat.transport) }}</div>
              </div>
              <div class="col-12 col-sm-6">
                <div class="text-caption text-grey-7">Балкон</div>
                <div class="text-body1">{{ viewedFlat.balcony ? 'Да' : 'Нет' }}</div>
              </div>
              <div class="col-12 col-sm-6">
                <div class="text-caption text-grey-7">Дата создания</div>
                <div class="text-body1">{{ formatDate(viewedFlat.creationDate) }}</div>
              </div>
            </div>
            
            <!-- Координаты -->
            <div class="row q-col-gutter-md q-mt-md">
              <div class="col-12">
                <div class="text-subtitle1 text-weight-medium q-pb-sm">Координаты</div>
                <q-separator />
              </div>
              
              <div class="col-12 col-sm-6">
                <div class="text-caption text-grey-7">Координата X</div>
                <div class="text-body1">{{ viewedFlat.coordinates.x }}</div>
              </div>
              <div class="col-12 col-sm-6">
                <div class="text-caption text-grey-7">Координата Y</div>
                <div class="text-body1">{{ viewedFlat.coordinates.y }}</div>
              </div>
            </div>
            
            <!-- Дом -->
            <div class="row q-col-gutter-md q-mt-md">
              <div class="col-12">
                <div class="text-subtitle1 text-weight-medium q-pb-sm">Информация о доме</div>
                <q-separator />
              </div>
              
              <div class="col-12 col-sm-6">
                <div class="text-caption text-grey-7">Название дома</div>
                <div class="text-body1">{{ viewedFlat.house.name }}</div>
              </div>
              <div class="col-12 col-sm-6">
                <div class="text-caption text-grey-7">Год постройки</div>
                <div class="text-body1">{{ viewedFlat.house.year }}</div>
              </div>
              <div class="col-12 col-sm-6">
                <div class="text-caption text-grey-7">Количество этажей</div>
                <div class="text-body1">{{ viewedFlat.house.number_of_floors }}</div>
              </div>
              <div class="col-12 col-sm-6">
                <div class="text-caption text-grey-7">Количество лифтов</div>
                <div class="text-body1">{{ viewedFlat.house.number_of_lifts }}</div>
              </div>
            </div>
          </div>
          
          <div v-else class="text-center q-pa-lg">
            <q-spinner-hourglass color="primary" size="2em" />
            <div class="q-mt-md">Загрузка данных...</div>
          </div>
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md">
          <q-btn 
            flat 
            label="Закрыть" 
            color="primary" 
            v-close-popup 
          />
          <q-btn 
            v-if="viewedFlat"
            label="Редактировать" 
            color="primary" 
            @click="editViewedFlat"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { flatsApi } from '../api/flats.js'

const $q = useQuasar()

// Колонки таблицы
const columns = [
  { name: 'id', label: 'ID', field: 'id', align: 'left', sortable: true },
  { name: 'name', label: 'Название', field: 'name', align: 'left', sortable: true },
  { name: 'area', label: 'Площадь', field: 'area', align: 'left', sortable: true },
  { name: 'number_of_rooms', label: 'Комнаты', field: 'number_of_rooms', align: 'left', sortable: true },
  { name: 'living_space', label: 'Жилая площадь', field: 'living_space', align: 'left', sortable: true },
  { name: 'furnish', label: 'Отделка', field: 'furnish', align: 'left', sortable: true },
  { name: 'transport', label: 'Транспорт', field: 'transport', align: 'left', sortable: true },
  { name: 'balcony', label: 'Балкон', field: 'balcony', align: 'left', format: val => val ? 'Да' : 'Нет' },
  { name: 'creationDate', label: 'Дата создания', field: 'creationDate', align: 'left', sortable: true },
  { name: 'actions', label: 'Действия', align: 'center' }
]

// Опции для селектов
const furnishOptions = [
  { label: 'DESIGNER', value: 'DESIGNER' },
  { label: 'NONE', value: 'NONE' },
  { label: 'FINE', value: 'FINE' },
  { label: 'BAD', value: 'BAD' },
  { label: 'LITTLE', value: 'LITTLE' }
]

const transportOptions = [
  { label: 'FEW', value: 'FEW' },
  { label: 'NONE', value: 'NONE' },
  { label: 'LITTLE', value: 'LITTLE' },
  { label: 'ENOUGH', value: 'ENOUGH' }
]

// Реактивные данные
const jobLoading = ref(false)
const uniqueSpacesResult = ref(null)
const flatId = ref(null)
const showFlatViewDialog = ref(false)
const viewedFlat = ref(null)
const loadingFlat = ref(false)
const flats = ref([])
const loading = ref(false)
const showAddDialog = ref(false)
const showDeleteByFurnishDialog = ref(false)
const editingFlat = ref(null)
const deleteFurnish = ref(null)

// Пагинация
const pagination = ref({
  page: 1,
  rowsPerPage: 20,
  rowsNumber: 0
})

// Фильтры
const filters = ref({
  name: null,
  min_area: null,
  max_area: null,
  min_rooms: null,
  max_rooms: null,
  furnish: null,
  transport: null
})

// Форма квартиры
const flatForm = ref({
  name: '',
  coordinates: {
    x: 0,
    y: 0
  },
  area: 0,
  number_of_rooms: 0,
  living_space: 0,
  furnish: null,
  transport: null,
  balcony: false,
  house: {
    name: '',
    year: 0,
    number_of_floors: 0,
    number_of_lifts: 0
  }
})

// Загрузка данных
const loadFlats = async (props = {}) => {
  loading.value = true
  try {
    const page = props.pagination?.page || pagination.value.page
    const rowsPerPage = props.pagination?.rowsPerPage || pagination.value.rowsPerPage
    const sortBy = props.pagination?.sortBy || 'id'
    const descending = props.pagination?.descending || false

    const response = await flatsApi.getFlats(
      filters.value,
      { page: page - 1, size: rowsPerPage },
      { sortBy, descending }
    )

    flats.value = response.data.flats
    pagination.value.rowsNumber = response.data.totalElements || response.data.flats.length
    pagination.value.page = page
    pagination.value.rowsPerPage = rowsPerPage
    pagination.value.sortBy = sortBy
    pagination.value.descending = descending
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Ошибка при загрузке квартир: ' + error.message
    })
  } finally {
    loading.value = false
  }
}

// Обработчик запроса таблицы
const onRequest = (props) => {
  loadFlats(props)
}

// Сброс фильтров
const resetFilters = () => {
  filters.value = {
    name: null,
    min_area: null,
    max_area: null,
    min_rooms: null,
    max_rooms: null,
    furnish: null,
    transport: null
  }
  loadFlats()
}

// Редактирование квартиры
const editFlat = (flat) => {
  editingFlat.value = flat.id
  flatForm.value = { ...flat }
  showAddDialog.value = true
}

// Сохранение квартиры
const saveFlat = async () => {
  try {
    if (editingFlat.value) {
      await flatsApi.updateFlat(editingFlat.value, flatForm.value)
      $q.notify({
        type: 'positive',
        message: 'Квартира успешно обновлена'
      })
    } else {
      await flatsApi.createFlat(flatForm.value)
      $q.notify({
        type: 'positive',
        message: 'Квартира успешно создана'
      })
    }

    showAddDialog.value = false
    resetForm()
    loadFlats()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Ошибка при сохранении квартиры: ' + error.message
    })
  }
}

// Удаление квартиры
const deleteFlat = async (id) => {
  $q.dialog({
    title: 'Подтверждение',
    message: 'Вы уверены, что хотите удалить эту квартиру?',
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await flatsApi.deleteFlat(id)
      $q.notify({
        type: 'positive',
        message: 'Квартира успешно удалена'
      })
      loadFlats()
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: 'Ошибка при удалении квартиры: ' + error.message
      })
    }
  })
}

// Удаление по отделке
const confirmDeleteByFurnish = async () => {
  if (!deleteFurnish.value) {
    $q.notify({
      type: 'warning',
      message: 'Выберите тип отделки'
    })
    return
  }

  $q.dialog({
    title: 'Подтверждение',
    message: `Вы уверены, что хотите удалить все квартиры с отделкой "${deleteFurnish.value}"?`,
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await flatsApi.deleteByFurnish(deleteFurnish.value)
      $q.notify({
        type: 'positive',
        message: 'Квартиры успешно удалены'
      })
      showDeleteByFurnishDialog.value = false
      deleteFurnish.value = null
      loadFlats()
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: 'Ошибка при удалении квартир: ' + error.message
      })
    }
  })
}

// Сброс формы
const resetForm = () => {
  flatForm.value = {
    name: '',
    coordinates: {
      x: 0,
      y: 0
    },
    area: 0,
    number_of_rooms: 0,
    living_space: 0,
    furnish: null,
    transport: null,
    balcony: false,
    house: {
      name: '',
      year: 0,
      number_of_floors: 0,
      number_of_lifts: 0
    }
  }
  editingFlat.value = null
}

// Получение квартиры по ID
const getFlatById = async () => {
  if (!flatId.value) {
    $q.notify({
      type: 'warning',
      message: 'Введите ID квартиры',
      position: 'top'
    })
    return
  }

  loadingFlat.value = true
  try {
    const response = await flatsApi.getFlatById(flatId.value)
    viewedFlat.value = response.data
    showFlatViewDialog.value = true
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Ошибка при получении квартиры: ' + error.message,
      position: 'top'
    })
  } finally {
    loadingFlat.value = false
  }
}

// Метод для удаления квартиры по ID
const deleteFlatById = async () => {
  if (!flatId.value) {
    $q.notify({
      type: 'warning',
      message: 'Введите ID квартиры',
      position: 'top'
    })
    return
  }

  $q.dialog({
    title: 'Подтверждение удаления',
    message: `Вы уверены, что хотите удалить квартиру с ID ${flatId.value}?`,
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await flatsApi.deleteFlat(flatId.value)
      $q.notify({
        type: 'positive',
        message: 'Квартира успешно удалена',
        position: 'top'
      })
      flatId.value = null
      loadFlats() // Перезагружаем список квартир
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: 'Ошибка при удалении квартиры: ' + error.message,
        position: 'top'
      })
    }
  })
}

// Метод для редактирования просматриваемой квартиры
const editViewedFlat = () => {
  if (viewedFlat.value) {
    editingFlat.value = viewedFlat.value.id
    flatForm.value = { ...viewedFlat.value }
    showFlatViewDialog.value = false
    showAddDialog.value = true
  }
}

// Вспомогательные методы для отображения
const getFurnishLabel = (value) => {
  const option = furnishOptions.find(opt => opt.value === value)
  return option ? option.label : value
}

const getTransportLabel = (value) => {
  const option = transportOptions.find(opt => opt.value === value)
  return option ? option.label : value
}

const formatDate = (dateString) => {
  if (!dateString) return 'Не указана'
  return new Date(dateString).toLocaleDateString('ru-RU')
}

const launchUniqueLivingSpacesJob = async () => {
  jobLoading.value = true
  try {
    await flatsApi.launchUniqueLivingSpacesJob()
    $q.notify({
      type: 'positive',
      message: 'Задача по поиску уникальных значений жилой площади запущена',
      position: 'top'
    })
    // Сбрасываем предыдущий результат, так как запускаем новую задачу
    uniqueSpacesResult.value = null
  } catch (error) {
    console.error('Ошибка при запуске задачи:', error)
    $q.notify({
      type: 'negative',
      message: 'Ошибка при запуске задачи: ' + error.message,
      position: 'top'
    })
  } finally {
    jobLoading.value = false
  }
}

// Метод для получения результата (GET)
const getUniqueLivingSpaces = async () => {
  try {
    const response = await flatsApi.getUniqueLivingSpaces()
    uniqueSpacesResult.value = response.data
    
    if (uniqueSpacesResult.value && uniqueSpacesResult.value.length > 0) {
      $q.dialog({
        title: 'Уникальные значения жилой площади',
        message: uniqueSpacesResult.value.join(', '),
        ok: {
          label: 'Закрыть',
          color: 'primary'
        }
      })
    } else {
      $q.notify({
        type: 'info',
        message: 'Результат пока не доступен или пуст',
        position: 'top'
      })
    }
  } catch (error) {
    console.error('Ошибка при получении уникальных значений:', error)
    $q.notify({
      type: 'negative',
      message: 'Ошибка при получении результата: ' + error.message,
      position: 'top'
    })
  }
}

// Метод для отмены задачи
const cancelUniqueLivingSpacesJob = async () => {
  try {
    await flatsApi.cancelUniqueLivingSpacesJob()
    $q.notify({
      type: 'positive',
      message: 'Задача по поиску уникальных значений жилой площади отменена',
      position: 'top'
    })
    uniqueSpacesResult.value = null
  } catch (error) {
    console.error('Ошибка при отмене задачи:', error)
    $q.notify({
      type: 'negative',
      message: 'Ошибка при отмене задачи: ' + error.message,
      position: 'top'
    })
  }
}

// Загрузка при монтировании
onMounted(() => {
  loadFlats()
})
</script>