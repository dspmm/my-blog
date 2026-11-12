import request from './request';

export function getNotifications() {
  return request('/notifications');
}

export function markNotificationRead(id) {
  return request(`/notifications/${id}/read`, {
    method: 'POST',
  });
}

export function markAllNotificationsRead() {
  return request('/notifications/read-all', {
    method: 'POST',
  });
}

export function publishNotification(data) {
  return request('/notifications', {
    method: 'POST',
    body: data,
  });
}

export function deleteNotification(id) {
  return request(`/notifications/${id}`, {
    method: 'DELETE',
  });
}

export function clearReadNotifications(category) {
  const query = category ? `?category=${encodeURIComponent(category)}` : '';
  return request(`/notifications/clear-read${query}`, {
    method: 'POST',
  });
}

export function submitFeedback(data) {
  return request('/notifications/feedback', {
    method: 'POST',
    body: data,
  });
}
