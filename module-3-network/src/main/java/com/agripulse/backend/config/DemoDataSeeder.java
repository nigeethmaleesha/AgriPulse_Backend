package com.agripulse.backend.config;

import com.agripulse.backend.service.network.SupplyNetworkService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Optional startup demo data.
 *
 * The project now defaults to manual network configuration. Set
 * SEED_DEMO=true only when a ready-made reproducible demonstration graph is
 * wanted at startup. The UI also provides an explicit "Load Demo Network"
 * action.
 */
@Component
public class DemoDataSeeder implements CommandLineRunner {

    private final SupplyNetworkService supplyNetworkService;

    @Value("${agripulse.seed-demo:false}")
    private boolean seedDemo;

    public DemoDataSeeder(SupplyNetworkService supplyNetworkService) {
        this.supplyNetworkService = supplyNetworkService;
    }

    @Override
    public void run(String... args) {
        if (seedDemo) {
            supplyNetworkService.seedDemoIfEmpty();
        }
    }
}
