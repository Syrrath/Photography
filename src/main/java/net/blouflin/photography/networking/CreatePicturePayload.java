package net.blouflin.photography.networking;

import net.blouflin.image2map.Image2Map;
import net.blouflin.image2map.renderer.MapRenderer;
import net.blouflin.photography.client.PhotographyHud;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryWrapper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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

        System.out.println("running CreatePicturePayload");

        CompletableFuture<Void> future = new CompletableFuture<>();

        client.execute(() -> {

            RegistryWrapper.WrapperLookup registryLookup = client.player.getRegistryManager();
            MapState mapState = MapState.fromNbt(nbtCompound, registryLookup);

            PhotographyHud.CAMERA_SCOPE_TO_RENDER = PhotographyHud.CAMERA_SCOPE_CLEAR;

            PhotographyHud.setScreenshotFuture(future);

            future.thenRun(() -> {
                NativeImage nativeImage = ScreenshotRecorder.takeScreenshot(client.getFramebuffer());
                ScreenshotRecorder.saveScreenshot(client.runDirectory, client.getFramebuffer(), (text) -> {});

                PhotographyHud.CAMERA_SCOPE_TO_RENDER = PhotographyHud.CAMERA_SCOPE;
                PhotographyHud.spyglassFlashOpacity = 1.0f;
                PhotographyHud.isTakingPhoto = false;

                System.out.println("testing1");
                int[] pixels = nativeImage.copyPixelsArgb();
                BufferedImage bufferedImage = new BufferedImage(nativeImage.getWidth(), nativeImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                bufferedImage.setRGB(0, 0, nativeImage.getWidth(), nativeImage.getHeight(), pixels, 0, nativeImage.getWidth());
                System.out.println("testing1");

                try {
                    bufferedImage = CreatePicturePayload.crop(bufferedImage, bufferedImage.getHeight(), bufferedImage.getHeight());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("bufferedImage: "+bufferedImage);
                MapState mapState1  = MapRenderer.render(bufferedImage, Image2Map.DitherMode.FLOYD, id, mapState);
                System.out.println("mapstate1: "+mapState1);

                SpawnPicturePayload payload = new SpawnPicturePayload(id, nbtCompound);
                ClientPlayNetworking.send(payload);
                System.out.println("Cropping succeeded!");

//                System.out.println("testing1");
//                System.out.println("bufferedImage: "+bufferedImage);
//
//                MapState mapState1  = MapRenderer.render(bufferedImage, Image2Map.DitherMode.FLOYD, id, mapState);
//                System.out.println("mapstate1: "+mapState1);
//
//                SpawnPicturePayload payload = new SpawnPicturePayload(id, nbtCompound);
//                ClientPlayNetworking.send(payload);
//                System.out.println("payload: "+payload);

//                try {
//                    //byte[] imageBytes = nativeImage.getBytes();
//                    //byte[] imageBytes = nativeImage.makePixelArray()
//                    //BufferedImage bufferedImage = ImageIO.read(imageBytes)
//
//                    //System.out.println("nativeImage: "+nativeImage);
//                    //byte[] imageBytes = nativeImage.getFormat().toString().getBytes();
//                    //int[] imageBytes = convertImageTo
//                    //BufferedImage bufferedImage = ImageIO.read(ScreenshotRecorder.SCREENSHOTS_DIRECTORY.getBytes(nativeImage));
////                    System.out.println("imageBytes: "+imageBytes);
//                    //BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
//                    //bufferedImage = CreatePicturePayload.crop(bufferedImage, bufferedImage.getHeight(), bufferedImage.getHeight());
//
//                    int[] pixels = nativeImage.copyPixelsArgb();
//                    BufferedImage bufferedImage = new BufferedImage(nativeImage.getWidth(), nativeImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
//                    bufferedImage.setRGB(0, 0, nativeImage.getWidth(), nativeImage.getHeight(), pixels, 0, nativeImage.getWidth());
//                    bufferedImage = CreatePicturePayload.crop(bufferedImage, bufferedImage.getHeight(), bufferedImage.getHeight());
//
//                    System.out.println("testing1");
//                    System.out.println("bufferedImage: "+bufferedImage);
//
//                    MapState mapState1  = MapRenderer.render(bufferedImage, Image2Map.DitherMode.FLOYD, id, mapState);
//                    System.out.println("mapstate1: "+mapState1);
//
//                    SpawnPicturePayload payload = new SpawnPicturePayload(id, nbtCompound);
//                    ClientPlayNetworking.send(payload);
//                    System.out.println("payload: "+payload);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            });
        });
    }

    public static BufferedImage crop(BufferedImage bufferedImage, int targetWidth, int targetHeight) throws IOException {
        System.out.println("bufferedImage width: "+bufferedImage.getWidth() + " bufferedImage height: "+bufferedImage.getHeight());

        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();
        System.out.println("Height: "+height+" Width: "+width);

        // Coordinates of the image's middle
        int xc = (width - targetWidth) / 2;
        int yc = (height - targetHeight) / 2;
//        int xc = width / 2;
//        int yc = height / 2;
        System.out.println("xc: "+xc+" yc: "+yc);

        // Crop
//        BufferedImage croppedImage = bufferedImage.getSubimage(
//                xc,
//                height,
//                targetWidth, // width
//                targetHeight // height
//        );
        BufferedImage croppedImage = bufferedImage.getSubimage(
                xc,
                yc,
                targetWidth, // width
                targetHeight // height
        );
        System.out.println("croppedImage: "+croppedImage);
        return croppedImage;
    }
}