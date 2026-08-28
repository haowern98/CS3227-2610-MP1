package com.possessionmanager.storage;

import java.nio.file.Path;

/**
 * Resolves the platform-independent local data-file location.
 */
public final class AppDataFile {
    private static final String APPLICATION_DIRECTORY = ".possession-manager";
    private static final String DATA_FILE_NAME = "data.json";

    private AppDataFile() {
    }

    /**
     * Returns the default JSON file below the current user's home directory.
     *
     * @return default data-file path.
     */
    public static Path getDataFilePath() {
        return Path.of(System.getProperty("user.home"), APPLICATION_DIRECTORY, DATA_FILE_NAME);
    }
}
