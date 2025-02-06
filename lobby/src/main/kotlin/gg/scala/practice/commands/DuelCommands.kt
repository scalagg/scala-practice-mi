package gg.scala.practice.commands

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandCompletion
import gg.scala.commons.acf.annotation.Optional
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.annotations.commands.HighPriority
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.lemon.player.wrapper.AsyncLemonPlayer
import gg.scala.lemon.util.QuickAccess
import gg.scala.parties.model.Party
import gg.scala.parties.service.PartyService
import gg.scala.parties.service.PartyService.findPartyByUniqueId
import gg.scala.practice.duel.DuelRequestUtilities
import gg.scala.practice.friendship.FriendshipStateSetting
import gg.scala.practice.friendship.Friendships
import gg.scala.practice.kit.Kit
import gg.scala.practice.lobbyGroup
import gg.scala.practice.menu.pipeline.DuelRequestPipeline
import gg.scala.practice.player.LobbyPlayerService
import gg.scala.practice.player.PlayerState
import gg.scala.practice.queue.QueueService
import gg.scala.practice.region.PlayerRegionFromRedisProxy
import gg.scala.practice.settings.PracticeSettingCategory
import gg.scala.practice.suffixWhenDev
import gg.scala.staff.ScalaStaffPlugin
import gg.scala.staff.profile.StaffProfileService
import net.evilblock.cubed.util.CC

/**
 * @author GrowlyX
 * @since 10/14/2023
 */
@AutoRegister
@HighPriority
object DuelCommands : ScalaCommand()
{
    @CommandCompletion("@mip-players")
    @CommandAlias("accept")
    fun onDuelAccept(
        player: ScalaPlayer,
        target: AsyncLemonPlayer,
        kit: Kit
    ) = target.validatePlayers(
        player.bukkit(), false
    ) {
        it.identifier.offlineProfile

        val duelRequest = DuelRequestUtilities
            .duelRequest(it.uniqueId, player.uniqueId, kit)
            .join()
            ?: throw ConditionFailedException(
                "${CC.YELLOW}${it.name}${CC.RED} has not sent you a duel request with the kit ${CC.YELLOW}${kit.displayName}${CC.RED}."
            )

        if (player.bukkit().hasMetadata("vanished"))
        {
            throw ConditionFailedException(
                "You are currently in vanish! Use ${CC.B}/vanish${CC.RED} to be able to accept a duel."
            )
        }

        val staffProfile = StaffProfileService.find(player.bukkit())
        if (staffProfile != null && player.bukkit().hasPermission(ScalaStaffPlugin.STAFF_NODE))
        {
            if (staffProfile.staffAutoVanishEnabled)
            {
                throw ConditionFailedException("You currently have AutoVanish enabled! Use ${CC.B}/toggleautovanish${CC.RED} to be able to join a queue.")
            }
        }

        if (it.bukkitPlayer?.hasMetadata("vanished") == true)
        {
            throw ConditionFailedException(
                "${CC.YELLOW}${it.name}${CC.RED} is not logged onto the network!"
            )
        }

        if (player.bukkit().hasMetadata("frozen"))
        {
            throw ConditionFailedException(
                "You cannot accept this duel as you are frozen!"
            )
        }

        val party: Party? = findPartyByUniqueId(player.bukkit().uniqueId)

        val targetParty: Party? = findPartyByUniqueId(player.bukkit().uniqueId)

        if (targetParty != null && party != null && party.identifier == targetParty.identifier)
        {
            throw ConditionFailedException(CC.RED + "You cannot send duel requests to yourself.")
        }

        if (party != null && party.leader.uniqueId != player.uniqueId)
        {
            throw ConditionFailedException(CC.RED + "You're not the leader of your party.")
        }

        val profile = LobbyPlayerService.find(player.bukkit())
            ?: return@validatePlayers

        if (profile.state != PlayerState.Idle)
        {
            throw ConditionFailedException(
                "You are not in the right state to accept a duel!"
            )
        }

        QueueService.createMessage(
            "accept-duel",
            "request" to duelRequest
        ).publish(
            channel = "practice:queue".suffixWhenDev()
        )

        player.sendMessage("${CC.GREEN}Attempting to accept the duel...")
    }

    @CommandCompletion("@players")
    @CommandAlias("duel|d|fight|sendduelrequest")
    fun onDuelRequest(
        player: ScalaPlayer,
        target: AsyncLemonPlayer,
        @Optional kit: Kit?
    ) = target.validatePlayers(
        player.bukkit(), false
    ) {
        if (it.uniqueId == player.uniqueId)
        {
            throw ConditionFailedException(
                "You cannot send a duel request to yourself!"
            )
        }

        val profile = LobbyPlayerService.find(player.bukkit())
            ?: return@validatePlayers

        if (profile.state != PlayerState.Idle)
        {
            throw ConditionFailedException(
                "You cannot send a duel request right now!"
            )
        }

        val basicsProfile = it.identifier.basicsProfile
        it.identifier.offlineProfile

        val duelRequests = basicsProfile
            .setting<FriendshipStateSetting>(
                "${PracticeSettingCategory.PRACTICE_SETTING_PREFIX}:duel-requests-fr"
            )

        if (duelRequests == FriendshipStateSetting.Disabled)
        {
            throw ConditionFailedException(
                "${CC.YELLOW}${it.name}${CC.RED} has their duel requests disabled!"
            )
        }

        if (duelRequests == FriendshipStateSetting.FriendsOnly)
        {
            val relationshipExists = Friendships.requirements
                .existsBetween(player.uniqueId, it.uniqueId)
                .join()

            if (!relationshipExists)
            {
                throw ConditionFailedException(
                    "You must be friends with ${CC.YELLOW}${it.name}${CC.RED} to send them a duel request!"
                )
            }
        }

        if (it.bukkitPlayer?.hasMetadata("vanished") == true)
        {
            throw ConditionFailedException(
                "${CC.YELLOW}${it.name}${CC.RED} is not logged onto the network!"
            )
        }

        val server = QuickAccess.server(it.uniqueId)
            .join()
            ?: throw ConditionFailedException(
                "${CC.YELLOW}${it.name}${CC.RED} is not logged onto the network!"
            )

        if (lobbyGroup().suffixWhenDev() !in server.groups)
        {
            throw ConditionFailedException(
                "${CC.YELLOW}${it.name}${CC.RED} is not on a practice lobby!"
            )
        }

        if (kit != null)
        {
            DuelRequestPipeline.automateDuelRequestWithMapSelectUI(
                player = player.bukkit(),
                target = it.uniqueId,
                kit = kit,
                region = PlayerRegionFromRedisProxy
                    .of(player.bukkit())
                    .join()
            )
            return@validatePlayers
        }

        DuelRequestPipeline
            .build(player.bukkit(), it.identifier)
            .openMenu(player.bukkit())
    }
}
