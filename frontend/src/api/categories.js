import request from './request';

export function getCategories() {
  return request('/categories');
}

export function createCategory(data) {
  return request('/categories', {
    method: 'POST',
    body: data,
  });
}

export function updateCategory(id, data) {
  return request(`/categories/${id}`, {
    method: 'PUT',
    body: data,
  });
}

export function deleteCategory(id) {
  return request(`/categories/${id}`, {
    method: 'DELETE',
  });
}
