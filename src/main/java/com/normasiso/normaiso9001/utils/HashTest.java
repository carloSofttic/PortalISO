/*package com.normasiso.normaiso9001.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashTest {

    public static void main(String[] args) {

        // 1) La contraseña en texto plano que quieres hashear
        String rawPassword = "Entrar.12345";

        // 2) Crea el encoder de BCrypt
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 3) Genera el hash
        String hash = encoder.encode(rawPassword);

        // 4) Muestra el resultado
        System.out.println("Hash generado para '" + rawPassword + "':");
        System.out.println(hash);

        // (Opcional) Verificar que sí matchea
        boolean coincide = encoder.matches(rawPassword, hash);
        System.out.println("¿Coincide el hash con la contraseña original? " + coincide);
    }
}
*/