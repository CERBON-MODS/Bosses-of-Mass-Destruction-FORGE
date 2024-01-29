package com.cerbon.bosses_of_mass_destruction.platform;

import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;

import java.util.ServiceLoader;

public class BMDServices {

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        BMDConstants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
