# Prizmod e compagni

Usa un Prizmod per aprire la tua collezione. Tutti i colori condividono gli stessi dati.

Apri **Mod → Spectrobes Reforged → Configurazione**. Riassegna **R** (scanner) e **G** (Prizmod) nei comandi. G funziona con un Prizmod equipaggiato o nell’inventario/mano secondaria. I tasti non agiscono mentre scrivi o usi altre schermate.


## Equipaggia il Prizmod

**Integrazione facoltativa:** [Curios API 9.5.1+1.21.1 NeoForge](https://modrinth.com/mod/curios/version/yohfFbgD) aggiunge lo slot extra. **Senza Curios**, porta il Prizmod nell’inventario/mano secondaria e aprilo con **G** o clic destro. Per lo slot multigiocatore installa Curios su server e client. Sposta l’oggetto nell’inventario normale prima di disinstallare Curios.

Apri l’inventario e il **pulsante equipaggiamento Curios**. Inserisci uno dei sedici colori nello slot **Prizmod**, chiudi e premi **G**. Nessuno slot normale o armatura viene occupato. Restano validi inventario, mano secondaria e clic destro.

Curios usa G per impostazione predefinita. Se i tasti coincidono, G apre l’equipaggiamento senza Prizmod e la collezione con un Prizmod portato o equipaggiato. Il pulsante dell’inventario resta disponibile; entrambi i tasti sono riassegnabili.

Curios salva l’equipaggiamento. La morte segue le impostazioni server e, di base, `keepInventory`. La collezione appartiene al giocatore: perdere o rimuovere l’oggetto non la cancella. Nessun punto armatura.

## Collezione e comandi

Il deposito mostra **16 voci per pagina**. Cerca per nome, filtra per elemento o corona e ordina per nome o livello. Fai clic su una scheda. I filtri non modificano l’ordine salvato.

La scheda mostra stadio, elemento, PS, attacco, difesa, livello, XP, qualità, minerali e vittorie. Usa **Compagno**, **Deposita**, **Squadra** o **Nutri**. Un clic su un minerale nell’inventario ne offre un’unità in Sopravvivenza.

Il compagno deve essere un Child vivo. I sei posti della squadra accettano Adult/Evolved; le specie attuali sono Child. Entrambe le destinazioni vengono verificate prima di uno scambio. L’evoluzione non è ancora disponibile.

## Qualità e allenamento

L’integrità del fossile dà **+1% a 70–84, +2% a 85–94 e +3% a 95–100**, arrotondato per difetto per ogni statistica base. Uno scavo perfetto aggiunge una corona dorata permanente dopo il nome del fossile e dello Spectrobe risvegliato. Qualità e corona sono separate. Le vecchie voci senza qualità conservano le statistiche.

Gli elementali C/B/A/A+/S danno **10/20/40/60/100% base** con affinità uguale, metà altrimenti, prima della curva per livello. Power/Defense/Health allenano solo statistiche e non danno XP. Gli altri minerali mantengono gli effetti.

L’efficienza dei minerali diminuisce gradualmente: **livello 1 → 100%, 25 → 80%, 50 → 60%, 75 → 45%, 100 → 35%** della percentuale base. Un minerale base del 10% dà circa il 6% al livello 50, arrotondato all’XP intera superiore. Dopo un cambio di livello, il resto usa la nuova efficienza. Effetti sulle statistiche, conteggio dei minerali e XP di combattimento restano invariati. Al livello massimo 100 non si ottengono altre XP.

I Child guadagnano XP mangiando. Un’incursione completata assegna una vittoria e fino a 60 XP agli Adult/Evolved vivi di ogni partecipante, ridotti dopo la sua morte. I Child non ricevono XP di battaglia. Questi contatori preparano l’evoluzione futura senza attivarla.

## Compagni e salvataggio

Seleziona un Child vivo e premi Compagno. Ti segue e aiuta nelle scansioni. Durante gli [incontri con vortici](vortices.md) resta al sicuro nel Prizmod, poi ritorna. Danni, allenamento e collezione vengono salvati. Una voce senza PS non appare. Salva il mondo prima di aggiornare.

La scheda selezionata mostra il modello 3D animato nel colore corretto accanto alle statistiche. Le carte mantengono le icone.

## Riferimento evoluzione: Beyond the Portals

| Spectrobe | Minerali |
| --- | --- |
| Komainu | 5 |
| Inkana | 4 |
| Vilar | 7 |
| Segu | 4 |
| Spiko | 3 |
| Shakin | 4 |

Solo riferimento; evoluzione ancora non disponibile.


## Fabbricazione e colori

{{PRIZMOD_RECIPE}}

{{DYE_RECIPES}}

{{ITEM_CREDITS}}


I compagni Child non subiscono danni di combattimento o ambientali: frecce, fuoco, lava, cadute, soffocamento e annegamento inclusi. Anche i PS salvati sono protetti. Aprire il Prizmod ripristina i Child feriti o a zero PS dei vecchi salvataggi, conservando statistiche, allenamento e medaglie. Adult/Evolved mantengono le loro regole di danno. I Child restano riposti durante le incursioni.


## HUD del compagno e della squadra

Un Child equipaggiato mostra in alto a sinistra un pannello compatto con ritratto, medaglie, livello e XP. Sei slot numerati rappresentano la squadra; Adult/Evolved assegnati mostrano ritratto e PS. Gli slot vuoti non attivano evoluzione o combattimento. Il tasto Prizmod segue i controlli. Nascosto con F1, debug, menu, spettatore o senza compagno e squadra.


## Ultime versioni sperimentali

Le medaglie hanno spazi separati accanto alla vista 3D. Passa il cursore su ogni icona per sapere come è stata ottenuta. Non coprono più i ritratti.

### Minerali e ritrovamenti — prova sperimentale 14

Gli elementali C/B/A/A+/S danno **10/20/40/60/100% base** con affinità uguale, metà altrimenti, prima della curva per livello. Power/Defense/Health allenano solo statistiche e non danno XP. Gli altri minerali mantengono gli effetti.

Semi: ATT +1, DIF +1, PS +10. Limite condiviso cumulabile: un seme per livello, massimo 100, entro i massimi della specie; nessuna XP. Evolve M completa minerali, L il livello definito, B le battaglie definite. Requisito assente o già soddisfatto: nessun consumo. I Child attuali richiedono solo minerali. Nessuna evoluzione automatica.
