package net.blouflin.photography;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import net.minecraft.item.map.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;

import java.util.Objects;

public class PhotographyUtil {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static MapState fromNbt(NbtCompound nbt) {

        // unused map data; the MapState.of method used below accept these
//        int i = nbt.getInt("xCenter", 0);
//        int j = nbt.getInt("zCenter", 0);
//        boolean bl = nbt.getBoolean("showDecorations", false);
//        boolean bl2 = nbt.getBoolean("unlimitedTracking", false);

        // used map data; provided to MapState.of below
        String dimension = nbt.getString("dimension", "minecraft:overworld");
        byte scale = (byte) MathHelper.clamp(nbt.getByte("scale", (byte) 3), 0, 4);
        boolean locked = nbt.getBoolean("locked", true);

        MapState mapState = MapState.of(scale, locked, RegistryKey.of(RegistryKeys.WORLD, Identifier.of(dimension)));

        // add color data to map after creating it
        byte[] dummyByte = new byte[0];
        byte[] colorBytes = nbt.getByteArray("colors").orElse(dummyByte);
        if (colorBytes.length == 16384) {
            mapState.colors = colorBytes;
        }

        return mapState;
    }


    public static NbtCompound writeNbt(NbtCompound nbt, MapState state) {

        //unused data; see fromNbt method above
//        nbt.putInt("xCenter", state.centerX);
//        nbt.putInt("zCenter", state.centerZ);
//
//        nbt.putBoolean("showDecorations", state.showDecorations);
//        nbt.putBoolean("unlimitedTracking", state.unlimitedTracking);


        // get dimension
        DataResult<NbtElement> var10000 = Identifier.CODEC.encodeStart(NbtOps.INSTANCE, state.dimension.getValue());
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
