package com.example.domain

enum class FuelGrade(
    val id: String,
    val displayName: String,
    val shortCode: String,
    val standardCode: String,
    val minSpec15C: Double,
    val maxSpec15C: Double,
    val defaultObservedDensity: Int,
    val defaultObservedTemp: Int,
    val defaultTankName: String
) {
    PETROL_MS(
        id = "PETROL_MS",
        displayName = "Petrol MS",
        shortCode = "MS-91",
        standardCode = "IS 2796 / ASTM D4814",
        minSpec15C = 720.0,
        maxSpec15C = 775.0,
        defaultObservedDensity = 732,
        defaultObservedTemp = 28,
        defaultTankName = "Tank 01 • MS-1"
    ),
    DIESEL_HSD(
        id = "DIESEL_HSD",
        displayName = "Diesel HSD",
        shortCode = "HSD",
        standardCode = "IS 1460 / ASTM D975",
        minSpec15C = 815.0,
        maxSpec15C = 845.0,
        defaultObservedDensity = 823,
        defaultObservedTemp = 28,
        defaultTankName = "Tank 02 • HSD-1"
    ),
    PREMIUM_SPEED(
        id = "PREMIUM_SPEED",
        displayName = "Premium Speed",
        shortCode = "SPD-95",
        standardCode = "IS 2796 High Octane",
        minSpec15C = 730.0,
        maxSpec15C = 775.0,
        defaultObservedDensity = 744,
        defaultObservedTemp = 27,
        defaultTankName = "Tank 03 • SPD-1"
    ),
    BIO_DIESEL(
        id = "BIO_DIESEL",
        displayName = "Bio-Diesel",
        shortCode = "B100",
        standardCode = "IS 15607 / ASTM D6751",
        minSpec15C = 860.0,
        maxSpec15C = 900.0,
        defaultObservedDensity = 869,
        defaultObservedTemp = 29,
        defaultTankName = "Tank 04 • BIO-1"
    );

    companion object {
        fun fromId(id: String): FuelGrade = entries.firstOrNull { it.id == id } ?: DIESEL_HSD
    }
}
