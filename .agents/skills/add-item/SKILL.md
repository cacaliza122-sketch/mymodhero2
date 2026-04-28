---
name: add-item
description: Use when adding a new item to the mod (including consumables, transformation items, mana refills).
---

# Add Item

1. **Класс предмета** в `src/main/java/com/example/superheroes/item/<Name>Item.java`
   - extends `Item` (или `TransformationItem` для смены героя, или специальные базы из `item/`)

2. **Регистрация в `ModItems`**
   ```java
   public static final Item FOO = register("foo", new FooItem(new Item.Properties()...));
   ```

3. **Текстура**: `assets/superheroes/textures/item/<name>.png` (16x16)

4. **Модель**: `assets/superheroes/models/item/<name>.json`
   ```json
   {"parent": "minecraft:item/generated", "textures": {"layer0": "superheroes:item/<name>"}}
   ```

5. **Локализация**: `lang/en_us.json` + `lang/ru_ru.json`
   - Ключ: `"item.superheroes.<name>": "Foo"`

6. **Recipe** (опционально): `data/superheroes/recipes/<name>.json`

7. **Item group**: `ModItemGroups` если нужно в creative tab

8. **Build & verify**: skill `build-mod`

## Специфические подвиды

- **Transformation item** (даёт героя при right-click): расширить `TransformationItem` или скопировать паттерн из `IronManSuitItem`/`HomelanderSuitItem`
- **Mana refill** (восполняет ману): использовать паттерн с `useDuration` + `finishUsingItem` который зовёт `ResourceController.addMana(player, X)`
