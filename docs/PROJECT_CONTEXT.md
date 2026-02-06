# 🍞 Bakery Rhythm - Project Context

This file gives Cursor full context about the game project.

---

# Project Goal

Build and release a small Android mobile game on Google Play.

This is a solo indie project.
Scope must stay MVP and shippable.

---

# Game Summary

Bakery Rhythm is a **cute casual rhythm + tycoon mobile game**.

Player runs a small bakery and earns money by playing a short rhythm mini game to bake bread.

Money is used to buy upgrades that increase earnings.

The game is **fully active**:
Player only earns money by playing the rhythm game.

---

# Target Platform

Android (Jetpack Compose)

Offline game.
No backend.
No login.
No multiplayer.

---

# Game Pillars

1. Cozy and relaxing
2. Short play sessions (20–30 seconds)
3. Simple but satisfying progression
4. Minimal scope MVP

---

# MVP Scope

The first release MUST only include:

### Screens
1. Main_Bakery
2. Rhythm_Game
3. Result

No extra screens.

---

# Core Game Loop

1. Player taps "Start Baking"
2. Rhythm mini game starts
3. Player plays for ~25 seconds
4. Bread quality calculated from accuracy
5. Coins rewarded
6. Player buys upgrades
7. Repeat

Player earns **zero coins while idle**.

---

# Theme

Cute animal bakery.
Friendly and cozy vibe.
No stress / no failure game over.

Main character: Bear baker.

---

# Rhythm Mini Game Spec

### BPM
120 BPM

### Duration
25 seconds

### Lane
Single vertical lane.

### Note Types
- Tap
- Hold
- Swipe (left / right)

### Judgement Windows
Perfect: ±80ms  
Good: ±160ms  
Miss: otherwise

### Score Values
Perfect = 100  
Good = 50  
Miss = 0

### Accuracy Formula
accuracy = earnedScore / maxScore

---

# Bread Quality

Accuracy → price multiplier

95–100% → x1.5  
80–94% → x1.2  
60–79% → x1.0  
<60% → x0.5

---

# Economy (Initial Values)

Base bread price = 100 coins  
Session length = 25 sec

---

# Upgrades (MVP only)

### 1. Recipe Upgrade
Increase bread base price +10%

### 2. Better Oven
Increase Perfect judgement window

### 3. Bigger Oven
Increase breads per play (1 → 5)

---

# UI Screens

## Main_Bakery
Top: Money HUD  
Center: Kitchen stage (character area)  
Primary button: "Start Baking"  
Bottom: 3 upgrade buttons

![Main Bakery.png](img/Main%20Bakery.png)

## Rhythm_Game
Top: Progress bar + timer  
Center: Note lane + judgement line  
Bottom: Score + Combo  
Show judgement text (Perfect / Good / Miss)

![Mobile Rhythm Game Play Screen.png](img/Mobile%20Rhythm%20Game%20Play%20Screen.png)

## Result
Title: Baking Result  
Show: Accuracy %, Coins Earned  
Buttons:
- Play Again
- Back to Bakery

![Game Result Screen.png](img/Game%20Result%20Screen.png)

---

# Tech Stack

Android
Kotlin
Jetpack Compose
Single module app (for now)

Architecture to be decided next.