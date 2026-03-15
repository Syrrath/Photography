package net.blouflin.photography.networking;

import net.blouflin.photography.player.PlayerIsUsingCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import java.util.UUID;

public record GetUsingPhotographyCameraPayload(UUID player, Boolean isUsingPhotographyCamera, String handUsingPhotographyCamera) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<GetUsingPhotographyCameraPayload> ID = CustomPacketPayload.createType("photography_get_using_photography_camera");
    public static final StreamCodec<FriendlyByteBuf, GetUsingPhotographyCameraPayload> CODEC = StreamCodec.ofMember((value, buf) -> buf.writeUUID(value.player).writeBoolean(value.isUsingPhotographyCamera).writeUtf(value.handUsingPhotographyCamera), buf -> new GetUsingPhotographyCameraPayload(buf.readUUID(),buf.readBoolean(),buf.readUtf()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(Minecraft client, UUID player, Boolean isUsingPhotographyCamera, String handUsingPhotographyCamera) {
        client.execute(() -> {

            ((PlayerIsUsingCamera) client.level.getPlayerByUUID(player)).setUsingPhotographyCamera(isUsingPhotographyCamera,handUsingPhotographyCamera);
        });
    }
}
