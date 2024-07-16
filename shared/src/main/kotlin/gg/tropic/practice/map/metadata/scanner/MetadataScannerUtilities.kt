package gg.tropic.practice.map.metadata.scanner

import gg.tropic.practice.map.metadata.scanner.impl.*

/**
 * @author GrowlyX
 * @since 11/10/2022
 */
object MetadataScannerUtilities
{
    private val scanners = mutableListOf<AbstractMapMetadataScanner<*>>(
        // TODO: dynamic scan with @Service please
        MapZoneMetadataScanner,
        MapSpawnMetadataScanner,
        MapLevelMetadataScanner,
        MapPortalMetadataScanner,
        MapBedMetadataScanner
    )

    fun matches(type: String) =
        scanners.firstOrNull {
            it.type.equals(type, true)
        }
}
