# Kleine Krawl-Wirbel

**{{VERSION}}**

Server-Standardwerte; siehe [Einstellungen](settings.md).

Der Wirbel erkennt lebende Nicht-Zuschauer in **64 Blöcken** Entfernung und bewegt sich auf freiem, ebenem Boden mit **5 Blöcken/Sekunde**. Gehen reicht nicht mehr zur Flucht; Sprinten schafft Abstand. Berührung startet **drei Krawl je Teilnehmer**, unabhängig vom gehaltenen Gegenstand. Eine Aktionsleistenmeldung nennt das Element beim Verfolgungsbeginn.

## Natürliches Auftreten {#discovery-in-survival}

In der Oberwelt erfolgt für lebende Spieler außerhalb Kreativ/Zuschauer unter freiem Himmel alle **20 Sekunden** eine Prüfung mit **50 % Versuchschance**. Bis zu **sechs Stellen in 32–48 Blöcken Entfernung** werden geprüft. Kein garantierter Spawn: Gelände, Spieler und vorhandene Wirbel können ihn verhindern. `doMobSpawning` muss aktiv sein; Schwierigkeit Leicht, Normal oder Schwer.

Die Stelle muss geladen, trocken, innerhalb der Weltgrenze und drei Blöcke hoch frei sein. Natürliche Böden über `vortex_ground`: Erde, Sand, Stein, Kies, Terrakotta, Schnee, Eis, Schlamm und Sandstein. Kein Spieler unter 24 Blöcken zur Stelle, kein Wirbel innerhalb 96 Blöcken zum suchenden Spieler. Kein erzwungenes Laden. Unterirdische Spieler lösen keine Suche aus.

Nach erfolgreichem Spawn sowie nach Abschluss/Abbruch eines eigenen Kampfes bei verbundenem Besitzer gilt eine Pause von **60 Sekunden**. Während des eigenen Kampfes keine Suche. Der Abstand zu vorhandenen Wirbeln gilt auch für andere Spieler. Unbenutzte Wirbel verschwinden nach 20 Minuten geladener Ticks.

## Krawl nach Biom {#krawl-by-biome}

Das **Ursprungsbiom des Wirbels** legt die Gegner fest, auch bei beschworenen Wirbeln. Ein Biomwechsel ändert sie nicht; die Auswahl wird gespeichert.

| Klima | Gegner | Beispiele |
| --- | --- | --- |
| Heiß | Blovant · Corona | Wüsten, Savannen, Badlands und markierte Dschungel |
| Gemäßigt | Viblova · Aurora grün | Ebenen, Wälder; Sümpfe und Mangroven nach Temperatur |
| Kalt | Blova · Flash blau | Schneebiome, vereiste Gipfel und markierte Taiga |

Tags haben Vorrang: **hot → temperate → cold**. Ohne passenden Tag: **Temperatur ≥ 1,0 → Corona; < 0,2 → Flash; sonst Aurora**, auch für Mod-Biome. Maßgeblich ist die Grundtemperatur, nicht Wetter, Tageszeit oder höhenabhängiger Schnee. Explizite Listen:

{{BIOMES}}

## Verfolgung und Gelände

Erkennung bis 64 Blöcke; ein Ziel bleibt bis **80 Blöcke** erhalten, sofern kein näheres erkannt wird. 0,25 Blöcke/Tick bei 20 TPS. Ein-Block-Kanten, Stufen, Treppen und Kies werden unterstützt. Hohe Mauern, Wasser, enge Durchgänge und Klippen können blockieren. In der Luft erzeugte Wirbel sinken zu Boden. Lag verlangsamt die Echtzeitbewegung; `randomTickSpeed` beschleunigt keine Spawns.

## Kooperative Überfälle {#encounter-rules}

Eine Leiste im Vanilla-Stil zeigt verbleibende Krawl und Teilnehmerzahl. Lebende Spieler im Überlebens-/Abenteuermodus treten innerhalb von **32 Blöcken** bei: **drei Krawl je Spieler**, maximal **16 Spieler / 48 Gegner**. Jede UUID wird nur einmal aufgenommen, auch nach Verlassen, Rückkehr oder Neuladen. Zwei aktive Überfälle gleichzeitig sind ausgeschlossen. Fehlen drei sichere Plätze, wartet der Beitritt. Alle Wellen behalten den Krawl-Typ des Ursprungsbioms. Begleiter bleiben für alle Teilnehmer verborgen.

Ein Tod im Einzelspiel beendet den Überfall sofort und entfernt Gegner ohne Beute. Mehrspieler können weitermachen, solange ein Verbündeter überlebt. Lebend zurückzukehren ermöglicht die Teilnahme erneut, behält aber den Todesabzug. Sind alle registrierten Teilnehmer gleichzeitig gefallen, ist der Überfall verloren. Friedlich, zehn geladene Minuten oder zwanzig geladene Sekunden ohne lebenden Teilnehmer innerhalb von 32 Blöcken brechen ebenfalls ab.

Besiegt **alle Gegner sämtlicher Beitrittswellen**. Persönliche Belohnungen werden einmal am Ende vergeben; diese Krawl hinterlassen keine eigene Beute oder EP. Tote oder getrennte Spieler erhalten ausstehende Belohnungen, sobald sie wieder lebend online sind. Verlassen erzeugt keinen zusätzlichen Anspruch.

| Registrierte Spieler | C-Mineralien je Person | B-Mineralien je Person | EP je Person |
| --- | --- | --- | --- |
| 1 | 2 | 0 | 15 |
| 2 | 3 | 0 | 20 |
| 3 | 3 | 1 | 25 |
| 4–16 | 3 | 1 | 30 |

Zufällige Kraft-, Verteidigungs- oder Gesundheitsmineralien. Tod entfernt B und zieht je Tod ein C ab, höchstens zwei (mindestens ein C). EP werden durch Tode + 1 geteilt, auf ein Vielfaches von fünf abgerundet, mindestens fünf. Maximal drei Tode zählen. Berechtigte Adult/Evolved erhalten 60 Trainings-EP ohne Tod, sonst 60 / (Tode + 1), mindestens zehn. Inventarüberlauf bleibt für den Empfänger reserviert. Keine Blockzerstörung; Minecraft-Waffen verwenden. Child-Entwicklung bleibt deaktiviert.

## Schnelltest {#quick-test}

```text
/summon spectrobesreforged:krawl_vortex ~10 ~ ~
```

Den Wirbel auf freiem Gelände beschwören und auf Überleben wechseln. Gehen/Sprinten, Kanten, Kies, Biomgrenzen und Speichern/Laden testen. Natürliche Spawns funktionieren in vorhandenen Chunks.


Ein wartender oder verfolgender Wirbel lässt Pfeile wie ein Vanilla-Schild mit Blockgeräusch abprallen, auch mit Durchschuss. Ein Schuss zerstört ihn nicht und startet keinen Überfall. Nach Kampfbeginn fängt der unsichtbare Controller keine Projektile mehr ab.
