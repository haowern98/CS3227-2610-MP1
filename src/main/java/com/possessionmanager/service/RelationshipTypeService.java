package com.possessionmanager.service;

import com.possessionmanager.model.RelationshipKind;
import com.possessionmanager.model.RelationshipType;
import com.possessionmanager.model.RelationshipTypeInput;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages controlled labels for possession relationships.
 */
public final class RelationshipTypeService {
    private final Map<UUID, RelationshipType> relationshipTypes = new LinkedHashMap<>();

    /**
     * Creates an empty relationship-type service.
     */
    public RelationshipTypeService() {
    }

    /**
     * Creates a relationship-type service from persisted types.
     *
     * @param types relationship types to load.
     */
    public RelationshipTypeService(List<RelationshipType> types) {
        Objects.requireNonNull(types, "types must not be null");
        for (RelationshipType type : types) {
            addPersistedType(type);
        }
    }

    /**
     * Adds a relationship type with validated labels.
     *
     * @param input user-editable type details.
     * @return the new relationship type.
     */
    public RelationshipType addType(RelationshipTypeInput input) {
        RelationshipTypeInput normalized = normalize(input);
        ensureUniqueName(normalized.name(), null);
        RelationshipType type = RelationshipType.create(normalized);
        relationshipTypes.put(type.id(), type);
        return type;
    }

    /**
     * Updates an existing relationship type with validated labels.
     *
     * @param typeId identifier of the type to update.
     * @param input replacement type details.
     * @return updated relationship type.
     */
    public RelationshipType updateType(UUID typeId, RelationshipTypeInput input) {
        RelationshipType existing = requireType(typeId);
        RelationshipTypeInput normalized = normalize(input);
        ensureUniqueName(normalized.name(), existing.id());
        RelationshipType updated = existing.update(normalized);
        relationshipTypes.put(typeId, updated);
        return updated;
    }

    /**
     * Deletes an existing relationship type.
     *
     * @param typeId identifier of the type to delete.
     */
    public void deleteType(UUID typeId) {
        if (relationshipTypes.remove(typeId) == null) {
            throw new ValidationException("Relationship type was not found.");
        }
    }

    /**
     * Finds a relationship type by its stable identifier.
     *
     * @param typeId identifier to search for.
     * @return matching type when present.
     */
    public Optional<RelationshipType> findById(UUID typeId) {
        return Optional.ofNullable(relationshipTypes.get(typeId));
    }

    /**
     * Lists relationship types ordered by name.
     *
     * @return available relationship types.
     */
    public List<RelationshipType> listTypes() {
        return relationshipTypes.values().stream()
                .sorted(Comparator.comparing(RelationshipType::name, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private void addPersistedType(RelationshipType type) {
        Objects.requireNonNull(type, "type must not be null");
        RelationshipTypeInput normalized = normalize(new RelationshipTypeInput(type.name(), type.forwardLabel(),
                type.inverseLabel(), type.kind()));
        if (!matches(normalized, type)) {
            throw new ValidationException("Saved data contains an invalid relationship type.");
        }
        ensureUniqueName(normalized.name(), null);
        if (relationshipTypes.putIfAbsent(type.id(), type) != null) {
            throw new ValidationException("Saved data contains duplicate relationship type IDs.");
        }
    }

    private boolean matches(RelationshipTypeInput input, RelationshipType type) {
        return input.name().equals(type.name())
                && input.forwardLabel().equals(type.forwardLabel())
                && input.inverseLabel().equals(type.inverseLabel())
                && input.kind() == type.kind();
    }

    private RelationshipType requireType(UUID typeId) {
        return findById(typeId)
                .orElseThrow(() -> new ValidationException("Relationship type was not found."));
    }

    private RelationshipTypeInput normalize(RelationshipTypeInput input) {
        Objects.requireNonNull(input, "input must not be null");
        if (input.kind() == null) {
            throw new ValidationException("Relationship kind is required.");
        }
        String name = normalizeText(input.name());
        String forwardLabel = normalizeText(input.forwardLabel());
        if (name.isEmpty() || forwardLabel.isEmpty()) {
            throw new ValidationException("Name and relationship label are required.");
        }
        if (input.kind() == RelationshipKind.SYMMETRIC) {
            return new RelationshipTypeInput(name, forwardLabel, forwardLabel, input.kind());
        }
        String inverseLabel = normalizeText(input.inverseLabel());
        if (inverseLabel.isEmpty()) {
            throw new ValidationException("Directed relationship types need an inverse label.");
        }
        if (forwardLabel.equalsIgnoreCase(inverseLabel)) {
            throw new ValidationException("Directed relationship labels must be different.");
        }
        return new RelationshipTypeInput(name, forwardLabel, inverseLabel, input.kind());
    }

    private void ensureUniqueName(String name, UUID excludedTypeId) {
        boolean duplicate = relationshipTypes.values().stream()
                .filter(type -> !type.id().equals(excludedTypeId))
                .map(type -> type.name().toLowerCase(Locale.ROOT))
                .anyMatch(name.toLowerCase(Locale.ROOT)::equals);
        if (duplicate) {
            throw new ValidationException("Relationship type names must be unique.");
        }
    }

    private String normalizeText(String text) {
        return text == null ? "" : text.trim();
    }
}
