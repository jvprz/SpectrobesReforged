# Erste Schritte

Benötigt werden **Minecraft 1.21.1**, **NeoForge 21.1.219+**, **GeckoLib 4.8.3+ für NeoForge 1.21.1** und **Java 21**. Lade den Mod von [CurseForge](https://www.curseforge.com/minecraft/mc-mods/spectrobes-reforged). Lege beide JAR-Dateien in den `mods`-Ordner deiner NeoForge-Instanz. Verwende nur eine Mod-Version und sichere die Welt.

## Ausrüstung herstellen {#craft-your-equipment}

### Weißer Prizmod {#white-prizmod}

{{PRIZMOD_RECIPE}}

Ergibt einen Prizmod. Mit Farbstoff umfärben: 16 Farben, Sammlung bleibt erhalten.

### Inkubator {#incubator}

{{INCUBATOR_RECIPE}}

Ergibt einen Inkubator; Redstone ist erforderlich.

Lasse zwei waagerechte Plätze für den Inkubator frei, versorge ihn mit Redstone, lege ein Komainu-Fossil ein und starte. Nach etwa zehn versorgten Sekunden erscheint der Eintrag im Prizmod. Der Scanner verwendet standardmäßig **R**; die Taste ist änderbar. Rezepte gibt es seit 0.6.0-beta.1. Der Mod folgt der Minecraft-Sprache.

## Grabungstest {#excavation}

**0.6.2-beta.3**

Lege ein Rohfossil oder Rohmineral C/B/A/A+ ein. Klicke dreimal, um die Oberfläche zu öffnen, und entferne die Deckschicht mit dem passenden Werkzeug. Zum Graben klicken und ziehen; feine Schichten und Staub mit dem Gebläse reinigen. Bergung ab **90 %**. **R** schaltet den Scanner um; Die zehn Werkzeuge aus Beyond the Portals erscheinen als Symbole um die mittige Grabungsfläche. **I** wechselt zwischen Grabung und Inventar. Reparatur, Giga und Sprengwerkzeug wirken einmal pro Klick; die Explosion verlangt eine zweite Bestätigung. Werkzeughinweise erscheinen beim Überfahren.

Manuelle Bergung steigert eine eigene Grabungsfertigkeit und erfasst den genauen Fundtyp im Katalog. Giga benötigt einen bereits geborgenen Typ und gibt weder Erfahrung noch Krone. Die Fertigkeit bleibt nach dem Tod erhalten. Vier Reparaturen gehören zum Fund, auch beim Tischwechsel. Zeit, Deckschicht, Gestein, Spannung, Hitze, Gas und Fortschritt bleiben gespeichert. Eine Krone verlangt **100 %, keinen Schaden und höchstens 60 aktive Sekunden**; sie bleibt beim Erwecken gespeichert, ermöglicht aber noch keine Erwachsenenentwicklung.

{{EXCAVATION_RECIPE}}



| Werkzeug | Wirkung |
| --- | --- |
| {{TOOL_DRILL}} | Trägt Gestein ab. Bewege den Zeiger; loslassen oder blasen senkt die Spannung. |
| {{TOOL_BLOWER}} | Entfernt Sand, dünne Schichten, Staub und Gas sicher. Kühlt den Fundort. |
| {{TOOL_RECOVERY}} | Repariert 10 Punkte, vier Anwendungen pro Fund. Stellt eine verlorene Krone nicht wieder her. |
| {{TOOL_PITON}} | Bricht zwei Schichten auf großer Fläche. Freigelegte Objekte vermeiden. |
| {{TOOL_FIRE}} | Schmilzt die Eisschicht. Hitze beobachten und das Gebläse nutzen. |
| {{TOOL_WATER}} | Entfernt die Lavaschicht und kühlt den Arbeitsbereich. |
| {{TOOL_VACUUM}} | Entfernt Wasser, Schlamm, trübe Rückstände und Gas. |
| {{TOOL_BLOWER_S}} | Entfernt Staub, Gas, dünne Ablagerungen und Sand auf der ganzen Fläche. |
| {{TOOL_GIGA}} | Legt bereits von Hand geborgene Arten automatisch frei. Keine Erfahrung oder Krone. |
| {{TOOL_BLAST}} | 50 % sofortige Bergung, 50 % Zerstörung. Keine Erfahrung oder Krone. Zweimal klicken zum Bestätigen. |

Stein, Erde und Deckschichten nutzen Minecraft-Blocktexturen. Heller loser Staub hebt sich ab; der Zeigerhinweis nennt Material und Werkzeug. Ein zerstörtes Fossil oder Mineral wird verbraucht, sein Bild entfernt und ein Verlusthinweis mit Fragmenten angezeigt. Unfertige Grabungen bleiben gespeichert; auch ein Fehlschlag bleibt beim erneuten Öffnen klar erkennbar.

### Geologische Taschen — 0.6.2-beta.6

Umgebungsvorkommen erzeugen nun eine kleine vergrabene Tasche mit einem zentralen Erz. Eis (Flash) nur in verschneiten/gefrorenen Biomen; Sandstein (Corona) nur in der Wüste; roter Sandstein (Corona) in den drei Tafelbergvarianten; Schlamm (Aurora) nur in Sumpf und Mangrovensumpf. Alle vier Familien enthalten C/B/A/A+-Mineralien. Taschen sind 5–7 Blöcke breit und 3–5 hoch, ihr Zentrum liegt 2–6 Blöcke unter der Oberfläche. Nur erlaubter natürlicher Boden wird ersetzt, niemals Luft, Flüssigkeiten, andere Erze oder Stämme. Die zentrale Hülle muss in festen Boden und das passende Biom passen.

| ID | Fósil / Fossil | C / B / A / A+ |
| --- | --- | --- |
| `frozen` | `frozen_flash_fossil_ore` | `frozen_{c,b,a,a_plus}_mineral_ore` |
| `sandy` | `sandy_corona_fossil_ore` | `sandy_{c,b,a,a_plus}_mineral_ore` |
| `red_sandy` | `red_sandy_corona_fossil_ore` | `red_sandy_{c,b,a,a_plus}_mineral_ore` |
| `muddy` | `muddy_aurora_fossil_ore` | `muddy_{c,b,a,a_plus}_mineral_ore` |

Fossilien: 2 Versuche pro Chunk. Mineralien: C 1/2, B 1/4, A 1/12, A+ 1/32. Versuche können scheitern. Nur neue Chunks; normale temperaturabhängige Untergrunderze bleiben bestehen. Rohfunde behalten ihre rote Geologie und alte Grabungen ihre gespeicherten Schichten.


Die zehn Ausgrabungswerkzeuge sind auch als Gegenstände im Kreativ-Werkzeugtab verfügbar (`excavation_drill`, `excavation_blower` usw.). Rezepte und Einsatz als Tischzutaten folgen später; der Tisch stellt seine Werkzeuge weiterhin ohne Inventargegenstände bereit.


{{ITEM_CREDITS}}


## Einstellungen

Öffne **Mods → Spectrobes Reforged → Konfiguration**. **R** (Scanner) und **G** (Prizmod) lassen sich in der Tastenbelegung ändern. G funktioniert mit einem ausgerüsteten Prizmod oder einem im Inventar bzw. in der Nebenhand. Keine Auslösung beim Schreiben oder in anderen Bildschirmen.

[Einstellungen und Server](settings.md).


**Optionale Integration:** [Curios API 9.5.1+1.21.1 für NeoForge](https://modrinth.com/mod/curios/version/yohfFbgD) fügt den Zusatzslot hinzu. **Ohne Curios** das Prizmod im Inventar/Nebenhand tragen und mit **G** oder Rechtsklick öffnen. Für den Mehrspieler-Slot Curios auf Server und Clients installieren. Vor dem Entfernen von Curios das Item ins normale Inventar legen.

Inventar öffnen und die **Curios-Ausrüstungsschaltfläche** wählen. Eine der sechzehn Farben in den Slot **Prizmod** legen, schließen und **G** drücken. Kein normaler Inventar- oder Rüstungsslot wird belegt. Inventar, Nebenhand und Rechtsklick funktionieren weiterhin.


## Kooperative Ausgrabung {#cooperative-excavation}

Öffnet denselben Tisch: Bis zu **16 Spieler** teilen ein Fundstück, Schichten, Staub, Hitze, Integrität und Ergebnis. Farbige Zeiger zeigen andere Spieler, ein Symbol ihr aktives Werkzeug. Ein Werkzeug bleibt während der Benutzung reserviert; verschiedene Werkzeuge können gleichzeitig arbeiten. Loslassen, Werkzeugwechsel, Schließen oder Trennen gibt es frei; unterbrochene Reservierungen verfallen nach etwa einer halben Sekunde.

Es gibt **ein Ergebnis**, das einmal entnommen wird. Jeder berechtigte Helfer erhält einmal Ausgrabungs-EP und Entdeckungsnachweis; Zuschauen zählt nicht. Schließen speichert den Fortschritt. Der Server prüft Reservierungen, Entfernung und Aktionen. [Überfallregeln](vortices.md#encounter-rules).


Zwei verschiedene Spieler müssen erfolgreich Werkzeuge am selben Fundstück benutzen, um das türkise Koop-Andenken ⚒ zu erhalten. Nur Öffnen zählt nicht. Es bleibt am gereinigten Fossil oder Mineral und am erweckten Spectrobe neben einer möglichen Krone. Keine Änderung von Werten, EP oder Belohnungen.


[Gemeinsame Ausgrabung anerkennen](advancements.md): Wer erfolgreich ein Werkzeug am Fundstück benutzt hat und bei der Entnahme lebend, online und höchstens 8 Blöcke entfernt ist, erhält die passenden bestehenden Erfolge für manuelle Ausgrabung, perfekte Funde und Gelände. Jeder berechtigte Helfer erhält eine Entdeckung, den Katalogeintrag und normale Fertigkeits-EP; 10/100 Funde und Stufe 5 bleiben persönliche Schwellen. Auch der Entnehmende erhält Fortschritt. Zuschauen zählt nicht. Reparatur zählt für den Benutzer des Reparaturwerkzeugs; Erwecken und Training bleiben persönlich. Automatische Entnahme gibt keinen manuellen Fortschritt. Weiterhin nur ein Fundstück, kein neuer Koop-Erfolg.


Der neue Tisch richtet sich zum platzierenden Spieler aus. Der gereinigte Fund hat Vorrang in der Vertiefung. Ein Rohfund kann im Eingang warten, wird aber erst nach Entnahme der Ausgabe bearbeitet; ein leerer Tisch zeigt nichts.
