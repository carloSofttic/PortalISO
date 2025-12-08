// ===============================
// Búsqueda AJAX en tiempo real
// ===============================
const inputBusqueda = document.getElementById('buscarProcedimiento');
const tbody = document.getElementById('tbodyProcedimientos');

if (inputBusqueda) {
  inputBusqueda.addEventListener('input', () => {
    const q = inputBusqueda.value;

    fetch('/procedimientos/buscar?q=' + encodeURIComponent(q))
      .then(res => res.json())
      .then(data => {
        tbody.innerHTML = '';

        if (!data || data.length === 0) {
          const tr = document.createElement('tr');
          tr.innerHTML =
            '<td colspan="9" style="text-align:center; padding:0.75rem;">Sin resultados.</td>';
          tbody.appendChild(tr);
          return;
        }

        data.forEach(p => {
          const tr = document.createElement('tr');

          const estadoDoc = p.statusFormat || '';
          const estadoVig = p.statusValidity || '';

          let classDoc = '';
          if (estadoDoc === 'En uso') classDoc = 'badge-status-ok';
          else if (estadoDoc === 'Revision') classDoc = 'badge-status-warn';
          else if (estadoDoc === 'Rechazado') classDoc = 'badge-status-bad';

          let classVig = '';
          if (estadoVig === 'Vigente') classVig = 'badge-status-ok';
          else if (estadoVig === 'Por expirar') classVig = 'badge-status-warn';
          else if (estadoVig === 'Expirado') classVig = 'badge-status-bad';

          tr.innerHTML = `
            <td>${p.nombreProceso || ''}</td>
            <td>${p.tipoDocFormat || ''}</td>
            <td>${p.codigoDocumento || ''}</td>
            <td>${p.nombreDocumento || ''}</td>
            <td>${p.fechaEmision || ''}</td>
            <td>${p.fechaVencimiento || ''}</td>

            <td>
              <span class="badge-status ${classDoc}">${estadoDoc}</span>
            </td>

            <td>
              <span class="badge-status ${classVig}">${estadoVig}</span>
            </td>

            <td>${p.metodoResguardo || ''}</td>
            <td class="acciones">
              <button type="button" class="btn btn-secondary btn-xs btnEditar"
                      data-id="${p.idDocumento}">Editar</button>
              <button type="button" class="btn btn-danger btn-xs btnEliminar"
                      data-id="${p.idDocumento}">Eliminar</button>
            </td>
          `;

          tbody.appendChild(tr);
        });
      })
      .catch(err => {
        console.error('Error en búsqueda AJAX:', err);
      });
  });
}


// ===============================
// Modal alta / edición (skeleton)
// ===============================
const modal = document.getElementById('modalProcedimiento');
const btnNuevo = document.getElementById('btnNuevoProcedimiento');
const btnCerrar = document.getElementById('btnCerrarModal');
const btnCancelar = document.getElementById('btnCancelarModal');

function abrirModal() {
    modal.classList.add('show');
}

function cerrarModal() {
    modal.classList.remove('show');
}

if (btnNuevo) btnNuevo.addEventListener('click', abrirModal);
if (btnCerrar) btnCerrar.addEventListener('click', cerrarModal);
if (btnCancelar) btnCancelar.addEventListener('click', cerrarModal);

// Aquí después puedes agregar listeners para .btnEditar y .btnEliminar

data.forEach(p => {
    const tr = document.createElement('tr');

    const estadoDoc = p.statusFormat || '';
    const estadoVigencia = p.statusValidity || '';

    // clases para estado del documento
    let classDoc = '';
    if (estadoDoc === 'En uso') classDoc = 'badge-status-ok';
    else if (estadoDoc === 'Revision') classDoc = 'badge-status-warn';
    else if (estadoDoc === 'Rechazado') classDoc = 'badge-status-bad';

    // clases para estado de vigencia
    let classVig = '';
    if (estadoVigencia === 'Vigente') classVig = 'badge-status-ok';
    else if (estadoVigencia === 'Por expirar') classVig = 'badge-status-warn';
    else if (estadoVigencia === 'Expirado') classVig = 'badge-status-bad';

    tr.innerHTML = `
        <td>${p.nombreProceso || ''}</td>
        <td>${p.codigoDocumento || ''}</td>
        <td>${p.nombreDocumento || ''}</td>
        <td>${p.fechaEmision || ''}</td>
        <td>${p.fechaVencimiento || ''}</td>

        <td>
            <span class="badge-status ${classDoc}">
                ${estadoDoc}
            </span>
        </td>

        <td>
            <span class="badge-status ${classVig}">
                ${estadoVigencia}
            </span>
        </td>

        <td>${p.metodoResguardo || ''}</td>

        <td class="acciones">
            <button type="button" class="btn btn-secondary btn-xs btnEditar"
                data-id="${p.idDocumento}">Editar</button>
            <button type="button" class="btn btn-danger btn-xs btnEliminar"
                data-id="${p.idDocumento}">Eliminar</button>
        </td>
    `;
    tbody.appendChild(tr);
});
