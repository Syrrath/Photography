package net.blouflin.photography.networking;

import net.blouflin.photography.player.PlayerIsUsingCamera;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;

public record SetUsingPhotographyCameraPayload(Boolean isUsingPhotographyCamera, String handUsingPhotographyCamera) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetUsingPhotographyCameraPayload> ID = CustomPacketPayload.createType("photography_set_using_photography_camera");
    public static final StreamCodec<FriendlyByteBuf, SetUsingPhotographyCameraPayload> CODEC = StreamCodec.ofMember((value, buf) -> buf.writeBoolean(value.isUsingPhotographyCamera).writeUtf(value.handUsingPhotographyCamera), buf -> new SetUsingPhotographyCameraPayload(buf.readBoolean(),buf.readUtf()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(ServerPlayer player, Boolean isUsingPhotographyCamera, String handUsingPhotographyCamera) {
        player.level().getServer().execute(() -> {

            ((PlayerIsUsingCamera) player).setUsingPhotographyCamera(isUsingPhotographyCamera,handUsingPhotographyCamera);
            if (isUsingPhotographyCamera) {
                player.playSound(SoundEvents.SPYGLASS_USE, 1.0f, 1.0f);
            } else {
                player.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0f, 1.0f);
            }
            for (ServerPlayer otherPlayer : player.level().getServer().getPlayerList().getPlayers()) {
                GetUsingPhotographyCameraPayload payload = new GetUsingPhotographyCameraPayload(player.getUUID(),isUsingPhotographyCamera,handUsingPhotographyCamera);
                ServerPlayNetworking.send(otherPlayer,payload);
            }
        });
    }
}
