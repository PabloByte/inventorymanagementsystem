// dashboard.js — aislado: no maneja categorías ni intercepta navegación
// Solo corre en /pages/dashboard.html

class Dashboard {
  constructor() {
    this.contentArea = document.getElementById("contentArea");
    if (!this.contentArea) return; // seguridad adicional

    this.init();
  }

  init() {
    this.checkAuth();
    this.renderHome();
    this.bindSignOut(); // opcional, si existe el botón
  }

  checkAuth() {
    if (!localStorage.getItem("isLoggedIn")) {
      window.location.href = "/pages/login.html";
      return;
    }
    const cu = document.getElementById("currentUser");
    if (cu) cu.textContent = localStorage.getItem("username") || "Admin";
  }

  renderHome() {
    const pageTitle = document.getElementById("pageTitle");
    if (pageTitle) pageTitle.textContent = "Dashboard";

    this.contentArea.innerHTML = `
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">Dashboard</h3>
        </div>
        <div class="card-body">
          <p>Bienvenido. Esta vista está aislada y no interfiere con <code>/pages/category.html</code> ni otros módulos.</p>
        </div>
      </div>
    `;
  }

  bindSignOut() {
    const signOutEl = document.getElementById("signOut");
    if (!signOutEl) return;
    signOutEl.addEventListener("click", (e) => {
      e.preventDefault();
      localStorage.removeItem("isLoggedIn");
      localStorage.removeItem("username");
      window.location.href = "/pages/login.html";
    });
  }
}

// Instancia SOLO si estás en /pages/dashboard.html
document.addEventListener("DOMContentLoaded", () => {
  const isDashboardPage = /\/pages\/dashboard\.html$/i.test(location.pathname);
  if (!isDashboardPage) return;
  window.dashboard = new Dashboard();
});




