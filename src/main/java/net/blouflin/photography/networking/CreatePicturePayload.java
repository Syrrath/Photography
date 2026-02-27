package net.blouflin.photography.networking;

import net.blouflin.image2map.Image2Map;
import net.blouflin.image2map.renderer.MapRenderer;
import net.blouflin.photography.PhotographyUtil;
import net.blouflin.photography.client.PhotographyHud;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryWrapper;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public record CreatePicturePayload(Integer id, NbtCompound nbtCompound) implements CustomPayload {
    public static final CustomPayload.Id<CreatePicturePayload> ID = CustomPayload.id("photography_create_picture");
    public static final PacketCodec<PacketByteBuf, CreatePicturePayload> CODEC = PacketCodec.of((value, buf) -> buf.writeInt(value.id).writeNbt(value.nbtCompound), buf -> new CreatePicturePayload(buf.readInt(),buf.readNbt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(MinecraftClient client, Integer id, NbtCompound nbtCompound) {

        CompletableFuture<Void> future = new CompletableFuture<>();

        client.execute(() -> {

            RegistryWrapper.WrapperLookup registryLookup = client.player.getRegistryManager();
            MapState mapState = PhotographyUtil.fromNbt(nbtCompound);

            PhotographyHud.setScreenshotFuture(future);

            future.thenRun(() -> {
                //ScreenshotRecorder.saveScreenshot(client.runDirectory, client.getFramebuffer(), (text) -> {});

                PhotographyHud.CAMERA_SCOPE_TO_RENDER = PhotographyHud.CAMERA_SCOPE;
                PhotographyHud.spyglassFlashOpacity = 1.0f;
                PhotographyHud.isTakingPhoto = false;

                ScreenshotRecorder.takeScreenshot(client.getFramebuffer(), (nativeImage -> {
                    int[] pixels = nativeImage.copyPixelsArgb();
                    BufferedImage bufferedImage = new BufferedImage(nativeImage.getWidth(), nativeImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    bufferedImage.setRGB(0, 0, nativeImage.getWidth(), nativeImage.getHeight(), pixels, 0, nativeImage.getWidth());
                    //System.out.println("bufferedImage: "+bufferedImage);
                    nativeImage.close();

                    try {
                        bufferedImage = CreatePicturePayload.crop(bufferedImage, bufferedImage.getHeight(), bufferedImage.getHeight());
                        ScreenshotRecorder.saveScreenshot(client.runDirectory, client.getFramebuffer(), (text) -> {});

                        // TODO Debug
                       // System.out.println("testing3");
                        //System.out.println("bufferedImage: "+bufferedImage);

                        //System.out.println("Printing nbtCompound from CreatePicturePayload: " + nbtCompound);
                        MapState mapState1 = MapRenderer.render(bufferedImage, Image2Map.DitherMode.FLOYD, id, mapState);
                        //System.out.println("Printing nbtCompound from CreatePicturePayload after: " + nbtCompound);

                        // TODO Debug
                        //System.out.println("mapstate1: "+mapState1);

                        SpawnPicturePayload payload = new SpawnPicturePayload(id, nbtCompound);
                        ClientPlayNetworking.send(payload);

                        // TODO Debug
                        //System.out.println("Cropping succeeded!");
                        //System.out.println("payload: "+payload);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));
            });
        });
    }

    public static BufferedImage crop(BufferedImage bufferedImage, int targetWidth, int targetHeight) throws IOException {
        // TODO: This cropping system doesn't work when the image height is greater than the image width
        //System.out.println("bufferedImage width: "+bufferedImage.getWidth() + " bufferedImage height: "+bufferedImage.getHeight());

        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();
        // TODO Debug
        //System.out.println("Height: "+height+" Width: "+width);

        // Coordinates of the image's middle
        int xc = (width - targetWidth) / 2;
        int yc = (height - targetHeight) / 2;
        // TODO Debug
        //System.out.println("xc: "+xc+" yc: "+yc);

        // Crop
        BufferedImage croppedImage = bufferedImage.getSubimage(
                xc,
                yc,
                targetWidth, // width
                targetHeight // height
        );
        // TODO Debug
        //System.out.println("croppedImage: "+croppedImage);

        return croppedImage;
    }
}