package com.normasiso.normaiso9001.service;

import com.normasiso.normaiso9001.repository.usersCrud.UsuarioAdminRepository;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UsernameGeneratorService {

    private final UsuarioAdminRepository usuarioAdminRepository;

    public UsernameGeneratorService(UsuarioAdminRepository usuarioAdminRepository) {
        this.usuarioAdminRepository = usuarioAdminRepository;
    }

    /**
     * Genera un username único basado en iniciales + 5 dígitos aleatorios.
     * Ejemplo: CAR12345, JGM54321, XA87322
     */
    public String generateUniqueUsername(String nombre, String apPaterno, String apMaterno) {

        String username = generarUsernameBase(nombre, apPaterno, apMaterno);

        // Repetir hasta encontrar uno que no exista en la BD
        while (usuarioAdminRepository.existsUsuarioByUsername(username)) {
            username = generarUsernameBase(nombre, apPaterno, apMaterno);
        }

        return username;
    }

    /**
     * Genera el username base (iniciales + números)
     */
    private String generarUsernameBase(String nombre, String apPaterno, String apMaterno) {

        String inicialNombre = (nombre != null && !nombre.isBlank())
                ? nombre.substring(0, 1).toUpperCase()
                : "X";

        String inicialApPat = (apPaterno != null && !apPaterno.isBlank())
                ? apPaterno.substring(0, 1).toUpperCase()
                : "X";

        String inicialApMat = (apMaterno != null && !apMaterno.isBlank())
                ? apMaterno.substring(0, 1).toUpperCase()
                : "X";

        int numeros = new Random().nextInt(90000) + 10000;

        return inicialNombre + inicialApPat + inicialApMat + numeros;
    }
}
