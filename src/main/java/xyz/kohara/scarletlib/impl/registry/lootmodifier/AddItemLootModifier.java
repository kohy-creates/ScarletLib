package xyz.kohara.scarletlib.impl.registry.lootmodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddItemLootModifier extends LootModifier {

	public static final Codec<AddItemLootModifier> CODEC = RecordCodecBuilder.create(inst -> codecStart(inst).and(
			inst.group(
					ItemStack.CODEC.fieldOf("item").forGetter(AddItemLootModifier::getItemStack),
					Codec.STRING.listOf().fieldOf("loot_tables").forGetter(AddItemLootModifier::getLootTables),
					Codec.INT.optionalFieldOf("maxStackSize", 1).forGetter(AddItemLootModifier::getMaxStackSize)
			)
	).apply(inst, AddItemLootModifier::new));

	private final ItemStack itemStack;
	private final List<String> lootTables;
	private final int maxStackSize;

	public AddItemLootModifier(LootItemCondition[] conditionsIn, ItemStack itemStack, List<String> lootTables) {
		this(conditionsIn, itemStack, lootTables, 1);
	}

	public AddItemLootModifier(LootItemCondition[] conditionsIn, ItemStack itemStack, List<String> lootTables, int maxStackSize) {
		super(conditionsIn);
		this.itemStack = itemStack;
		this.lootTables = lootTables;
		this.maxStackSize = maxStackSize;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public List<String> getLootTables() {
		return lootTables;
	}

	public int getMaxStackSize() {
		return maxStackSize;
	}

	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		if (getLootTables().contains(context.getQueriedLootTableId().toString())) {
			ItemStack stack = getItemStack().copy();
			stack.setCount(context.getRandom().nextInt(1, getMaxStackSize() + 1));
			generatedLoot.add(stack);
		}
		return generatedLoot;
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC;
	}
}
