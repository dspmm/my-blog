import request from './request';

export function getUsers() {
  return request('/users');
}

export function toggleUserStatus(id, newStatus) {
  return request(`/users/${id}/status?status=${newStatus}`, {
    method: 'PUT',
  });
}

export function deleteUser(id) {
  return request(`/users/${id}`, {
    method: 'DELETE',
  });
}

export function updateProfile(data) {
  return request('/users/profile', {
    method: 'PUT',
    body: data,
  });
}

export function updatePassword(data) {
  return request('/users/password', {
    method: 'PUT',
    body: data,
  });
}
