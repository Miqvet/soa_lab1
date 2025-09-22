<template>
  <q-layout view="hHh lpR fFf">
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-toolbar-title>
          Real Estate App
        </q-toolbar-title>
        <q-btn flat round dense icon="menu" class="q-mr-xs" />
      </q-toolbar>

      <q-tabs align="center">
        <q-route-tab to="/flats" label="Квартиры" />
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
