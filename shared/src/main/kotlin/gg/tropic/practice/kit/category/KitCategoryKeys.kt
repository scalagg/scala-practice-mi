package gg.tropic.practice.kit.category

import gg.scala.commons.persist.datasync.DataSyncKeys

/**
 * @author GrowlyX
 * @since 10/12/2022
 */
object KitCategoryKeys : DataSyncKeys
{
    override fun store() = keyOf("mi-practice-kits-categories", "")
    override fun sync() = keyOf("mi-practice-kits-categories", "update")
}