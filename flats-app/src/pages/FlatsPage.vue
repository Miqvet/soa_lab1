<template>
  <q-page class="q-pa-md">
    <div class="text-h4 q-mb-md">Управление квартирами</div>

    <!-- Фильтры -->
    <q-card class="q-mb-md">
      <q-card-section>
        <div class="text-h6">Фильтры</div>
        <q-form @submit="loadFlats" class="q-gutter-md">
          <div class="row q-col-gutter-md">
            <div class="col-12 col-sm-6 col-md-3">
              <q-input v-model="filters.name" label="Название" />
            </div>
            <div class="col-12 col-sm-6 col-md-3">
              <q-input v-model="filters.min_area" type="number" label="Мин. площадь" />
            </div>
            <div class="col-12 col-sm-6 col-md-3">
              <q-input v-model="filters.max_area" type="number" label="Макс. площадь" />
            </div>
            <div class="col-12 col-sm-6 col-md-3">
              <q-input v-model="filters.min_rooms" type="number" label="Мин. комнаты" />
            </div>
            <div class="col-12 col-sm-6 col-md-3">
              <q-input v-model="filters.max_rooms" type="number" label="Макс. комнаты" />
            </div>
            <div class="col-12 col-sm-6 col-md-3">
              <q-select
                v-model="filters.furnish"
                :options="furnishOptions"
                label="Отделка"
                emit-value
                map-options
              />
            </div>
            <div class="col-12 col-sm-6 col-md-3">
              <q-select
                v-model="filters.transport"
                :options="transportOptions"
                label="Транспорт"
                emit-value
                map-options
              />
            </div>
            <div class="col-12 col-sm-6 col-md-3 flex items-end">
              <q-btn label="Применить фильтры" type="submit" color="primary" />
              <q-btn label="Сбросить" @click="resetFilters" color="secondary" class="q-ml-sm" />
            </div>
          </div>
        </q-form>
      </q-card-section>
    </q-card>

    <!-- Дополнительные действия -->
    <div class="q-mb-md">
      <q-btn label="Добавить квартиру" @click="showAddDialog = true" color="positive" class="q-mr-sm" />
      <q-btn label="Уникальные жилые площади" @click="getUniqueLivingSpaces" color="info" class="q-mr-sm" />
      <q-btn label="Удалить по отделке" @click="showDeleteByFurnishDialog = true" color="negative" />
    </div>

    <!-- Таблица квартир -->
    <q-table
      :rows="flats"
      :columns="columns"
      row-key="id"
      :loading="loading"
      :pagination="pagination"
      @request="onRequest"
      binary-state-sort
    >
      <template v-slot:body-cell-actions="props">
        <q-td :props="props">
          <q-btn icon="edit" size="sm" flat @click="editFlat(props.row)" class="q-mr-xs" />
          <q-btn icon="delete" size="sm" flat @click="deleteFlat(props.row.id)" />
        </q-td>
      </template>
    </q-table>

    <!-- Диалог добавления/редактирования квартиры -->
    <q-dialog v-model="showAddDialog" persistent>
      <q-card style="min-width: 70%">
        <q-card-section>
          <div class="text-h6">{{ editingFlat ? 'Редактирование' : 'Добавление' }} квартиры</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-form @submit="saveFlat" class="q-gutter-md">
            <div class="row q-col-gutter-md">
              <div class="col-12 col-sm-6">
                <q-input
                  v-model="flatForm.name"
                  label="Название *"
                  :rules="[val => !!val || 'Обязательное поле']"
                />
              </div>
              <div class="col-12 col-sm-6">
                <q-input
                  v-model="flatForm.area"
                  type="number"
                  label="Площадь *"
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
                  :rules="[val => !!val || 'Обязательное поле']"
                />
              </div>
              <div class="col-12 col-sm-6">
                <q-select
                  v-model="flatForm.furnish"
                  :options="furnishOptions"
                  label="Отделка *"
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
                  emit-value
                  map-options
                  :rules="[val => !!val || 'Обязательное поле']"
                />
              </div>
              <div class="col-12 col-sm-6">
                <q-checkbox
                  v-model="flatForm.balcony"
                  label="Балкон"
                />
              </div>

              <!-- Координаты -->
              <div class="col-12">
                <div class="text-subtitle1">Координаты</div>
              </div>
              <div class="col-12 col-sm-6">
                <q-input
                  v-model="flatForm.coordinates.x"
                  type="number"
                  label="Координата X *"
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
                  :rules="[
                    val => !!val || 'Обязательное поле',
                    val => val > -733 || 'Значение должно быть больше -733'
                  ]"
                />
              </div>

              <!-- Дом -->
              <div class="col-12">
                <div class="text-subtitle1">Дом</div>
              </div>
              <div class="col-12 col-sm-6">
                <q-input
                  v-model="flatForm.house.name"
                  label="Название дома *"
                  :rules="[val => !!val || 'Обязательное поле']"
                />
              </div>
              <div class="col-12 col-sm-6">
                <q-input
                  v-model="flatForm.house.year"
                  type="number"
                  label="Год постройки *"
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
                  :rules="[
                    val => !!val || 'Обязательное поле',
                    val => val >= 1 || 'Должен быть хотя бы 1 лифт'
                  ]"
                />
              </div>
            </div>

            <q-card-actions align="right">
              <q-btn flat label="Отмена" color="primary" v-close-popup />
              <q-btn type="submit" :label="editingFlat ? 'Сохранить' : 'Добавить'" color="primary" />
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

// Получение уникальных значений жилой площади
const getUniqueLivingSpaces = async () => {
  try {
    const response = await flatsApi.getUniqueLivingSpaces()
    $q.dialog({
      title: 'Уникальные значения жилой площади',
      message: response.data.join(', ')
    })
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Ошибка при получении уникальных значений: ' + error.message
    })
  }
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

// Загрузка при монтировании
onMounted(() => {
  loadFlats()
})
</script>