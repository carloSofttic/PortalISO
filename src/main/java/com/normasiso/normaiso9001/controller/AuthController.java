package com.normasiso.normaiso9001.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.normasiso.normaiso9001.dto.RegisterForm;
import com.normasiso.normaiso9001.service.RegistrationService;

@Controller
public class AuthController {

    private final RegistrationService registrationService;

    public AuthController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new RegisterForm());
        }
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(
            @Valid @ModelAttribute("form") RegisterForm form,
            BindingResult result,
            RedirectAttributes ra) {

        // 1) Errores de validación (anotaciones @NotBlank, etc. en RegisterForm)
        if (result.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.form", result);
            ra.addFlashAttribute("form", form);
            return "redirect:/register";
        }

        try {
            // 2) Lógica de negocio (empresa ya existe, correo duplicado, etc.)
            registrationService.register(form);

        } catch (IllegalStateException ex) {
            // Aquí caen mensajes como:
            // "Ya existe una empresa registrada con ese nombre."
            // "Ese correo ya está registrado."
            // "La fecha de creación es obligatoria (yyyy-MM-dd)."
            //
            // Si el mensaje tiene que ver con la empresa, lo ligamos al campo nombreEmpresa:
            if (ex.getMessage() != null &&
                ex.getMessage().toLowerCase().contains("empresa")) {
                result.rejectValue("nombreEmpresa", "empresa.duplicada", ex.getMessage());
            } else if (ex.getMessage() != null &&
                       ex.getMessage().toLowerCase().contains("correo")) {
                result.rejectValue("correo", "correo.duplicado", ex.getMessage());
            } else {
                // error global genérico
                result.reject("registroError", ex.getMessage());
            }

            ra.addFlashAttribute("org.springframework.validation.BindingResult.form", result);
            ra.addFlashAttribute("form", form);
            return "redirect:/register";
        }

        // 3) Todo ok → redirige a selección de membresía
        ra.addFlashAttribute("ok",
                "Cuenta creada correctamente. Selecciona tu membresía para continuar.");
        return "redirect:/memberships";
    }

    @GetMapping("/memberships")
    public String showMemberships() {
        return "memberships";
    }
}
