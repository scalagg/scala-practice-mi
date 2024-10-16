package gg.tropic.practice.kit.category

import gg.scala.basics.plugin.ScalaBasicsPlugin
import gg.scala.commons.persist.datasync.DataSyncService
import gg.scala.commons.persist.datasync.DataSyncSource
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Service

/**
 * @author GrowlyX
 * @since 10/16/2022
 */
@Service
object KitCategoryService : DataSyncService<KitCategoryConfigModel>()
{
    @Inject
    lateinit var plugin: ScalaBasicsPlugin

    override fun locatedIn() = DataSyncSource.Mongo

    override fun keys() = KitCategoryKeys
    override fun type() = KitCategoryConfigModel::class.java
}