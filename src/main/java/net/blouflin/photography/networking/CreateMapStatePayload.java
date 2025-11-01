package net.blouflin.photography.networking;

import net.blouflin.image2map.Image2Map;
import net.blouflin.photography.Photography;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.MapColor;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.GlobalPos;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Objects;

public record CreateMapStatePayload() implements CustomPayload {
    public static final CustomPayload.Id<CreateMapStatePayload> ID = CustomPayload.id("photography_create_map_state");
    public static final PacketCodec<PacketByteBuf, CreateMapStatePayload> CODEC = PacketCodec.of((value, buf) -> {}, buf -> new CreateMapStatePayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(ServerPlayerEntity player) {

        // TODO Debug
        System.out.println("running CreateMapStatePayload");

        player.getServer().execute(() -> {

            ServerWorld world = player.getWorld();
            MapIdComponent id = world.increaseAndGetMapId();
            MapState state = MapState.of(player.getX(), player.getZ(), (byte) 3, false, false, RegistryKey.of(RegistryKeys.WORLD, Identifier.of("image2map", "generated")));

            world.putMapState(id, state);

            NbtCompound nbtCompound = new NbtCompound();

//            nbtCompound = state.writeNbt(nbtCompound, registryLookup);
//            nbtCompound =


//            ServerWorld world = player.getWorld();
//            var id = world.increaseAndGetMapId();
//            NbtCompound nbt = new NbtCompound();
//
//            nbt.putString("dimension", world.getRegistryKey().getValue().toString());
//            nbt.putInt("xCenter", (int) player.getX());
//            nbt.putInt("zCenter", (int) player.getZ());
//            nbt.putBoolean("locked", true);
//            nbt.putBoolean("unlimitedTracking", false);
//            nbt.putBoolean("trackingPosition", false);
//            nbt.putByte("scale", (byte) 3);
//            MapState state = MapState.of(player.getX(), player.getZ(), (byte) 3, false, false, RegistryKey.of(RegistryKeys.WORLD, Identifier.of("image2map", "generated")));
//            world.putMapState(id, state);
//            stack.getOrCreateNbt().putInt("map", id);
//            ItemStack stack;
//
//
//            var stack = new ItemStack(Items.FILLED_MAP);
//            stack.set(DataComponentTypes.MAP_ID, id);
//            stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(ImageData.CODEC.codec().encodeStart(NbtOps.INSTANCE, data).result().orElseThrow().asCompound().orElseThrow()));
//
//            NbtCompound nbtCompound = new NbtCompound();
//            nbtCompound = stack.getComponents()
//            nbtCompound = state.(nbtCompound, registryLookup);




//            int id = player.getWorld().increaseAndGetMapId().id();
//            NbtCompound nbt = new NbtCompound();
//            RegistryWrapper.WrapperLookup registryLookup = player.getRegistryManager();
//            nbt.putString("dimension", player.getWorld().getRegistryKey().getValue().toString());
//            nbt.putInt("xCenter", (int) player.getX());
//            nbt.putInt("zCenter", (int) player.getZ());
//            nbt.putBoolean("locked", true);
//            nbt.putBoolean("unlimitedTracking", false);
//            nbt.putBoolean("trackingPosition", false);
//            nbt.putByte("scale", (byte) 3);
//            nbt.put("banners", new NbtList());
//            nbt.put("frames", new NbtList());
//            // TODO MapState state = MapState.fromNbt(nbt,registryLookup);
//
//            //MapState state = MapState.of(0, 0, (byte) 0, false, false, RegistryKey.of(RegistryKeys.WORLD, Identifier.of("photography", "generated")));
//            MapState state = MapState.of((int) player.getX(), (int) player.getZ(), (byte) 3, true, false, RegistryKey.of(RegistryKeys.WORLD, Identifier.of("photography", "generated")));
//            player.getWorld().putMapState(id, state);
//            NbtCompound nbtCompound = new NbtCompound();
//            //nbtCompound = state. (nbtCompound, registryLookup);
//            // TODO nbtCompound = state.writeNbt(nbtCompound, registryLookup);
//            //nbtCompound = state.set(nbtCompound, registryLookup);
//            //nbtCompound


            for (ServerPlayerEntity otherPlayer : player.getServer().getPlayerManager().getPlayerList()) {
                //TODO Debug
                for (ServerPlayerEntity otherPlayer2 : player.getServer().getPlayerManager().getPlayerList()) {
                    Photography.LOGGER.info("for ServerPlayerEntity : getPlayerManager" + otherPlayer + otherPlayer2);

                    PlayCameraShutterSoundPayload payload = new PlayCameraShutterSoundPayload(GlobalPos.create(player.getWorld().getRegistryKey(), player.getBlockPos()));
                    ServerPlayNetworking.send(otherPlayer2, payload);
                }

                CreatePicturePayload payload = new CreatePicturePayload(id.id(), nbtCompound);
                System.out.println(payload);
                ServerPlayNetworking.send(player, payload);
            }
        });
    }
}
