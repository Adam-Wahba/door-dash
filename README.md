# 🚪 Monsters, Inc.: Screams & Stairs

> A fully-featured competitive board game built in Java with a custom JavaFX GUI, original artwork, background music, and sound effects — developed as a university project.

---

## 🎮 Overview

**Screams & Stairs** is a 2-player strategy board game set in the world of Monsters Inc. Two monsters race across a 100-cell board — collecting energy from children's doors, drawing cards, triggering traps, and using unique powerups — to reach Boo's Door (cell 99) with at least **1000 energy** and win.

Think Snakes & Ladders, but with monster abilities, a card system, role-based energy mechanics, and a fully animated GUI.

---

## ✨ Features

- 🎨 **Fully custom JavaFX GUI** — animated board, monster portraits, status icons, and cell icons
- 🎵 **Audio system** — background music for menu and gameplay, plus 8 sound effects (dice roll, card draw, powerup, win, and more)
- 🧟 **8 unique playable monsters** across 4 types with distinct abilities
- 🃏 **25-card deck** with 5 card types and strategic effects
- 🏁 **100-cell zigzag board** with 6 cell types including conveyor belts, contamination socks, monster cells, and door cells
- 📋 **Live game log** — every action narrated in real time on screen
- 🛡️ **Status effects** — shields, confusion, freezing, momentum, and more
- ✅ **Full test suite** — Milestone 1 & 2 public and private tests

---

## 🧟 Monsters

| Monster | Type | Team | Starting Energy | Special Ability |
|---|---|---|---|---|
| James P. Sullivan | Dynamo | Scarer | 300 | 2× energy gains & losses |
| Mike Wazowski | Dasher | Laugher | 100 | 2× movement speed |
| Randall Boggs | Schemer | Scarer | 20 | +10 bonus on all energy changes |
| Celia Mae | Multitasker | Laugher | 50 | +200 energy bonus per action |
| Roz | Multitasker | Scarer | 100 | +200 energy bonus per action |
| Fungus | Dasher | Laugher | 50 | 2× movement speed |
| Henry J. Waternoose | Schemer | Scarer | 70 | Chain steal from all monsters |
| Yeti | Dynamo | Laugher | 100 | 2× energy gains & losses |

---

## 🃏 Cards

| Card | Count | Effect |
|---|---|---|
| Position Swap | 4 | Swap places with opponent if behind |
| Contamination Code | 2 | Player returns to cell 0 |
| 2319 Alert | 3 | Opponent returns to cell 0 |
| Small Snatcher | 3 | Steal 50 energy from opponent |
| Sneaky Thief | 2 | Steal 100 energy from opponent |
| Mega Drain | 1 | Steal 150 energy from opponent |
| Super Shield | 5 | Block next negative energy effect |
| Mind Scramble | 3 | Both players swap roles for 2 turns |
| Total Confusion | 2 | Both players swap roles for 3 turns |

---

## 🏆 Win Condition

Reach **cell 99 (Boo's Door)** with **≥ 1000 energy**.

---

## 🏗️ Architecture

```
src/
├── game/
│   ├── controller/     # GameGUI — main controller wiring engine to views
│   ├── engine/
│   │   ├── cards/      # Card, ConfusionCard, EnergyStealCard, ShieldCard, StartOverCard, SwapperCard
│   │   ├── cells/      # Cell, DoorCell, CardCell, MonsterCell, ConveyorBelt, ContaminationSock
│   │   ├── monsters/   # Monster, Dasher, Dynamo, MultiTasker, Schemer
│   │   ├── exceptions/ # Custom exceptions for invalid moves, turns, energy, and CSV format
│   │   ├── interfaces/ # CanisterModifier
│   │   └── dataloader/ # CSV-based board configuration loader
│   ├── view/           # JavaFX views — MainMenu, SideSelection, GameBoard, GameOver, Instructions
│   └── tests/          # Milestone 1 & 2 test suites
```

**Key OOP concepts used:** Abstract classes, inheritance, polymorphism, interfaces, custom exceptions, MVC architecture.

---

## ▶️ How to Run

**Requirements:** Java 11+, JavaFX SDK

```bash
# Clone the repo
git clone https://github.com/Adam-Wahba/door-dash.git
cd door-dash
```

> Open the project in Eclipse: File → Import → Existing Projects into Workspace → Select the DoorDash folder → Run As → Java Application → GameGUI

---

## 👥 Team

Built by Adam Wahba and team as part of a Computer Science university course.

---

*"We scare because we care." – or – "We laugh, that's our path."*

© 2024 Monsters, Inc. Game Lab | University Project | Version 1.1
