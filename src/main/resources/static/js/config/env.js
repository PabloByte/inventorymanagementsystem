// main/static/js/config/env.js

// Valor por defecto (sirve cuando backend y front viven juntos en Spring Boot)
const DEFAULT_API_BASE = "/api";

// Permite override desde HTML o desde un script previo:
// 1) <meta name="api-base" content="https://tu-dominio.com/api">
// 2) window.__API_BASE__ = "https://tu-dominio.com/api"
const meta = document.querySelector('meta[name="api-base"]');
const override = (window.__API_BASE__ ?? (meta ? meta.content : null)) || DEFAULT_API_BASE;

// Normaliza (sin slash final)
const apiUrl = String(override).replace(/\/$/, "");

// Flags útiles
const ENV = {
  apiUrl,
  origin: window.location.origin,
  isLocal: ["localhost", "127.0.0.1"].includes(window.location.hostname),
  isSupabase: /supabaseapp\.com$/i.test(window.location.hostname),
};

export { apiUrl, ENV };
export default ENV;
