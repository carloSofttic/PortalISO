document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("registerForm");
  
    const telefono = document.getElementById("telefono");
    const correo   = document.getElementById("correo");
    const rfc      = document.getElementById("rfc");
    const password = document.getElementById("password");
  
    const bar = document.getElementById("password-bar");
    const msg = document.getElementById("password-msg");
  
    // Regex correctos (OJO: sin doble barra en regex literal)
    const telRegex   = /^[0-9]{10}$/;
    const mailRegex  = /^[\w._%+-]+@[\w.-]+\.[A-Za-z]{2,}$/;
    const rfcRegex   = /^[A-ZÑ&]{3,4}\d{6}[A-Z0-9]{3}$/;
  
    // Helpers
    const setOk = (input, span, text) => {
      if (span) { span.textContent = text || "✔ Correcto"; span.style.color = "#22c55e"; }
      input.style.borderColor = "#22c55e";
    };
    const setErr = (input, span, text) => {
      if (span) { span.textContent = text; span.style.color = "#f87171"; }
      input.style.borderColor = "#f87171";
    };
  
    // ===== TELÉFONO
    telefono?.addEventListener("input", () => {
      const span = telefono.nextElementSibling;
      const val  = telefono.value.trim();
      telRegex.test(val)
        ? setOk(telefono, span, "✔ Teléfono válido")
        : setErr(telefono, span, "El teléfono debe tener 10 dígitos numéricos.");
    });
  
    // ===== CORREO
    correo?.addEventListener("input", () => {
      const span = correo.nextElementSibling;
      const val  = correo.value.trim();
      mailRegex.test(val)
        ? setOk(correo, span, "✔ Correo válido")
        : setErr(correo, span, "Correo electrónico no válido.");
    });
  
    // ===== RFC (opcional)
    rfc?.addEventListener("input", () => {
      const span = rfc.nextElementSibling;
      const val  = rfc.value.trim().toUpperCase();
      /*if (val === "") {
        if (span) { span.textContent = "Campo opcional."; span.style.color = "#94a3b8"; }
        rfc.style.borderColor = "#334155";
        return;
      }*/
      rfcRegex.test(val)
        ? setOk(rfc, span, "✔ RFC válido")
        : setErr(rfc, span, "Formato de RFC no válido.");
    });
  
    // ===== CONTRASEÑA + barra
    password?.addEventListener("input", () => {
      const val = password.value;
      let strength = 0;
      if (val.length >= 8) strength++;
      if (/[A-Z]/.test(val)) strength++;
      if (/[0-9]/.test(val)) strength++;
      if (/[^A-Za-z0-9]/.test(val)) strength++;
  
      if (!bar || !msg) return;
  
      switch (strength) {
        case 0:
        case 1:
          bar.style.width = "25%";
          bar.style.background = "#ef4444";
          msg.textContent = "Contraseña débil";
          msg.style.color = "#ef4444";
          password.style.borderColor = "#ef4444";
          break;
        case 2:
          bar.style.width = "50%";
          bar.style.background = "#f59e0b";
          msg.textContent = "Contraseña media";
          msg.style.color = "#f59e0b";
          password.style.borderColor = "#f59e0b";
          break;
        case 3:
          bar.style.width = "75%";
          bar.style.background = "#3b82f6";
          msg.textContent = "Contraseña buena";
          msg.style.color = "#3b82f6";
          password.style.borderColor = "#3b82f6";
          break;
        case 4:
          bar.style.width = "100%";
          bar.style.background = "#22c55e";
          msg.textContent = "Contraseña fuerte ✓";
          msg.style.color = "#22c55e";
          password.style.borderColor = "#22c55e";
          break;
      }
    });
  
    // ===== VALIDACIÓN FINAL (submit)
    form?.addEventListener("submit", (e) => {
      let valid = true;
  
      if (telefono && !telRegex.test(telefono.value.trim())) {
        setErr(telefono, telefono.nextElementSibling, "El teléfono debe tener 10 dígitos numéricos.");
        if (valid) telefono.focus();
        valid = false;
      }
  
      if (correo && !mailRegex.test(correo.value.trim())) {
        setErr(correo, correo.nextElementSibling, "Correo electrónico no válido.");
        if (valid) correo.focus();
        valid = false;
      }
  
      if (password) {
        const val = password.value;
        const ok = val.length >= 8 && /[A-Z]/.test(val) && /[0-9]/.test(val) && /[^A-Za-z0-9]/.test(val);
        if (!ok) {
          if (msg) { msg.textContent = "La contraseña debe tener 8+ caracteres, una MAYÚSCULA, un número y un símbolo."; msg.style.color = "#ef4444"; }
          password.style.borderColor = "#ef4444";
          if (valid) password.focus();
          valid = false;
        }
      }
  
      if (!valid) e.preventDefault();
    });
  });
  