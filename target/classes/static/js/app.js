// app.js
// 1) Componente SelectX (dropdown custom)  2) Carga de Normas ISO por sector
(() => {
    "use strict";
  
    /* ============ 1) SELECTX (dropdown custom para Sector) ============ */
    class SelectX {
      constructor(root) {
        this.root = root;
        this.toggle = root.querySelector(".selectx-toggle");
        this.menu = root.querySelector(".selectx-menu");
        this.label = root.querySelector(".selectx-label");
        this.hiddenInput = root.querySelector('input[type="hidden"]');
        this.options = Array.from(root.querySelectorAll(".selectx-option"));
  
        this.toggle?.setAttribute("aria-expanded", "false");
        this.menu?.setAttribute("role", "listbox");
        this.options.forEach(o => o.setAttribute("tabindex", "-1"));
  
        this.toggle?.addEventListener("click", this.onToggleClick);
        this.menu?.addEventListener("click", this.onOptionClick);
      }
      open(){ this.root.classList.add("open"); this.toggle?.setAttribute("aria-expanded","true"); }
      close(){ this.root.classList.remove("open"); this.toggle?.setAttribute("aria-expanded","false"); }
      isOpen(){ return this.root.classList.contains("open"); }
  
      onToggleClick = (e) => {
        e.stopPropagation();
        SelectX.closeAll(this.root);
        this.isOpen() ? this.close() : this.open();
      };
      onOptionClick = (e) => {
        const opt = e.target.closest(".selectx-option");
        if (!opt) return;
        this.options.forEach(o => o.removeAttribute("aria-selected"));
        opt.setAttribute("aria-selected","true");
        const val = opt.getAttribute("data-value");
        const text = opt.textContent.trim();
        if (this.label) this.label.textContent = text;
        if (this.hiddenInput) this.hiddenInput.value = val;
  
        // 🔗 Dispara evento para que otras partes (normas ISO) reaccionen
        this.root.dispatchEvent(new CustomEvent("selectx:change", { detail: { value: val, label: text }}));
        this.close();
      };
  
      static closeAll(except){
        document.querySelectorAll(".selectx.open").forEach(el=>{
          if (el!==except){
            el.classList.remove("open");
            el.querySelector(".selectx-toggle")?.setAttribute("aria-expanded","false");
          }
        });
      }
    }
  
    /* ============ 2) LÓGICA: cargar Normas ISO según sector ============ */
    async function loadNormasBySector(idSector) {
      const isoSelect = document.getElementById("isoSeleccionada");
      if (!isoSelect) return;
  
      // Estado mientras carga
      isoSelect.innerHTML = `<option value="">Cargando…</option>`;
      isoSelect.disabled = true;
  
      if (!idSector) {
        isoSelect.innerHTML = `<option value="">Selecciona un sector primero</option>`;
        return;
      }
  
      try {
        const res = await fetch(`/api/sectores/${encodeURIComponent(idSector)}/normas`, {
          headers: { "Accept": "application/json" }
        });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const data = await res.json(); // [{id,codigo,nombre}]
  
        isoSelect.innerHTML = `<option value="">Selecciona…</option>`;
        if (Array.isArray(data) && data.length) {
          for (const n of data) {
            const opt = document.createElement("option");
            opt.value = n.id;
            opt.textContent = n.codigo ? `${n.codigo} — ${n.nombre}` : n.nombre;
            isoSelect.appendChild(opt);
          }
          isoSelect.disabled = false;
        } else {
          isoSelect.innerHTML = `<option value="">No hay normas para este sector</option>`;
        }
      } catch (err) {
        console.error("Error cargando normas:", err);
        isoSelect.innerHTML = `<option value="">Error al cargar normas</option>`;
      }
    }
  
    /* ============ 3) INICIALIZACIÓN ============ */
    document.addEventListener("DOMContentLoaded", () => {
      // Instancia del dropdown custom
      const selectxRoot = document.querySelector(".selectx");
      if (!selectxRoot) return;
  
      const selectx = new SelectX(selectxRoot);
  
      // Si ya hay un valor (por back del navegador), intenta cargar normas
      const hidden = selectxRoot.querySelector('input[type="hidden"]');
      if (hidden && hidden.value) {
        loadNormasBySector(hidden.value);
      }
  
      // Cuando el usuario cambia el sector en el dropdown custom
      selectxRoot.addEventListener("selectx:change", (e) => {
        const { value } = e.detail || {};
        loadNormasBySector(value);
      });
  
      // Cerrar si haces click fuera
      document.addEventListener("click", () => SelectX.closeAll());
      // Accesibilidad extra opcional (teclas) la puedes añadir luego si quieres
    });

        /* ============ 4) SUBMIT: validar y evitar doble envío ============ */
        document.addEventListener("DOMContentLoaded", () => {
            const form  = document.querySelector('form[method="post"]');
            if (!form) return;
      
            const nombre = document.getElementById("nombreComercial");
            const sector = document.getElementById("sectorCompania");     // hidden del SelectX
            const iso    = document.getElementById("isoSeleccionada");
            const btn    = form.querySelector('button[type="submit"]');
      
            form.addEventListener("submit", (e) => {
              // Validaciones básicas
              const errs = [];
              if (!nombre || !nombre.value.trim()) errs.push("Ingresa el Nombre Comercial.");
              if (!sector || !sector.value)        errs.push("Selecciona un Sector o Giro.");
              if (!iso || !iso.value)              errs.push("Selecciona una Norma ISO.");
      
              if (errs.length) {
                e.preventDefault();
                alert(errs.join("\n"));
                return;
              }
      
              // Evita doble submit
              if (btn) {
                btn.disabled = true;
                btn.textContent = "Guardando…";
              }
            });
          });
      
  })();
  