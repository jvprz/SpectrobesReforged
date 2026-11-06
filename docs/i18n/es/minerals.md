# Minerales y recetas

Las menas identifican la familia, no el grado: Coronium, Aurorium y Flashium tienen variantes de piedra y pizarra profunda. El hielo azul contiene Flashium; la arenisca y la arenisca roja, Coronium; y el barro, Aurorium. Las vetas de piedra y pizarra siguen la clasificación de temperatura de los vórtices. Se conservan las frecuencias y alturas de cada grado, que queda guardado internamente y se mantiene con Toque de Seda. La generación nueva aparece en zonas sin explorar; los bloques antiguos siguen siendo compatibles.

Usa un mineral sobre **tu propio compañero vivo vinculado al Prizmod**. Las estadísticas se limitan entre su base y máximo. Un mineral puede subir una y reducir otra, sin bajar de la base. Si no cambia ninguna estadística ni la XP, no se consume ni avanza el contador. Un cambio válido consume una unidad en Supervivencia e incrementa el contador. Los minerales de vida ajustan la vida actual según el cambio de vida máxima; no son una curación completa independiente.

{{MINERALS}}

Diamond sigue sin efecto. El Evolve antiguo ahora equivale a Evolve M: completa el requisito de minerales ingeridos, sin evolucionar al Spectrobe. Gold conserva sus reducciones de estadísticas.

## Chroma {#chroma}

`chroma_0`, `chroma_1` y `chroma_2` aplican la variante 0, 1 y 2. Si ya coincide, no se consume. Un cambio válido consume una unidad fuera de Creativo e incrementa el contador en una entrada vinculada.

## Minerales del mundo

Usa pico de hierro o superior. Se generan en el Overworld:

{{MINERAL_ORES}}

## Recetas

{{RECIPES}}


## Niveles y XP

La integridad del fósil concede **+1% con 70–84, +2% con 85–94 y +3% con 95–100**, redondeando hacia abajo cada estadística base. Una excavación perfecta añade una corona dorada permanente detrás del nombre del fósil y del Spectrobe despierto. Corona y calidad se guardan por separado; las entradas antiguas sin calidad conservan sus estadísticas.

Los elementales C/B/A/A+/S aportan **10/20/40/60/100% base de un nivel** con afinidad coincidente y la mitad con otra afinidad, antes de aplicar la curva por nivel. Power/Defense/Health solo entrenan estadísticas y ya no dan XP. Los minerales mixtos y especiales anteriores conservan sus efectos.

La eficacia de XP de los minerales disminuye gradualmente entre estos puntos: **nivel 1 → 100%, 25 → 80%, 50 → 60%, 75 → 45%, 100 → 35%** del porcentaje base. Por ejemplo, un mineral base del 10% aporta aproximadamente un 6% a nivel 50 (redondeado hacia arriba a XP entera). Si cruza de nivel, la parte restante usa la eficacia del nuevo nivel. Solo se reduce la XP del mineral: sus efectos en estadísticas y el contador de un mineral ingerido no cambian. La XP de combate tampoco cambia. El nivel 100 sigue siendo el máximo y no admite más XP.

Los Child obtienen XP mediante alimentación. Al completar una incursión, cada Adult/Evolved vivo del equipo de cada participante recibe una victoria y hasta 60 XP, reducidos si ese jugador murió. Los Child no reciben XP de batalla. Estos contadores preparan la futura evolución sin activarla.

Consulta [Prizmod](prizmod.md) para alimentar desde la colección.


[Piezas en bruto por material](fossils.md#environmental-raws)

## Minerales y hallazgos — prueba experimental 14

Al extraer el mineral se fija Coronium en biomas cálidos, Aurorium en templados y Flashium en fríos, con la misma clasificación de los vórtices. El bruto conserva su origen aunque cambies de bioma. C/B/A/A+ mantienen sus alturas de generación y capas; el 1% de los nuevos brutos A+ resulta de grado S. Las excavaciones antiguas ya empezadas conservan su resultado.

Power/Defense/Health se consiguen al completar vórtices, no en excavaciones nuevas. Se mantienen las cantidades C/B y las penalizaciones por muerte. Sin morir puede añadirse un A/A+: con n participantes (1–16), la probabilidad de A+ es (5+n)/1000 y la de A es (30+3n)/1000. Probabilidades independientes sin morir: 10% de una semilla aleatoria y 1% de Evolve M. Solo se entregan al completar el encuentro, sin repetir la tirada al salir o entrar.

Semillas: ATQ +1, DEF +1 y PS +10. Límite compartido acumulable de una semilla por nivel, máximo 100, respetando los máximos de la especie; no dan XP. Evolve M completa minerales ingeridos; L alcanza el nivel requerido; B completa el número de combates. Si el requisito no existe o ya está cumplido, no se consume. Los Child actuales solo tienen requisito de minerales: L/B quedan listos para futuras definiciones. Ninguno inicia la evolución.

Aplicados los sprites propios de semillas, Evolve M/L/B, Metalium, Titanium, Marble y los 20 cubos. Las piedras misteriosas y los dos objetos reliquia conservan iconos provisionales. Los coleccionables y materiales siguen preparados, sin nuevas funciones ni drops.

{{DISCOVERY_ITEMS}}

Titanium ahora es un material, no alimento. Se ha eliminado el duplicado Placa de titanio. Al cargar, los Uranium antiguos pasan a Aurorium S, Chrome a Flashium S, Evolve a Evolve M y Placa de titanio a Titanium mediante alias de registro, conservando las cantidades.
