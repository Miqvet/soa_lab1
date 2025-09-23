<template>
  <q-page class="q-pa-md">
    <div class="text-h4 q-mb-md">Операции агентства</div>

    <!-- Поиск самой дорогой квартиры из трех -->
    <q-card class="q-mb-md">
      <q-card-section>
        <div class="text-h6">Найти самую дорогую квартиру из трех</div>
        <q-form @submit="findMostExpensive" class="q-gutter-pa-md">
          <div class="row q-col-gutter-md">
            <div class="col-12 col-sm-4">
              <q-input
                v-model="expensiveIds.id1"
                type="number"
                label="ID квартиры 1"
                :rules="[val => !!val || 'Обязательное поле']"
              />
            </div>
            <div class="col-12 col-sm-4">
              <q-input
                v-model="expensiveIds.id2"
                type="number"
                label="ID квартиры 2"
                :rules="[val => !!val || 'Обязательное поле']"
              />
            </div>
            <div class="col-12 col-sm-4">
              <q-input
                v-model="expensiveIds.id3"
                type="number"
                label="ID квартиры 3"
                :rules="[val => !!val || 'Обязательное поле']"
              />
            </div>
            <div class="col-12">
              <q-btn label="Найти" type="submit" color="primary" />
            </div>
          </div>
        </q-form>

        <div v-if="mostExpensiveFlat" class="q-mt-md">
          <div class="text-subtitle1">Самая дорогая квартира:</div>
          <pre>{{ JSON.stringify(mostExpensiveFlat, null, 2) }}</pre>
        </div>
      </q-card-section>
    </q-card>

    <!-- Поиск квартиры с балконом -->
    <q-card>
      <q-card-section>
        <div class="text-h6">Найти квартиру с балконом</div>
        <q-form @submit="findBalconyFlat" class="q-gutter-pa-md">
          <div class="row q-col-gutter-md">
            <div class="col-12 col-sm-6">
              <q-select
                v-model="balconyParams.cheapest"
                :options="[
                  { label: 'Самая дешевая', value: true },
                  { label: 'Самая дорогая', value: false }
                ]"
                label="Тип поиска"
                emit-value
                map-options
                :rules="[val => val !== null || 'Обязательное поле']"
              />
            </div>
            <div class="col-12 col-sm-6">
              <q-select
                v-model="balconyParams.withBalcony"
                :options="[
                  { label: 'С балконом', value: true },
                  { label: 'Без балкона', value: false }
                ]"
                label="Наличие балкона"
                emit-value
                map-options
                :rules="[val => val !== null || 'Обязательное поле']"
              />
            </div>
            <div class="col-12">
              <q-btn label="Найти" type="submit" color="primary" />
            </div>
          </div>
        </q-form>

        <div v-if="balconyFlat" class="q-mt-md">
          <div class="text-subtitle1">Найденная квартира:</div>
          <pre>{{ JSON.stringify(balconyFlat, null, 2) }}</pre>
        </div>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref } from 'vue'
import { useQuasar } from 'quasar'
import { agencyApi } from '../api/agency.js'

const $q = useQuasar()

// Данные для формы поиска самой дорогой квартиры
const expensiveIds = ref({
  id1: null,
  id2: null,
  id3: null
})

const mostExpensiveFlat = ref(null)

// Данные для формы поиска квартиры с балконом
const balconyParams = ref({
  cheapest: null,
  withBalcony: null
})

const balconyFlat = ref(null)

// Поиск самой дорогой квартиры
const findMostExpensive = async () => {
  try {
    const response = await agencyApi.getMostExpensive(
      expensiveIds.value.id1,
      expensiveIds.value.id2,
      expensiveIds.value.id3
    )
    mostExpensiveFlat.value = response.data
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Ошибка при поиске самой дорогой квартиры: ' + error.message
    })
  }
}

// Поиск квартиры с балконом
const findBalconyFlat = async () => {
  try {
    const response = await agencyApi.findWithBalcony(
      balconyParams.value.cheapest,
      balconyParams.value.withBalcony
    )
    balconyFlat.value = response.data
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Ошибка при поиске квартиры: ' + error.message
    })
  }
}
</script>