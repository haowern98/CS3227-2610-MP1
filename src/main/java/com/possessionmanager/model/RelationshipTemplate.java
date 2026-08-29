package com.possessionmanager.model;

import java.util.List;
import java.util.Objects;

/**
 * Defines a predefined pair of relationship phrases for a possession category.
 */
public enum RelationshipTemplate {
    STORED_IN(RelationshipCategory.STORAGE, "stored in", "contains"),
    CHARGED_BY(RelationshipCategory.CHARGING, "charged by", "charges"),
    COMPATIBLE_WITH(RelationshipCategory.COMPATIBILITY, "compatible with", "compatible with"),
    PART_OF(RelationshipCategory.PART_ACCESSORY, "part of", "has part"),
    USED_WITH(RelationshipCategory.USE_TOGETHER, "used with", "used with");

    private final RelationshipCategory category;
    private final String forwardLabel;
    private final String inverseLabel;

    RelationshipTemplate(RelationshipCategory category, String forwardLabel, String inverseLabel) {
        this.category = category;
        this.forwardLabel = forwardLabel;
        this.inverseLabel = inverseLabel;
    }

    /**
     * Returns templates available for one selected category.
     *
     * @param category category whose templates are requested.
     * @return predefined templates in the category.
     */
    public static List<RelationshipTemplate> forCategory(RelationshipCategory category) {
        Objects.requireNonNull(category, "category must not be null");
        return java.util.Arrays.stream(values())
                .filter(template -> template.category == category)
                .toList();
    }

    /**
     * Returns the category that groups this template.
     *
     * @return template category.
     */
    public RelationshipCategory category() {
        return category;
    }

    /**
     * Returns the phrase shown from the first possession to the related possession.
     *
     * @return first possession's phrase.
     */
    public String forwardLabel() {
        return forwardLabel;
    }

    /**
     * Returns the phrase shown from the related possession back to the first possession.
     *
     * @return related possession's phrase.
     */
    public String inverseLabel() {
        return inverseLabel;
    }
}
