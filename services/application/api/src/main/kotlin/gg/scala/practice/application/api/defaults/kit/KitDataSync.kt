package gg.scala.practice.application.api.defaults.kit

import gg.scala.practice.application.api.DPSDataSync
import gg.scala.practice.application.api.DPSDataSyncKeys
import gg.scala.practice.application.api.DPSDataSyncSource
import gg.scala.practice.namespace
import gg.scala.practice.namespaceShortened
import gg.scala.practice.suffixWhenDev
import net.kyori.adventure.key.Key

/**
 * @author GrowlyX
 * @since 9/24/2023
 */
object KitDataSync : DPSDataSync<ImmutableKitContainer>()
{
    object DPSMapKeys : DPSDataSyncKeys
    {
        override fun newStore() = "mi-practice-kits"

        override fun store() = Key.key(namespace(), "kits")
        override fun sync() = Key.key(namespaceShortened().suffixWhenDev(), "ksync")
    }

    override fun locatedIn() = DPSDataSyncSource.Mongo

    override fun keys() = DPSMapKeys
    override fun type() = ImmutableKitContainer::class.java

    private val hooks = mutableListOf<() -> Unit>()

    fun onReload(hook: () -> Unit) = hooks.add(hook)

    override fun postReload()
    {
        super.postReload()
        hooks.forEach { it() }
    }
}
