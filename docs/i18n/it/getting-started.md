# Primi passi

Servono **Minecraft 1.21.1**, **NeoForge 21.1.219+**, **GeckoLib 4.8.3+ per NeoForge 1.21.1** e **Java 21**. Scarica il mod da [CurseForge](https://www.curseforge.com/minecraft/mc-mods/spectrobes-reforged). Metti i due JAR nella cartella `mods` dell’istanza NeoForge. Mantieni una sola versione e salva una copia del mondo.

## Crea l’equipaggiamento {#craft-your-equipment}

### Prizmod bianco {#white-prizmod}

{{PRIZMOD_RECIPE}}

Produce un Prizmod. Combinalo con un colorante: 16 colori, collezione conservata.

### Incubatrice {#incubator}

{{INCUBATOR_RECIPE}}

Produce un’incubatrice, da alimentare con redstone.

Lascia due spazi orizzontali liberi per l’incubatrice, alimentala con redstone, inserisci un fossile di Komainu e avvia. Dopo circa dieci secondi alimentati, controlla il Prizmod. Lo scanner usa **R**, modificabile nei comandi. Le ricette esistono da 0.6.0-beta.1. Il mod segue la lingua di Minecraft.

## Prova di scavo {#excavation}

**0.6.2-beta.3**

Inserisci un fossile grezzo o un minerale C/B/A/A+ grezzo. Fai tre clic per aprire la superficie e rimuovi la copertura con lo strumento adatto. Tieni premuto e trascina per scavare; pulisci sedimenti sottili e detriti con il soffiatore. Estrai dal **90%**. **R** attiva lo scanner; I dieci strumenti di Beyond the Portals appaiono come icone attorno allo scavo centrale. **I** alterna scavo e inventario. Riparazione, Giga ed esplosivo agiscono una volta per clic; l’esplosivo richiede una seconda conferma. Passa sui pulsanti per leggere le funzioni.

Il recupero manuale sviluppa un’abilità di scavo e registra il tipo esatto nel catalogo. Giga richiede un tipo già recuperato e non dà esperienza o corona. L’abilità resta dopo la morte. Le quattro riparazioni appartengono al reperto anche cambiando tavolo. Tempo, copertura, strati, tensione, calore, gas e progresso seguono il reperto. La corona richiede **100%, nessun danno e massimo 60 secondi attivi**; resta dopo il risveglio ma non abilita ancora l’evoluzione adulta.

{{EXCAVATION_RECIPE}}



| Strumento | Effetto |
| --- | --- |
| {{TOOL_DRILL}} | Rimuove la roccia. Muovi il cursore; rilascia o soffia per ridurre la tensione. |
| {{TOOL_BLOWER}} | Pulisce sabbia, strato sottile, detriti e gas senza danni. Raffredda la zona. |
| {{TOOL_RECOVERY}} | Ripara 10 punti, quattro usi per reperto. Non ripristina una corona persa. |
| {{TOOL_PITON}} | Rompe due strati su una zona ampia. Evita gli oggetti esposti. |
| {{TOOL_FIRE}} | Scioglie il ghiaccio. Controlla il calore e usa il soffiatore. |
| {{TOOL_WATER}} | Rimuove lo strato di lava e raffredda la zona. |
| {{TOOL_VACUUM}} | Rimuove acqua, fango, detriti torbidi e gas. |
| {{TOOL_BLOWER_S}} | Pulisce polvere, gas, sedimenti sottili e sabbia su tutta la superficie. |
| {{TOOL_GIGA}} | Scopre automaticamente un tipo già scavato a mano. Niente esperienza o corona. |
| {{TOOL_BLAST}} | 50% di estrazione istantanea, 50% di distruzione. Niente esperienza o corona. Due clic per confermare. |

Pietra, terra e coperture usano le texture dei blocchi di Minecraft. La polvere chiara si distingue dal terreno; il cursore indica materiale e strumento. Un fossile o minerale distrutto viene consumato, la sua immagine scompare e appaiono frammenti e un avviso. Lo scavo incompleto resta salvato; anche il fallimento rimane chiaro riaprendo il tavolo.

### Sacche geologiche — 0.6.2-beta.6

I depositi creano una piccola sacca sepolta con un minerale centrale. Ghiaccio (Flash) solo nei biomi nevosi o gelati; arenaria (Corona) nel deserto; arenaria rossa (Corona) nelle tre varianti di badlands; fango (Aurora) in paludi e mangrovie. Tutte le famiglie includono minerali C/B/A/A+. Sacche larghe 5–7 blocchi e alte 3–5, con centro 2–6 blocchi sotto la superficie. Si sostituisce solo terreno naturale consentito, mai aria, fluidi, altri minerali o tronchi. Il guscio centrale deve essere nel terreno solido e nel bioma adatto.

| ID | Fósil / Fossil | C / B / A / A+ |
| --- | --- | --- |
| `frozen` | `frozen_flash_fossil_ore` | `frozen_{c,b,a,a_plus}_mineral_ore` |
| `sandy` | `sandy_corona_fossil_ore` | `sandy_{c,b,a,a_plus}_mineral_ore` |
| `red_sandy` | `red_sandy_corona_fossil_ore` | `red_sandy_{c,b,a,a_plus}_mineral_ore` |
| `muddy` | `muddy_aurora_fossil_ore` | `muddy_{c,b,a,a_plus}_mineral_ore` |

Fossili: 2 tentativi per chunk. Minerali: C 1/2, B 1/4, A 1/12, A+ 1/32. Il terreno inadatto può impedire la generazione. Solo nuovi chunk; restano i normali minerali sotterranei per temperatura. I reperti conservano la geologia rossa e gli scavi esistenti gli strati salvati.


I dieci strumenti sono anche oggetti nella scheda Creativa degli strumenti (`excavation_drill`, `excavation_blower`, ecc.). Ricette e uso come ingredienti del tavolo arriveranno più avanti; il tavolo continua a fornire gli strumenti senza oggetti in inventario.


{{ITEM_CREDITS}}


## Impostazioni

Apri **Mod → Spectrobes Reforged → Configurazione**. Riassegna **R** (scanner) e **G** (Prizmod) nei comandi. G funziona con un Prizmod equipaggiato o nell’inventario/mano secondaria. I tasti non agiscono mentre scrivi o usi altre schermate.

[Impostazioni e server](settings.md).


**Integrazione facoltativa:** [Curios API 9.5.1+1.21.1 NeoForge](https://modrinth.com/mod/curios/version/yohfFbgD) aggiunge lo slot extra. **Senza Curios**, porta il Prizmod nell’inventario/mano secondaria e aprilo con **G** o clic destro. Per lo slot multigiocatore installa Curios su server e client. Sposta l’oggetto nell’inventario normale prima di disinstallare Curios.

Apri l’inventario e il **pulsante equipaggiamento Curios**. Inserisci uno dei sedici colori nello slot **Prizmod**, chiudi e premi **G**. Nessuno slot normale o armatura viene occupato. Restano validi inventario, mano secondaria e clic destro.


## Scavo cooperativo {#cooperative-excavation}

Aprite lo stesso tavolo: fino a **16 operatori** condividono reperto, strati, polvere, calore, integrità e risultato. Cursori colorati mostrano gli altri e un’icona il loro strumento attivo. Ogni strumento è riservato durante l’uso; strumenti diversi funzionano insieme. Rilasciare, cambiare strumento, chiudere o disconnettersi libera la prenotazione; se interrotta scade dopo circa mezzo secondo.

C’è **un solo risultato**, estratto una sola volta. Ogni collaboratore idoneo riceve una volta XP di scavo e scoperta; osservare non conta. Chiudere conserva il progresso. Il server verifica prenotazioni, distanza e azioni. [Regole delle incursioni](vortices.md#encounter-rules).


Due giocatori diversi devono usare strumenti con successo sullo stesso reperto per ottenere il ricordo cooperativo ciano ⚒. Aprire il tavolo non basta. Resta sul fossile o minerale pulito e sullo Spectrobe risvegliato, insieme all’eventuale corona. Non cambia statistiche, XP o premi.


[Credito di scavo condiviso](advancements.md): Chi ha usato uno strumento con successo sul reperto ed è vivo, connesso ed entro 8 blocchi all’estrazione riceve gli obiettivi esistenti di scavo manuale, ritrovamento perfetto e terreno applicabili. Ogni collaboratore idoneo riceve una scoperta, catalogo e XP normali di abilità; 10/100 scoperte e livello 5 seguono il progresso personale. Anche chi estrae riceve credito. Guardare non conta. Riparare spetta all’utilizzatore di Recupero; risveglio e allenamento restano personali. L’estrazione automatica non dà progresso manuale. Sempre un solo oggetto, nessun nuovo obiettivo cooperativo.


Il nuovo tavolo guarda il giocatore che lo posiziona. L’oggetto pulito ha priorità nella vaschetta. Un reperto grezzo può attendere in ingresso, ma non si lavora finché l’uscita non viene ritirata; il tavolo vuoto non mostra nulla.


Le icone si chiamano **Medaglia perfetta** (♛) e **Medaglia cooperativa** (⚒). L’oggetto mostra solo i nomi; i requisiti sono in questa guida.


## Soffiatore portatile

Tieni premuto Usa/clic destro per spingere creature in un cono fino a cinque blocchi. I muri solidi bloccano l’aria. Nessun danno diretto o spinta ai giocatori. Durabilità 256: un punto ogni cinque tick, nessun consumo in Creativa. Il soffiatore del minigioco resta indipendente. In Creativa o con `/give @s spectrobesreforged:excavation_blower`, senza nuova ricetta.
