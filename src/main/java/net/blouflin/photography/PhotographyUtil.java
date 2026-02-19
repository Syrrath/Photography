package net.blouflin.photography;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.map.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PhotographyUtil {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int SIZE = 128;
    private static final int SIZE_HALF = 64;
    public static final int MAX_SCALE = 4;
    public static final int MAX_DECORATIONS = 256;
    private static final String FRAME_PREFIX = "frame-";
    public final int centerX;
    public final int centerZ;
    public final RegistryKey<World> dimension;
    private final boolean showDecorations;
    private final boolean unlimitedTracking;
    public final byte scale;
    public byte[] colors = new byte[16384];
    public final boolean locked;
    private final List<MapState.PlayerUpdateTracker> updateTrackers = Lists.newArrayList();
    private final Map<PlayerEntity, MapState.PlayerUpdateTracker> updateTrackersByPlayer = Maps.newHashMap();
    private final Map<String, MapBannerMarker> banners = Maps.newHashMap();
    final Map<String, MapDecoration> decorations = Maps.newLinkedHashMap();
    private final Map<String, MapFrameMarker> frames = Maps.newHashMap();
    private int decorationCount;

    private PhotographyUtil(int centerX, int centerZ, byte scale, boolean showDecorations, boolean unlimitedTracking, boolean locked, RegistryKey<World> dimension) {
        this.scale = scale;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.dimension = dimension;
        this.showDecorations = showDecorations;
        this.unlimitedTracking = unlimitedTracking;
        this.locked = locked;
    }

    public static MapState fromNbt(NbtCompound nbt/*, RegistryWrapper.WrapperLookup registries*/) {
        //DataResult var10000 = DimensionType.worldFromDimensionNbt(new Dynamic(NbtOps.INSTANCE, nbt.get("dimension")));
//        DataResult var10000 = DimensionType.REGISTRY_CODEC.decode(new Dynamic(NbtOps.INSTANCE, nbt.get("dimension")));
//        Logger var10001 = LOGGER;
//        Objects.requireNonNull(var10001);
//        RegistryKeys.toDimensionKey((RegistryKey)var10000.resultOrPartial(var10001::error).orElseThrow(() -> new IllegalArgumentException("Invalid map dimension: " + String.valueOf(nbt.get("dimension"))));)
//        RegistryKey<World> registryKey = (RegistryKey)var10000;
//        RegistryKey<World> registryKey = (RegistryKey)var10000.resultOrPartial(var10001::error).orElseThrow(() -> new IllegalArgumentException("Invalid map dimension: " + String.valueOf(nbt.get("dimension"))));
        String w = nbt.getString("dimension", String.valueOf(DimensionTypes.OVERWORLD));
        int i = nbt.getInt("xCenter", 0);
        int j = nbt.getInt("zCenter", 0);
        byte b = (byte) MathHelper.clamp(nbt.getByte("scale", (byte) 3), 0, 4);
//        boolean bl = !nbt.contains("trackingPosition", 1) || nbt.getBoolean("trackingPosition", false);
        boolean bl2 = nbt.getBoolean("unlimitedTracking", false);
        boolean bl3 = nbt.getBoolean("locked", true);
        MapState mapState = MapState.of(i, j, b, bl2, bl3, RegistryKey.of(RegistryKeys.WORLD, Identifier.of("photography", "generated")));
        byte[] dummyByte = new byte[0];
        byte[] bs = nbt.getByteArray("colors").orElse(dummyByte);
        if (bs.length == 16384) {
            mapState.colors = bs;
        }

//        RegistryOps<NbtElement> registryOps = registries.getOps(NbtOps.INSTANCE);

//        for(MapBannerMarker mapBannerMarker : (List)MapBannerMarker.LIST_CODEC.parse(registryOps, nbt.get("banners")).resultOrPartial((banner) -> LOGGER.warn("Failed to parse map banner: '{}'", banner)).orElse(List.of())) {
//            mapState.banners.put(mapBannerMarker.getKey(), mapBannerMarker);
//            mapState.addDecoration(mapBannerMarker.getDecorationType(), (WorldAccess)null, mapBannerMarker.getKey(), (double)mapBannerMarker.pos().getX(), (double)mapBannerMarker.pos().getZ(), (double)180.0F, (Text)mapBannerMarker.name().orElse((Object)null));
//        }

//        NbtList nbtList = nbt.getList("frames", 10);

//        for(int k = 0; k < nbtList.size(); ++k) {
//            MapFrameMarker mapFrameMarker = MapFrameMarker.fromNbt(nbtList.getCompound(k));
//            if (mapFrameMarker != null) {
//                mapState.frames.put(mapFrameMarker.getKey(), mapFrameMarker);
//                mapState.addDecoration(MapDecorationTypes.FRAME, (WorldAccess)null, getFrameDecorationKey(mapFrameMarker.getEntityId()), (double)mapFrameMarker.getPos().getX(), (double)mapFrameMarker.getPos().getZ(), (double)mapFrameMarker.getRotation(), (Text)null);
//            }
//        }

        return mapState;
    }


    public static NbtCompound writeNbt(NbtCompound nbt, MapState state/*, RegistryWrapper.WrapperLookup registries*/) {
        DataResult var10000 = Identifier.CODEC.encodeStart(NbtOps.INSTANCE, state.dimension.getValue());
        Logger var10001 = LOGGER;
        Objects.requireNonNull(var10001);
        //var10000.resultOrPartial(var10001::error).ifPresent((dimension) -> nbt.put("dimension", dimension));
        //nbt.put("dimension", var10000.resultOrPartial().ifPresent((dimension) -> nbt.put("dimension", (NbtElement) dimension));
        nbt.putInt("xCenter", state.centerX);
        nbt.putInt("zCenter", state.centerZ);
        nbt.putByte("scale", state.scale);
        nbt.putByteArray("colors", state.colors);
        nbt.putBoolean("trackingPosition", state.showDecorations);
        nbt.putBoolean("unlimitedTracking", state.unlimitedTracking);
        nbt.putBoolean("locked", state.locked);
//        RegistryOps<NbtElement> registryOps = registries.getOps(NbtOps.INSTANCE);
//        nbt.put("banners", (NbtElement)MapBannerMarker.LIST_CODEC.encodeStart(registryOps, List.copyOf(this.banners.values())).getOrThrow());
//        NbtList nbtList = new NbtList();

//        for(MapFrameMarker mapFrameMarker : this.frames.values()) {
//            nbtList.add(mapFrameMarker.toNbt());
//        }

//        nbt.put("frames", nbtList);
        return nbt;
    }
}
