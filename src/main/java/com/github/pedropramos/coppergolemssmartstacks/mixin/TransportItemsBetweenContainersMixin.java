package com.github.pedropramos.coppergolemssmartstacks.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.TransportItemsBetweenContainers;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TransportItemsBetweenContainers.class)
public class TransportItemsBetweenContainersMixin {
	@Unique
    private static final int ROW_WIDTH = 9;

	@Unique
	enum Direction {
		RIGHT, LEFT, DOWN, UP;
	}

	@Unique
    private static int applyDirection(int position, Direction direction, Container container) {
		return switch (direction) {
			case RIGHT -> position % ROW_WIDTH != 8 ? position + 1 : -1;
			case LEFT  -> position % ROW_WIDTH != 0 ? position - 1 : -1;
			case DOWN  -> position + ROW_WIDTH < container.getContainerSize() ? position + ROW_WIDTH : -1;
			case UP    -> position - ROW_WIDTH >= 0 ? position - ROW_WIDTH : -1;
		};
	}

	@WrapMethod(method = "addItemsToContainer")
	private static ItemStack addItemsToContainer(PathfinderMob mob, Container container, Operation<ItemStack> original) {
		ItemStack heldItemStack = mob.getMainHandItem();

		// Phase 1: Start by looking for existing stacks
		for (int i = 0; i < container.getContainerSize(); i++) {
			ItemStack storedItemStack = container.getItem(i);

			if (!storedItemStack.isEmpty() && ItemStack.isSameItemSameComponents(storedItemStack, heldItemStack) && storedItemStack.getCount() < storedItemStack.getMaxStackSize()) {
				int remainingCapacity = storedItemStack.getMaxStackSize() - storedItemStack.getCount();
				int amountToMove = Math.min(remainingCapacity, heldItemStack.getCount());

				storedItemStack.grow(amountToMove);
				heldItemStack.shrink(amountToMove);
				container.setItem(i, storedItemStack);

				if (heldItemStack.isEmpty()) {
					return ItemStack.EMPTY;
				}
			}
		}

		// Phase 2: If there are existing stacks but they're full, look for adjacent empty stacks
		// Try right, left, down, up (in this order)
		for (Direction direction : Direction.values()) {
			for (int i = 0; i < container.getContainerSize(); i++) {
				ItemStack storedItemStack = container.getItem(i);
				if (!storedItemStack.isEmpty() && ItemStack.isSameItemSameComponents(storedItemStack, heldItemStack) && storedItemStack.getCount() == storedItemStack.getMaxStackSize()) {
					int adjacentPosition = applyDirection(i, direction, container);
					if (adjacentPosition == -1) continue;

					if (container.getItem(adjacentPosition).isEmpty()) {
						container.setItem(adjacentPosition, heldItemStack.copy());
						heldItemStack.setCount(0);
						return ItemStack.EMPTY;
					}
				}
			}
		}

		// Phase 3: Fallback to original implementation
		return original.call(mob, container);
	}
}