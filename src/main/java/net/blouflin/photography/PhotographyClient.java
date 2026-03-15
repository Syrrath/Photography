package net.blouflin.photography;

import net.blouflin.photography.client.PhotographyHud;
import net.blouflin.photography.networking.CreatePicturePayload;
import net.blouflin.photography.networking.GetUsingPhotographyCameraPayload;
import net.blouflin.photography.networking.PlayCameraShutterSoundPayload;
import net.blouflin.photography.player.PlayerIsUsingCamera;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.blouflin.photography.Photography.CAMERA_SHUTTER;
import static net.blouflin.photography.Photography.CAMERA_SHUTTER_SOUND;

@Environment(EnvType.CLIENT)
public class PhotographyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_SHUTTER_SOUND, CAMERA_SHUTTER);

        //ModelLoadingPlugin.register(pluginContext -> pluginContext.addModels(Identifier.of("photography","item/camera")));

        ClientPlayNetworking.registerGlobalReceiver(CreatePicturePayload.ID, (payload, handler) -> CreatePicturePayload.receive(handler.client(), payload.id(), payload.nbtCompound()));
        ClientPlayNetworking.registerGlobalReceiver(GetUsingPhotographyCameraPayload.ID, (payload, handler) -> GetUsingPhotographyCameraPayload.receive(handler.client(), payload.player(), payload.isUsingPhotographyCamera(), payload.handUsingPhotographyCamera()));
        ClientPlayNetworking.registerGlobalReceiver(PlayCameraShutterSoundPayload.ID, (payload, handler) -> PlayCameraShutterSoundPayload.receive(handler.client(), payload.globalPos()));

        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("photography", "photography_hud"), this::onHudRender);
        //HudRenderCallback.EVENT.register(this::onHudRender);

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {


            ItemStack photographyCameraStack = null;
            try {
                photographyCameraStack = new ItemStack(Items.SPYGLASS);
                photographyCameraStack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, comp -> comp.update(currentNbt -> {
                    currentNbt.putBoolean("isPhotographyCamera",true);
                }));
            } catch (NullPointerException exception) {
                // ItemStacks no longer exist until a world is loaded
            }

            System.out.println(photographyCameraStack);

            if (((PlayerIsUsingCamera) player).isUsingPhotographyCamera()) {
                player.getItemInHand(InteractionHand.valueOf(PhotographyHud.handUsingPhotographyCamera)).use(world, player, InteractionHand.valueOf(PhotographyHud.handUsingPhotographyCamera));
                return InteractionResult.FAIL;
            } else if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == photographyCameraStack.getItem()) {
                if (Objects.equals(player.getItemInHand(InteractionHand.MAIN_HAND).getComponents().get(DataComponents.CUSTOM_DATA), photographyCameraStack.getComponents().get(DataComponents.CUSTOM_DATA))) {
                    player.getItemInHand(InteractionHand.MAIN_HAND).use(world, player, InteractionHand.MAIN_HAND);
                    return InteractionResult.FAIL;
                }
            }

            return InteractionResult.PASS;


        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            ItemStack photographyCameraStack = null;

            try {
                photographyCameraStack = new ItemStack(Items.SPYGLASS);
                photographyCameraStack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, comp -> comp.update(currentNbt -> {
                    currentNbt.putBoolean("isPhotographyCamera",true);
                }));
            } catch (NullPointerException exception) {
                // ItemStacks no longer exist until a world is loaded
            }

            if (((PlayerIsUsingCamera) player).isUsingPhotographyCamera()) {
                player.getItemInHand(InteractionHand.valueOf(PhotographyHud.handUsingPhotographyCamera)).use(world, player, InteractionHand.valueOf(PhotographyHud.handUsingPhotographyCamera));
                return InteractionResult.FAIL;
            } else if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == photographyCameraStack.getItem()) {
                if (Objects.equals(player.getItemInHand(InteractionHand.MAIN_HAND).getComponents().get(DataComponents.CUSTOM_DATA), photographyCameraStack.getComponents().get(DataComponents.CUSTOM_DATA))) {
                    player.getItemInHand(InteractionHand.MAIN_HAND).use(world, player, InteractionHand.MAIN_HAND);
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack photographyCameraStack = null;

            try {
                photographyCameraStack = new ItemStack(Items.SPYGLASS);
                photographyCameraStack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, comp -> comp.update(currentNbt -> {
                    currentNbt.putBoolean("isPhotographyCamera",true);
                }));
            } catch (NullPointerException exception) {
                // ItemStacks no longer exist until a world is loaded
            }

            if (((PlayerIsUsingCamera) player).isUsingPhotographyCamera()) {
                player.getItemInHand(InteractionHand.valueOf(PhotographyHud.handUsingPhotographyCamera)).use(world, player, InteractionHand.valueOf(PhotographyHud.handUsingPhotographyCamera));
                return InteractionResult.FAIL;
            } else if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == photographyCameraStack.getItem()) {
                if (Objects.equals(player.getItemInHand(InteractionHand.MAIN_HAND).getComponents().get(DataComponents.CUSTOM_DATA), photographyCameraStack.getComponents().get(DataComponents.CUSTOM_DATA))) {
                    player.getItemInHand(InteractionHand.MAIN_HAND).use(world, player, InteractionHand.MAIN_HAND);
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });
    }

    private void onHudRender(GuiGraphicsExtractor context, DeltaTracker renderTickCounter) {
        if (PhotographyHud.isUsingPhotographyCamera) {
            // TODO
            System.out.println("Happening!");
            PhotographyHud.renderPhotographyCameraOverlay(context);
        }
    }
}
