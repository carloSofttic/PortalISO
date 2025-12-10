// ===============================
//  CATÁLOGO DE DOCUMENTOS - FRONT
// ===============================

document.addEventListener("DOMContentLoaded", () => {

  const form = document.getElementById("formTipoDoc");
  const inputId = document.getElementById("idTipoDoc");
  const inputNombre = document.getElementById("nombreTipoDoc");
  const inputCodigo = document.getElementById("codigoTipoDoc");
  const errorForm = document.getElementById("errorFormTipoDoc");

  const inputBuscar = document.getElementById("buscarTipoDoc");
  const btnRefrescar = document.getElementById("btnRefrescarTipoDoc");
  const tbody = document.getElementById("tbodyTiposDoc");

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
  async function cargarTipos(q = "") {
    try {
      const res = await fetch(`/admin/catalogoDocumentos/buscar?q=${encodeURIComponent(q)}`, {
        headers: {
          [csrfHeader]: csrfToken
        }
      });

      const data = await res.json();
      pintarTabla(data);
    } catch (e) {
      console.error("Error al cargar tipos de documento:", e);
    }
  }

  function pintarTabla(lista) {
    tbody.innerHTML = "";

    if (!lista || lista.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="4" class="catalog-empty">
            No se encontraron tipos de documentos registrados.
          </td>
        </tr>`;
      return;
    }

    lista.forEach(t => {
      const fila = `
        <tr>
          <td>${t.idTipoDoc}</td>
          <td>${t.codigoTipoDoc}</td>
          <td>${t.nombreTipoDoc}</td>
          <td class="col-acciones">
            <button
              type="button"
              class="btn btn-secondary btn-xs btnEditarTipoDoc"
              data-id="${t.idTipoDoc}"
              data-nombre="${t.nombreTipoDoc}"
              data-codigo="${t.codigoTipoDoc}">
              Editar
            </button>
            <button
              type="button"
              class="btn btn-danger btn-xs btnEliminarTipoDoc"
              data-id="${t.idTipoDoc}">
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
      idTipoDoc: id,
      nombreTipoDoc: nombre,
      codigoTipoDoc: codigo
    };

    const url = id
      ? `/admin/catalogoDocumentos/api/${id}`
      : `/admin/catalogoDocumentos/api`;

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
        mostrarError(msg || "Ya existe un tipo de documento con esos datos.");
        return;
      }

      if (!res.ok) {
        mostrarError("Ocurrió un error al guardar el tipo de documento.");
        return;
      }

      const q = inputBuscar.value.trim();
      await cargarTipos(q);

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
    if (btn.classList.contains("btnEditarTipoDoc")) {
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
    if (btn.classList.contains("btnEliminarTipoDoc")) {
      const id = btn.dataset.id;
      const ok = confirm("¿Seguro que deseas eliminar este tipo de documento?");
      if (!ok) return;

      try {
        const res = await fetch(`/admin/catalogoDocumentos/api/${id}`, {
          method: "DELETE",
          headers: {
            [csrfHeader]: csrfToken
          }
        });

        if (!res.ok) {
          alert("No se pudo eliminar el tipo de documento.");
          return;
        }

        if (inputId.value === id) {
          limpiarForm();
        }

        const q = inputBuscar.value.trim();
        await cargarTipos(q);

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
    cargarTipos(q);
  });

  // ===========================
  //   BOTÓN REFRESCAR
  // ===========================
  btnRefrescar.addEventListener("click", () => {
    inputBuscar.value = "";
    cargarTipos("");
  });

  // Carga inicial
  cargarTipos("");
});
