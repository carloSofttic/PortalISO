// ==========================================================
// DASHBOARD.JS - Plataforma ISO
// ==========================================================

// =============================
// Inicialización al cargar DOM
// =============================
document.addEventListener("DOMContentLoaded", () => {
  setupSidebarToggle();        // Menú móvil (clase body.sidebar-open)
  setupMenuHandlers();         // Menú usuario + notificaciones
  setupSidebarCollapse();      // Colapsar/expandir sidebar en escritorio
  setupSubmenuCrearArchivo();  // Submenú "Crear archivo"

  // Ejemplo de toast ficticio al cargar (puedes quitarlo cuando tengas backend)
  setTimeout(() => {
    showToast("🔔 Nueva actividad asignada");
  }, 2000);
});


// ==========================================================
// 1) SIDEBAR MÓVIL (botón ☰)
// ==========================================================
function setupSidebarToggle() {
  const menuToggle = document.querySelector(".menu-toggle");
  if (!menuToggle) return;

  // Al hacer clic en el botón ☰, alterna la clase .sidebar-open en <body>
  menuToggle.addEventListener("click", () => {
    document.body.classList.toggle("sidebar-open");
  });
}


// ==========================================================
// 1.1) SUBMENÚ "CREAR ARCHIVO"
// ==========================================================
function setupSubmenuCrearArchivo() {
  const toggle  = document.querySelector(".submenu-toggle");
  const submenu = document.getElementById("submenu-crear-archivo");

  const toggle2  = document.querySelector(".submenu-toggle2");
  const submenu2 = document.getElementById("submenu-crear-usuarios");

  const toggle3  = document.querySelector(".submenu-toggle3");
  const submenu3 = document.getElementById("submenu-crear-catalogos");

  const sidebar = document.querySelector(".sidebar");

  // 1) Primer submenú
  if (toggle && submenu) {
    toggle.addEventListener("click", (e) => {
      e.preventDefault();

      if (sidebar && sidebar.classList.contains("collapsed")) return;

      submenu.classList.toggle("show");
      toggle.classList.toggle("open");
    });
  }

  // 2) Segundo submenú
  if (toggle2 && submenu2) {
    toggle2.addEventListener("click", (e) => {
      e.preventDefault();

      if (sidebar && sidebar.classList.contains("collapsed")) return;

      submenu2.classList.toggle("show");
      toggle2.classList.toggle("open");
    });
  }

  // 3) Tercer submenú
  if (toggle3 && submenu3) {
    toggle3.addEventListener("click", (e) => {
      e.preventDefault();

      if (sidebar && sidebar.classList.contains("collapsed")) return;

      submenu3.classList.toggle("show");
      toggle3.classList.toggle("open");
    });
  }
}



// ==========================================================
// 2) MENÚ DE USUARIO Y PANEL DE NOTIFICACIONES
// ==========================================================
function setupMenuHandlers() {
  document.addEventListener("click", (event) => {
    const userMenu = document.querySelector(".user-menu");
    const notifContainer = document.querySelector(".notifications");

    // ----- Menú de usuario -----
    if (userMenu) {
      if (userMenu.contains(event.target)) {
        userMenu.classList.toggle("open");
      } else {
        userMenu.classList.remove("open");
      }
    }

    // ----- Panel de notificaciones (campana) -----
    if (notifContainer) {
      const notifButton = notifContainer.querySelector(".icon-btn");

      if (notifButton && notifButton.contains(event.target)) {
        notifContainer.classList.toggle("open");
      } else if (!notifContainer.contains(event.target)) {
        notifContainer.classList.remove("open");
      }
    }
  });
}


// ==========================================================
// 3) SIDEBAR COLAPSABLE (ESCRITORIO)
// ==========================================================
function setupSidebarCollapse() {
  const sidebar = document.querySelector(".sidebar");
  const collapseBtn = document.querySelector(".sidebar-collapse-btn");
  const submenu = document.getElementById("submenu-crear-archivo");
  const toggle = document.querySelector(".submenu-toggle");

  if (!sidebar || !collapseBtn) return;

  // Restaurar estado al cargar según localStorage
  if (localStorage.getItem("sidebarCollapsed") === "true") {
    sidebar.classList.add("collapsed");
    // aseguramos que el submenú esté cerrado
    submenu?.classList.remove("show");
    toggle?.classList.remove("open");
  }

  // Al hacer clic en el botón, alternar la clase .collapsed
  collapseBtn.addEventListener("click", () => {
    sidebar.classList.toggle("collapsed");

    // Si se colapsa → cerramos el submenú y guardamos estado
    if (sidebar.classList.contains("collapsed")) {
      submenu?.classList.remove("show");
      toggle?.classList.remove("open");
      localStorage.setItem("sidebarCollapsed", "true");
    } else {
      localStorage.removeItem("sidebarCollapsed");
    }
  });
}


// ==========================================================
// 4) TOAST (ALERTA EMERGENTE)
// ==========================================================
function showToast(message = "Tienes una nueva notificación") {
  let toast = document.getElementById("toast");

  if (!toast) {
    toast = document.createElement("div");
    toast.id = "toast";
    toast.className = "toast";
    toast.innerHTML = `
      <!--<div class="toast-icon">🔔</div>-->
      <div class="toast-text">${message}</div>
    `;
    document.body.appendChild(toast);
  } else {
    const textEl = toast.querySelector(".toast-text");
    if (textEl) {
      textEl.textContent = message;
    }
  }

  toast.classList.add("show");

  setTimeout(() => {
    toast.classList.remove("show");
  }, 3500);
}
