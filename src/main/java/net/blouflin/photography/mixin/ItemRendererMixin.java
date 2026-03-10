package net.blouflin.photography.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.block.TranslucentBlock;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
//import net.minecraft.client.render.item.BuiltinModelItemRenderer;
//import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
//import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixUtil;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    /*@Shadow @Final private ItemModels models;

    @Shadow @Final private BuiltinModelItemRenderer builtinModelItemRenderer;

    @Shadow protected abstract void renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices);

    @Shadow @Final private static ModelIdentifier TRIDENT;

    @Shadow @Final private static ModelIdentifier SPYGLASS;

    @Shadow public abstract BakedModel getModel(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity, int seed);

    @Shadow @Final private ItemModelManager itemModelManager;

    @Inject(at = @At("HEAD"), cancellable = true, method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V")
    private void injected(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        boolean bl;
        if (stack.isEmpty()) {
            return;
        }
        matrices.push();
        boolean bl2 = bl = renderMode == ModelTransformationMode.GUI || renderMode == ModelTransformationMode.GROUND || renderMode == ModelTransformationMode.FIXED;
        if (bl) {
            if (stack.isOf(Items.TRIDENT)) {
                model = this.models.getModelManager().getModel(TRIDENT);
            } else if (stack.isOf(Items.SPYGLASS)) {
                model = this.models.getModelManager().getModel(SPYGLASS);
            }
        }
        if (stack.isOf(Items.SPYGLASS) && stack.getComponents().contains(DataComponentTypes.CUSTOM_DATA)) {
            if (stack.getComponents().get(DataComponentTypes.CUSTOM_DATA).toString().contains("isPhotographyCamera:1b")) {
                model = this.models.getModelManager().getModel(Identifier.of("photography","item/camera"));
            }
        }
        model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);
        matrices.translate(-0.5f, -0.5f, -0.5f);
        if (model.isBuiltin() || stack.isOf(Items.TRIDENT) && !bl) {
            this.builtinModelItemRenderer.render(stack, renderMode, matrices, vertexConsumers, light, overlay);
        } else {
            VertexConsumer vertexConsumer;
            BlockItem blockItem;
            Block block;
            Item item;
            boolean bl22 = renderMode != ModelTransformationMode.GUI && !renderMode.isFirstPerson() && (item = stack.getItem()) instanceof BlockItem ? !((block = (blockItem = (BlockItem)item).getBlock()) instanceof TranslucentBlock) && !(block instanceof StainedGlassPaneBlock) : true;
            RenderLayer renderLayer = RenderLayers.getItemLayer(stack, bl22);
            if (usesDynamicDisplay(stack) && stack.hasGlint()) {
                MatrixStack.Entry entry = matrices.peek().copy();
                if (renderMode == ModelTransformationMode.GUI) {
                    MatrixUtil.scale(entry.getPositionMatrix(), 0.5f);
                } else if (renderMode.isFirstPerson()) {
                    MatrixUtil.scale(entry.getPositionMatrix(), 0.75f);
                }
                vertexConsumer = ItemRenderer.getDynamicDisplayGlintConsumer(vertexConsumers, renderLayer, entry);
            } else {
                vertexConsumer = bl22 ? ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint()) : ItemRenderer.getItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint());
            }
            this.renderBakedItemModel(model, stack, light, overlay, matrices, vertexConsumer);
        }
        matrices.pop();
        ci.cancel();
    }

    @Unique
    private static boolean usesDynamicDisplay(ItemStack stack) {
        return stack.isIn(ItemTags.COMPASSES) || stack.isOf(Items.CLOCK);
    }*/
}
