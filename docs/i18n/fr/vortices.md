# Petits vortex Krawl

Le vortex poursuit le joueur vivant non spectateur le plus proche à **24 blocs** maximum. Le contact déclenche trois Krawl, quel que soit l’objet tenu. Le modèle se contracte en **0,8 seconde** ; le compagnon scanner est rangé dans le Prizmod.

## Apparition naturelle

Dans l’Overworld, en Survie et hors Paisible : une vérification par minute, avec **25 % de chance de tenter** une apparition à **40–64 blocs**. Le terrain doit être chargé, sec, dégagé, exposé au ciel et couvert de terre, sable ou pierre naturelle acceptée. Aucun joueur à moins de 32 blocs, ni vortex existant dans les 128 blocs autour du découvreur. Aucune apparition garantie ni chargement forcé. Pas d’apparition naturelle en Créatif ; expiration après 20 minutes de ticks chargés.

## Krawl par biome {#krawl-by-biome}

**Chauds (`hot`) : Blovant/Corona ; tempérés (`temperate`) : Viblova/Aurora vert ; autres : Blova/Flash bleu.** Les tags sont ceux de `spectrobesreforged`. Le chaud prime en cas de chevauchement. Le groupe est choisi une fois au contact.

## Victoire et annulation

Tuez les trois et restez à **32 blocs** : le propriétaire reçoit **deux minéraux C**, choisis entre Power, Defense et Health, plus **15 points d’expérience en orbes**, une seule fois. Des doublons sont possibles. Les autres joueurs peuvent aider. Le progrès de vortex est accordé et le compagnon revient normalement sous une seconde.

L’absence pendant 20 secondes de suivi, 10 minutes de combat ou le mode Paisible annulent la rencontre sans récompense de victoire. Seules les morts confirmées comptent. La progression sauvegardée ne redonne pas de récompense après rechargement. Les ennemis chargés sont supprimés à l’annulation ; les autres sont nettoyés à leur retour.

Modèle de 12 cubes, gaz noir/violet dense, effets Krawl et poussière du sol. Un vortex aérien descend ; il suit les surfaces et franchit une marche d’un bloc, sans traverser les murs hauts ni détruire les blocs.

```text
/summon spectrobesreforged:krawl_vortex ~3 ~ ~
```
