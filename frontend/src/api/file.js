import request from './request';

/**
 * 上传图片至 MinIO 分布式对象存储
 * @param {File|Blob} file 待上传的图片文件
 * @returns {Promise<{url: string, name: string}>} 图片公网访问直链及文件名
 */
export async function uploadImage(file) {
  const formData = new FormData();
  formData.append('file', file);

  const res = await request('/files/upload', {
    method: 'POST',
    body: formData,
  });

  return res.data;
}
