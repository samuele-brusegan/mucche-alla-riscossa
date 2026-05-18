# Sprites

Questa cartella contiene le immagini PNG usate dal gioco. I file sono caricati
da `MuccheAllaRiscossa.view.assets.ResourceLoader` con il path
`/sprites/<nome>.png`.

## Sprite attesi (64x64 PNG con alfa)

### Difensori
- `vitellino.png` — Vitellino Vedeo
- `mucca.png` — Mucca Beatrice (trappolaia)
- `cornuta.png` — Mucca Cornuta Assunta
- `vacca.png` — Vacca

### Nemici
- `zanzara.png`
- `moscerino.png`
- `moscone.png`
- `tafano.png` — GranTafano (boss)

## Fallback

Se un PNG non è presente, `ResourceLoader` genera al volo un placeholder
quadrato colorato con la lettera iniziale, così il gioco funziona anche prima
che gli asset siano stati creati.
