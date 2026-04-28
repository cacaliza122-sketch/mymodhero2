package com.example.superheroes.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class IronManReactorItem extends Item {
	public IronManReactorItem(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("item.superheroes.iron_man_reactor.lore.line1").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
		tooltip.add(Component.empty());
		tooltip.add(Component.translatable("item.superheroes.iron_man_reactor.lore.usage").withStyle(ChatFormatting.GOLD));
	}
}
