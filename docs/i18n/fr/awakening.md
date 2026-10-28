# Incubateur et éveil

{{INCUBATOR_RECIPE}}

**Expérimental en 0.6.2-beta.3 :** le minerai donne un fossile brut à fouiller sur une table avant l’incubateur. Les anciens fossiles identifiés fonctionnent encore. Voir [l’essai de fouille](getting-started.md#excavation). Le tableau indique les résultats identifiés possibles.

[Fabriquez l’incubateur](getting-started.md#incubator), libérez deux cases horizontales et alimentez l’une des moitiés par **redstone**. Il n’utilise ni carburant ni énergie FE.

Insérez un fossile compatible et démarrez : **200 ticks, environ 10 secondes à 20 TPS**. Une coupure suspend la progression. Le joueur à l’origine du processus doit être connecté pour recevoir l’entrée ; sinon, la livraison attend. Le fossile n’est consommé qu’après création réussie. Un emplacement enfant vide reçoit l’entrée ; sinon, elle va au stockage.

La fréquence du **dernier tick** fixe la variante : **0–5 → 0**, **6–10 → 1**, **11–16 → 2**. Le silence permet l’éveil. Aucun microphone réel n’est utilisé : seuls les événements du jeu et les juke-box en lecture à **16 blocs** maximum influencent le résultat. [Chroma](minerals.md#chroma) change ensuite la couleur.


## Interface et capture du signal

Le compteur affiche trois bandes de couleur, le signal 0–16, la variante prévue, la source (événements, jukebox ou les deux), les secondes restantes et la pause sans redstone. Les trois dernières secondes affichent 3–2–1 avec des sons locaux. Seul le joueur ayant démarré peut arrêter avant la fin, sans consommer le fossile.

Les sons plus faibles récents remplacent les anciens pics. Les événements simultanés utilisent la valeur la plus forte, indépendamment de leur ordre. Un filtre bref adoucit les transitions ; le signal capturé final détermine toujours la couleur. Les événements des blocs incubateurs sont ignorés. La distance du jukebox et la priorité à la source la plus forte sont conservées. Il s’agit de force d’événements Minecraft, pas de microphone ni de Hertz.

Présentation inspirée du compteur et du compte à rebours du manuel BTP. La durée reste dix secondes et le silence permet toujours l’éveil ; aucune bande n’est nécessaire pour réussir.
