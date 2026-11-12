export function formatDate(dt) {
  if (!dt) return '刚刚';
  return String(dt).split('T')[0];
}

export function parseTags(tags) {
  if (!tags) return [];
  return String(tags).split(',').map(t => t.trim()).filter(Boolean);
}

export function getCategoryIcon(cat) {
  const map = {
    '云原生 & Linux': '☁️',
    'Java & Spring Boot': '🍃',
    'AI & 本地模型': '🧠',
    '全栈开发 & CI/CD': '⚙️',
  };
  return map[cat] || '📑';
}
