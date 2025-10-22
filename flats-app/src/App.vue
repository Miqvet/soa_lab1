<template>
  <q-layout view="hHh lpR fFf">
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-toolbar-title align="center">
        Менеджер квартир
        </q-toolbar-title>
      </q-toolbar>

      <q-tabs align="center">
        <q-route-tab to="/flat" label="Квартиры" />
        <q-route-tab to="/flats" label="Список квартир" />
        <q-route-tab to="/agency" label="Агентство" />
      </q-tabs>
    </q-header>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup>
// Основной компонент приложения
</script>
