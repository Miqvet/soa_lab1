import axios from 'axios'

const API_BASE_URL = 'https://localhost:8181' // Замените на ваш URL

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const flatsApi = {
  // Получить квартиру по ID
  getFlatById: (id) => {
    return api.get(`/api/flats/${id}`)
  },

  // Обновить квартиру
  updateFlat: (id, flatData) => {
    return api.put(`/api/flats/${id}`, flatData)
  },

  // Удалить квартиру
  deleteFlat: (id) => {
    return api.delete(`/api/flats/${id}`)
  },

  // Создать новую квартиру
  createFlat: (flatData) => {
    return api.post('/api/flats', flatData)
  },

  // Получить уникальные значения жилой площади get(Статус джоба)
  getUniqueLivingSpaces: () => {
    return api.get('/api/flats/unique-living-spaces')
  },

  // Получить уникальные значения жилой площади post(Начать асинхр задачу)
  postUniqueLivingSpaces: () => {
    return api.post('/api/flats/unique-living-spaces')
  },

  // Получить уникальные значения жилой площади delete(Остановить задачу)
  deleteUniqueLivingSpaces: () => {
    return api.delete('/api/flats/unique-living-spaces')
  },

  getFlats: (filters, pagination, sort) => {
    const params = {
      pageNumber: pagination.pageNumber || 0,      // Изменено с page на pageNumber
      pageSize: pagination.pageSize || 20,         // Изменено с size на pageSize
      sortBy: sort.sortBy || 'id',
      sortDirection: sort.sortDirection || 'asc',   // Добавлен sortDirection
    }

    return api.post('/api/flats/filter', filters, { params })
  },

  // Получить квартиры с количеством комнат больше указанного
  getFlatsWithMoreRooms: (rooms) => {
    return api.get(`/api/flats/rooms-greater-than/${rooms}`)
  },

  // Удалить квартиры по типу отделки
  deleteByFurnish: (furnish) => {
    return api.delete(`/api/flats/by-furnish/${furnish}`)
  },
}