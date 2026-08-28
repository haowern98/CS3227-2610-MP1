package com.possessionmanager.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.possessionmanager.model.AppData;
import com.possessionmanager.service.PossessionService;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Saves and restores the application's local JSON data file.
 */
public final class JsonStorage {
    private static final DateTimeFormatter BACKUP_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final Path dataFile;
    private final Gson gson;

    /**
     * Creates JSON storage for one explicit data file.
     *
     * @param dataFile location of the JSON file.
     */
    public JsonStorage(Path dataFile) {
        this.dataFile = dataFile;
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class,
                        (com.google.gson.JsonSerializer<LocalDateTime>) (value, type, context) ->
                                new com.google.gson.JsonPrimitive(value.toString()))
                .registerTypeAdapter(LocalDateTime.class,
                        (com.google.gson.JsonDeserializer<LocalDateTime>) (value, type, context) ->
                                LocalDateTime.parse(value.getAsString()))
                .create();
    }

    /**
     * Loads the current JSON snapshot or returns empty data when no file exists.
     *
     * @return loaded application data.
     * @throws StorageException if the existing file is unreadable or invalid.
     */
    public AppData load() {
        if (Files.notExists(dataFile)) {
            return AppData.empty();
        }
        try (Reader reader = Files.newBufferedReader(dataFile)) {
            AppData data = gson.fromJson(reader, AppData.class);
            if (data == null) {
                throw new JsonParseException("Data file is empty.");
            }
            new PossessionService(data);
            return data;
        } catch (IOException | RuntimeException exception) {
            preserveCorruptFile(exception);
            throw new StorageException("Could not load possession data. A backup was kept.", exception);
        }
    }

    /**
     * Saves a snapshot using a temporary file before replacing the previous file.
     *
     * @param data complete data snapshot to save.
     * @throws StorageException if the snapshot cannot be written safely.
     */
    public void save(AppData data) {
        try {
            new PossessionService(data);
            Path parent = dataFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Path temporaryFile = Files.createTempFile(parent, "data-", ".json.tmp");
            try (Writer writer = Files.newBufferedWriter(temporaryFile)) {
                gson.toJson(data, writer);
            }
            replaceDataFile(temporaryFile);
        } catch (IOException | RuntimeException exception) {
            throw new StorageException("Could not save possession data.", exception);
        }
    }

    private void replaceDataFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, dataFile, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void preserveCorruptFile(Exception loadFailure) {
        Path backupFile = backupPath();
        try {
            Files.move(dataFile, backupFile);
        } catch (IOException backupFailure) {
            loadFailure.addSuppressed(backupFailure);
        }
    }

    private Path backupPath() {
        String fileName = dataFile.getFileName().toString();
        int extensionStart = fileName.lastIndexOf('.');
        String baseName = extensionStart < 0 ? fileName : fileName.substring(0, extensionStart);
        String extension = extensionStart < 0 ? "" : fileName.substring(extensionStart);
        return dataFile.resolveSibling(baseName + ".corrupt-" + BACKUP_TIMESTAMP.format(LocalDateTime.now())
                + extension);
    }
}
