# RaindanceMC/e2e-harness

- **`e2e-harness`** — Rain's Flexible Modules — end-to-end harness: Plays a plugin the way players do: boots a real Paper server, connects headless bot clients in offline mode and lets a scenario drive them — commands, menu clicks, chat buttons — asserting on what the bots actually receive (inventories, game mode, position, sidebar, chat, titles, boss bars). Generic: a plugin's own scenarios live in its own module and only depend on this.

Part of [RaindanceMC](https://github.com/RaindanceMC); built on [RainsCore](https://github.com/RaindanceMC/RainsCore) and the shared parent in [framework](https://github.com/RaindanceMC/framework). Split out of `rains-flexible-modules` on 16.10.2026, history kept.

## Build

```
mvn install
```

Needs Java 25 and read access to `packages.tstieh.de` (server ids `packages-releases` / `packages-snapshots` in `~/.m2/settings.xml`).

## Versions

A module's version is a property in this repository's root `pom.xml`, a plugin's is `plugin.version` in its own pom. CI publishes every version that is not in the registry yet on the next push to master.
