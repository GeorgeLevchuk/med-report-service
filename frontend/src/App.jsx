import React, { useState } from 'react';
import { Card, Typography, Button, Alert, Divider } from 'antd';
import { DownloadOutlined } from '@ant-design/icons';
import { fetchReport } from './api';

const { Title, Paragraph, Text } = Typography;

function saveBlob(blob, filename) {
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}

const docNumber = (() => {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}`;
})();

const docDate = new Date().toLocaleDateString('ru-RU', {
  day: '2-digit',
  month: '2-digit',
  year: 'numeric',
});

const LEDGER_ROWS = [
  ['Источник данных', 'test-data.db3'],
  ['Отчётный период', '2014 г.'],
  ['Формат выгрузки', '.xlsx'],
];

export default function App() {
  const [loading, setLoading] = useState(false);
  const [status, setStatus] = useState(null);

  const handleGenerate = async () => {
    setStatus(null);
    setLoading(true);
    try {
      const { blob, filename } = await fetchReport();
      saveBlob(blob, filename);
      setStatus({ type: 'success', message: `Отчёт сформирован и сохранён как ${filename}.` });
    } catch (err) {
      setStatus({ type: 'error', message: `Не удалось сформировать отчёт: ${err.message}` });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page">
      <main className="page__main">
        <div className="eyebrow">
          <span>
            Свод № <span className="mono">{docNumber}</span>
          </span>
          <span className="mono">{docDate}</span>
        </div>

        <Card bordered className="card">
          <Title level={3} className="title">
            Отчёт по случаям оказания медицинской помощи
          </Title>
          <Paragraph className="subtitle">
            Разбивка по иерархии <Text strong className="teal">СМО → МО → МКБ</Text> с
            количеством случаев на каждом уровне.
          </Paragraph>

          <div className="ledger">
            {LEDGER_ROWS.map(([label, value]) => (
              <div className="ledger__row" key={label}>
                <span>{label}</span>
                <b className="mono">{value}</b>
              </div>
            ))}
          </div>

          <Button
            type="primary"
            size="large"
            block
            icon={<DownloadOutlined />}
            loading={loading}
            onClick={handleGenerate}
          >
            {loading ? 'Формируем отчёт…' : 'Сформировать и скачать отчёт'}
          </Button>

          {status && (
            <>
              <Divider style={{ margin: '16px 0 12px' }} />
              <Alert
                type={status.type}
                message={status.message}
                showIcon
                closable
                onClose={() => setStatus(null)}
              />
            </>
          )}
        </Card>

        <footer className="footer mono">мед-отчёт-сервис · GET /api/report</footer>
      </main>
    </div>
  );
}
