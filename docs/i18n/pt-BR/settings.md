# Configurações e servidores

**{{VERSION}}**

Abra **Mods → Spectrobes Reforged → Configurar**. Reatribua **R** (scanner) e **G** (Prizmod) nos controles. G funciona com um Prizmod equipado ou no inventário/mão secundária. Atalhos não disparam enquanto digita ou usa outra tela.

## Preferências pessoais

| Ajuste | Padrão | Efeito |
| --- | --- | --- |
| Partículas dos vórtices | Alta | Altera apenas a densidade de partículas. O ajuste de partículas do Minecraft continua valendo. |
| Volume de efeitos (%) | 100% | Volume dos efeitos do mod: scanner, despertar, Krawl e batalhas. Os volumes das categorias do Minecraft também se aplicam. |
| Avisos do scanner | Visuais e som | Sinais visuais, som ou ambos. Não altera alcance nem regras do servidor. |
| Explicações de escavação | Ativado | Mostra explicações detalhadas. Nomes, reparos, avisos e estado permanecem visíveis. |
| Reduzir movimento das interfaces | Desativado | Reduz decoração móvel, fragmentos e movimento da prévia do Prizmod. Progresso e avisos continuam visíveis. |

## Configurações do mundo e servidor

Os padrões correspondem ao guia dos vórtices. O servidor controla as regras para todos; o cliente não pode substituí-las. No jogo solo o mundo também atua como servidor. Terreno adequado, dificuldade não Pacífica e `doMobSpawning` continuam necessários.

| Ajuste | Padrão | Efeito |
| --- | --- | --- |
| Vórtices naturais | Ativado | Controla vórtices naturais deste mundo. Os invocados por comandos continuam disponíveis. |
| Intervalo de busca (segundos) | 20 | Segundos de jogo entre verificações a 20 TPS. Uma verificação não garante aparecimento. |
| Probabilidade de tentativa (0–1) | 0.5 (50%) | 0 impede tentativas naturais bem-sucedidas; 0,5 = 50%; 1 tenta a cada verificação válida. |
| Pausa pessoal (segundos) | 60 | Pausa após aparição bem-sucedida e combate concluído/cancelado. Vórtices existentes permanecem. |
| Velocidade do vórtice (blocos/segundo) | 5 | Velocidade em terreno plano livre a 20 TPS. O servidor decide para todos os jogadores. |
| Limite de vórtices carregados por dimensão | 32 | Impede aparições naturais ao atingir esse total carregado, incluindo combates ativos. Não remove entidades existentes. |

## Arquivos

Pessoais: `config/spectrobesreforged-client.toml`, um por jogador.

Servidor: `<mundo>/serverconfig/spectrobesreforged-server.toml`. Edite o arquivo daquele mundo no servidor dedicado. NeoForge sincroniza os valores; reinicie após mudanças manuais para aplicá-las com segurança. Reduzir o limite não remove vórtices existentes; comandos continuam disponíveis.

## Multijogador

Instale a **mesma versão do mod no servidor e clientes**, com Minecraft 1.21.1, NeoForge, GeckoLib e Java 21. Beta.12 usa protocolo 6, incompatível com protocolos anteriores. Os dados salvos são preservados.

O servidor valida Prizmod, propriedade, seleção, ingredientes e distância das máquinas. Limita abrir, mover, alimentar e escanear; spam não prolonga o scanner indefinidamente. Coleções pessoais e até 16 operadores cooperativos por mesa.

Alcances e definições Krawl são sincronizados ao entrar e após `/reload`, sem curar Krawl existentes. Desconectar limpa apenas o cache local. Modelos e texturas continuam exigindo recursos correspondentes; datapacks não baixam imagens.

## Verificação

Testes de desenvolvimento: servidor sem cliente, dados e configurações, criação Krawl, descida do vórtice, posse do Prizmod e limites por jogador. Uma sessão real com dois clientes humanos continua sendo uma verificação adicional.


**Integração opcional:** [Curios API 9.5.1+1.21.1 para NeoForge](https://modrinth.com/mod/curios/version/yohfFbgD) adiciona o slot extra. **Sem Curios**, leve o Prizmod no inventário/mão secundária e abra com **G** ou botão direito. Para o slot multijogador, instale Curios no servidor e clientes. Retire o item equipado para o inventário normal antes de desinstalar Curios.

Abra o inventário e o **botão de equipamento Curios**. Coloque uma das dezesseis cores no slot **Prizmod**, feche e pressione **G**. Não ocupa espaço normal nem slot de armadura. Inventário, mão secundária e botão direito continuam funcionando.

Curios também usa G por padrão. Se as teclas coincidirem, G abre equipamento sem Prizmod e a coleção com um Prizmod carregado ou equipado. O botão do inventário continua disponível; ambas as teclas podem ser alteradas.


[Incursões cooperativas](vortices.md#encounter-rules) · [Escavação cooperativa](getting-started.md#cooperative-excavation).


## Posição do HUD e feedback da beta

Abra **Mods → Spectrobes Reforged → Config → Posicionar HUD**. Arraste o painel e use a roda do mouse (50–150%). É possível ocultar HUD ou espaços e redefinir. Posição e tamanho ficam salvos localmente. Tamanho inicial: 85%. O botão Discord abre o convite após confirmação, com uma mensagem local da beta ao entrar, sem enviar aos outros jogadores ou repetir após morte ou troca de dimensão. Ative HUD vertical para uma coluna e use − / + ou a roda do mouse para o tamanho.
