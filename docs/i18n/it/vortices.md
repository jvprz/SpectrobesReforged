# Piccoli vortici Krawl

Il vortice segue il giocatore vivo non spettatore più vicino entro **24 blocchi**. Il contatto avvia tre Krawl qualunque oggetto tu tenga. Si contrae in **0,8 secondi** e il compagno scanner viene custodito nel Prizmod.

## Generazione naturale

Overworld, Sopravvivenza, difficoltà non Pacifica: controllo ogni minuto con **25 % di probabilità di tentare** una comparsa a **40–64 blocchi**. Terreno caricato, asciutto, libero, esposto al cielo e su terra, sabbia o pietra naturale ammessa. Nessun giocatore entro 32 blocchi o vortice entro 128 dal giocatore che lo scopre. Nessuna comparsa garantita né caricamento forzato. Non compare naturalmente in Creativa; scade dopo 20 minuti di tick caricati.

## Krawl per bioma {#krawl-by-biome}

**Caldo (`hot`): Blovant/Corona; temperato (`temperate`): Viblova/Aurora verde; altri: Blova/Flash blu.** Tag di `spectrobesreforged`; caldo ha priorità se entrambi corrispondono. Scelta unica al contatto.

## Vittoria e annullamento

Sconfiggi i tre e resta entro **32 blocchi**: il proprietario riceve una volta **due minerali C** tra Power, Defense e Health e **15 punti esperienza in sfere**. Sono possibili doppioni; altri possono aiutare. Ottieni il progresso del vortice e il compagno torna normalmente entro un secondo.

20 secondi di assenza rilevata, dieci minuti di combattimento o Pacifica annullano senza ricompensa di vittoria. Contano solo morti confermate. Il salvataggio non duplica ondata o premio. I nemici caricati vengono rimossi, quelli scaricati ripuliti quando tornano a essere elaborati.

Modello di 12 cubi con gas nero/viola e polvere del suolo. Se compare in aria scende; segue superfici e gradini di un blocco senza attraversare muri alti né distruggere blocchi.

```text
/summon spectrobesreforged:krawl_vortex ~3 ~ ~
```
