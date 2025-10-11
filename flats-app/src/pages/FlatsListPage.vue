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
      {
        pageNumber: page - 1,
        pageSize: rowsPerPage
      },
      {
        sortBy,
        sortDirection: descending ? 'desc' : 'asc'  // Добавлен sortDirection
      }
    )

    flats.value = response.data.flats
    pagination.value.rowsNumber = response.data.numberOfElements  // Изменено с totalElements на numberOfElements
    pagination.value.page = page
    pagination.value.rowsPerPage = rowsPerPage
    pagination.value.sortBy = sortBy
    pagination.value.descending = descending
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

// Получение уникальных значений жилой площади
const getUniqueLivingSpaces = async () => {
  try {
    const response = await flatsApi.getUniqueLivingSpaces()
    $q.dialog({
      title: 'Уникальные значения жилой площади',
      message: response.data.join(', ')
    })
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