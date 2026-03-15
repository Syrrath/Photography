package net.blouflin.photography.networking;

import net.blouflin.photography.Photography;
import net.minecraft.client.Minecraft;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundSource;

public record PlayCameraShutterSoundPayload(GlobalPos globalPos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayCameraShutterSoundPayload> ID = CustomPacketPayload.createType("photography_play_camera_shutter_sound");
    public static final StreamCodec<FriendlyByteBuf, PlayCameraShutterSoundPayload> CODEC = StreamCodec.ofMember((value, buf) -> buf.writeGlobalPos(value.globalPos), buf -> new PlayCameraShutterSoundPayload(buf.readGlobalPos()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(Minecraft client, GlobalPos globalPos) {
        client.execute(() -> {
            client.level.playLocalSound(globalPos.pos().getX(),globalPos.pos().getY(),globalPos.pos().getZ(),Photography.CAMERA_SHUTTER,SoundSource.PLAYERS,0.7f,1.0f,true);
        });
    }
}
