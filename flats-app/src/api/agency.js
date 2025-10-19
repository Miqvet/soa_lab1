import axios from 'axios'

const API_BASE_URL = '/agency-api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const agencyApi = {
  // Выбрать наиболее дорогую квартиру из трёх
  getMostExpensive: (id1, id2, id3) => {
    return api.post(`/agency/get-most-expensive/${id1}/${id2}/${id3}`)
  },

  // Найти самую дешёвую/дорогую квартиру с балконом/без балкона
  findWithBalcony: (cheapest, withBalcony) => {
    return api.post(`/agency/find-with-balcony/${cheapest}/${withBalcony}`)
  },
}