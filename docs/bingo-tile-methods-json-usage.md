# Bingo Tile Methods JSON Usage

File: `docs/bingo-tile-methods-2025-07_to_2026-03.json`

## Structure
- `tiles` (List): array of all tile method records.
- `by_tile` (Map/Dictionary): direct lookup by exact tile name.
- `by_match_key` (Map/Dictionary): grouped lookup for similar primary goals/methods.

## Tile Record Fields
- `tile`: tile name.
- description: normalized goal text from the community guide Description column (month-aware latest for that tile variant).
- primary_method: normalized recommended method.
- `key_prerequisites`: prerequisites needed for the method.
- `routing_tags`: broad route categories.
- `seen`: months where this tile was seen.
- `latest_seen` / `latest_seen_iso`: newest occurrence.
- `match_keys`: similarity keys for matching new-board goals.

## Suggested Lookup Flow For New Boards
1. Normalize the new tile text to lowercase.
2. Infer one or more `match_keys` from goal/method terms (e.g. `enderman`, `jacob`, `commission`, `f1 score`, `angler`, `visitor`).
3. Query `by_match_key[<key>]`.
4. Pick the first results (already sorted by newest `latest_seen_iso`).
5. Use `key_prerequisites` + `primary_method` as the routing seed.

## Common Similarity Keys
- `enderman_combat`
- `dungeon_score`
- `dungeon_item_progression`
- `garden_visitors`
- `jacob_contest`
- `fishing_speed_scc`
- `fishing_minions`
- `dwarven_commissions`
- `hotm_mining`
- `crystal_hollows`
- `slayer_revenant`
- `slayer_tarantula`
- `foraging_treecap`
- `pet_progression`
- `accessory_power`
- `alchemy_brewing`
- `minion_throughput`
- `rift_progression`

## Notes
- `by_match_key` entries include `primary_method` and `key_prerequisites` so routing logic can use them directly.
- If a board tile has an exact name match, prefer `by_tile` first.

