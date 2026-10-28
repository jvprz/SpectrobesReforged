# Inkubator und Erwecken

{{INCUBATOR_RECIPE}}

**Experimentell in 0.6.2-beta.3:** Fossilerz gibt eine rohe Probe. Grabe sie am Tisch aus, bevor der identifizierte Fund in den Inkubator kommt. Alte saubere Fossilien bleiben nutzbar. Siehe [Ausgrabungstest](getting-started.md#excavation). Die Tabelle zeigt mögliche identifizierte Ergebnisse.

[Baue einen Inkubator](getting-started.md#incubator), lasse zwei waagerechte Plätze frei und versorge eine Hälfte mit **Redstone**. Brennstoff und FE-Energie werden nicht verwendet.

Lege ein unterstütztes Fossil ein und starte: **200 Ticks, etwa zehn Sekunden bei 20 TPS**. Ohne Strom pausiert der Vorgang. Der startende Spieler muss für die Übergabe online sein; andernfalls wartet sie. Das Fossil wird erst nach erfolgreicher Erstellung verbraucht. Ein freier Kind-Platz erhält den Eintrag, sonst das Lager.

Die Frequenz im **letzten Tick** bestimmt die Farbvariante: **0–5 → 0**, **6–10 → 1**, **11–16 → 2**. Stille ist ausreichend. Kein echtes Mikrofon: Spielereignisse und spielende Plattenspieler in höchstens **16 Blöcken** wirken auf den Wert. [Chroma](minerals.md#chroma) kann die Farbe später ändern.


## Signalanzeige und Erfassung

Die Anzeige zeigt drei Farbbänder, Signal 0–16, erwartete Variante, Quelle (Ereignisse, Plattenspieler oder beide), Restsekunden und Pause ohne Redstone. Die letzten Sekunden zeigen 3–2–1 mit lokalen Tönen. Nur der startende Spieler kann vor Abschluss stoppen; das Fossil bleibt erhalten.

Neuere leisere Geräusche ersetzen alte Spitzen. Gleichzeitige Ereignisse verwenden unabhängig von der Reihenfolge den höchsten Wert. Ein kurzer Filter glättet Übergänge; das letzte erfasste Signal bestimmt weiter die Farbe. Ereignisse von Inkubatorblöcken werden ignoriert. Entfernung und Vorrang der stärksten Quelle bleiben erhalten. Gemessen wird Minecraft-Ereignisstärke, kein Mikrofon und keine echten Hertz.

Die Darstellung orientiert sich an Anzeige und Countdown des BTP-Handbuchs. Zehn Sekunden Dauer und Erwecken in Stille bleiben erhalten; kein Farbband ist für Erfolg erforderlich.
