# Prizmod und Begleiter

Öffne mit einem beliebigen Prizmod deine Sammlung. Alle Farben teilen dieselben Spielerdaten.

Öffne **Mods → Spectrobes Reforged → Konfiguration**. **R** (Scanner) und **G** (Prizmod) lassen sich in der Tastenbelegung ändern. G funktioniert mit einem ausgerüsteten Prizmod oder einem im Inventar bzw. in der Nebenhand. Keine Auslösung beim Schreiben oder in anderen Bildschirmen.


## Prizmod ausrüsten

**Optionale Integration:** [Curios API 9.5.1+1.21.1 für NeoForge](https://modrinth.com/mod/curios/version/yohfFbgD) fügt den Zusatzslot hinzu. **Ohne Curios** das Prizmod im Inventar/Nebenhand tragen und mit **G** oder Rechtsklick öffnen. Für den Mehrspieler-Slot Curios auf Server und Clients installieren. Vor dem Entfernen von Curios das Item ins normale Inventar legen.

Inventar öffnen und die **Curios-Ausrüstungsschaltfläche** wählen. Eine der sechzehn Farben in den Slot **Prizmod** legen, schließen und **G** drücken. Kein normaler Inventar- oder Rüstungsslot wird belegt. Inventar, Nebenhand und Rechtsklick funktionieren weiterhin.

Curios verwendet ebenfalls standardmäßig G. Bei gleicher Taste öffnet G ohne Prizmod die Ausrüstung und mit getragenem/ausgerüstetem Prizmod die Sammlung. Die Inventarschaltfläche bleibt verfügbar; beide Tasten sind umbelegbar.

Curios speichert die Ausrüstung. Tod folgt den Servereinstellungen und standardmäßig `keepInventory`. Die Sammlung gehört dem Spieler: Verlust/Ablegen des Gegenstands löscht sie nicht. Keine Rüstungspunkte.

## Sammlung und Bedienung

Das Lager zeigt **16 Einträge pro Seite**. Suche nach Namen, filtere nach Element oder Krone und sortiere nach Name oder Level. Klicke eine Karte an. Filter verändern die gespeicherte Reihenfolge nicht.

Die Detailansicht zeigt Stadium, Element, LP, Angriff, Verteidigung, Level, XP, Qualität, Mineralien und Siege. Nutze **Begleiter**, **Ablegen**, **Team** oder **Füttern**. Ein Klick auf ein Inventarmineral verfüttert im Überlebensmodus ein Stück.

Der Begleiterplatz akzeptiert einen lebenden Child. Die sechs Teamplätze akzeptieren Adult/Evolved; derzeit sind nur Child-Arten enthalten. Beide Ziele werden vor einem Tausch geprüft. Entwicklung ist noch nicht verfügbar.

## Qualität und Training

Fossilintegrität gibt **+1% bei 70–84, +2% bei 85–94 und +3% bei 95–100**, pro Basiswert abgerundet. Eine perfekte Ausgrabung verleiht eine dauerhafte goldene Krone hinter dem Fossilnamen und später dem Namen des erweckten Spectrobe. Qualität und Krone werden getrennt gespeichert. Alte Einträge ohne Qualität behalten ihre Werte.

C/B/A/A+-Mineralien geben **10/20/40/60% eines Levels** zusätzlich zu ihren bisherigen Werteänderungen. Andere aktive Mineralien geben 25%; Diamond/Evolve bleiben wirkungslos. Höchstlevel: **100**. Werte wachsen bis zur Artgrenze. Auch bei maximalem Wert kann ein Mineral XP geben. Ohne XP- oder Werteänderung wird es nicht verbraucht.

Children erhalten XP durch Füttern. Ein abgeschlossener Überfall gibt lebenden Adult/Evolved jedes Teilnehmers einen Sieg und bis zu 60 Trainings-XP, nach dessen Tod verringert. Children erhalten keine Kampf-XP. Diese Zähler bereiten spätere Entwicklung vor, aktivieren sie aber nicht.

## Begleiter und Speicherung

Wähle einen lebenden Child und drücke Begleiter. Er folgt dir und hilft beim Scannen. Während [Vortex-Begegnungen](vortices.md) bleibt er sicher verstaut und kehrt danach zurück. Schaden, Training und Sammlung bleiben gespeichert.  Sichere deine Welt vor Updates.

Der ausgewählte Eintrag zeigt sein animiertes 3D-Modell mit der richtigen Farbe neben den Werten. Sammlungskarten behalten ihre Porträts.

## Entwicklungsvorbild: Beyond the Portals

| Spectrobe | Mineralien |
| --- | --- |
| Komainu | 5 |
| Inkana | 4 |
| Vilar | 7 |
| Segu | 4 |
| Spiko | 3 |
| Shakin | 4 |

Nur als Referenz; Entwicklung bleibt unverfügbar.


## Herstellung und Farben

{{PRIZMOD_RECIPE}}

{{DYE_RECIPES}}

{{ITEM_CREDITS}}


Child-Begleiter erleiden keinen Kampf- oder Umgebungsschaden, einschließlich Pfeilen, Feuer, Lava, Stürzen, Ersticken und Ertrinken. Auch gespeicherte LP bleiben geschützt. Beim Öffnen des Prizmod werden verletzte Child oder solche mit null LP aus alten Spielständen wiederhergestellt; Werte, Training und Medaillen bleiben erhalten. Adult/Evolved behalten ihre Schadensregeln. Während Überfällen werden Child weiter verstaut.


## Begleiter- und Team-HUD

Ein ausgerüsteter Child zeigt oben links ein kompaktes Prizmod-Feld mit Porträt, Medaillen, Stufe und EP. Sechs nummerierte Plätze stehen für das Team; zugewiesene Adult/Evolved zeigen Porträt und LP. Leere Plätze aktivieren weder Entwicklung noch Kampf. Die Prizmod-Taste folgt den Einstellungen. Bei F1, Debugansicht, Menüs, Zuschauermodus oder ohne Begleiter/Team verborgen.
