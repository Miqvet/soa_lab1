<template>
  <q-page class="q-pa-md">
    <div class="text-h4 q-mb-md">Управление квартирами</div>

    <!-- Дополнительные действия -->
    <div class="q-mb-md">
      <q-btn label="Добавить квартиру" @click="showAddDialog = true" color="positive" class="q-mr-sm" />
      <div class="row items-center q-gutter-sm q-mt-xs"></div>
      <!-- Кнопки для работы с уникальными значениями жилой площади -->
       <div class="row items-center q-gutter-sm q-mt-md">
              <q-btn
                label="Запустить задачу"
                @click="launchUniqueLivingSpacesJob"
                color="info"
                class="q-mr-sm"
                :loading="jobLoading"
                :disable="jobStatus === 'running'"
              />
              <q-btn
                label="Проверить статус"
                @click="getUniqueLivingSpaces"
                color="info"
                class="q-mr-sm"
                :disable="jobStatus === 'idle'"
              />
              <q-btn
                label="Отменить задачу"
                @click="cancelUniqueLivingSpacesJob"
                color="negative"
                class="q-mr-sm"
                :disable="jobStatus !== 'running'"
              />
            </div>

            <!-- Статус задачи -->
            <div v-if="jobStatus !== 'idle'" class="q-mt-sm">
              <q-badge :color="statusColor" class="q-pa-sm">
                Статус: {{ statusText }}
              </q-badge>
              <q-badge v-if="uniqueSpacesResult" color="positive" class="q-pa-sm q-ml-sm">
                Результат готов
              </q-badge>
            </div>

            <div class="row items-center q-gutter-sm q-mt-xs"></div>
            <q-btn label="Удалить по отделке" @click="showDeleteByFurnishDialog = true" color="negative" />

            <!-- Поиск и удаление по ID -->
            <div class="row items-center q-gutter-sm q-mt-xs">
              <q-input class="q-mt-lg"
                v-model="flatId"
                label="ID квартиры"
                type="number"
                outlined
                dense
                style="min-width: 150px; max-width: 200px"
                :rules="[validatePositiveInteger]"
                @update:model-value="val => flatId = enforceInteger(val)"
              />
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
            </div>
          </div>

    <!-- Диалог добавления/редактирования квартиры -->
    <q-dialog v-model="showAddDialog" persistent>
      <q-card style="min-width: 70%; max-width: 900px">
        <q-card-section class="bg-primary text-white">
          <div class="text-h6">{{ editingFlat ? 'Редактирование' : 'Добавление' }} квартиры</div>
        </q-card-section>

        <q-card-section class="q-pt-lg scroll" style="max-height: 70vh">
          <q-form @submit="saveFlat" class="q-gutter-pa-lg" ref="formRef">
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
                  maxlength="250"
                  counter
                  :rules="[
                    val => !!val || 'Обязательное поле',
                    val => val && val.trim().length > 0 || 'Название не может быть пустым',
                    val => val.length <= 250 || 'Максимальная длина: 250 символов'
                  ]"
                />
              </div>
              <div class="col-12 col-sm-6">
                <q-input
                  v-model.number="flatForm.area"
                  type="number"
                  label="Площадь *"
                  outlined
                  :rules="[
                    val => !!val || 'Обязательное поле',
                    val => validatePositiveInteger(val) === true || validatePositiveInteger(val),
                    val => val <= 784 || 'Максимальная площадь: 784'
                  ]"
                  @update:model-value="val => flatForm.area = enforceInteger(val)"
                />
              </div>
              <div class="col-12 col-sm-6">
                <q-input
                  v-model.number="flatForm.number_of_rooms"
                  type="number"
                  label="Количество комнат *"
                  outlined
                  :rules="[
                    val => !!val || 'Обязательное поле',
                    val => validatePositiveInteger(val) === true || validatePositiveInteger(val),
                    val => val <= 8 || 'Максимальное количество комнат: 8'
                  ]"
                  @update:model-value="val => flatForm.number_of_rooms = enforceInteger(val)"
                />
              </div>
              <div class="col-12 col-sm-6">
                <q-input
                  v-model.number="flatForm.living_space"
                  type="number"
                  label="Жилая площадь *"
                  outlined
                  step="0.001"
                  :rules="[
                    val => !!val || 'Обязательное поле',
                    val => val > 0 || 'Жилая площадь должна быть больше 0',
                    val => val <= 1000000000 || 'Максимальное значение: 1 000 000 000',
                    val => validateDecimalPlaces(val, 3) === true || validateDecimalPlaces(val, 3)
                  ]"
                />
              </div>
              <div class="col-12 col-sm-6">
                <q-input
                  v-model.number="flatForm.price"
                  type="number"
                  label="Цена *"
                  outlined
                  step="0.001"
                  :rules="[
                    val => !!val || 'Обязательное поле',
                    val => val > 0 || 'Цена должна быть больше 0',
                    val => val <= 1000000000 || 'Максимальное значение: 1 000 000 000',
                    val => validatePositiveInteger(val) === true || validatePositiveInteger(val)
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
                  v-model.number="flatForm.coordinates.x"
                  type="number"
                  label="Координата X *"
                  outlined
                  :rules="[
                    val => val !== null && val !== undefined || 'Обязательное поле',
                    val => validateInteger(val) === true || validateInteger(val),
                    val => val > -13 || 'Значение должно быть больше -13',
                    val => val <= 1000000000 || 'Максимальное значение: 1 000 000 000'
                  ]"
                  @update:model-value="val => flatForm.coordinates.x = enforceInteger(val)"
                />
              </div>
              <div class="col-12 col-sm-6">
                <q-input
                  v-model.number="flatForm.coordinates.y"
                  type="number"
                  label="Координата Y *"
                  outlined
                  step="0.001"
                  :rules="[
                    val => val !== null && val !== undefined || 'Обязательное поле',
                    val => val > -733 || 'Значение должно быть больше -733',
                    val => val <= 1000000000 || 'Максимальное значение: 1 000 000 000',
                    val => validateDecimalPlaces(val, 3) === true || validateDecimalPlaces(val, 3)
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
                  maxlength="250"
                  counter
                  :rules="[
                    val => !!val || 'Обязательное поле',
                    val => val && val.trim().length > 0 || 'Название дома не может быть пустым',
                    val => val.length <= 250 || 'Максимальная длина: 250 символов'
                  ]"
                />
              </div>
              <div class="col-12 col-sm-6">
                <q-input
                  v-model.number="flatForm.house.year"
                  type="number"
                  label="Год постройки *"
                  outlined
                  :rules="[
                    val => !!val || 'Обязательное поле',
                    val => validatePositiveInteger(val) === true || validatePositiveInteger(val),
                    val => val <= 370 || 'Максимальный год: 370'
                  ]"
                  @update:model-value="val => flatForm.house.year = enforceInteger(val)"
                />
              </div>
              <div class="col-12 col-sm-6">
                <q-input
                  v-model.number="flatForm.house.number_of_floors"
                  type="number"
                  label="Количество этажей *"
                  outlined
                  :rules="[
                    val => !!val || 'Обязательное поле',
                    val => validatePositiveInteger(val) === true || validatePositiveInteger(val),
                    val => val <= 1000000000 || 'Максимальное значение: 1 000 000 000'
                  ]"
                  @update:model-value="val => flatForm.house.number_of_floors = enforceInteger(val)"
                />
              </div>
              <div class="col-12 col-sm-6">
                <q-input
                  v-model.number="flatForm.house.number_of_lifts"
                  type="number"
                  label="Количество лифтов *"
                  outlined
                  :rules="[
                    val => val !== null && val !== undefined || 'Обязательное поле',
                    val => validatePositiveInteger(val) === true || validatePositiveInteger(val),
                    val => val <= 1000000000 || 'Максимальное значение: 1 000 000 000'
                  ]"
                  @update:model-value="val => flatForm.house.number_of_lifts = enforceInteger(val)"
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
                :disable="!isFormValid"
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
                <div class="text-body1">{{ formatDecimal(viewedFlat.living_space) }} м²</div>
              </div>
              <div class="col-12 col-sm-6">
                <div class="text-caption text-grey-7">Цена</div>
                <div class="text-body1">{{ formatDecimal(viewedFlat.price) }} ₽</div>
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
                <div class="text-body1">{{ viewedFlat.has_balcony ? 'Да' : 'Нет' }}</div>
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
                <div class="text-body1">{{ formatDecimal(viewedFlat.coordinates.y) }}</div>
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
import { ref, onMounted, computed, nextTick } from 'vue'
import { useQuasar } from 'quasar'
import { flatsApi } from '../api/flats.js'

const $q = useQuasar()

// Опции для селектов с русскими названиями
const furnishOptions = [
  { label: 'Дизайнерская', value: 'DESIGNER' },
  { label: 'Без отделки', value: 'NONE' },
  { label: 'Хорошая', value: 'FINE' },
  { label: 'Плохая', value: 'BAD' },
  { label: 'Минимальная', value: 'LITTLE' }
]

const transportOptions = [
  { label: 'Мало', value: 'FEW' },
  { label: 'Отсутствует', value: 'NONE' },
  { label: 'Чуть-чуть', value: 'LITTLE' },
  { label: 'Достаточно', value: 'ENOUGH' }
]

// Жесткая валидация целых положительных чисел
const validatePositiveInteger = (val) => {
  if (val === null || val === '' || val === undefined) {
    return 'Обязательное поле'
  }

  const num = Number(val)
  if (isNaN(num) || !Number.isInteger(num) || num <= 0) {
    return 'Введите целое положительное число'
  }

  if (num > 1000000000) {
    return 'Максимальное значение: 1 000 000 000'
  }

  return true
}

// Валидация целых чисел (может быть отрицательным)
const validateInteger = (val) => {
  if (val === null || val === '' || val === undefined) {
    return 'Обязательное поле'
  }

  const num = Number(val)
  if (isNaN(num) || !Number.isInteger(num)) {
    return 'Введите целое число'
  }

  if (num > 1000000000) {
    return 'Максимальное значение: 1 000 000 000'
  }

  return true
}

// Валидация количества знаков после запятой
const validateDecimalPlaces = (val, maxDecimalPlaces) => {
  if (val === null || val === '' || val === undefined) {
    return true
  }

  const num = Number(val)
  if (isNaN(num)) {
    return 'Введите число'
  }

  if (num > 1000000000) {
    return 'Максимальное значение: 1 000 000 000'
  }

  // Проверка количества знаков после запятой
  const decimalPart = String(val).split('.')[1]
  if (decimalPart && decimalPart.length > maxDecimalPlaces) {
    return `Максимум ${maxDecimalPlaces} знака после запятой`
  }

  return true
}

// Принудительное приведение к целому числу
const enforceInteger = (val) => {
  if (val === null || val === '' || val === undefined) {
    return null
  }

  const num = Number(val)
  if (isNaN(num)) {
    return null
  }

  return Math.floor(num)
}

// Форматирование чисел с плавающей точкой для отображения
const formatDecimal = (value) => {
  if (value === null || value === undefined) return '0'
  const num = Number(value)
  if (isNaN(num)) return '0'

  // Округляем до 3 знаков после запятой для отображения
  return Math.round(num * 1000) / 1000
}

// Реактивные данные
const jobLoading = ref(false)
const uniqueSpacesResult = ref(null)
const flatId = ref(null)
const showFlatViewDialog = ref(false)
const viewedFlat = ref(null)
const loadingFlat = ref(false)
const flats = ref([])
const loading = ref(false)
const saving = ref(false)
const showAddDialog = ref(false)
const showDeleteByFurnishDialog = ref(false)
const editingFlat = ref(null)
const deleteFurnish = ref(null)
const formRef = ref(null)

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

// Вычисляемое свойство для проверки валидности формы
const isFormValid = computed(() => {
  const form = flatForm.value

  // Проверка основных полей
  if (!form.name || !form.name.trim() || form.name.length > 250) return false
  if (!form.area || form.area <= 0 || form.area > 784) return false
  if (!form.number_of_rooms || form.number_of_rooms <= 0 || form.number_of_rooms > 8) return false
  if (!form.living_space || form.living_space <= 0 || form.living_space > 1000000000) return false
  if (!form.price || form.price <= 0 || form.price > 1000000000) return false
  if (!form.furnish) return false
  if (!form.transport) return false

  // Проверка координат
  if (form.coordinates.x === null || form.coordinates.x === undefined || form.coordinates.x <= -13 || form.coordinates.x > 1000000000) return false
  if (form.coordinates.y === null || form.coordinates.y === undefined || form.coordinates.y <= -733 || form.coordinates.y > 1000000000) return false

  // Проверка дома
  if (!form.house.name || !form.house.name.trim() || form.house.name.length > 250) return false
  if (!form.house.year || form.house.year <= 0 || form.house.year > 370) return false
  if (!form.house.number_of_floors || form.house.number_of_floors <= 0 || form.house.number_of_floors > 1000000000) return false
  if (form.house.number_of_lifts === null || form.house.number_of_lifts === undefined || form.house.number_of_lifts <= 0 || form.house.number_of_lifts > 1000000000) return false

  return true
})

// Загрузка данных
const loadFlats = async (props = {}) => {
  loading.value = true
  try {
    const page = props.pagination?.page || pagination.value.page
    const rowsPerPage = props.pagination?.rowsPerPage || pagination.value.rowsPerPage

    const response = await flatsApi.getFlats(
      filters.value,
      {
        pageNumber: page - 1,
        pageSize: rowsPerPage
      },
      {}
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

// Редактирование квартиры
const editFlat = (flat) => {
  editingFlat.value = flat.id
  flatForm.value = { ...flat }
  showAddDialog.value = true
}

// Сохранение квартиры
const saveFlat = async () => {
  // Дополнительная проверка перед отправкой
  if (!isFormValid.value) {
    $q.notify({
      type: 'negative',
      message: 'Пожалуйста, заполните все поля корректно',
      position: 'top'
    })
    return
  }

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
    message: `Вы уверены, что хотите удалить все квартиры с отделкой "${getFurnishLabel(deleteFurnish.value)}"?`,
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

  // Дополнительная проверка перед отправкой
  if (validatePositiveInteger(flatId.value) !== true) {
    $q.notify({
      type: 'negative',
      message: 'ID должен быть целым положительным числом',
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

  // Дополнительная проверка перед отправкой
  if (validatePositiveInteger(flatId.value) !== true) {
    $q.notify({
      type: 'negative',
      message: 'ID должен быть целым положительным числом',
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

// Состояние задачи для уникальных значений
const jobStatus = ref('idle') // 'idle', 'running', 'completed', 'cancelled'

// Вычисляемые свойства для статуса
const statusText = computed(() => {
  switch (jobStatus.value) {
    case 'running': return 'Задача выполняется'
    case 'completed': return 'Задача завершена'
    case 'cancelled': return 'Задача отменена'
    default: return 'Задача не запущена'
  }
})

const statusColor = computed(() => {
  switch (jobStatus.value) {
    case 'running': return 'orange'
    case 'completed': return 'positive'
    case 'cancelled': return 'negative'
    default: return 'grey'
  }
})

// Запуск задачи
const launchUniqueLivingSpacesJob = async () => {
  jobLoading.value = true
  try {
    await flatsApi.postUniqueLivingSpaces()
    jobStatus.value = 'running'
    uniqueSpacesResult.value = null
    $q.notify({
      type: 'positive',
      message: 'Задача по поиску уникальных значений жилой площади запущена',
      position: 'top'
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
  } finally {
    jobLoading.value = false
  }
}

// Проверка статуса и получение результата
const getUniqueLivingSpaces = async () => {
  try {
    const response = await flatsApi.getUniqueLivingSpaces()

    if (response.status === 200) {
      // Задача завершена, результат готов
      jobStatus.value = 'completed'
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
          message: 'Результат пуст',
          position: 'top'
        })
      }
    } else if (response.status === 202) {
      // Задача еще выполняется
      jobStatus.value = 'running'
      $q.notify({
        type: 'info',
        message: 'Задача еще выполняется. Проверьте позже.',
        position: 'top'
      })
    }
  } catch (error) {
    if (error.response && error.response.status === 404) {
      // Задача не найдена
      jobStatus.value = 'idle'
      $q.notify({
        type: 'warning',
        message: 'Задача не найдена. Запустите задачу сначала.',
        position: 'top'
      })
    } else {
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
}

// Отмена задачи
const cancelUniqueLivingSpacesJob = async () => {
  try {
    await flatsApi.deleteUniqueLivingSpaces()
    jobStatus.value = 'cancelled'
    uniqueSpacesResult.value = null
    $q.notify({
      type: 'positive',
      message: 'Задача по поиску уникальных значений жилой площади отменена',
      position: 'top'
    })
  } catch (error) {
    if (error.response && error.response.status === 404) {
      $q.notify({
        type: 'warning',
        message: 'Задача не найдена',
        position: 'top'
      })
    } else {
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
}

// Загрузка при монтировании
onMounted(() => {
  loadFlats()
})
</script>