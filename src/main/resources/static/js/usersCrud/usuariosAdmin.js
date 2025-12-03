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

// ========================= UTILIDADES =========================

function limpiarErrores() {
  errorForm.innerHTML = '';
  errorForm.style.display = 'none';
}

function mostrarErrores(errores) {
  if (!errores || errores.length === 0) {
    limpiarErrores();
    return;
  }
  errorForm.innerHTML = errores.map(e => `<div>• ${e}</div>`).join('');
  errorForm.style.display = 'block';
}

function limpiarEstadosCampos() {
  [nombre, apPaterno, apMaterno, telefono, email,  tipoUsuario, password]
    .forEach(campo => campo.classList.remove('is-valid', 'is-invalid'));
}

function marcarOk(campo) {
  campo.classList.remove('is-invalid');
  campo.classList.add('is-valid');
}

function marcarError(campo) {
  campo.classList.remove('is-valid');
  campo.classList.add('is-invalid');
}

// ================== VALIDACIÓN POR CAMPO (TIEMPO REAL) ==================

function validarCampoIndividual(campo) {
  const value = campo.value.trim();
  const id = campo.id;

  const regexLetras = /^[a-zA-ZÁÉÍÓÚáéíóúüÜñÑ\s]+$/;
  const regexTelefono = /^[0-9]{10}$/;
  const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  switch (id) {
    case 'nombre':
    case 'apPaterno':
    case 'apMaterno':
      if (value.length >= 2 && regexLetras.test(value)) {
        marcarOk(campo);
      } else {
        marcarError(campo);
      }
      break;

    case 'telefono':
      if (regexTelefono.test(value)) {
        marcarOk(campo);
      } else {
        marcarError(campo);
      }
      break;

    case 'email':
      if (regexEmail.test(value)) {
        marcarOk(campo);
      } else {
        marcarError(campo);
      }
      break;

    /*case 'username':
      if (value.length >= 4 && !/\s/.test(value)) {
        marcarOk(campo);
      } else {
        marcarError(campo);
      }
      break;
*/
    case 'tipoUsuario':
      if (value) {
        marcarOk(campo);
      } else {
        marcarError(campo);
      }
      break;

    case 'password':
      // Solo validar en alta (cuando idUsuario está vacío)
      if (!idUsuario.value) {
        if (
          value.length >= 6 &&
          /[A-Z]/.test(value) &&
          /[a-z]/.test(value) &&
          /[0-9]/.test(value)
        ) {
          marcarOk(campo);
        } else {
          marcarError(campo);
        }
      } else {
        campo.classList.remove('is-valid', 'is-invalid');
      }
      break;
  }
}

// ================== VALIDACIÓN COMPLETA AL GUARDAR ==================

function validarFormulario(esNuevo) {
  const errores = [];
  limpiarEstadosCampos();

  const vNombre = nombre.value.trim();
  const vApPaterno = apPaterno.value.trim();
  const vApMaterno = apMaterno.value.trim();
  const vTelefono = telefono.value.trim();
  const vEmail = email.value.trim();
  //const vUsername = username.value.trim();
  const vTipoUsuario = tipoUsuario.value;
  const vPassword = password.value.trim();

  const regexLetras = /^[a-zA-ZÁÉÍÓÚáéíóúüÜñÑ\s]+$/;
  const regexTelefono = /^[0-9]{10}$/;
  const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  // Nombre
  if (vNombre.length < 2 || !regexLetras.test(vNombre)) {
    errores.push('El nombre debe tener mínimo 2 letras y solo contener caracteres válidos.');
    marcarError(nombre);
  } else {
    marcarOk(nombre);
  }

  // Apellido paterno
  if (vApPaterno.length < 2 || !regexLetras.test(vApPaterno)) {
    errores.push('El apellido paterno debe tener mínimo 2 letras y solo contener caracteres válidos.');
    marcarError(apPaterno);
  } else {
    marcarOk(apPaterno);
  }

  // Apellido materno
  if (vApMaterno.length < 2 || !regexLetras.test(vApMaterno)) {
    errores.push('El apellido materno es obligatorio y debe contener solo letras (mínimo 2).');
    marcarError(apMaterno);
  } else {
    marcarOk(apMaterno);
  }

  // Teléfono
  if (!regexTelefono.test(vTelefono)) {
    errores.push('El teléfono es obligatorio y debe contener exactamente 10 dígitos numéricos.');
    marcarError(telefono);
  } else {
    marcarOk(telefono);
  }

  // Email
  if (!regexEmail.test(vEmail)) {
    errores.push('Ingresa un correo electrónico válido.');
    marcarError(email);
  } else {
    marcarOk(email);
  }

  // Username
 /* if (vUsername.length < 4 || /\s/.test(vUsername)) {
    errores.push('El nombre de usuario debe tener mínimo 4 caracteres y no puede contener espacios.');
    marcarError(username);
  } else {
    marcarOk(username);
  }*/

  // Tipo de usuario
  if (!vTipoUsuario) {
    errores.push('Selecciona un rol / tipo de usuario.');
    marcarError(tipoUsuario);
  } else {
    marcarOk(tipoUsuario);
  }

  // Password solo en alta
  if (esNuevo) {
    if (
      vPassword.length < 6 ||
      !/[A-Z]/.test(vPassword) ||
      !/[a-z]/.test(vPassword) ||
      !/[0-9]/.test(vPassword)
    ) {
      errores.push('El password (solo alta) debe tener mínimo 6 caracteres e incluir mayúscula, minúscula y número.');
      marcarError(password);
    } else {
      marcarOk(password);
    }
  } else {
    password.classList.remove('is-valid', 'is-invalid');
  }

  return errores;
}

// ========================= MODAL =========================

function abrirModal(modo, usuario = null) {
  limpiarErrores();
  limpiarEstadosCampos();

  if (modo === 'nuevo') {
    modalTitulo.textContent = 'Nuevo usuario';
    idUsuario.value = '';
    idMiembro.value = '';
    nombre.value = '';
    apPaterno.value = '';
    apMaterno.value = '';
    telefono.value = '';
    email.value = '';
  //  username.value = '';
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
   // username.value = usuario.username || '';
    tipoUsuario.value = usuario.tipoUsuario || usuario.rol || '';
    password.value = '';
    password.disabled = true;
  }

  modalBackdrop.style.display = 'flex';
}

function cerrarModal() {
  modalBackdrop.style.display = 'none';
}

// ========================= CARGA Y TABLA =========================

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

   // const tdUser = document.createElement('td');
   // tdUser.textContent = u.username || '';
   // tr.appendChild(tdUser);

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

// ========================= SUBMIT FORM =========================

formUsuario.addEventListener('submit', async e => {
  e.preventDefault();
  limpiarErrores();

  const payload = {
    idUsuario: idUsuario.value ? Number(idUsuario.value) : null,
    idMiembro: idMiembro.value ? Number(idMiembro.value) : null,
    nombre: nombre.value.trim(),
    apPaterno: apPaterno.value.trim(),
    apMaterno: apMaterno.value.trim(),
    telefono: telefono.value.trim(),
    email: email.value.trim(),
    emailMiembro: email.value.trim(),
   // username: username.value.trim(),
    tipoUsuario: tipoUsuario.value,
    rol: tipoUsuario.value
  };

  const esNuevo = !payload.idUsuario;
  if (esNuevo) {
    payload.password = password.value.trim();
  }

  const errores = validarFormulario(esNuevo);
  if (errores.length > 0) {
    mostrarErrores(errores);
    return;
  }

  let url = '/admin/usuarios/api';
  let method = 'POST';

  if (!esNuevo) {
    url = `/admin/usuarios/api/${payload.idUsuario}/${payload.idMiembro}`;
    method = 'PUT';
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
    mostrarErrores(['Ocurrió un error al guardar el usuario.']);
    return;
  }

  cerrarModal();
  await cargarUsuarios();
});

// ========================= EVENTOS VARIOS =========================

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

// Eventos para pintar en tiempo real
[
  nombre,
  apPaterno,
  apMaterno,
  telefono,
  email,
  //username,
  tipoUsuario,
  password
].forEach(campo => {
  campo.addEventListener('input', () => validarCampoIndividual(campo));
  campo.addEventListener('blur', () => validarCampoIndividual(campo));
});
