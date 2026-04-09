package com.hospital.erp.user;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PermissionCatalogInitializer implements ApplicationRunner {
    private final PermissionCatalogService permissionCatalogService;

    @Override
    public void run(ApplicationArguments args) {
        permissionCatalogService.ensureCatalog();
    }
}
