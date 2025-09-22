import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080' // Замените на ваш URL

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const flatsApi = {
  // Получить все квартиры с фильтрацией
  getFlats: (filters, pagination, sort) => {
    const params = {
      page: pagination.page || 0,
      size: pagination.rowsPerPage || 20,
      sortBy: sort.sortBy || 'id',
      sortDirection: sort.descending ? 'desc' : 'asc',
    }

    return api.post('/api/flats/filtered', filters, { params })
  },

  // Получить квартиру по ID
  getFlatById: (id) => {
    return api.get(`/api/flats/${id}`)
  },

  // Создать новую квартиру
  createFlat: (flatData) => {
    return api.post('/api/flats', flatData)
  },

  // Обновить квартиру
  updateFlat: (id, flatData) => {
    return api.put(`/api/flats/${id}`, flatData)
  },

  // Удалить квартиру
  deleteFlat: (id) => {
    return api.delete(`/api/flats/${id}`)
  },

  // Получить уникальные значения жилой площади
  getUniqueLivingSpaces: () => {
    return api.get('/api/flats/unique_living_spaces')
  },

  // Получить квартиры с количеством комнат больше указанного
  getFlatsWithMoreRooms: (rooms) => {
    return api.get(`/api/flats/rooms_greater_than/${rooms}`)
  },

  // Удалить квартиры по типу отделки
  deleteByFurnish: (furnish) => {
    return api.delete(`/api/flats/by_furnish/${furnish}`)
  },
}