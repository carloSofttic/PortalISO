// ===============================
//  CATÁLOGO DE PROCESOS - FRONT
// ===============================

document.addEventListener("DOMContentLoaded", () => {

  const form = document.getElementById("formProceso");
  const inputId = document.getElementById("idProceso");
  const inputNombre = document.getElementById("nombreProceso");
  const inputCodigo = document.getElementById("codigoProceso");
  const errorForm = document.getElementById("errorForm");

  const inputBuscar = document.getElementById("buscarProceso");
  const btnRefrescar = document.getElementById("btnRefrescarProcesos");
  const tbody = document.getElementById("tbodyProcesos");

  const csrfToken = document.querySelector('meta[name="_csrf"]').content;
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

  // ===========================
  //   UTILIDADES
  // ===========================
  function limpiarForm() {
    inputId.value = "";
    inputNombre.value = "";
    inputCodigo.value = "";
    errorForm.textContent = "";
    form.querySelector("button[type='submit']").textContent = "Guardar";
  }

  function mostrarError(msg) {
    errorForm.textContent = msg || "";
  }

  // ===========================
  //   CARGAR / PINTAR TABLA
  // ===========================
  async function cargarProcesos(q = "") {
    try {
      const res = await fetch(`/admin/catalogoProcesos/buscar?q=${encodeURIComponent(q)}`, {
        headers: {
          [csrfHeader]: csrfToken
        }
      });

      const data = await res.json();
      pintarTabla(data);
    } catch (e) {
      console.error("Error al cargar procesos:", e);
    }
  }

  function pintarTabla(lista) {
    tbody.innerHTML = "";

    if (!lista || lista.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="4" class="catalog-empty">
            No se encontraron procesos registrados.
          </td>
        </tr>`;
      return;
    }

    lista.forEach(p => {
      const fila = `
        <tr>
          <td>${p.idProceso}</td>
          <td>${p.codigoProceso}</td>
          <td>${p.nombreProceso}</td>
          <td class="col-acciones">
            <button
              type="button"
              class="btn btn-secondary btn-xs btnEditarProceso"
              data-id="${p.idProceso}"
              data-nombre="${p.nombreProceso}"
              data-codigo="${p.codigoProceso}">
              Editar
            </button>
            <button
              type="button"
              class="btn btn-danger btn-xs btnEliminarProceso"
              data-id="${p.idProceso}">
              Eliminar
            </button>
          </td>
        </tr>
      `;
      tbody.insertAdjacentHTML("beforeend", fila);
    });
  }

  // ===========================
  //   FORM: CREAR / EDITAR
  // ===========================
  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    mostrarError("");

    const nombre = inputNombre.value.trim();
    const codigo = inputCodigo.value.trim();
    const id = inputId.value ? Number(inputId.value) : null;

    if (!nombre || !codigo) {
      mostrarError("Nombre y código son obligatorios.");
      return;
    }

    const dto = {
      idProceso: id,
      nombreProceso: nombre,
      codigoProceso: codigo
    };

    const url = id
      ? `/admin/catalogoProcesos/api/${id}`
      : `/admin/catalogoProcesos/api`;

    const method = id ? "PUT" : "POST";

    try {
      const res = await fetch(url, {
        method,
        headers: {
          "Content-Type": "application/json",
          [csrfHeader]: csrfToken
        },
        body: JSON.stringify(dto)
      });

      if (res.status === 409) {
        const msg = await res.text();
        mostrarError(msg || "Ya existe un proceso con esos datos.");
        return;
      }

      if (!res.ok) {
        mostrarError("Ocurrió un error al guardar el proceso.");
        return;
      }

      // recargar tabla con el filtro actual
      const q = inputBuscar.value.trim();
      await cargarProcesos(q);

      // resetear a modo "nuevo"
      limpiarForm();

    } catch (e2) {
      console.error(e2);
      mostrarError("Error de comunicación con el servidor.");
    }
  });

  // ===========================
  //   CLICK EN EDITAR / ELIMINAR
  // ===========================
  tbody.addEventListener("click", async (e) => {
    const btn = e.target.closest("button");
    if (!btn) return;

    // EDITAR
    if (btn.classList.contains("btnEditarProceso")) {
      const id = btn.dataset.id;
      const nombre = btn.dataset.nombre || "";
      const codigo = btn.dataset.codigo || "";

      inputId.value = id;
      inputNombre.value = nombre;
      inputCodigo.value = codigo;
      mostrarError("");
      form.querySelector("button[type='submit']").textContent = "Actualizar";
      inputNombre.focus();
      return;
    }

    // ELIMINAR
    if (btn.classList.contains("btnEliminarProceso")) {
      const id = btn.dataset.id;
      const ok = confirm("¿Seguro que deseas eliminar este proceso?");
      if (!ok) return;

      try {
        const res = await fetch(`/admin/catalogoProcesos/api/${id}`, {
          method: "DELETE",
          headers: {
            [csrfHeader]: csrfToken
          }
        });

        if (!res.ok) {
          alert("No se pudo eliminar el proceso.");
          return;
        }

        // si estabas editando ese mismo, limpia el form
        if (inputId.value === id) {
          limpiarForm();
        }

        const q = inputBuscar.value.trim();
        await cargarProcesos(q);

      } catch (err) {
        console.error(err);
        alert("Error de comunicación al eliminar.");
      }
    }
  });

  // ===========================
  //   BUSCAR EN TIEMPO REAL
  // ===========================
  inputBuscar.addEventListener("input", () => {
    const q = inputBuscar.value.trim();
    cargarProcesos(q);
  });

  // ===========================
  //   BOTÓN REFRESCAR
  // ===========================
  btnRefrescar.addEventListener("click", () => {
    inputBuscar.value = "";
    cargarProcesos("");
  });

  // Carga inicial
  cargarProcesos("");
});
