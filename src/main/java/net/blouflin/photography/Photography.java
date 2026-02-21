package net.blouflin.photography;

import net.blouflin.photography.networking.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Photography implements ModInitializer {
	public static final String MOD_ID = "Photography";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Identifier CAMERA_SHUTTER_SOUND = Identifier.of("photography","camera_shutter");
	public static SoundEvent CAMERA_SHUTTER = SoundEvent.of(CAMERA_SHUTTER_SOUND);

	@Override
	public void onInitialize() {
		LOGGER.info("Photography mod (by BlouFlin) loaded !");

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(this::addItemsToCreativeTab);

		//Registry.register(Registries.SOUND_EVENT, CAMERA_SHUTTER_SOUND, CAMERA_SHUTTER);

		PayloadTypeRegistry.playS2C().register(CreatePicturePayload.ID, CreatePicturePayload.CODEC);
		PayloadTypeRegistry.playS2C().register(GetUsingPhotographyCameraPayload.ID, GetUsingPhotographyCameraPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(PlayCameraShutterSoundPayload.ID, PlayCameraShutterSoundPayload.CODEC);

		PayloadTypeRegistry.playC2S().register(CreateMapStatePayload.ID, CreateMapStatePayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(CreateMapStatePayload.ID, (payload, handler) -> CreateMapStatePayload.receive(handler.player()));

		PayloadTypeRegistry.playC2S().register(SpawnPicturePayload.ID, SpawnPicturePayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(SpawnPicturePayload.ID, (payload, handler) -> SpawnPicturePayload.receive(handler.player(), payload.id(), payload.nbtCompound()));

		PayloadTypeRegistry.playC2S().register(SetUsingPhotographyCameraPayload.ID, SetUsingPhotographyCameraPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(SetUsingPhotographyCameraPayload.ID, (payload, handler) -> SetUsingPhotographyCameraPayload.receive(handler.player(), payload.isUsingPhotographyCamera(), payload.handUsingPhotographyCamera()));
	}

	private void addItemsToCreativeTab(FabricItemGroupEntries entries) {
		ItemStack photographyCamera = new ItemStack(Items.SPYGLASS);
		photographyCamera.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(currentNbt -> {
			currentNbt.putBoolean("isPhotographyCamera",true);
		}));
        photographyCamera.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(), List.of(), List.of("isPhotographyCamera"), List.of()));
		photographyCamera.set(DataComponentTypes.ITEM_NAME, Text.literal("Camera"));
		entries.addAfter(Items.MAP, photographyCamera);

		ItemStack photographicPaper = new ItemStack(Items.FILLED_MAP);
		photographicPaper.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(currentNbt -> {
			currentNbt.putBoolean("isPhotographyEmptyMap",true);
		}));
        photographicPaper.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(), List.of(), List.of("isPhotographyEmptyMap"), List.of()));
		photographicPaper.set(DataComponentTypes.ITEM_NAME, Text.translatableWithFallback("photography:empty_map", "Photographic Paper"));
		entries.addBefore(Items.WRITABLE_BOOK, photographicPaper);
	}

//    private void countMaps(MapIdComponent id) {
//        // create a list of maps made using the mod with an associated timestamp
//    }
}