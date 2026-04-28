---
name: base-rules
description: Use when starting any work in this repo — base behavior rules from the user about what NOT to do.
triggers: ["model"]
---

# Base Rules

Жёсткие правила поведения в этом репо.

## Не делать без явного запроса
- НЕ создавать `.md` файлы (README, CHANGELOG, docs) без запроса
- НЕ комментировать код если не просили
- НЕ делать косметические правки (форматирование, импорты, переименования) — только по делу
- НЕ описывать структуру проекта в комментариях / правилах — она видна через filesystem
- НЕ использовать deprecated API Minecraft / Fabric / Loom

## Стиль коммитов / PR
- Коммиты на английском, conventional-style: `feat(scope): ...`, `fix(scope): ...`
- Branch: `devin/$(date +%s)-<short-name>` (это уже дефолт у Devin)
- PR title в том же формате что коммит
- PR description — на русском (так общается пользователь), детально перечислить изменения и Review Checklist

## Версионирование релизов
- `gradle.properties` `mod_version` — менять локально, **НЕ коммитить**, после `gh release create` откатывать `git checkout -- gradle.properties`
- Версии: `1.0.X` для фич, `1.0.X-suffix` для кастомных тегов. Сейчас текущая baseline = `1.0.6`.

## Коммуникация
- Пользователь пишет на русском → отвечать на русском, тех.термины оставлять английскими
- Минимизировать токены — короткие, по делу
- При завершении задачи давать ссылку на PR / релиз, не спамить промежуточные апдейты
