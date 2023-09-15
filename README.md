# SSolitaire

just solitaire


## About

School assignment showcase 4/4.

## Features

 - Draw 1 or draw 3 game
 - Displays top 3 high scores (see below for scoring scheme)
 - Re-deal a game without exiting

## How to use

1. Download as zip
2. Unzip into a folder
3. Open it in VSCode or another IDE
4. Run
5. Play solitaire!

### Scoring scheme

Probably not consistent with any other game out there.
Before this, I didn't know the piles had names.

|Move|Points|
|---|---|
|Card moved onto foundation pile|`+15`|
|Card removed from foundation pile|`-20`|
|Card moved from deck to tableau|`+5`|
|Card moved from one tableau to another|`+5`|
|Facedown card turned faceup|`+10`|
|Every 2 passes after 3 passes (4, 6, 8, etc) in *Draw 3*|`-50`|
|Every pass after 2 passes (3, 4, 5, etc) in *Draw 1**|`-50`|