// seguridad-global.js

(function () {
  document.addEventListener("DOMContentLoaded", function () {

    // ===== 1) Inyectar CSS del overlay =====
    const style = document.createElement("style");
    style.innerHTML = `
      #screenBlocker {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: black;
        opacity: 0;
        pointer-events: none;
        transition: opacity 0.2s ease;
        z-index: 999999;
      }
      #screenBlocker.showBlocker {
        opacity: 0.95;
        pointer-events: auto;
      }
    `;
    document.head.appendChild(style);

    // ===== 2) Crear overlay =====
    const blocker = document.createElement("div");
    blocker.id = "screenBlocker";
    document.body.appendChild(blocker);

    function mostrarBloqueo() {
      blocker.classList.add("showBlocker");
    }

    function ocultarBloqueo() {
      blocker.classList.remove("showBlocker");
    }

    // ===== 3) Bloqueos y detecciones =====
    document.addEventListener("keydown", function (e) {

      // ---- BLOQUEAR PRINT SCREEN ----
      if (e.key === "PrintScreen") {
        try {
          navigator.clipboard.writeText("");
        } catch (err) {}
        mostrarBloqueo();
        alert("Captura bloqueada");
        return;
      }

      // ---- BLOQUEAR Shift + S (dentro del navegador) ----
      if (e.shiftKey && !e.ctrlKey && !e.altKey && e.key.toLowerCase() === "s") {
        e.preventDefault();           // ya no escribe la "s"
        mostrarBloqueo();             // pantalla oscura
        return;
      }

      // ---- BLOQUEAR ATAJOS COMUNES ----
      if (e.ctrlKey && ["c","v","s","p","u"].includes(e.key.toLowerCase())) {
        e.preventDefault();
        return;
      }

      // F12 (DevTools)
      if (e.key === "F12") {
        e.preventDefault();
        return;
      }

      // Ctrl + Shift + I / J / C
      if (e.ctrlKey && e.shiftKey && ["i","j","c"].includes(e.key.toLowerCase())) {
        e.preventDefault();
        return;
      }
    });

    // ===== 4) Bloquear clic derecho =====
    document.addEventListener("contextmenu", function (event) {
      event.preventDefault();
    });

    // ===== 5) Oscurecer al perder foco (cuando cambian de ventana) =====
    window.addEventListener("blur", function () {
      mostrarBloqueo();
    });

    // ===== 6) Quitar bloqueo al volver =====
    window.addEventListener("focus", function () {
      ocultarBloqueo();
    });
  });
})();
