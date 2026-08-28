package com.possessionmanager.service;

import com.possessionmanager.model.AppData;
import com.possessionmanager.model.Possession;
import com.possessionmanager.model.PossessionCategory;
import com.possessionmanager.model.PossessionInput;
import com.possessionmanager.model.PossessionStatus;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Manages possession creation, updates, queries, and archival.
 */
public final class PossessionService {
    private final Map<UUID, Possession> possessions = new LinkedHashMap<>();

    /**
     * Creates an empty possession service.
     */
    public PossessionService() {
    }

    /**
     * Creates a possession service from a persisted snapshot.
     *
     * @param data persisted data to load.
     */
    public PossessionService(AppData data) {
        Objects.requireNonNull(data, "data must not be null");
        for (Possession possession : data.possessions()) {
            if (possessions.putIfAbsent(possession.id(), possession) != null) {
                throw new ValidationException("Saved data contains duplicate possession IDs.");
            }
        }
    }

    /**
     * Adds a possession with validated details.
     *
     * @param input user-editable possession details.
     * @return the new possession.
     */
    public Possession addPossession(PossessionInput input) {
        Possession possession = Possession.create(normalize(input));
        possessions.put(possession.id(), possession);
        return possession;
    }

    /**
     * Updates an existing possession.
     *
     * @param possessionId identifier of the possession to update.
     * @param input replacement possession details.
     * @return the updated possession.
     */
    public Possession updatePossession(UUID possessionId, PossessionInput input) {
        Possession updated = requirePossession(possessionId).updateDetails(normalize(input));
        possessions.put(possessionId, updated);
        return updated;
    }

    /**
     * Archives a possession without deleting its record.
     *
     * @param possessionId identifier of the possession to archive.
     */
    public void archivePossession(UUID possessionId) {
        possessions.compute(possessionId, (id, possession) -> {
            if (possession == null) {
                throw new ValidationException("Possession was not found.");
            }
            return possession.archive();
        });
    }

    /**
     * Finds a possession by its stable identifier.
     *
     * @param possessionId identifier to search for.
     * @return the matching possession when present.
     */
    public Optional<Possession> findById(UUID possessionId) {
        return Optional.ofNullable(possessions.get(possessionId));
    }

    /**
     * Lists non-archived possessions ordered by name.
     *
     * @return active possessions.
     */
    public List<Possession> listAll() {
        return activePossessions().toList();
    }

    /**
     * Lists archived possessions ordered by name.
     *
     * @return archived possessions.
     */
    public List<Possession> listArchived() {
        return possessions.values().stream()
                .filter(possession -> possession.status() == PossessionStatus.ARCHIVED)
                .sorted(byName())
                .toList();
    }

    /**
     * Searches active possessions by name or tag without case sensitivity.
     *
     * @param query text to search for.
     * @return matching active possessions.
     */
    public List<Possession> search(String query) {
        String normalizedQuery = normalizeText(query).toLowerCase(Locale.ROOT);
        if (normalizedQuery.isEmpty()) {
            return listAll();
        }
        return activePossessions()
                .filter(possession -> matches(possession, normalizedQuery))
                .toList();
    }

    /**
     * Applies the dashboard's optional text, category, and status filters together.
     *
     * @param query text to search for, or blank for all names and tags.
     * @param category category to match, or {@code null} for every category.
     * @param status status to match, or {@code null} for every active status.
     * @return matching active possessions.
     */
    public List<Possession> query(String query, PossessionCategory category, PossessionStatus status) {
        String normalizedQuery = normalizeText(query).toLowerCase(Locale.ROOT);
        return activePossessions()
                .filter(possession -> normalizedQuery.isEmpty() || matches(possession, normalizedQuery))
                .filter(possession -> category == null || possession.category() == category)
                .filter(possession -> status == null || possession.status() == status)
                .toList();
    }

    /**
     * Filters active possessions by category.
     *
     * @param category category to match.
     * @return matching active possessions.
     */
    public List<Possession> filterByCategory(PossessionCategory category) {
        Objects.requireNonNull(category, "category must not be null");
        return activePossessions().filter(possession -> possession.category() == category).toList();
    }

    /**
     * Filters active possessions by status.
     *
     * @param status status to match.
     * @return matching active possessions.
     */
    public List<Possession> filterByStatus(PossessionStatus status) {
        Objects.requireNonNull(status, "status must not be null");
        return activePossessions().filter(possession -> possession.status() == status).toList();
    }

    /**
     * Returns a snapshot suitable for persistence.
     *
     * @return all possession records, including archived records.
     */
    public AppData toAppData() {
        return new AppData(List.copyOf(possessions.values()));
    }

    private Possession requirePossession(UUID possessionId) {
        return findById(possessionId)
                .orElseThrow(() -> new ValidationException("Possession was not found."));
    }

    private PossessionInput normalize(PossessionInput input) {
        Objects.requireNonNull(input, "input must not be null");
        String name = normalizeText(input.name());
        if (name.isEmpty()) {
            throw new ValidationException("Name is required.");
        }
        if (input.category() == null || input.status() == null) {
            throw new ValidationException("Category and status are required.");
        }
        return new PossessionInput(name, input.category(), normalizeText(input.location()), input.status(),
                normalizeTags(input.tags()), normalizeText(input.notes()));
    }

    private Set<String> normalizeTags(Set<String> tags) {
        if (tags == null) {
            return Set.of();
        }
        return tags.stream()
                .filter(Objects::nonNull)
                .map(this::normalizeText)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private boolean matches(Possession possession, String normalizedQuery) {
        return possession.name().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                || possession.tags().stream()
                        .anyMatch(tag -> tag.toLowerCase(Locale.ROOT).contains(normalizedQuery));
    }

    private java.util.stream.Stream<Possession> activePossessions() {
        return possessions.values().stream()
                .filter(possession -> possession.status() != PossessionStatus.ARCHIVED)
                .sorted(byName());
    }

    private Comparator<Possession> byName() {
        return Comparator.comparing(Possession::name, String.CASE_INSENSITIVE_ORDER);
    }

    private String normalizeText(String text) {
        return text == null ? "" : text.trim();
    }
}
