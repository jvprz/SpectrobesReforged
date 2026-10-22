# Incubator and awakening

The incubator turns a supported fossil into a collection entry. It uses **redstone power** and listens to nearby Minecraft game events and playing jukeboxes.

## Set up the machine

1. [Craft an incubator](getting-started.md#incubator), or obtain one from the Creative inventory. The recipe is available from 0.6.0-beta.1 onward.
2. Leave two adjacent horizontal spaces free. The second half is placed to the left relative to your facing direction.
3. Supply a redstone signal to either half.
4. Use the powered machine to open its interface.

The current machine does not use an FE energy storage or fuel inventory. A “no power” message means it is not receiving redstone power.

## Awaken a fossil

1. Put a supported fossil in the single input slot.
2. Click the start button.
3. Keep the incubator powered while progress advances for **200 ticks**, about **10 seconds at 20 TPS**.
4. Keep the starting player online so delivery can complete.

The input cannot be picked up during awakening. Losing power pauses progress. At completion, the result is assigned to the player who started the process. If that player is offline, the full progress waits for delivery to be retried.

The fossil is consumed only after the collection entry is successfully created. A short transfer effect then plays for about 1.5 seconds. An empty child slot receives the new entry first; if occupied, the result goes into storage.

## Control the color

The machine’s captured sound frequency is clamped to **0–16**. The value at the **final awakening tick** determines the result:

| Final captured frequency | Color variant |
| --- | --- |
| 0–5 | 0: original colors |
| 6–10 | 1 |
| 11–16 | 2 |

Silence can produce variant 0; sound is not required for the progress timer to run. This is not microphone input: speaking into a real microphone does not drive the machine.

Nearby game-event sounds produce short impulses. A playing jukebox within **16 blocks** provides a changing frequency influenced by song time and distance. The machine uses the stronger of its current impulse and jukebox values, rather than averaging the full process. Watch the in-game meter near completion.

Chroma minerals can change the variant later; see [minerals](minerals.md#chroma).

## If awakening fails

- **No power:** check the redstone signal at either half.
- **Start unavailable:** insert a fossil and ensure a process is not already active.
- **Species data missing:** use one of the [six supported fossils](spectrobes.md).
- **Full progress but no delivery:** the player who started awakening must be online; the machine must remain loaded and powered.
- **Entry exists but no creature:** check the child slot and HP in the [Prizmod](prizmod.md).
