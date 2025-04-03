package com.digitalmoneyhouse.user_service.util;

import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@Component
public class CvuAliasGenerator {
    private static final Random RANDOM = new SecureRandom();

    public static String generateCVU() {
        StringBuilder cvu = new StringBuilder();
        for (int i = 0; i < 22; i++) {
            cvu.append(RANDOM.nextInt(10)); // Números del 0-9
        }
        return cvu.toString();
    }

    public static String generateAlias() {
        try {
            List<String> words = Files.readAllLines(Paths.get("src/main/resources/alias.txt"));
            if (words.size() < 3) {
                throw new RuntimeException("El archivo alias.txt debe contener al menos 3 palabras.");
            }
            return words.get(RANDOM.nextInt(words.size())) + "." +
                    words.get(RANDOM.nextInt(words.size())) + "." +
                    words.get(RANDOM.nextInt(words.size()));
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo alias.txt", e);
        }
    }
}
