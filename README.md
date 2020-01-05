# Plants vs Zombies — Java

> A networked Plants vs Zombies clone with GUI, user accounts, in-game chat, and client-server architecture, built in Java.

## Overview

A full-featured recreation of the classic Plants vs Zombies game, developed as a multi-phase Advanced Programming course project. The game features a graphical interface, account management, online chat between players, and a client-server model allowing multiplayer interactions.

### Features

- **Game engine** with plant/zombie creature system, lane-based gameplay, and game logic
- **Graphical UI** built with Java Swing — wallpapers, animated cards, game backgrounds
- **Account system** with registration, login, and player profiles
- **In-game chat** for player communication
- **Client-server architecture** enabling networked gameplay
- **UML diagrams** documenting the system design

## Project Structure

```
PlantsVsZombies/src/
├── main/          # Entry point and app bootstrapping
├── game/          # Core game logic and engine
├── creature/      # Plant and zombie entity definitions
├── line/          # Lane/row management
├── graphic/       # Swing-based UI components (buttons, labels, backgrounds)
│   ├── card/      # In-game cards
│   └── game/      # Game screen rendering
├── page/          # Menu screens (shop, settings, etc.)
├── account/       # User account management
├── chat/          # In-game messaging
├── player/        # Player state and profiles
├── server/        # Server-side networking
├── client/        # Client-side networking
├── util/          # Shared utilities
└── exception/     # Custom exception classes

uml/               # UML class and architecture diagrams
test/              # Test files
```

## Tech Stack

- Java (Swing for GUI)
- Socket-based client-server networking
- Object-oriented design with UML documentation

## Development Phases

The project was built incrementally across three tagged releases:
- **phase0** — Core game logic and console interface
- **phase1** — Account system, networking, and chat
- **phase2** — Full graphical interface and polish

## Team

| Name | Student Number |
|------|---------------|
| Hamidreza Kalbasi | 98109656 |
| Mehrshad Mirmohammadi | 98109634 |
| Amirmohammad Imani | 98109689 |

*Advanced Programming Course, Spring 2020*
