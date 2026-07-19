import axios from 'axios';

export async function fetchReport(zip = false) {
  try {
    const response = await axios.get('/api/report', {
      responseType: 'blob',
      params: zip ? { zip: true } : undefined,
    });

    const disposition = response.headers['content-disposition'] || '';
    const match = disposition.match(/filename="?([^"]+)"?/);
    const filename = match ? match[1] : (zip ? 'report.zip' : 'report.xlsx');

    return { blob: response.data, filename };
  } catch (err) {
    throw new Error(await extractErrorMessage(err));
  }
}

async function extractErrorMessage(err) {
  const data = err?.response?.data;

  if (data instanceof Blob) {
    try {
      const text = await data.text();
      const parsed = JSON.parse(text);
      if (parsed?.message) return parsed.message;
    } catch (_) {

    }
  }

  if (err.response) {
    return `Сервис вернул ошибку (${err.response.status})`;
  }
  if (err.request) {
    return 'Сервис недоступен. Проверьте, что backend запущен.';
  }
  return err.message || 'Неизвестная ошибка';
}
