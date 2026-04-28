package com.example.superheroes.client.hud;

import com.example.superheroes.client.ClientHeroState;
import com.example.superheroes.hero.IronManHero;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public final class JarvisOverlayHud {
	private static final int COLOR_GOLD = 0x66FFC400;
	private static final int COLOR_GOLD_BRIGHT = 0xCCFFB000;
	private static final int COLOR_RED = 0x44E2342B;
	private static final int CORNER_SIZE = 22;

	private JarvisOverlayHud() {
	}

	public static void render(GuiGraphics graphics, DeltaTracker tracker) {
		if (!ClientHeroState.data().hasHero()) {
			return;
		}
		ResourceLocation heroId = ClientHeroState.data().heroId();
		if (!IronManHero.ID.equals(heroId)) {
			return;
		}
		Minecraft mc = Minecraft.getInstance();
		int sw = mc.getWindow().getGuiScaledWidth();
		int sh = mc.getWindow().getGuiScaledHeight();
		long t = System.currentTimeMillis();
		double pulse = 0.5 + 0.5 * Math.sin(t / 500.0);
		int alpha = 0x33 + (int) (0x55 * pulse);
		int pulseColor = (alpha << 24) | (COLOR_GOLD & 0x00FFFFFF);

		drawCorner(graphics, 4, 4, false, false, pulseColor);
		drawCorner(graphics, sw - CORNER_SIZE - 4, 4, true, false, pulseColor);
		drawCorner(graphics, 4, sh - CORNER_SIZE - 4, false, true, pulseColor);
		drawCorner(graphics, sw - CORNER_SIZE - 4, sh - CORNER_SIZE - 4, true, true, pulseColor);

		drawCenterReticle(graphics, sw / 2, sh / 2, t);
	}

	private static void drawCorner(GuiGraphics g, int x, int y, boolean flipX, boolean flipY, int color) {
		int armLong = CORNER_SIZE;
		int armShort = 8;
		int t = 1;
		int x0 = x;
		int y0 = y;
		if (flipX) {
			g.fill(x0, y0 + (flipY ? armLong - t : 0), x0 + armLong, y0 + (flipY ? armLong - t : 0) + t, color);
			g.fill(x0 + armLong - t, y0 + (flipY ? armLong - armShort : 0), x0 + armLong, y0 + (flipY ? armLong : armShort), color);
		} else {
			g.fill(x0, y0 + (flipY ? armLong - t : 0), x0 + armLong, y0 + (flipY ? armLong - t : 0) + t, color);
			g.fill(x0, y0 + (flipY ? armLong - armShort : 0), x0 + t, y0 + (flipY ? armLong : armShort), color);
		}

		int dotX = flipX ? x0 + armLong - 4 : x0 + 2;
		int dotY = flipY ? y0 + armLong - 4 : y0 + 2;
		g.fill(dotX, dotY, dotX + 2, dotY + 2, COLOR_GOLD_BRIGHT);

		int hexX = flipX ? x0 + armLong - 12 : x0 + 6;
		int hexY = flipY ? y0 + armLong - 12 : y0 + 6;
		drawHex(g, hexX, hexY, color);
	}

	private static void drawHex(GuiGraphics g, int x, int y, int color) {
		g.fill(x + 1, y, x + 5, y + 1, color);
		g.fill(x, y + 1, x + 1, y + 5, color);
		g.fill(x + 5, y + 1, x + 6, y + 5, color);
		g.fill(x + 1, y + 5, x + 5, y + 6, color);
	}

	private static void drawCenterReticle(GuiGraphics g, int cx, int cy, long t) {
		int r = 16;
		int dotCount = 24;
		double rotate = (t / 2200.0) % (Math.PI * 2);
		for (int i = 0; i < dotCount; i++) {
			double a = rotate + (Math.PI * 2 * i) / dotCount;
			int dx = (int) Math.round(cx + Math.cos(a) * r);
			int dy = (int) Math.round(cy + Math.sin(a) * r);
			boolean major = i % 4 == 0;
			int c = major ? COLOR_GOLD_BRIGHT : COLOR_GOLD;
			int s = major ? 2 : 1;
			g.fill(dx, dy, dx + s, dy + s, c);
		}

		int gap = 4;
		int len = 3;
		g.fill(cx - gap - len, cy, cx - gap, cy + 1, COLOR_GOLD_BRIGHT);
		g.fill(cx + gap, cy, cx + gap + len, cy + 1, COLOR_GOLD_BRIGHT);
		g.fill(cx, cy - gap - len, cx + 1, cy - gap, COLOR_GOLD_BRIGHT);
		g.fill(cx, cy + gap, cx + 1, cy + gap + len, COLOR_GOLD_BRIGHT);
	}
}
