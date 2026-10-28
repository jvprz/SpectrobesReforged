# Incubatrice e risveglio

{{INCUBATOR_RECIPE}}

**Sperimentale in 0.6.2-beta.3:** il minerale dà un fossile grezzo da scavare al tavolo prima dell’incubatrice. I vecchi fossili identificati restano validi. Vedi [la prova di scavo](getting-started.md#excavation). La tabella mostra i possibili risultati identificati.

[Crea l’incubatrice](getting-started.md#incubator), lascia due spazi orizzontali e alimenta una metà con **redstone**. Non richiede combustibile o energia FE.

Inserisci un fossile compatibile e avvia: **200 tick, circa dieci secondi a 20 TPS**. Senza alimentazione il progresso si ferma. Il giocatore che ha iniziato deve essere online per ricevere l’entrata; altrimenti la consegna attende. Il fossile è consumato solo dopo la creazione riuscita. Uno slot infantile libero riceve l’entrata, altrimenti va al deposito.

La frequenza dell’**ultimo tick** determina la variante: **0–5 → 0**, **6–10 → 1**, **11–16 → 2**. Il silenzio va bene. Non viene usato il microfono reale: contano eventi di gioco e jukebox in riproduzione entro **16 blocchi**. [Chroma](minerals.md#chroma) permette di cambiare colore dopo.


## Interfaccia e cattura del segnale

L’indicatore mostra tre fasce colore, segnale 0–16, variante prevista, fonte (eventi, giradischi o entrambi), secondi restanti e pausa senza redstone. Gli ultimi secondi mostrano 3–2–1 con suoni locali. Solo chi ha avviato può fermare prima della fine, conservando il fossile.

I suoni più deboli recenti sostituiscono i vecchi picchi. Gli eventi simultanei usano il valore maggiore, indipendentemente dall’ordine. Un breve filtro rende fluide le transizioni; il segnale finale determina ancora il colore. Gli eventi dei blocchi incubatrice vengono ignorati. Restano distanza e priorità della fonte più forte. L’indicatore misura eventi Minecraft, non microfono o Hertz reali.

Presentazione ispirata all’indicatore e al conto alla rovescia del manuale BTP. Restano dieci secondi e il risveglio in silenzio, senza una fascia obbligatoria per riuscire.
