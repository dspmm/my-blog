const API_BASE = '/api';

export function getAuthHeaders() {
  const token = localStorage.getItem('ry_token');
  return token ? { 'Authorization': `Bearer ${token}` } : {};
}

export async function request(url, options = {}) {
  const fullUrl = url.startsWith('http') ? url : `${API_BASE}${url}`;
  const headers = {
    'Content-Type': 'application/json',
    ...getAuthHeaders(),
    ...(options.headers || {}),
  };

  const config = {
    ...options,
    headers,
  };

  if (options.body instanceof FormData) {
    delete headers['Content-Type'];
  } else if (options.body && typeof options.body === 'object') {
    config.body = JSON.stringify(options.body);
  }

  const res = await fetch(fullUrl, config);
  const data = await res.json().catch(() => null);

  if (!res.ok) {
    const errorMsg = data?.msg || data?.message || `请求失败 (${res.status})`;
    throw new Error(errorMsg);
  }

  return data;
}

export default request;
