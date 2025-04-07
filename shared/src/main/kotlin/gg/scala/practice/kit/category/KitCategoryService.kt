package gg.scala.practice.kit.category

import gg.scala.basics.plugin.ScalaBasicsPlugin
import gg.scala.commons.persist.datasync.DataSyncKeys
import gg.scala.commons.persist.datasync.DataSyncService
import gg.scala.commons.persist.datasync.DataSyncSource
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Service
import gg.scala.practice.kit.Kit
import gg.scala.practice.kit.group.KitGroupContainer
import gg.scala.practice.kit.group.KitGroupService
import gg.scala.practice.namespace
import gg.scala.practice.namespaceShortened
import gg.scala.practice.suffixWhenDev
import net.kyori.adventure.key.Key

/**
 * @author GrowlyX
 * @since 10/16/2022
 */
@Service
object KitCategoryService : DataSyncService<KitCategoryContainer>()
{
    object CategoryKeys : DataSyncKeys
    {
        override fun newStore() = "mi-practice-kits-categories"

        override fun store() = Key.key(namespace(), "categories")
        override fun sync() = Key.key(namespaceShortened().suffixWhenDev(), "csync")
    }

    override fun locatedIn() = DataSyncSource.Mongo

    override fun keys() = CategoryKeys
    override fun type() = KitCategoryContainer::class.java

    fun categoriesOf(kit: Kit) = KitCategoryService.cached().categories
        .filter {
            kit.id in it.kits
        }
}
