# Pequenos vórtices Krawl

O vórtice persegue o jogador vivo não espectador mais próximo em até **24 blocos**. O contato inicia três Krawl, independentemente do item na mão. Ele se contrai em **0,8 segundo** e guarda o companheiro scanner no Prizmod.

## Geração natural

Overworld, Sobrevivência e dificuldade não Pacífica: verificação por minuto, com **25 % de chance de tentar** gerar a **40–64 blocos**. Precisa de terreno carregado, seco, livre, com céu aberto e terra, areia ou pedra natural aceita. Nenhum jogador a menos de 32 blocos nem vórtice existente a 128 do descobridor. Não há geração garantida nem carregamento forçado. Não gera naturalmente no Criativo; expira após 20 minutos de ticks carregados.

## Krawl por bioma {#krawl-by-biome}

**Quente (`hot`): Blovant/Corona; temperado (`temperate`): Viblova/Aurora verde; demais: Blova/Flash azul.** Tags de `spectrobesreforged`; quente tem prioridade em sobreposição. A escolha ocorre uma vez no contato.

## Vitória e cancelamento

Derrote os três e fique a **32 blocos**: o dono recebe uma vez **dois minerais C** entre Power, Defense e Health, mais **15 pontos de experiência em orbes**. Pode haver repetidos; outros jogadores podem ajudar. O progresso do vórtice é concedido e o companheiro normalmente volta em cerca de um segundo.

20 segundos de ausência acompanhada, dez minutos de combate ou modo Pacífico cancelam sem recompensa de vitória. Só mortes confirmadas contam. O progresso salvo não duplica onda nem prêmio. Inimigos carregados são removidos; os demais são limpos quando voltam a ser processados.

Modelo de 12 cubos com gás preto/roxo e poeira do chão. Se aparecer no ar, desce; segue superfícies e sobe degraus de um bloco, sem atravessar muros altos ou destruir blocos.

```text
/summon spectrobesreforged:krawl_vortex ~3 ~ ~
```
