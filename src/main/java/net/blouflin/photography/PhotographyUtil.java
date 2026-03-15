package net.blouflin.photography;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.MapItem.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.slf4j.Logger;

import java.util.Objects;

public class PhotographyUtil {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static MapItemSavedData fromNbt(CompoundTag nbt) {

        // unused map data; the MapState.of method used below doesn't accept these
//        int i = nbt.getInt("xCenter", 0);
//        int j = nbt.getInt("zCenter", 0);
//        boolean bl = nbt.getBoolean("showDecorations", false);
//        boolean bl2 = nbt.getBoolean("unlimitedTracking", false);

        // used map data; provided to MapState.of below
        String dimension = nbt.getStringOr("dimension", "minecraft:overworld");
        byte scale = (byte) Mth.clamp(nbt.getByteOr("scale", (byte) 3), 0, 4);
        boolean locked = nbt.getBooleanOr("locked", true);

        MapItemSavedData mapState = MapItemSavedData.createForClient(scale, locked, ResourceKey.create(Registries.DIMENSION, Identifier.parse(dimension)));

        // add color data to map after creating it
        byte[] dummyByte = new byte[0];
        byte[] colorBytes = nbt.getByteArray("colors").orElse(dummyByte);
        if (colorBytes.length == 16384) {
            mapState.colors = colorBytes;
        }

        return mapState;
    }


    public static CompoundTag writeNbt(CompoundTag nbt, MapItemSavedData state) {

        //unused data; see fromNbt method above
//        nbt.putInt("xCenter", state.centerX);
//        nbt.putInt("zCenter", state.centerZ);
//
//        nbt.putBoolean("showDecorations", state.showDecorations);
//        nbt.putBoolean("unlimitedTracking", state.unlimitedTracking);


        // get dimension
        DataResult<Tag> var10000 = Identifier.CODEC.encodeStart(NbtOps.INSTANCE, state.dimension.identifier());
        Logger var10001 = LOGGER;
        Objects.requireNonNull(var10001);
        var10000.resultOrPartial(var10001::error).ifPresent((nbtElement) -> nbt.put("dimension", nbtElement));

        // get colors, scale, and locked/unlocked status
        nbt.putByteArray("colors", state.colors);
        nbt.putByte("scale", state.scale);
        nbt.putBoolean("locked", state.locked);

        return nbt;
    }
}
