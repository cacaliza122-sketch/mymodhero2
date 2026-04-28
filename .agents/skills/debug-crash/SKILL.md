---
name: debug-crash
description: Use when user shares a crash report, stacktrace, or asks to debug a runtime crash of the mod.
---

# Debug Crash

## 1. Найти источник
```bash
ls -la /home/ubuntu/repos/mymodhero/run/crash-reports/
ls -la /home/ubuntu/repos/mymodhero/run/logs/latest.log
```

Если краш в файле — `cat` или `read`. Если пользователь дал текст — работать с ним напрямую.

## 2. Найти первопричину
Искать в порядке:
- `Caused by:` (в самом конце stacktrace) — настоящая причина
- `Exception in thread` — точка падения
- `at com.example.superheroes.*` — наш код в стеке

## 3. Классифицировать
- **Наш код** — открыть указанную строку, исправить
- **Чужой мод** — проверить version compatibility, mixins
- **Vanilla / Mappings** — проверить что используем правильное API для 1.21 (см. skill `loader-gotchas`)
- **Mixin conflict** — типичный симптом: `org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError`

## 4. Типовые краши в этом проекте
- **`IllegalStateException: Receiving network packet on wrong side`** — забыли разделить client/server. Networking регистрировать без `@Environment`
- **`AttachmentSerializer ... NullPointerException`** — `HeroData` сохранил поле, которого больше нет в коде. Чистить `~/.minecraft/saves/<world>/data/playerdata/*.dat` или сделать миграцию в `HeroData.CODEC`
- **`NoSuchMethodError`** — Mojang mappings drift. См. skill `loader-gotchas`

## 5. Fix-flow
- Точечный фикс, не рефакторить
- Если фикс затрагивает много мест — задать вопрос пользователю прежде чем большим diff
- После фикса: `./gradlew build` (skill `build-mod`)
