package com.possessionmanager.model;

/**
 * Groups predefined relationship wording that is useful for physical possessions.
 */
public enum RelationshipCategory {
    STORAGE("Storage"),
    CHARGING("Charging"),
    COMPATIBILITY("Compatibility"),
    PART_ACCESSORY("Part / accessory"),
    USE_TOGETHER("Use together"),
    CUSTOM("Custom relationship…");

    private final String displayName;

    RelationshipCategory(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
