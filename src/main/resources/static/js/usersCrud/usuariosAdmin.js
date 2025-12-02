const tbody = document.getElementById('tbodyUsuarios');
const buscador = document.getElementById('buscador');
const btnRefrescar = document.getElementById('btnRefrescar');
const btnNuevo = document.getElementById('btnNuevo');

const modalBackdrop = document.getElementById('modalBackdrop');
const modalTitulo = document.getElementById('modalTitulo');
const btnCerrarModal = document.getElementById('btnCerrarModal');
const btnCancelar = document.getElementById('btnCancelar');
const formUsuario = document.getElementById('formUsuario');
const errorForm = document.getElementById('errorForm');

const idUsuario = document.getElementById('idUsuario');
const idMiembro = document.getElementById('idMiembro');
const nombre = document.getElementById('nombre');
const apPaterno = document.getElementById('apPaterno');
const apMaterno = document.getElementById('apMaterno');
const telefono = document.getElementById('telefono');
const email = document.getElementById('email');
const username = document.getElementById('username');
const tipoUsuario = document.getElementById('tipoUsuario');
const password = document.getElementById('password');

const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

function abrirModal(modo, usuario = null) {
  errorForm.textContent = '';

  if (modo === 'nuevo') {
    modalTitulo.textContent = 'Nuevo usuario';
    idUsuario.value = '';
    idMiembro.value = '';
    nombre.value = '';
    apPaterno.value = '';
    apMaterno.value = '';
    telefono.value = '';
    email.value = '';
    username.value = '';
    tipoUsuario.value = '';
    password.value = '';
    password.disabled = false;
  } else if (modo === 'editar' && usuario) {
    modalTitulo.textContent = 'Editar usuario';
    idUsuario.value = usuario.idUsuario;
    idMiembro.value = usuario.idMiembro;
    nombre.value = usuario.nombre || '';
    apPaterno.value = usuario.apPaterno || '';
    apMaterno.value = usuario.apMaterno || '';
    telefono.value = usuario.telefono || '';
    email.value = usuario.email || usuario.emailMiembro || '';
    username.value = usuario.username || '';
    tipoUsuario.value = usuario.tipoUsuario || usuario.rol || '';
    password.value = '';
    password.disabled = true; // no cambiamos password desde aquí
  }
  modalBackdrop.style.display = 'flex';
}

function cerrarModal() {
  modalBackdrop.style.display = 'none';
}

async function cargarUsuarios() {
  const q = buscador.value.trim();
  const params = q ? '?q=' + encodeURIComponent(q) : '';
  const res = await fetch('/admin/usuarios/api' + params, {
    headers: { Accept: 'application/json' }
  });

  if (!res.ok) {
    console.error('Error al cargar usuarios');
    return;
  }
  const data = await res.json();
  renderTabla(data);
}

function renderTabla(usuarios) {
  tbody.innerHTML = '';
  if (!usuarios || usuarios.length === 0) {
    const tr = document.createElement('tr');
    const td = document.createElement('td');
    td.colSpan = 7;
    td.textContent = 'No se encontraron usuarios.';
    tr.appendChild(td);
    tbody.appendChild(tr);
    return;
  }

  usuarios.forEach(u => {
    const tr = document.createElement('tr');

    const tdId = document.createElement('td');
    tdId.textContent = u.idUsuario;
    tr.appendChild(tdId);

    const tdNombre = document.createElement('td');
    const nombreCompleto = `${u.nombre || ''} ${u.apPaterno || ''} ${u.apMaterno || ''}`.trim();
    tdNombre.textContent = nombreCompleto;
    tr.appendChild(tdNombre);

    const tdEmail = document.createElement('td');
    tdEmail.textContent = u.email || u.emailMiembro || '';
    tr.appendChild(tdEmail);

    const tdUser = document.createElement('td');
    tdUser.textContent = u.username || '';
    tr.appendChild(tdUser);

    const tdRol = document.createElement('td');
    const spanRol = document.createElement('span');
    spanRol.className = 'badge badge-role';
    spanRol.textContent = u.tipoUsuario || u.rol || '';
    tdRol.appendChild(spanRol);
    tr.appendChild(tdRol);

    const tdFecha = document.createElement('td');
    tdFecha.textContent = u.fechaCreacion || '';
    tr.appendChild(tdFecha);

    const tdAcciones = document.createElement('td');

    const btnEdit = document.createElement('button');
    btnEdit.textContent = 'Editar';
    btnEdit.className = 'btn btn-secondary';
    btnEdit.style.marginRight = '0.3rem';
    btnEdit.addEventListener('click', () => abrirModal('editar', u));

    const btnDel = document.createElement('button');
    btnDel.textContent = 'Eliminar';
    btnDel.className = 'btn btn-danger';
    btnDel.addEventListener('click', async () => {
      if (!confirm('¿Eliminar este usuario y su miembro asociado?')) return;
      await eliminarUsuario(u.idUsuario, u.idMiembro);
      await cargarUsuarios();
    });

    tdAcciones.appendChild(btnEdit);
    tdAcciones.appendChild(btnDel);
    tr.appendChild(tdAcciones);

    tbody.appendChild(tr);
  });
}

async function eliminarUsuario(idUsuario, idMiembro) {
  const res = await fetch(`/admin/usuarios/api/${idUsuario}/${idMiembro}`, {
    method: 'DELETE',
    headers: { [csrfHeader]: csrfToken }
  });
  if (!res.ok) {
    alert('Error al eliminar usuario');
  }
}

formUsuario.addEventListener('submit', async e => {
  e.preventDefault();
  errorForm.textContent = '';

  if (
    !nombre.value.trim() ||
    !apPaterno.value.trim() ||
    !apMaterno.value.trim() ||
    !email.value.trim() ||
    !username.value.trim() ||
    !tipoUsuario.value
  ) {
    errorForm.textContent = 'Por favor, llena todos los campos obligatorios.';
    return;
  }

  const payload = {
    idUsuario: idUsuario.value ? Number(idUsuario.value) : null,
    idMiembro: idMiembro.value ? Number(idMiembro.value) : null,
    nombre: nombre.value.trim(),
    apPaterno: apPaterno.value.trim(),
    apMaterno: apMaterno.value.trim(),
    telefono: telefono.value.trim(),
    email: email.value.trim(), // USUARIO
    emailMiembro: email.value.trim(), // MIEMBRO
    username: username.value.trim(),
    tipoUsuario: tipoUsuario.value,
    rol: tipoUsuario.value
  };

  const esNuevo = !payload.idUsuario;
  let url = '/admin/usuarios/api';
  let method = 'POST';

  if (!esNuevo) {
    url = `/admin/usuarios/api/${payload.idUsuario}/${payload.idMiembro}`;
    method = 'PUT';
  } else {
    payload.password = password.value;
    if (!payload.password || !payload.password.trim()) {
      errorForm.textContent = 'El password es obligatorio para crear un usuario.';
      return;
    }
  }

  const res = await fetch(url, {
    method,
    headers: {
      'Content-Type': 'application/json',
      [csrfHeader]: csrfToken
    },
    body: JSON.stringify(payload)
  });

  if (!res.ok) {
    errorForm.textContent = 'Ocurrió un error al guardar el usuario.';
    return;
  }

  cerrarModal();
  await cargarUsuarios();
});

btnNuevo.addEventListener('click', () => abrirModal('nuevo'));
btnCerrarModal.addEventListener('click', cerrarModal);
btnCancelar.addEventListener('click', cerrarModal);
btnRefrescar.addEventListener('click', cargarUsuarios);

let searchTimeout;
buscador.addEventListener('input', () => {
  clearTimeout(searchTimeout);
  searchTimeout = setTimeout(cargarUsuarios, 300);
});

document.addEventListener('DOMContentLoaded', cargarUsuarios);
