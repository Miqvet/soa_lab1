import axios from 'axios'

const api = axios.create({
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

  // Получить все квартиры с фильтрацией
  getFlats: (filters, pagination, sort) => {
    const params = {
      page: pagination.page || 0,
      size: pagination.rowsPerPage || 20,
      sortBy: sort.sortBy || 'id',
      sortDirection: sort.descending ? 'desc' : 'asc',
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