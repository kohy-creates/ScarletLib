package xyz.kohara.scarletlib.impl.prompt.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.kohara.scarletlib.ScarletLib;
import xyz.kohara.scarletlib.api.event.ProximityPromptInteractEvent;
import xyz.kohara.scarletlib.api.prompt.ProximityPrompt;
import xyz.kohara.scarletlib.impl.network.ScarletLibPackets;
import xyz.kohara.scarletlib.impl.network.packet.prompt.RemoveProximityPromptS2CPacket;
import xyz.kohara.scarletlib.impl.network.packet.prompt.SyncAllProximityPromptsS2CPacket;
import xyz.kohara.scarletlib.api.prompt.ProximityPromptClientData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = ScarletLib.MOD_ID)
public class ServerProximityPromptRegistry {

	private static final Map<UUID, ProximityPrompt> PROMPTS = new ConcurrentHashMap<>();

	public static void register(ProximityPrompt prompt) {
		PROMPTS.put(prompt.getUuid(), prompt);
		syncToAllClients();
	}

	public static void unregister(ProximityPrompt prompt) {
		unregisterByUuid(prompt.getUuid());
	}

	public static void unregisterByUuid(UUID promptId) {
		if (PROMPTS.remove(promptId) != null) {
			ScarletLibPackets.INSTANCE.sendToAllPlayers(new RemoveProximityPromptS2CPacket(promptId));
		}
	}

	public static Collection<ProximityPrompt> getAllPrompts() {
		return PROMPTS.values();
	}

	public static void validateAllPrompts() {
		for (var prompt : getAllPrompts()) {
			var level = prompt.getLevel();
			var loc = prompt.getLocation();

			if (prompt.getBlock() != null) {
				BlockPos pos = BlockPos.containing(loc);
				if (level.getBlockState(pos).getBlock() != prompt.getBlock()) {
					unregister(prompt);
					ScarletLibPackets.INSTANCE.sendToAllPlayers(new RemoveProximityPromptS2CPacket(prompt.getUuid()));
				}
			} else if (prompt.getEntity() != null) {
				if (level.getEntity(prompt.getEntity()) == null) {
					unregister(prompt);

				}
			}
		}
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			validateAllPrompts();
		}
	}

	@SubscribeEvent
	public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			syncToPlayer(player);
		}
	}

	private static void syncToAllClients() {
		List<ProximityPromptClientData> dataList = PROMPTS.values().stream()
				.map(ProximityPromptClientData::fromPrompt)
				.toList();
		ScarletLibPackets.INSTANCE.sendToAllPlayers(new SyncAllProximityPromptsS2CPacket(dataList));
	}

	private static void syncToPlayer(ServerPlayer player) {
		List<ProximityPromptClientData> dataList = PROMPTS.values().stream()
				.map(ProximityPromptClientData::fromPrompt)
				.toList();
		ScarletLibPackets.INSTANCE.sendToPlayer(new SyncAllProximityPromptsS2CPacket(dataList), player);
	}

	public static void handleInteraction(UUID uuid, ServerPlayer player) {
		var eventPrompt = PROMPTS.get(uuid);
		var eventHandler = new ProximityPromptInteractEvent(eventPrompt, player, player.serverLevel());
		MinecraftForge.EVENT_BUS.post(eventHandler);
		if (eventHandler.isCanceled()) return;
		unregister(eventPrompt);
	}
}