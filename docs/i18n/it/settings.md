# Impostazioni e server

**{{VERSION}}**

Apri **Mod → Spectrobes Reforged → Configurazione**. Riassegna **R** (scanner) e **G** (Prizmod) nei comandi. G funziona con un Prizmod equipaggiato o nell’inventario/mano secondaria. I tasti non agiscono mentre scrivi o usi altre schermate.

## Preferenze personali

| Impostazione | Predefinito | Effetto |
| --- | --- | --- |
| Particelle dei vortici | Alta | Cambia solo la densità delle particelle. Restano validi i limiti di Minecraft. |
| Volume effetti (%) | 100% | Volume degli effetti del mod: scanner, risveglio, Krawl e battaglie. Si applicano anche i volumi delle categorie Minecraft. |
| Segnali dello scanner | Visivi e suoni | Segnali visivi, suoni o entrambi. Non cambia portata o regole del server. |
| Spiegazioni di scavo | Attivo | Mostra spiegazioni dettagliate. Nomi, riparazioni, avvisi e stato restano visibili. |
| Riduci movimento delle interfacce | Disattivo | Riduce decorazioni mobili, frammenti e movimento dell’anteprima Prizmod. Progresso e avvisi restano visibili. |

## Impostazioni mondo e server

I valori predefiniti corrispondono alla guida dei vortici. Il server decide le regole per tutti; il client non può sovrascriverle. Anche in singolo esiste un server del mondo. Restano necessari terreno adatto, difficoltà non Pacifica e `doMobSpawning`.

| Impostazione | Predefinito | Effetto |
| --- | --- | --- |
| Vortici naturali | Attivo | Controlla i vortici naturali del mondo. Quelli evocati dai comandi restano disponibili. |
| Intervallo di ricerca (secondi) | 20 | Secondi di gioco tra controlli a 20 TPS. Un controllo non garantisce una comparsa. |
| Probabilità di tentativo (0–1) | 0.5 (50%) | 0 impedisce tentativi naturali riusciti; 0,5 = 50%; 1 tenta a ogni controllo valido. |
| Pausa personale (secondi) | 60 | Pausa dopo comparsa riuscita e scontro concluso/annullato. I vortici esistenti restano. |
| Velocità del vortice (blocchi/secondo) | 5 | Velocità su terreno piano libero a 20 TPS. Il server decide per tutti i giocatori. |
| Limite vortici caricati per dimensione | 32 | Blocca nuove comparse naturali a questo numero di vortici caricati, scontri inclusi. Non elimina entità esistenti. |

## File

Personali: `config/spectrobesreforged-client.toml`, uno per giocatore.

Server: `<mondo>/serverconfig/spectrobesreforged-server.toml`. Modifica il file di quel mondo sul server dedicato. NeoForge sincronizza i valori; riavvia dopo modifiche manuali per applicarli con certezza. Ridurre il limite non elimina vortici esistenti; i comandi restano disponibili.

## Multigiocatore

Installa la **stessa versione del mod su server e client**, con Minecraft 1.21.1, NeoForge, GeckoLib e Java 21. Beta.12 usa il protocollo 6, incompatibile con protocolli precedenti. I dati salvati restano conservati.

Il server verifica Prizmod, proprietà, selezione, ingredienti e distanza dalle macchine. Limita apertura, spostamenti, alimentazione e scanner; lo spam non prolunga indefinitamente il rilevamento. Collezioni personali e fino a 16 operatori cooperativi per tavolo.

Portate e definizioni Krawl si sincronizzano all’accesso e dopo `/reload`, senza curare i Krawl esistenti. Disconnettersi svuota solo il cache locale. Modelli e texture richiedono risorse corrispondenti; i datapack non scaricano grafica.

## Verifiche

Test di sviluppo: server senza client, dati e configurazione, creazione Krawl, discesa del vortice, possesso Prizmod e limiti per giocatore. Una partita reale con due client umani resta una verifica aggiuntiva.


**Integrazione facoltativa:** [Curios API 9.5.1+1.21.1 NeoForge](https://modrinth.com/mod/curios/version/yohfFbgD) aggiunge lo slot extra. **Senza Curios**, porta il Prizmod nell’inventario/mano secondaria e aprilo con **G** o clic destro. Per lo slot multigiocatore installa Curios su server e client. Sposta l’oggetto nell’inventario normale prima di disinstallare Curios.

Apri l’inventario e il **pulsante equipaggiamento Curios**. Inserisci uno dei sedici colori nello slot **Prizmod**, chiudi e premi **G**. Nessuno slot normale o armatura viene occupato. Restano validi inventario, mano secondaria e clic destro.

Curios usa G per impostazione predefinita. Se i tasti coincidono, G apre l’equipaggiamento senza Prizmod e la collezione con un Prizmod portato o equipaggiato. Il pulsante dell’inventario resta disponibile; entrambi i tasti sono riassegnabili.


[Incursioni cooperative](vortices.md#encounter-rules) · [Scavo cooperativo](getting-started.md#cooperative-excavation).


## Posizione HUD e feedback beta

Apri **Mods → Spectrobes Reforged → Config → Posiziona HUD**. Trascina il pannello e usa la rotella (50–150%). Puoi nascondere HUD o slot e ripristinare le impostazioni. Posizione e dimensione restano salvate localmente. Dimensione iniziale: 85%. Il pulsante Discord apre un invito dopo conferma, con un messaggio locale della beta a ogni accesso, senza invio agli altri giocatori o ripetizione dopo morte o cambio dimensione. Attiva HUD verticale per una colonna e usa − / + o la rotella per la dimensione.
