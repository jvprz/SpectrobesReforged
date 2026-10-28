# Incubadora e despertar

{{INCUBATOR_RECIPE}}

**Experimental na 0.6.2-beta.3:** o minério dá uma peça bruta para escavar na mesa antes da incubadora. Fósseis antigos identificados continuam válidos. Veja [o teste de escavação](getting-started.md#excavation). A tabela mostra os possíveis resultados identificados.

[Crie a incubadora](getting-started.md#incubator), deixe dois espaços horizontais e alimente uma metade com **redstone**. Não utiliza combustível nem energia FE.

Insira um fóssil compatível e inicie: **200 ticks, cerca de dez segundos a 20 TPS**. Sem energia, o progresso pausa. O jogador que iniciou deve estar conectado para receber a entrada; caso contrário, a entrega aguarda. O fóssil só é consumido após a criação bem-sucedida. Um espaço infantil vazio recebe a entrada; se ocupado, ela vai ao armazenamento.

A frequência do **último tick** determina a variante: **0–5 → 0**, **6–10 → 1**, **11–16 → 2**. O silêncio permite concluir. Não usa microfone real: eventos do jogo e jukeboxes tocando a até **16 blocos** influenciam o valor. [Chroma](minerals.md#chroma) muda a cor depois.


## Interface e captura de sinal

O indicador mostra três faixas de cor, sinal 0–16, variante prevista, fonte (eventos, jukebox ou ambos), segundos restantes e pausa sem redstone. Os últimos segundos mostram 3–2–1 com sons locais. Só quem iniciou pode parar antes do fim, preservando o fóssil.

Sons novos mais fracos substituem picos antigos. Eventos simultâneos usam o maior valor, independentemente da ordem. Um filtro curto suaviza as transições; o sinal final continua definindo a cor. Eventos de blocos de incubadora são ignorados. Distância e prioridade da fonte mais forte continuam iguais. O indicador mede eventos do Minecraft, não microfone nem Hertz reais.

A apresentação se inspira no indicador e na contagem do manual de BTP. Continuam os dez segundos e o despertar em silêncio, sem faixa obrigatória para conseguir despertar.
