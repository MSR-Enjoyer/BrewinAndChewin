package umpaz.brewinandchewin.data.builder;

import net.minecraft.world.item.ItemStack;

// TODO: Reintroduce when Create gets updated.
public class CreatePotionPouringRecipeBuilder {
    private final ItemStack container;
    private final int amount;

    private CreatePotionPouringRecipeBuilder(ItemStack container, int amount) {
        this.container = container;
        this.amount = amount;
    }

//    public static CreatePotionPouringRecipeBuilder createPotionPouringRecipe(ItemLike container, int amount) {
//        return new CreatePotionPouringRecipeBuilder(container.asItem().getDefaultInstance(), amount);
//    }
//
//    public void build(Consumer<FinishedRecipe> consumerIn, ResourceLocation id) {
//        consumerIn.accept(new CreatePotionPouringRecipeBuilder.Result(id, container, amount));
//    }
//
//    public static class Result implements FinishedRecipe {
//        private final ResourceLocation id;
//        private final ItemStack container;
//        private final int amount;
//
//        public Result(ResourceLocation idIn, ItemStack containerIn, int amountIn) {
//            this.id = idIn;
//            this.container = containerIn;
//            this.amount = amountIn;
//        }
//
//        @Override
//        public void serializeRecipeData(JsonObject json) {
//
//            JsonObject objectContainer = new JsonObject();
//            objectContainer.addProperty("item", ForgeRegistries.ITEMS.getKey(container.getItem()).toString());
//            json.add("container", objectContainer);
//            if (container.hasTag()) {
//                objectContainer.addProperty("nbt", container.getTag().toString());
//            }
//
//            json.addProperty("amount", amount);
//
//            JsonArray conds = new JsonArray();
//            conds.add(CraftingHelper.serialize(new ModLoadedCondition("create")));
//            json.add("conditions", conds);
//        }
//
//        @Override
//        public ResourceLocation getId() {
//            return id;
//        }
//
//        @Override
//        public RecipeSerializer<?> getType() {
//            return BnCRecipeSerializers.CREATE_POTION_POURING.get();
//        }
//
//        @Nullable
//        @Override
//        public JsonObject serializeAdvancement() {
//            return null;
//        }
//
//        @Nullable
//        @Override
//        public ResourceLocation getAdvancementId() {
//            return null;
//        }
//    }
}
