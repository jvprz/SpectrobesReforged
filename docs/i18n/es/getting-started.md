# Primeros pasos

Necesitas **Minecraft 1.21.1**, **NeoForge 21.1.219 o posterior**, **GeckoLib 4.8.3 o posterior para NeoForge 1.21.1** y **Java 21**.

Descarga el mod desde [CurseForge](https://www.curseforge.com/minecraft/mc-mods/spectrobes-reforged). Coloca el JAR y GeckoLib en la carpeta `mods` de tu instancia NeoForge. Mantén una sola versión del mod y realiza una copia del mundo.

## Fabrica tu equipo {#craft-your-equipment}

### Prizmod blanco {#white-prizmod}

{{PRIZMOD_RECIPE}}

Produce un Prizmod blanco. Combínalo con un tinte para cambiarlo a cualquiera de los 16 colores: la colección pertenece al jugador y se conserva.

### Incubadora {#incubator}

{{INCUBATOR_RECIPE}}

Produce una incubadora; necesita señal de redstone para funcionar.

Colócala con dos espacios horizontales libres, introduce un fósil de Komainu y pulsa iniciar. El proceso dura unos 10 segundos con alimentación. Abre el Prizmod para comprobar la entrada. La tecla de escaneo predeterminada es **R**; puedes cambiarla en los controles. Las recetas existen desde 0.6.0-beta.1. El mod sigue el idioma de Minecraft.

## Prueba de excavación {#excavation}

**0.6.2-beta.3**

Introduce un fósil en bruto o un mineral C/B/A/A+ en bruto. Haz tres clics para abrir la superficie y retira la cubierta ambiental con la herramienta adecuada. Mantén y arrastra para excavar; limpia sedimento fino y restos con el soplador. Extrae desde el **90%**. **R** activa el escáner y Los diez útiles de Beyond the Portals aparecen como iconos alrededor de la excavación central. **I** alterna entre excavación e inventario. Reparador, Giga y explosivo actúan una vez por clic; el explosivo exige otro clic de confirmación. Pasa el ratón por los botones para consultar sus funciones.

La recuperación manual concede una habilidad de excavación propia y registra el tipo exacto en tu catálogo. Giga exige haber recuperado antes ese tipo y no concede experiencia ni corona. La habilidad se conserva al morir. Las cuatro reparaciones pertenecen a la pieza incluso si la cambias de mesa. Tiempo, cubierta, capas, tensión, calor, gas y progreso viajan con la pieza. La corona exige **100%, sin daño y en 60 segundos activos**; se conserva al despertar, pero aún no permite evolución adulta.

{{EXCAVATION_RECIPE}}



| Herramienta | Función |
| --- | --- |
| {{TOOL_DRILL}} | Retira roca de forma equilibrada. Mueve el cursor; suelta o sopla para bajar la tensión. |
| {{TOOL_BLOWER}} | Limpia arena, la capa fina, restos y gas sin dañar. También enfría la zona. |
| {{TOOL_RECOVERY}} | Repara 10 de integridad, hasta cuatro usos por pieza. No recupera una corona perdida por daño. |
| {{TOOL_PITON}} | Rompe dos capas de roca en una zona amplia. Evita el objeto descubierto. |
| {{TOOL_FIRE}} | Derrite la capa de hielo. Vigila el calor y enfría con el soplador. |
| {{TOOL_WATER}} | Retira la capa de lava y enfría la zona de trabajo. |
| {{TOOL_VACUUM}} | Retira agua y barro, residuos turbios y gas. |
| {{TOOL_BLOWER_S}} | Limpia polvo, gas, sedimento fino y arena de toda la superficie. |
| {{TOOL_GIGA}} | Descubre automáticamente un tipo que ya hayas excavado a mano. Sin experiencia ni corona. |
| {{TOOL_BLAST}} | 50% de extracción instantánea y 50% de destrucción. Sin experiencia ni corona. Haz clic dos veces para confirmar. |

La piedra, la tierra y las cubiertas ambientales usan texturas de bloques de Minecraft. El polvo suelto se distingue por su tono claro y el aviso del cursor indica el material y la herramienta. Romper un fósil o mineral consume la pieza, retira su imagen y muestra fragmentos con un aviso de pérdida. La excavación sin terminar se conserva al cerrar; el resultado fallido también queda claro al reabrir la mesa.

### Bolsas geológicas — 0.6.2-beta.6

Los depósitos ambientales ahora crean una pequeña bolsa enterrada con una mena central, sin necesitar que ya exista su material anfitrión. Hielo (Flash) solo en biomas nevados o congelados; arenisca (Corona) solo en desierto; arenisca roja (Corona) solo en badlands, badlands boscosas y erosionadas; barro (Aurora) solo en pantano y manglar. Las cuatro familias incluyen minerales C/B/A/A+. Los parches miden 5–7 bloques de ancho y 3–5 de alto, con el centro 2–6 bloques bajo la altura de superficie. Sustituyen terreno natural seleccionado, nunca aire, fluidos, otras menas ni troncos. La cubierta central debe caber en terreno sólido permitido y dentro de su familia de biomas.

| ID | Fósil / Fossil | C / B / A / A+ |
| --- | --- | --- |
| `frozen` | `frozen_flash_fossil_ore` | `frozen_{c,b,a,a_plus}_mineral_ore` |
| `sandy` | `sandy_corona_fossil_ore` | `sandy_{c,b,a,a_plus}_mineral_ore` |
| `red_sandy` | `red_sandy_corona_fossil_ore` | `red_sandy_{c,b,a,a_plus}_mineral_ore` |
| `muddy` | `muddy_aurora_fossil_ore` | `muddy_{c,b,a,a_plus}_mineral_ore` |

Fósiles: 2 intentos por chunk. Minerales: C 1/2, B 1/4, A 1/12 y A+ 1/32 intentos por chunk. Los intentos pueden fallar si el terreno no sirve. Solo afecta a chunks nuevos. Se mantienen las menas subterráneas normales por temperatura. Las piezas conservan su geología, incluida arena/arenisca roja en la mesa; las excavaciones existentes mantienen sus capas guardadas.


Las diez herramientas de excavación también existen como objetos en la pestaña de herramientas de Creativo (`excavation_drill`, `excavation_blower`, etc.). Sus recetas y su uso para craftear la mesa quedan para después; la mesa sigue ofreciendo sus herramientas sin exigir objetos en el inventario.


{{ITEM_CREDITS}}


## Ajustes

Abre **Mods → Spectrobes Reforged → Configurar** para tus preferencias. Reasigna **R** (escanear) y **G** (abrir Prizmod) en **Opciones → Controles → Asignación de teclas**. G funciona con el Prizmod equipado o con uno que lleves en el inventario o la mano secundaria; no da acceso sin tener el objeto. Los atajos no se activan al escribir en el chat ni dentro de otra pantalla.

[Ajustes y servidores](settings.md).


**Integración opcional:** [Curios API 9.5.1+1.21.1 para NeoForge](https://modrinth.com/mod/curios/version/yohfFbgD) añade la ranura extra. **Sin Curios**, lleva el Prizmod en el inventario o la mano secundaria y ábrelo con **G** o clic derecho, como antes. Para usar la ranura en multijugador, instala Curios en servidor y clientes. Antes de desinstalar Curios, pasa el objeto equipado al inventario normal.

Abre el inventario y pulsa el **botón de equipo de Curios**. Coloca cualquiera de los dieciséis colores en la ranura **Prizmod**, cierra el inventario y pulsa **G**. No ocupa una casilla normal ni una ranura de armadura. También se mantiene el uso desde el inventario, la mano secundaria y el clic derecho.


## Excavación cooperativa {#cooperative-excavation}

Abrid la misma mesa: hasta **16 operadores** comparten pieza, capas, polvo, calor, integridad y resultado. Los cursores de colores muestran dónde apuntan los demás y un icono indica la herramienta activa. Cada herramienta queda reservada mientras alguien la utiliza; se pueden utilizar herramientas distintas a la vez. Soltar, cambiar de herramienta, cerrar o desconectarse libera la reserva; si se interrumpe la conexión caduca en aproximadamente medio segundo.

Hay **un único resultado**, que se extrae una sola vez. Cada colaborador elegible recibe una vez la XP de excavación y el registro del descubrimiento; mirar no cuenta. Cerrar conserva el progreso de la pieza. El servidor comprueba reservas, distancia y acciones. [Reglas y premios de incursión](vortices.md#encounter-rules).


Dos jugadores distintos deben utilizar herramientas con éxito en la misma pieza para obtener el recuerdo cooperativo ⚒ en cian. Abrir la mesa sin trabajar no cuenta. Se conserva en el fósil o mineral limpio y en el Spectrobe despierto, junto a la corona si corresponde. No modifica estadísticas, XP ni recompensas.


[Reconocimiento de excavación compartida](advancements.md): Quienes hayan utilizado una herramienta con éxito en la pieza y estén vivos, conectados y a 8 bloques o menos al extraerla reciben los logros existentes de excavación manual, hallazgo perfecto y terreno que correspondan. Cada colaborador elegible obtiene un descubrimiento, su registro y la XP normal de habilidad; los logros de 10/100 hallazgos y nivel 5 se calculan con su progreso personal. Quien extrae también recibe crédito. Mirar no cuenta. Reparar corresponde a quien utiliza Recuperación; despertar y entrenar siguen siendo personales. La extracción automática no concede progreso manual. Sigue habiendo un solo objeto de salida y ningún logro cooperativo nuevo.


La mesa nueva se orienta hacia quien la coloca. El objeto limpio tiene prioridad visual en la bandeja. Puede esperar una pieza en bruto en la entrada, pero no se puede trabajar hasta recoger la salida; la mesa vacía no muestra nada.


Los iconos se llaman **Medalla perfecta** (♛) y **Medalla cooperativa** (⚒). El objeto muestra únicamente sus nombres; los requisitos se consultan en esta guía.
