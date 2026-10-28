# Primeiros passos

Requer **Minecraft 1.21.1**, **NeoForge 21.1.219+**, **GeckoLib 4.8.3+ para NeoForge 1.21.1** e **Java 21**. Baixe o mod no [CurseForge](https://www.curseforge.com/minecraft/mc-mods/spectrobes-reforged). Coloque os dois JARs na pasta `mods` da instância NeoForge. Mantenha apenas uma versão e faça backup do mundo.

## Crie seu equipamento {#craft-your-equipment}

### Prizmod branco {#white-prizmod}

{{PRIZMOD_RECIPE}}

Produz um Prizmod. Combine com um corante: 16 cores, coleção preservada.

### Incubadora {#incubator}

{{INCUBATOR_RECIPE}}

Produz uma incubadora, que precisa de redstone.

Deixe dois espaços horizontais livres para a incubadora, forneça redstone, insira um fóssil de Komainu e inicie. Após cerca de dez segundos com energia, confira a entrada no Prizmod. O scanner usa **R**, alterável nos controles. As receitas existem desde 0.6.0-beta.1. O mod acompanha o idioma do Minecraft.

## Teste de escavação {#excavation}

**0.6.2-beta.3**

Insira um fóssil bruto ou mineral C/B/A/A+ bruto. Clique três vezes para abrir a superfície e remova a cobertura com a ferramenta adequada. Segure e arraste para escavar; limpe sedimentos finos e resíduos com o soprador. Extraia a partir de **90%**. **R** ativa o scanner; As dez ferramentas de Beyond the Portals aparecem como ícones ao redor da escavação central. **I** alterna entre escavação e inventário. Reparador, Giga e explosivo agem uma vez por clique; o explosivo pede outra confirmação. Passe o mouse nos botões para ler suas funções.

A recuperação manual desenvolve uma habilidade própria e registra o tipo exato no catálogo. Giga exige um tipo já recuperado e não dá experiência nem coroa. A habilidade continua após a morte. Os quatro reparos pertencem à peça, mesmo mudando de mesa. Tempo, cobertura, camadas, tensão, calor, gás e progresso acompanham a peça. A coroa exige **100%, nenhum dano e até 60 segundos ativos**; permanece após o despertar, mas ainda não permite evolução adulta.

{{EXCAVATION_RECIPE}}



| Ferramenta | Função |
| --- | --- |
| {{TOOL_DRILL}} | Remove rocha. Mova o cursor; solte ou sopre para reduzir a tensão. |
| {{TOOL_BLOWER}} | Limpa areia, camada fina, resíduos e gás sem danos. Também esfria a área. |
| {{TOOL_RECOVERY}} | Repara 10 pontos, quatro usos por peça. Não recupera uma coroa perdida por dano. |
| {{TOOL_PITON}} | Quebra duas camadas numa área ampla. Evite objetos expostos. |
| {{TOOL_FIRE}} | Derrete a camada de gelo. Observe o calor e use o soprador. |
| {{TOOL_WATER}} | Remove a camada de lava e esfria a área. |
| {{TOOL_VACUUM}} | Remove água, lama, resíduos turvos e gás. |
| {{TOOL_BLOWER_S}} | Limpa poeira, gás, sedimentos finos e areia em toda a superfície. |
| {{TOOL_GIGA}} | Revela automaticamente um tipo já escavado à mão. Sem experiência nem coroa. |
| {{TOOL_BLAST}} | 50% de extração instantânea, 50% de destruição. Sem experiência nem coroa. Dois cliques para confirmar. |

Pedra, terra e coberturas usam texturas de blocos do Minecraft. A poeira clara se destaca e o cursor indica material e ferramenta. Um fóssil ou mineral destruído é consumido: sua imagem desaparece e surgem fragmentos e um aviso de perda. A escavação inacabada continua salva; a falha também permanece clara ao reabrir a mesa.

### Bolsas geológicas — 0.6.2-beta.6

Os depósitos agora criam pequenas bolsas enterradas com um minério central. Gelo (Flash) apenas em biomas nevados ou congelados; arenito (Corona) no deserto; arenito vermelho (Corona) nas três variantes de badlands; lama (Aurora) em pântanos e manguezais. As quatro famílias incluem minerais C/B/A/A+. Bolsas de 5–7 blocos de largura e 3–5 de altura, com centro 2–6 blocos abaixo da superfície. Substituem apenas terreno natural permitido, nunca ar, fluidos, outros minérios ou troncos. A camada central deve caber em solo sólido e no bioma adequado.

| ID | Fósil / Fossil | C / B / A / A+ |
| --- | --- | --- |
| `frozen` | `frozen_flash_fossil_ore` | `frozen_{c,b,a,a_plus}_mineral_ore` |
| `sandy` | `sandy_corona_fossil_ore` | `sandy_{c,b,a,a_plus}_mineral_ore` |
| `red_sandy` | `red_sandy_corona_fossil_ore` | `red_sandy_{c,b,a,a_plus}_mineral_ore` |
| `muddy` | `muddy_aurora_fossil_ore` | `muddy_{c,b,a,a_plus}_mineral_ore` |

Fósseis: 2 tentativas por chunk. Minerais: C 1/2, B 1/4, A 1/12, A+ 1/32. Tentativas podem falhar em terreno inadequado. Apenas chunks novos; minérios subterrâneos normais por temperatura continuam. Peças mantêm a geologia vermelha e escavações existentes preservam suas camadas.


As dez ferramentas também existem como itens na aba Criativa de ferramentas (`excavation_drill`, `excavation_blower` etc.). Receitas e uso como ingredientes da mesa ficam para depois; a mesa continua fornecendo ferramentas sem exigir itens no inventário.


{{ITEM_CREDITS}}


## Configurações

Abra **Mods → Spectrobes Reforged → Configurar**. Reatribua **R** (scanner) e **G** (Prizmod) nos controles. G funciona com um Prizmod equipado ou no inventário/mão secundária. Atalhos não disparam enquanto digita ou usa outra tela.

[Configurações e servidores](settings.md).


**Integração opcional:** [Curios API 9.5.1+1.21.1 para NeoForge](https://modrinth.com/mod/curios/version/yohfFbgD) adiciona o slot extra. **Sem Curios**, leve o Prizmod no inventário/mão secundária e abra com **G** ou botão direito. Para o slot multijogador, instale Curios no servidor e clientes. Retire o item equipado para o inventário normal antes de desinstalar Curios.

Abra o inventário e o **botão de equipamento Curios**. Coloque uma das dezesseis cores no slot **Prizmod**, feche e pressione **G**. Não ocupa espaço normal nem slot de armadura. Inventário, mão secundária e botão direito continuam funcionando.


## Escavação cooperativa {#cooperative-excavation}

Abram a mesma mesa: até **16 operadores** compartilham peça, camadas, poeira, calor, integridade e resultado. Cursores coloridos mostram os outros, com ícone da ferramenta ativa. Uma ferramenta fica reservada enquanto é usada; ferramentas diferentes funcionam ao mesmo tempo. Soltar, trocar, fechar ou desconectar libera a reserva; uma reserva interrompida expira em cerca de meio segundo.

Há **um único resultado**, extraído uma vez. Cada colaborador elegível recebe uma vez XP de escavação e registro da descoberta; observar não conta. Fechar preserva o progresso. O servidor verifica reservas, distância e ações. [Regras das incursões](vortices.md#encounter-rules).


Dois jogadores diferentes precisam usar ferramentas com sucesso na mesma peça para obter a lembrança cooperativa ciano ⚒. Só abrir a mesa não conta. Ela fica no fóssil ou mineral limpo e no Spectrobe despertado, junto da coroa quando houver. Não altera atributos, XP ou prêmios.


[Crédito de escavação compartilhado](advancements.md): Quem usou uma ferramenta com sucesso na peça e estiver vivo, conectado e a até 8 blocos ao extraí-la recebe os avanços existentes de escavação manual, achado perfeito e terreno aplicáveis. Cada colaborador elegível recebe uma descoberta, catálogo e XP normal de habilidade; 10/100 descobertas e nível 5 usam progresso pessoal. Quem extrai também recebe crédito. Observar não conta. Reparar cabe ao usuário de Recuperação; despertar e treino continuam pessoais. Extração automática não concede progresso manual. Continua havendo um só objeto, sem novo avanço cooperativo.


A nova mesa fica voltada para quem a coloca. O objeto limpo tem prioridade na bandeja. Uma peça bruta pode esperar na entrada, mas só pode ser trabalhada após retirar a saída; a mesa vazia não mostra nada.
