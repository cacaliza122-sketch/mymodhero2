package com.example.superheroes.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class UraniumIsotopeItem extends Item {
	public UraniumIsotopeItem(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("item.superheroes.uranium_isotope.lore.line1").withStyle(ChatFormatting.GREEN, ChatFormatting.ITALIC));
		tooltip.add(Component.translatable("item.superheroes.uranium_isotope.lore.line2").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
		tooltip.add(Component.empty());
		tooltip.add(Component.translatable("item.superheroes.uranium_isotope.lore.usage").withStyle(ChatFormatting.GOLD));
	}
}
