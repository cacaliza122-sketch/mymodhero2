---
name: add-block
description: Use when adding a new block to the mod. Full pipeline including registration, model, blockstate, texture, loot table.
---

# Add Block

Полный pipeline добавления блока в Fabric 1.21.

1. **Класс блока** в `src/main/java/com/example/superheroes/block/<Name>Block.java`
   - extends `Block` (или `BaseEntityBlock` если есть BE)

2. **Регистрация в `ModBlocks`**
   ```java
   public static final Block FOO = register("foo", new FooBlock(BlockBehaviour.Properties.of()...));
   ```
   Если нужен предмет — также `BlockItem` через `ModItems`.

3. **Модель блока**: `src/main/resources/assets/superheroes/models/block/<name>.json`

4. **Модель предмета** (если есть BlockItem): `models/item/<name>.json` — обычно `{"parent": "superheroes:block/<name>"}`

5. **Blockstate**: `assets/superheroes/blockstates/<name>.json`

6. **Текстура**: `assets/superheroes/textures/block/<name>.png` (16x16, sides если multi-face)

7. **Локализация**: `assets/superheroes/lang/en_us.json` + `lang/ru_ru.json`
   - Ключ: `"block.superheroes.<name>": "Foo"`

8. **Loot table** (если drops != self): `data/superheroes/loot_table/blocks/<name>.json`

9. **Recipe** (если crafting): `data/superheroes/recipes/<name>.json`

10. **Item group**: добавить в `ModItemGroups` если нужно показать в creative

11. **Build & verify**: skill `build-mod`
