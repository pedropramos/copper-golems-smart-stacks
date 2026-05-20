# Copper Golems Smart Stacks

A Minecraft Fabric mod that gives copper golems intelligent item stacking behavior.

## Features

- **Smart Stacking**: Copper golems automatically fill existing item stacks before placing new items, maximizing storage efficiency
- **Adjacent Slot Optimization**: When an existing stack is full, the golem will look for adjacent empty slots (right, left, down, up) to continue storing items

## Requirements

- Minecraft 1.21.11
- Java 21
- Fabric Loader 0.19.2+
- Fabric API 0.141.4+

## Installation

1. Download the latest release JAR file
2. Place it in your `.minecraft/mods/` folder
3. Launch Minecraft and enjoy!

## Building

```bash
./gradlew build
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/github/pedropramos/coppergolemssmartstacks/
│   │       ├── CopperGolemsSmartStacks.java      # Main mod entry point
│   │       └── mixin/
│   │           └── TransportItemsBetweenContainersMixin.java  # Core smart stacking logic
│   └── resources/
│       ├── fabric.mod.json                        # Mod metadata
│       └── copper-golems-smart-stacks.mixins.json # Mixin configuration
```
