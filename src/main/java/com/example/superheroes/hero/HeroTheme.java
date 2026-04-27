package com.example.superheroes.hero;

public record HeroTheme(
		int panelTop,
		int panelBottom,
		int panelBorder,
		int panelHighlight,
		int heroNameColor,
		int energyDark,
		int energyBright,
		int energyGlow,
		int energyIcon,
		int manaDark,
		int manaBright,
		int manaGlow,
		int manaIcon,
		int radialBorderIdle,
		int radialBorderActive,
		int radialKeyActive,
		int radialTextActive,
		int radialGlow
) {
	public static final HeroTheme HOMELANDER = new HeroTheme(
			0xE0181C2A,
			0xD0080A14,
			0x88FFD27A,
			0x33FFFFFF,
			0xFFFFE07A,
			0xFFB35900,
			0xFFFFD060,
			0x55FFE08A,
			0xFFFFC538,
			0xFF3B1F8A,
			0xFFB58CFF,
			0x55C7A8FF,
			0xFFB58CFF,
			0x55FFD27A,
			0xFFFFC538,
			0xFFFFC538,
			0xFFFFF1B0,
			0x55FFD27A
	);

	public static final HeroTheme IRON_MAN = new HeroTheme(
			0xE03A0608,
			0xD01A0204,
			0x99FFD24A,
			0x44FFEEAA,
			0xFFFFE060,
			0xFF7A0000,
			0xFFFF3030,
			0x66FF6060,
			0xFFFF3838,
			0xFF8A4A00,
			0xFFFFC85A,
			0x66FFE090,
			0xFFFFC85A,
			0x66FF8A38,
			0xFFFFD24A,
			0xFFFFD24A,
			0xFFFFEFB0,
			0x66FF8A38
	);

	public static final HeroTheme DEFAULT = HOMELANDER;
}
