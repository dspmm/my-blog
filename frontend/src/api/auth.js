import request from './request';

export function login(loginForm) {
  return request('/auth/login', {
    method: 'POST',
    body: loginForm,
  });
}

export function register(registerForm) {
  return request('/auth/register', {
    method: 'POST',
    body: registerForm,
  });
}

export function sendEmailCode(email) {
  return request('/auth/send-code', {
    method: 'POST',
    body: { email },
  });
}

export function getUserInfo() {
  return request('/auth/info');
}
