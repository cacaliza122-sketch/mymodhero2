package com.example.superheroes.item;

import com.example.superheroes.hero.RegulusHero;
import com.example.superheroes.transform.TransformationItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class RegulusSuitItem extends TransformationItem {
	public RegulusSuitItem(Properties properties) {
		super(RegulusHero.ID, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("item.superheroes.regulus_suit.lore.line1").withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC));
		tooltip.add(Component.translatable("item.superheroes.regulus_suit.lore.line2").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
		tooltip.add(Component.empty());
		tooltip.add(Component.translatable("item.superheroes.regulus_suit.lore.usage").withStyle(ChatFormatting.YELLOW));
		tooltip.add(Component.translatable("item.superheroes.regulus_suit.lore.untransform").withStyle(ChatFormatting.GOLD));
	}
}
