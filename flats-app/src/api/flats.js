import axios from 'axios'

const API_BASE_URL = '/flats-api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const flatsApi = {
  // Получить квартиру по ID
  getFlatById: (id) => {
    return api.get(`/flats/${id}`)
  },

  // Обновить квартиру
  updateFlat: (id, flatData) => {
    return api.put(`/flats/${id}`, flatData)
  },

  // Удалить квартиру
  deleteFlat: (id) => {
    return api.delete(`/flats/${id}`)
  },

  // Создать новую квартиру
  createFlat: (flatData) => {
    return api.post('/flats', flatData)
  },

  // Получить уникальные значения жилой площади get(Статус джоба)
  getUniqueLivingSpaces: () => {
    return api.get('/flats/unique-living-spaces')
  },

  // Получить уникальные значения жилой площади post(Начать асинхр задачу)
  postUniqueLivingSpaces: () => {
    return api.post('/flats/unique-living-spaces')
  },

  // Получить уникальные значения жилой площади delete(Остановить задачу)
  deleteUniqueLivingSpaces: () => {
    return api.delete('/flats/unique-living-spaces')
  },

  getFlats: (filters, pagination, sort) => {
    const params = {
      pageNumber: pagination.pageNumber || 0,
      pageSize: pagination.pageSize || 20,
    }

    // Добавляем параметры сортировки если они есть
    if (sort && sort.sortBy) {
      params.sortBy = sort.sortBy
      params.sortDirection = sort.sortDirection || 'asc'
    }

    // Очищаем фильтры от null/undefined значений
    const cleanFilters = {}
    Object.keys(filters).forEach(key => {
      if (filters[key] !== null && filters[key] !== undefined && filters[key] !== '') {
        cleanFilters[key] = filters[key]
      }
    })

    return api.post('/flats/filter', cleanFilters, { params })
  },

  // Получить квартиры с количеством комнат больше указанного
  getFlatsWithMoreRooms: (rooms) => {
    return api.get(`/flats/rooms-greater-than/${rooms}`)
  },

  // Удалить квартиры по типу отделки
  deleteByFurnish: (furnish) => {
    return api.delete(`/flats/by-furnish/${furnish}`)
  },
}