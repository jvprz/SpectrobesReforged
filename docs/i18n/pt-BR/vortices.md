# Pequenos vórtices Krawl

**{{VERSION}}**

Padrões do servidor; veja as [configurações](settings.md).

O vórtice detecta jogadores vivos não espectadores a **64 blocos** e avança a **5 blocos/segundo** em terreno plano livre. Caminhar já não basta para fugir; correr permite ganhar distância. O contato gera **três Krawl iguais**, independentemente do item na mão. Um aviso acima da barra rápida informa o elemento ao iniciar a perseguição.

## Aparecimento natural {#discovery-in-survival}

No Overworld, jogadores vivos fora de Criativo/Espectador e a céu aberto recebem uma verificação a cada **20 segundos**, com **50% de chance de tentativa**. Cada tentativa procura até **seis posições a 32–48 blocos**. Não é uma aparição garantida: terreno, jogadores e vórtices existentes podem impedir. `doMobSpawning` precisa estar ativo; dificuldade Fácil, Normal ou Difícil.

O local precisa estar carregado, seco, dentro da borda e com três blocos de ar sobre chão natural adequado. `vortex_ground` aceita terra, areia, pedra, cascalho, terracota, neve, gelo, lama e arenito. Nenhum jogador a menos de 24 blocos do local e nenhum vórtice a menos de 96 blocos do jogador. Sem carregar chunks à força. Jogadores no subsolo não iniciam tentativas.

Após uma aparição bem-sucedida e após terminar ou cancelar o próprio encontro enquanto conectado: pausa de **60 segundos**. Sem tentativas durante seu combate. O limite de vórtices próximos também vale para outros jogadores. Vórtices inativos somem após 20 minutos de ticks carregados.

## Krawl por bioma {#krawl-by-biome}

O **bioma de origem do vórtice** define os inimigos, inclusive por comandos. Atravessar outro bioma não altera a escolha, que é salva.

| Clima | Inimigos | Exemplos |
| --- | --- | --- |
| Quente | Blovant · Corona | Desertos, savanas, badlands e selvas nas tags |
| Temperado | Viblova · Aurora verde | Planícies, florestas; pântanos e manguezais pela temperatura |
| Frio | Blova · Flash azul | Biomas nevados, picos gelados e taiga nas tags |

As tags têm prioridade: **hot → temperate → cold**. Sem tag: **temperatura ≥ 1,0 → Corona; < 0,2 → Flash; demais Aurora**, inclusive em biomas de mods. Usa a temperatura base, não clima, horário ou neve calculada pela altura. Listas explícitas:

{{BIOMES}}

## Perseguição e terreno

Detecção até 64 blocos; mantém o alvo até **80 blocos** se não encontrar outro mais próximo. Velocidade de 0,25 blocos/tick a 20 TPS. Aceita degraus de um bloco, lajes, escadas e cascalho. Muros altos, água, passagens estreitas e penhascos podem bloquear. Vórtices no ar descem ao chão. Lag reduz o movimento real; `randomTickSpeed` não acelera aparições.

## Incursões cooperativas {#encounter-rules}

Uma barra no estilo vanilla mostra os Krawl restantes e os participantes. Jogadores vivos em Sobrevivência/Aventura entram a **32 blocos**: **três Krawl por jogador**, até **16 jogadores / 48 inimigos**. Cada UUID entra uma única vez, inclusive após sair, voltar ou recarregar. Ninguém participa de duas incursões ativas. Sem três posições seguras, a entrada aguarda. Todas as ondas mantêm o tipo do bioma de origem. Os companheiros ficam ocultos para todos.

Morrer sozinho cancela imediatamente a incursão e remove os inimigos sem saque. No multiplayer, ela continua enquanto um aliado sobreviver. Voltar vivo permite participar novamente, mantendo a penalidade. Se todos os participantes registrados estiverem caídos ao mesmo tempo, a incursão é perdida. Pacífico, dez minutos carregados ou vinte segundos carregados sem participante vivo a 32 blocos também cancelam.

Derrotem **todos os inimigos de todas as ondas**. Recompensas pessoais são entregues uma vez ao final; esses Krawl não dão saque nem XP separados. Mortos ou desconectados recebem o prêmio pendente ao voltarem vivos e conectados. Sair não gera prêmios extras.

| Jogadores registrados | Minerais C por pessoa | Minerais B por pessoa | XP por pessoa |
| --- | --- | --- | --- |
| 1 | 2 | 0 | 15 |
| 2 | 3 | 0 | 20 |
| 3 | 3 | 1 | 25 |
| 4–16 | 3 | 1 | 30 |

Minerais aleatórios de poder, defesa ou saúde. Morrer remove os B e subtrai um C por morte até dois, mínimo um. XP dividida por mortes + 1, arredondada ao múltiplo de cinco inferior, mínimo cinco. Penalidade limitada a três mortes. Adult/Evolved elegíveis recebem 60 XP de treino sem mortes, senão 60 / (mortes + 1), mínimo dez. Saque excedente é reservado ao destinatário. Não destrói blocos; use armas Minecraft. Evolução Child continua indisponível.

## Teste rápido {#quick-test}

```text
/summon spectrobesreforged:krawl_vortex ~10 ~ ~
```

Invoque o vórtice em terreno aberto e passe para Sobrevivência. Teste caminhada/corrida, degraus, cascalho, fronteiras dos biomas e salvar/recarregar. Aparições naturais funcionam em chunks existentes.
