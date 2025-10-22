<template>
  <q-page class="q-pa-md">
    <div class="text-h4 q-mb-md">Управление квартирами</div>

    <!-- Фильтры -->
    <q-card class="q-mb-md">
      <q-card-section>
        <div class="text-h6">Фильтры</div>
        <q-form @submit="loadFlats" class="q-gutter-pa-md">
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
              <q-input v-model="filters.min_price" type="number" label="Мин. цена" />
            </div>
            <div class="col-12 col-sm-6 col-md-3">
              <q-input v-model="filters.max_price" type="number" label="Макс. цена" />
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
            <div class="col-12 col-sm-6 col-md-3">
              <q-checkbox
                v-model="filters.has_balcony"
                label="Только с балконом"
                size="md"
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

    <!-- Сортировка -->
    <q-card class="q-mb-md">
      <q-card-section>
        <div class="text-h6">Сортировка</div>
        <div class="row q-col-gutter-md items-center">
          <div class="col-12 col-sm-6">
            <q-select
              v-model="sortFields"
              :options="sortableFields"
              label="Поля для сортировки"
              multiple
              emit-value
              map-options
              use-chips
              clearable
            />
          </div>
          <div class="col-12 col-sm-3">
            <q-select
              v-model="sortDirection"
              :options="directionOptions"
              label="Направление"
              emit-value
              map-options
            />
          </div>
          <div class="col-12 col-sm-3">
            <q-btn
              label="Применить сортировку"
              @click="applySorting"
              color="primary"
              :disabled="!sortFields.length"
            />
          </div>
        </div>
        <div v-if="currentSorting.length" class="q-mt-sm">
          <small class="text-grey">Текущая сортировка: {{ currentSorting.join(', ') }}</small>
        </div>
      </q-card-section>
    </q-card>

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
                <q-input
                  v-model="flatForm.price"
                  type="number"
                  label="Цена *"
                  outlined
                  :rules="[
                    val => !!val || 'Обязательное поле',
                    val => val > 0 || 'Цена должна быть положительной'
                  ]"
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
                  v-model="flatForm.has_balcony"
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
  </q-page>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
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
  { name: 'price', label: 'Цена', field: 'price', align: 'left', sortable: true },
  { name: 'furnish', label: 'Отделка', field: 'furnish', align: 'left', sortable: true },
  { name: 'transport', label: 'Транспорт', field: 'transport', align: 'left', sortable: true },
  { name: 'has_balcony', label: 'Балкон', field: 'has_balcony', align: 'left', format: val => val ? 'Да' : 'Нет' },
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

// Поля для сортировки
const sortableFields = [
  { label: 'ID', value: 'id' },
  { label: 'Название', value: 'name' },
  { label: 'Площадь', value: 'area' },
  { label: 'Количество комнат', value: 'numberOfRooms' },
  { label: 'Жилая площадь', value: 'livingSpace' },
  { label: 'Цена', value: 'price' },
  { label: 'Отделка', value: 'furnish' },
  { label: 'Транспорт', value: 'transport' },
  { label: 'Балкон', value: 'has_balcony' },
  { label: 'Дата создания', value: 'creationDate' }
]

const directionOptions = [
  { label: 'По возрастанию', value: 'asc' },
  { label: 'По убыванию', value: 'desc' }
]

// Реактивные данные
const flats = ref([])
const loading = ref(false)
const saving = ref(false)
const showAddDialog = ref(false)
const editingFlat = ref(null)

// Сортировка
const sortFields = ref([])
const sortDirection = ref('asc')

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
  min_price: null,
  max_price: null,
  furnish: null,
  transport: null,
  has_balcony: null
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
  price: 0,
  furnish: null,
  transport: null,
  has_balcony: false,
  house: {
    name: '',
    year: 0,
    number_of_floors: 0,
    number_of_lifts: 0
  }
})

// Вычисляемое свойство для отображения текущей сортировки
const currentSorting = computed(() => {
  if (!sortFields.value.length) return []
  return sortFields.value.map(field => {
    const fieldLabel = sortableFields.find(f => f.value === field)?.label || field
    return `${fieldLabel} (${sortDirection.value === 'asc' ? '↑' : '↓'})`
  })
})

// Загрузка данных
const loadFlats = async (props = {}) => {
  loading.value = true
  try {
    const page = props.pagination?.page || pagination.value.page
    const rowsPerPage = props.pagination?.rowsPerPage || pagination.value.rowsPerPage

    // Формируем параметры сортировки
    const sortParams = {}
    if (sortFields.value.length) {
      sortParams.sortBy = sortFields.value.join(',')
      sortParams.sortDirection = sortDirection.value
    }

    const response = await flatsApi.getFlats(
      filters.value,
      {
        pageNumber: page - 1,
        pageSize: rowsPerPage
      },
      sortParams
    )

    flats.value = response.data.flats
    pagination.value.rowsNumber = response.data.numberOfElements
    pagination.value.page = page
    pagination.value.rowsPerPage = rowsPerPage
  } catch (error) {
    let errorMessage = 'Неизвестная ошибка'
    if (error.response && error.response.data) {
      const errorData = error.response.data
      if (errorData.message) {
        errorMessage = errorData.message + '. '
        if (errorData.errors && errorData.errors.length > 0) {
          errorMessage += errorData.errors.join(', ')
        }
      } else {
        errorMessage = errorData.message || JSON.stringify(errorData)
      }
    } else if (error.message) {
      errorMessage = error.message
    }
    $q.notify({
      type: 'negative',
      message: errorMessage,
      position: 'top'
    })
  } finally {
    loading.value = false
  }
}

// Обработчик запроса таблицы
const onRequest = (props) => {
  loadFlats(props)
}

// Применение сортировки
const applySorting = () => {
  pagination.value.page = 1
  loadFlats()
}

// Сброс фильтров
const resetFilters = () => {
  filters.value = {
    name: null,
    min_area: null,
    max_area: null,
    min_rooms: null,
    max_rooms: null,
    min_price: null,
    max_price: null,
    furnish: null,
    transport: null,
    has_balcony: null
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
  saving.value = true
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
    let errorMessage = 'Неизвестная ошибка'
    if (error.response && error.response.data) {
      const errorData = error.response.data
      if (errorData.message) {
        errorMessage = errorData.message + '. '
        if (errorData.errors && errorData.errors.length > 0) {
          errorMessage += errorData.errors.join(', ')
        }
      } else {
        errorMessage = errorData.message || JSON.stringify(errorData)
      }
    } else if (error.message) {
      errorMessage = error.message
    }
    $q.notify({
      type: 'negative',
      message: errorMessage,
      position: 'top'
    })
  } finally {
    saving.value = false
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
      let errorMessage = 'Неизвестная ошибка'
      if (error.response && error.response.data) {
        const errorData = error.response.data
        if (errorData.message) {
          errorMessage = errorData.message + '. '
          if (errorData.errors && errorData.errors.length > 0) {
            errorMessage += errorData.errors.join(', ')
          }
        } else {
          errorMessage = errorData.message || JSON.stringify(errorData)
        }
      } else if (error.message) {
        errorMessage = error.message
      }
      $q.notify({
        type: 'negative',
        message: errorMessage,
        position: 'top'
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
    price: 0,
    furnish: null,
    transport: null,
    has_balcony: false,
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