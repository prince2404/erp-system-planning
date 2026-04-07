package com.hospital.erp.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Path file = findDotenv();
        if (file == null) {
            return;
        }
        Map<String, Object> values = new LinkedHashMap<>();
        try {
            for (String rawLine : Files.readAllLines(file)) {
                String line = rawLine.trim();
                if (line.isBlank() || line.startsWith("#") || !line.contains("=")) {
                    continue;
                }
                int split = line.indexOf('=');
                String key = line.substring(0, split).trim();
                String value = line.substring(split + 1).trim();
                values.put(key, stripQuotes(value));
            }
        } catch (IOException ignored) {
            return;
        }
        if (!values.isEmpty()) {
            environment.getPropertySources().addLast(new MapPropertySource("dotenv", values));
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private Path findDotenv() {
        Path workingDir = Path.of("").toAbsolutePath();
        Path local = workingDir.resolve(".env");
        if (Files.isRegularFile(local)) {
            return local;
        }
        Path parent = workingDir.getParent();
        if (parent != null) {
            Path root = parent.resolve(".env");
            if (Files.isRegularFile(root)) {
                return root;
            }
        }
        return null;
    }

    private String stripQuotes(String value) {
        if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
