package net.blouflin.photography.networking;

import net.blouflin.photography.PhotographyUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public record CreateMapStatePayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CreateMapStatePayload> ID = CustomPacketPayload.createType("photography_create_map_state");
    public static final StreamCodec<FriendlyByteBuf, CreateMapStatePayload> CODEC = StreamCodec.ofMember((value, buf) -> {}, buf -> new CreateMapStatePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(ServerPlayer player) {

        player.level().getServer().execute(() -> {

            int id = player.level().getFreeMapId().id();
            CompoundTag nbt = new CompoundTag();
            HolderLookup.Provider registryLookup = player.registryAccess();
            nbt.putString("dimension", player.level().dimension().identifier().toString());
            nbt.putInt("xCenter", (int) player.getX());
            nbt.putInt("zCenter", (int) player.getZ());
            nbt.putBoolean("locked", true);
            nbt.putBoolean("unlimitedTracking", false);
            nbt.putBoolean("showDecorations", false);
            nbt.putByte("scale", (byte) 3);
            nbt.put("banners", new ListTag());
            nbt.put("frames", new ListTag());
            MapItemSavedData state = PhotographyUtil.fromNbt(nbt);


            CompoundTag nbtCompound = new CompoundTag();
            nbtCompound = PhotographyUtil.writeNbt(nbtCompound, state);

            for (ServerPlayer otherPlayer : player.level().getServer().getPlayerList().getPlayers()) {
                PlayCameraShutterSoundPayload payload = new PlayCameraShutterSoundPayload(GlobalPos.of(player.level().dimension(),player.blockPosition()));
                ServerPlayNetworking.send(otherPlayer,payload);
            }

            CreatePicturePayload payload = new CreatePicturePayload(id, nbtCompound);
            ServerPlayNetworking.send(player, payload);
        });
    }
}
