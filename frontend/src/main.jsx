import React from 'react';
import ReactDOM from 'react-dom/client';
import { ConfigProvider } from 'antd';
import ruRU from 'antd/locale/ru_RU';
import App from './App.jsx';
import './index.css';


const theme = {
  token: {
    colorPrimary: '#2F6F62',
    colorLink: '#2F6F62',
    colorError: '#B3402A',
    colorSuccess: '#2F6F62',
    borderRadius: 3,
    fontFamily: "'IBM Plex Sans', sans-serif",
  },
  components: {
    Button: {
      fontWeight: 600,
      controlHeight: 48,
    },
    Card: {
      borderRadiusLG: 3,
    },
  },
};

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <ConfigProvider theme={theme} locale={ruRU}>
      <App />
    </ConfigProvider>
  </React.StrictMode>,
);
