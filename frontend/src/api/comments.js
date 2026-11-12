import request from './request';

export function getComments(postId) {
  return request(`/posts/${postId}/comments`);
}

export function submitComment(postId, data) {
  return request(`/posts/${postId}/comments`, {
    method: 'POST',
    body: data,
  });
}

export function getRecentComments() {
  return request('/comments/recent');
}

export function getAdminComments(params = {}) {
  let query = [];
  if (params.postId) query.push(`postId=${encodeURIComponent(params.postId)}`);
  if (params.status && params.status !== 'ALL') query.push(`status=${encodeURIComponent(params.status)}`);
  if (params.keyword) query.push(`keyword=${encodeURIComponent(params.keyword)}`);
  const qs = query.length > 0 ? `?${query.join('&')}` : '';
  return request(`/comments/all${qs}`);
}

export function getCommentAuditStats() {
  return request('/comments/stats');
}

export function auditComment(commentId, status, remark = '') {
  return request(`/comments/${commentId}/audit`, {
    method: 'PUT',
    body: { status, remark },
  });
}

export function toggleCommentTop(commentId) {
  return request(`/comments/${commentId}/top`, {
    method: 'PUT',
  });
}

export function deleteComment(commentId) {
  return request(`/comments/${commentId}`, {
    method: 'DELETE',
  });
}
