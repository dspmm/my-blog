import request from './request';

export function getPosts(params = {}) {
  const query = new URLSearchParams();
  if (params.category && params.category !== '全部') {
    query.append('category', params.category);
  }
  if (params.search && params.search.trim()) {
    query.append('search', params.search.trim());
  }
  const qs = query.toString();
  return request(`/posts${qs ? `?${qs}` : ''}`);
}

export function getAllPosts() {
  return request('/posts');
}

export function getPostById(id) {
  return request(`/posts/${id}`);
}

export function createPost(data) {
  return request('/posts', {
    method: 'POST',
    body: data,
  });
}

export function updatePost(id, data) {
  return request(`/posts/${id}`, {
    method: 'PUT',
    body: data,
  });
}

export function deletePost(id) {
  return request(`/posts/${id}`, {
    method: 'DELETE',
  });
}

export function likePost(id) {
  return request(`/posts/${id}/like`, {
    method: 'POST',
  });
}

export function getStats() {
  return request('/stats');
}
