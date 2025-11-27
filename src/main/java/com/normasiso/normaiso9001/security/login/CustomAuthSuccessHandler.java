package com.normasiso.normaiso9001.security.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        AppUserDetails user = (AppUserDetails) authentication.getPrincipal();

        if (user.isPrimerInicioSesion()) {
            // Primer inicio -> onboarding
            getRedirectStrategy().sendRedirect(request, response, "/onboarding");
        } else {
            // Ya pasó onboarding -> dashboard
            getRedirectStrategy().sendRedirect(request, response, "/dashboard");
        }
    }
}
