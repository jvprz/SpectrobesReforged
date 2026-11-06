# Prizmod e companheiros

Use qualquer Prizmod para abrir sua coleção. Todas as cores compartilham os mesmos dados.

Abra **Mods → Spectrobes Reforged → Configurar**. Reatribua **R** (scanner) e **G** (Prizmod) nos controles. G funciona com um Prizmod equipado ou no inventário/mão secundária. Atalhos não disparam enquanto digita ou usa outra tela.


## Equipe o Prizmod

**Integração opcional:** [Curios API 9.5.1+1.21.1 para NeoForge](https://modrinth.com/mod/curios/version/yohfFbgD) adiciona o slot extra. **Sem Curios**, leve o Prizmod no inventário/mão secundária e abra com **G** ou botão direito. Para o slot multijogador, instale Curios no servidor e clientes. Retire o item equipado para o inventário normal antes de desinstalar Curios.

Abra o inventário e o **botão de equipamento Curios**. Coloque uma das dezesseis cores no slot **Prizmod**, feche e pressione **G**. Não ocupa espaço normal nem slot de armadura. Inventário, mão secundária e botão direito continuam funcionando.

Curios também usa G por padrão. Se as teclas coincidirem, G abre equipamento sem Prizmod e a coleção com um Prizmod carregado ou equipado. O botão do inventário continua disponível; ambas as teclas podem ser alteradas.

Curios salva o equipamento. A morte segue suas configurações de servidor e, por padrão, `keepInventory`. A coleção pertence ao jogador: perder ou remover o item não a apaga. Não concede pontos de armadura.

## Coleção e controles

O armazém mostra **16 entradas por página**. Busque por nome, filtre por elemento ou coroa e ordene por nome ou nível. Clique em uma ficha para selecionar. Os filtros não alteram a ordem salva.

A ficha mostra estágio, elemento, PV, ataque, defesa, nível, XP, qualidade, minerais e vitórias. Use **Companheiro**, **Guardar**, **Equipe** ou **Alimentar**. Clicar em um mineral do inventário entrega uma unidade na Sobrevivência.

O companheiro aceita um Child vivo. Os seis espaços da equipe aceitam Adult/Evolved; as espécies atuais são Child. Ambos os destinos são validados antes da troca. A evolução ainda não está disponível.

## Qualidade e treinamento

A integridade do fóssil concede **+1% com 70–84, +2% com 85–94 e +3% com 95–100**, arredondando cada atributo base para baixo. Uma escavação perfeita adiciona uma coroa dourada permanente após o nome do fóssil e do Spectrobe desperto. Qualidade e coroa são salvas separadamente. Entradas antigas sem qualidade mantêm seus atributos.

Elementais C/B/A/A+/S dão **10/20/40/60/100% base** com afinidade igual, metade com outra, antes da curva de nível. Power/Defense/Health treinam apenas atributos e não dão XP. Outros minerais mantêm os efeitos.

A eficiência dos minerais diminui gradualmente: **nível 1 → 100%, 25 → 80%, 50 → 60%, 75 → 45%, 100 → 35%** da porcentagem base. Um mineral base de 10% dá cerca de 6% no nível 50, arredondado para XP inteira acima. Após subir de nível, o restante usa a nova eficiência. Efeitos nos atributos, contador de minerais e XP de combate permanecem iguais. No nível máximo 100 não há mais ganho de XP.

Children ganham XP pela alimentação. Concluir uma incursão dá uma vitória e até 60 XP aos Adult/Evolved vivos de cada participante, reduzidos após a morte desse jogador. Children não recebem XP de batalha. Esses contadores preparam a futura evolução sem ativá-la.

## Companheiros e persistência

Selecione um Child vivo e pressione Companheiro. Ele segue você e ajuda a escanear. Durante [encontros de vórtice](vortices.md), fica guardado com segurança e depois retorna. Danos, treinamento e coleção persistem. Entradas sem PV não aparecem. Faça uma cópia do mundo antes de atualizar.

A ficha selecionada mostra o modelo 3D animado na cor correta junto aos atributos. As cartas mantêm seus ícones.

## Referência de evolução: Beyond the Portals

| Spectrobe | Minerais |
| --- | --- |
| Komainu | 5 |
| Inkana | 4 |
| Vilar | 7 |
| Segu | 4 |
| Spiko | 3 |
| Shakin | 4 |

Apenas referência; a evolução continua indisponível.


## Fabricação e cores

{{PRIZMOD_RECIPE}}

{{DYE_RECIPES}}

{{ITEM_CREDITS}}


Companheiros Child não recebem dano de combate ou ambiental, incluindo flechas, fogo, lava, quedas, sufocamento e afogamento. Os PV salvos também ficam protegidos. Abrir o Prizmod restaura Child feridos ou com zero PV de saves anteriores, preservando atributos, treino e medalhas. Adult/Evolved mantêm suas regras de dano. Child continuam guardados durante incursões.


## HUD do companheiro e da equipe

Um Child equipado mostra um painel compacto no canto superior esquerdo com retrato, medalhas, nível e XP. Seis espaços numerados representam a equipe; Adult/Evolved atribuídos mostram retrato e PV. Espaços vazios não ativam evolução nem combate. A tecla Prizmod segue os controles. Oculto com F1, depuração, menus, espectador ou sem companheiro e equipe.


## Últimas versões de teste

As medalhas têm espaços separados junto à visualização 3D. Passe o cursor em cada ícone para saber como foi obtida. Elas não cobrem mais os retratos.

### Minerais e descobertas — teste experimental 14

Elementais C/B/A/A+/S dão **10/20/40/60/100% base** com afinidade igual, metade com outra, antes da curva de nível. Power/Defense/Health treinam apenas atributos e não dão XP. Outros minerais mantêm os efeitos.

Sementes: ATQ +1, DEF +1, PV +10. Limite compartilhado acumulável de uma semente por nível, máximo 100, respeitando a espécie; nenhuma XP. Evolve M completa minerais, L o nível definido, B as batalhas definidas. Requisito ausente ou já completo: sem consumo. Os Child atuais só exigem minerais. Sem evolução automática.
