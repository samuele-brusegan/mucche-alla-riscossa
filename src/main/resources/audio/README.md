# Audio

Effetti sonori in formato WAV, caricati da `AudioPlayer.play("nome.wav")`.

## File attesi

- `attack.wav` — colpo della mucca
- `hit.wav` — insetto colpito
- `bovine-death.wav` — mucca sconfitta
- `wave-start.wav` — inizio nuova ondata
- `game-over.wav` — fatal error stalla

## Comportamento

Se un WAV manca, `AudioPlayer.play` non fa nulla e non genera errori:
il gioco resta giocabile senza audio.
