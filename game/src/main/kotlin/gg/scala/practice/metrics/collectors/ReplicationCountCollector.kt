package gg.scala.practice.metrics.collectors

import dev.cubxity.plugins.metrics.api.metric.collector.Collector
import dev.cubxity.plugins.metrics.api.metric.data.GaugeMetric
import gg.scala.practice.map.MapReplicationService

/**
 * @author GrowlyX
 * @since 3/2/2024
 */
object ReplicationCountCollector : Collector
{
    override fun collect() = listOf(
        GaugeMetric(
            "practice_replication_count", mapOf(),
            MapReplicationService.mapReplications.size
        ),
        GaugeMetric(
            "practice_replication_inuse_count", mapOf(),
            MapReplicationService.mapReplications.count { it.inUse }
        ),
    )

}
