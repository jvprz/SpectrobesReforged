# Piccoli vortici Krawl

**{{VERSION}}**

Valori predefiniti del server; vedi le [impostazioni](settings.md).

Il vortice rileva giocatori vivi non spettatori entro **64 blocchi** e si muove a **5 blocchi/secondo** su terreno piano libero. Camminare non basta più per sfuggire; correre permette di guadagnare distanza. Il contatto genera **tre Krawl uguali**, qualunque oggetto tu tenga. Un avviso sopra la barra rapida indica il suo elemento all’inizio dell’inseguimento.

## Comparsa naturale {#discovery-in-survival}

Nell’Overworld, per giocatori vivi fuori da Creativa/Spettatore a cielo aperto: controllo ogni **20 secondi**, con **50% di probabilità di tentativo**. Ogni tentativo prova fino a **sei posizioni a 32–48 blocchi**. Nessuna comparsa garantita: terreno, giocatori e vortici vicini possono impedirla. `doMobSpawning` deve essere attivo; difficoltà Facile, Normale o Difficile.

Il luogo deve essere caricato, asciutto, dentro il confine e con tre blocchi d’aria sopra un suolo naturale adatto. `vortex_ground` comprende terra, sabbia, pietra, ghiaia, terracotta, neve, ghiaccio, fango e arenaria. Nessun giocatore entro 24 blocchi dal punto e nessun vortice entro 96 blocchi dal giocatore. Nessun caricamento forzato; sottoterra non si attivano tentativi.

Dopo una comparsa riuscita e dopo la conclusione o annullamento del proprio scontro mentre si è connessi: pausa di **60 secondi**. Nessun tentativo durante il proprio scontro. Il limite dei vortici vicini vale anche per altri giocatori. Quelli inattivi svaniscono dopo 20 minuti di tick caricati.

## Krawl per bioma {#krawl-by-biome}

Il **bioma di origine del vortice** determina i nemici, anche usando comandi. Attraversare un confine non li cambia; la scelta viene salvata.

| Clima | Nemici | Esempi |
| --- | --- | --- |
| Caldo | Blovant · Corona | Deserti, savane, badlands e giungle nei tag |
| Temperato | Viblova · Aurora verde | Pianure, foreste; paludi e mangrovie secondo temperatura |
| Freddo | Blova · Flash blu | Biomi innevati, picchi gelati e taiga nei tag |

I tag hanno priorità: **hot → temperate → cold**. Senza tag: **temperatura ≥ 1,0 → Corona; < 0,2 → Flash; altrimenti Aurora**, anche nei biomi di altri mod. Si usa la temperatura base, non meteo, ora o neve dipendente dall’altezza. Liste esplicite:

{{BIOMES}}

## Inseguimento e terreno

Rilevamento entro 64 blocchi; bersaglio mantenuto fino a **80 blocchi** senza uno più vicino rilevato. Velocità 0,25 blocchi/tick a 20 TPS. Supporta gradini di un blocco, lastre, scale e ghiaia. Muri alti, acqua, passaggi stretti e precipizi possono bloccarlo. Se nasce in aria scende al suolo. Il lag rallenta il movimento reale; `randomTickSpeed` non accelera le comparse.

## Incursioni cooperative {#encounter-rules}

Una barra in stile vanilla mostra i Krawl rimasti e il numero di partecipanti. I giocatori vivi in Sopravvivenza/Avventura entrano entro **32 blocchi**: **tre Krawl a giocatore**, fino a **16 giocatori / 48 nemici**. Ogni UUID entra una sola volta, anche tornando o ricaricando. Non si può partecipare a due incursioni attive. Senza tre posizioni sicure, l’ingresso attende. Tutte le ondate mantengono il tipo del bioma d’origine. I compagni restano nascosti per tutti.

Morire da soli annulla subito l’incursione e rimuove i nemici senza bottino. In multigiocatore continua finché sopravvive un alleato. Tornare vivi ripristina la partecipazione mantenendo la penalità. Se tutti i partecipanti registrati sono caduti contemporaneamente, l’incursione è persa. Pacifica, dieci minuti caricati o venti secondi caricati senza partecipanti vivi entro 32 blocchi annullano l’incontro.

Sconfiggete **tutti i nemici di tutte le ondate**. Premi personali consegnati una sola volta al termine; questi Krawl non danno bottino o XP separati. Chi è morto o disconnesso riceve il premio in attesa tornando vivo e connesso. Uscire non genera premi aggiuntivi.

| Giocatori registrati | Minerali C ciascuno | Minerali B ciascuno | XP ciascuno |
| --- | --- | --- | --- |
| 1 | 2 | 0 | 15 |
| 2 | 3 | 0 | 20 |
| 3 | 3 | 1 | 25 |
| 4–16 | 3 | 1 | 30 |

Minerali casuali di potenza, difesa o salute. Morire elimina i B e sottrae un C per morte fino a due, minimo uno. XP divisi per morti + 1, arrotondati al multiplo di cinque inferiore, minimo cinque. Penalità limitata a tre morti. Gli Adult/Evolved idonei ricevono 60 XP di allenamento senza morti, altrimenti 60 / (morti + 1), minimo dieci. Il bottino in eccesso è riservato al destinatario. Nessun blocco distrutto; usate armi Minecraft. Evoluzione Child ancora disattivata.

## Prova rapida {#quick-test}

```text
/summon spectrobesreforged:krawl_vortex ~10 ~ ~
```

Evoca il vortice su terreno libero e passa in Sopravvivenza. Prova camminata/corsa, gradini, ghiaia, confini dei biomi e salvataggio/ricaricamento. Le comparse naturali funzionano nei chunk esistenti.


Un vortice in attesa o all’inseguimento fa rimbalzare le frecce come uno scudo vanilla, con suono di blocco, anche con Perforazione. Sparare non lo distrugge e non avvia un’incursione. Dopo l’inizio del combattimento il controllore invisibile non intercetta più i proiettili.
