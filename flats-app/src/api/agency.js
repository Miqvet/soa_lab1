import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080' // Замените на ваш URL

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const agencyApi = {
  // Выбрать наиболее дорогую квартиру из трёх
  getMostExpensive: (id1, id2, id3) => {
    return api.get(`/api/agency/get-most-expensive/${id1}/${id2}/${id3}`)
  },

  // Найти самую дешёвую/дорогую квартиру с балконом/без балкона
  findWithBalcony: (cheapest, withBalcony) => {
    return api.get(`/api/agency/find-with-balcony/${cheapest}/${withBalcony}`)
  },
}