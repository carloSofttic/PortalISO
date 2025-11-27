// ==========================================================
// DASHBOARD.JS - Plataforma ISO
// Controla:
//   - Sidebar móvil (abrir/cerrar)
//   - Sidebar colapsable en escritorio
//   - Menú de usuario (dropdown)
//   - Panel de notificaciones (campana)
//   - Toast emergente para notificaciones
// ==========================================================


// =============================
// Inicialización al cargar DOM
// =============================
document.addEventListener("DOMContentLoaded", () => {
    setupSidebarToggle();      // Menú móvil (clase body.sidebar-open)
    setupMenuHandlers();       // Menú usuario + notificaciones
    setupSidebarCollapse();    // Colapsar/expandir sidebar en escritorio
  
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
  // 2) MENÚ DE USUARIO Y PANEL DE NOTIFICACIONES
  // ==========================================================
  function setupMenuHandlers() {
    document.addEventListener("click", (event) => {
      const userMenu = document.querySelector(".user-menu");
      const notifContainer = document.querySelector(".notifications");
  
      // ----- Menú de usuario -----
      if (userMenu) {
        // Si el click ocurre dentro del área del usuario (avatar + dropdown)
        if (userMenu.contains(event.target)) {
          userMenu.classList.toggle("open");
        } else {
          // Click fuera → cerramos el menú
          userMenu.classList.remove("open");
        }
      }
  
      // ----- Panel de notificaciones (campana) -----
      if (notifContainer) {
        const notifButton = notifContainer.querySelector(".icon-btn");
  
        // Click en la campana → toggle del panel
        if (notifButton && notifButton.contains(event.target)) {
          notifContainer.classList.toggle("open");
        } else if (!notifContainer.contains(event.target)) {
          // Click fuera del panel → lo cerramos
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
  
    if (!sidebar || !collapseBtn) return;
  
    // Restaurar estado al cargar según localStorage
    if (localStorage.getItem("sidebarCollapsed") === "true") {
      sidebar.classList.add("collapsed");
    }
  
    // Al hacer clic en el botón, alternar la clase .collapsed
    collapseBtn.addEventListener("click", () => {
      sidebar.classList.toggle("collapsed");
  
      // Guardar o quitar estado en localStorage
      if (sidebar.classList.contains("collapsed")) {
        localStorage.setItem("sidebarCollapsed", "true");
      } else {
        localStorage.removeItem("sidebarCollapsed");
      }
    });
  }
  
  
  // ==========================================================
  // 4) TOAST (ALERTA EMERGENTE)
  // ==========================================================
  
  /**
   * Muestra un toast (ventana emergente pequeña) abajo a la derecha.
   * @param {string} message - Mensaje a mostrar en el toast.
   */
  function showToast(message = "Tienes una nueva notificación") {
    // Intentamos obtener el toast ya existente
    let toast = document.getElementById("toast");
  
    // Si no existe, lo creamos dinámicamente y lo agregamos al <body>
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
      // Si ya existe, solo actualizamos el texto
      const textEl = toast.querySelector(".toast-text");
      if (textEl) {
        textEl.textContent = message;
      }
    }
  
    // Agregamos la clase .show para activar la animación de entrada
    toast.classList.add("show");
  
    // Después de 3.5 segundos, ocultamos el toast quitando la clase .show
    setTimeout(() => {
      toast.classList.remove("show");
    }, 3500);
  }
  