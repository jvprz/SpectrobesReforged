# Einstellungen und Server

**{{VERSION}}**

Öffne **Mods → Spectrobes Reforged → Konfiguration**. **R** (Scanner) und **G** (Prizmod) lassen sich in der Tastenbelegung ändern. G funktioniert mit einem ausgerüsteten Prizmod oder einem im Inventar bzw. in der Nebenhand. Keine Auslösung beim Schreiben oder in anderen Bildschirmen.

## Persönliche Einstellungen

| Einstellung | Standard | Wirkung |
| --- | --- | --- |
| Wirbelpartikel | Hoch | Ändert nur die Partikeldichte. Minecraft-Partikeleinstellungen gelten weiterhin. |
| Effektlautstärke (%) | 100% | Lautstärke der Mod-Effekte: Scanner, Erwecken, Krawl und Kämpfe. Minecraft-Kategorielautstärken gelten weiterhin. |
| Scanner-Signale | Bild und Ton | Visuelle Signale, Ton oder beides. Ändert weder Reichweite noch Serverregeln. |
| Ausgrabungserklärungen | Ein | Zeigt ausführliche Werkzeugtipps. Namen, Reparaturen, Warnungen und Status bleiben sichtbar. |
| Weniger Oberflächenbewegung | Aus | Reduziert bewegte Dekoration, Ausgrabungssplitter und die Prizmod-Vorschau. Fortschritt und Warnungen bleiben sichtbar. |

## Welt-/Servereinstellungen

Die Standardwerte entsprechen dem Wirbel-Leitfaden. Der Server bestimmt die Regeln für alle; Clients können sie nicht überschreiben. Einzelspielerwelten besitzen ebenfalls einen Server. Gültiges Gelände, nicht Friedlich und `doMobSpawning` bleiben erforderlich.

| Einstellung | Standard | Wirkung |
| --- | --- | --- |
| Natürliche Wirbel | Ein | Steuert natürliche Wirbel dieser Welt. Per Befehl beschworene Wirbel bleiben verfügbar. |
| Suchintervall (Sekunden) | 20 | Spielsekunden zwischen Prüfungen bei 20 TPS. Eine Prüfung garantiert keinen Spawn. |
| Versuchswahrscheinlichkeit (0–1) | 0.5 (50%) | 0 verhindert erfolgreiche natürliche Versuche; 0,5 = 50 %; 1 versucht jeden zulässigen Check. |
| Persönliche Pause (Sekunden) | 60 | Pause nach erfolgreichem Spawn und beendetem/abgebrochenem Kampf. Vorhandene Wirbel bleiben. |
| Wirbelgeschwindigkeit (Blöcke/Sekunde) | 5 | Geschwindigkeit auf ebenem freiem Boden bei 20 TPS. Der Server legt sie für alle fest. |
| Geladene Wirbel pro Dimension | 32 | Ab diesem Wert geladener Wirbel stoppt die natürliche Suche, aktive Kämpfe eingeschlossen. Löscht keine Entitäten. |

## Dateien

Persönlich: `config/spectrobesreforged-client.toml`, pro Spieler.

Server: `<Welt>/serverconfig/spectrobesreforged-server.toml`. Auf dedizierten Servern die Datei dieser Welt bearbeiten. NeoForge synchronisiert die Werte; nach manuellen Änderungen zuverlässig neu starten. Ein niedrigeres Limit löscht keine vorhandenen Wirbel; Befehlsbeschwörungen bleiben möglich.

## Mehrspieler

**Gleiche Mod-Version auf Server und Clients**, Minecraft 1.21.1, NeoForge, GeckoLib und Java 21. Beta.12 verwendet Netzwerkprotokoll 6, nicht mit früheren Protokollen kompatibel. Welt-Speicherdaten bleiben erhalten.

Der Server prüft Prizmod-Besitz, Eigentümer, Auswahl, Zutaten und Maschinenentfernung. Öffnen, Verschieben, Füttern und Scannen sind begrenzt; Scanner-Spam verlängert die Dauer nicht unbegrenzt. Sammlungen bleiben persönlich; Ausgrabungstische unterstützen bis zu 16 kooperative Spieler.

Reichweiten und Krawl-Daten werden beim Beitritt und `/reload` synchronisiert, ohne bestehende Krawl zu heilen. Abmelden leert nur den lokalen Cache. Modelle/Texturen erfordern weiterhin passende Ressourcen; Datapacks übertragen keine Grafikdateien.

## Prüfung

Entwicklungstests: Serverstart ohne Client, Daten und Konfiguration, Krawl-Erstellung, Wirbelabsenkung, Prizmod-Besitz und getrennte Anfragelimits. Eine echte Sitzung mit zwei verbundenen menschlichen Clients bleibt ein zusätzlicher Spieltest.


**Optionale Integration:** [Curios API 9.5.1+1.21.1 für NeoForge](https://modrinth.com/mod/curios/version/yohfFbgD) fügt den Zusatzslot hinzu. **Ohne Curios** das Prizmod im Inventar/Nebenhand tragen und mit **G** oder Rechtsklick öffnen. Für den Mehrspieler-Slot Curios auf Server und Clients installieren. Vor dem Entfernen von Curios das Item ins normale Inventar legen.

Inventar öffnen und die **Curios-Ausrüstungsschaltfläche** wählen. Eine der sechzehn Farben in den Slot **Prizmod** legen, schließen und **G** drücken. Kein normaler Inventar- oder Rüstungsslot wird belegt. Inventar, Nebenhand und Rechtsklick funktionieren weiterhin.

Curios verwendet ebenfalls standardmäßig G. Bei gleicher Taste öffnet G ohne Prizmod die Ausrüstung und mit getragenem/ausgerüstetem Prizmod die Sammlung. Die Inventarschaltfläche bleibt verfügbar; beide Tasten sind umbelegbar.


[Kooperative Überfälle](vortices.md#encounter-rules) · [Kooperative Ausgrabung](getting-started.md#cooperative-excavation).


## HUD und Beta-Feedback

Öffne **Mods → Spectrobes Reforged → Config → HUD anpassen**. Ziehe die Vorschau und skaliere sie mit dem Mausrad (50–150%). HUD und Teamplätze lassen sich ausblenden und zurücksetzen. Position und Größe werden lokal gespeichert. Standard: 85%. Der Discord-Knopf öffnet nach Bestätigung eine Einladung, mit einer lokalen Beta-Nachricht beim Verbinden, ohne Nachricht an andere Spieler und ohne Wiederholung beim Wiederbeleben oder Dimensionswechsel. Das vertikale HUD zeigt eine Teamspalte; − / + oder Mausrad ändern die Größe.
