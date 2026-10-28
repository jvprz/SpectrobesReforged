# Prizmod y compañeros

Usa cualquier Prizmod con clic derecho para abrir tu colección. Todos los colores comparten los mismos datos.

Abre **Mods → Spectrobes Reforged → Configurar** para tus preferencias. Reasigna **R** (escanear) y **G** (abrir Prizmod) en **Opciones → Controles → Asignación de teclas**. G funciona con el Prizmod equipado o con uno que lleves en el inventario o la mano secundaria; no da acceso sin tener el objeto. Los atajos no se activan al escribir en el chat ni dentro de otra pantalla.


## Equipa el Prizmod

**Integración opcional:** [Curios API 9.5.1+1.21.1 para NeoForge](https://modrinth.com/mod/curios/version/yohfFbgD) añade la ranura extra. **Sin Curios**, lleva el Prizmod en el inventario o la mano secundaria y ábrelo con **G** o clic derecho, como antes. Para usar la ranura en multijugador, instala Curios en servidor y clientes. Antes de desinstalar Curios, pasa el objeto equipado al inventario normal.

Abre el inventario y pulsa el **botón de equipo de Curios**. Coloca cualquiera de los dieciséis colores en la ranura **Prizmod**, cierra el inventario y pulsa **G**. No ocupa una casilla normal ni una ranura de armadura. También se mantiene el uso desde el inventario, la mano secundaria y el clic derecho.

Curios también usa G por defecto. Si ambas acciones comparten tecla, G abre el equipo cuando no tienes Prizmod y la colección cuando lo llevas o lo tienes equipado. El botón del inventario siempre permite abrir el equipo; ambas teclas se pueden reasignar.

Curios guarda el equipo. Al morir se respetan sus ajustes del servidor y, por defecto, `keepInventory`. La colección pertenece al jugador, no al objeto: desequiparlo o perderlo no borra los Spectrobes. El accesorio no concede puntos de armadura.

## Colección y controles

El almacén muestra **16 entradas por página**, con botones para avanzar y retroceder. Puedes buscar por nombre, filtrar por elemento o corona y ordenar por nombre o nivel. Haz clic en una ficha para seleccionarla. Los filtros no reordenan los datos guardados.

La ficha muestra fase, elemento, vida, ataque, defensa, nivel, XP, bonus de calidad, minerales y victorias. Usa **Compañero**, **Guardar**, **Equipo** o **Alimentar**. Al alimentar aparecen los minerales de tu inventario: cada clic entrega una unidad en Supervivencia.

El compañero admite un Child vivo. Los seis huecos del equipo aceptan Adult/Evolved; las especies disponibles actualmente son infantiles. Los intercambios validan ambos destinos antes de mover las entradas. La evolución aún no está disponible.

## Calidad y entrenamiento

La integridad del fósil concede **+1% con 70–84, +2% con 85–94 y +3% con 95–100**, redondeando hacia abajo cada estadística base. Una excavación perfecta añade una corona dorada permanente detrás del nombre del fósil y del Spectrobe despierto. Corona y calidad se guardan por separado; las entradas antiguas sin calidad conservan sus estadísticas.

Los minerales C/B/A/A+ aportan **10/20/40/60% de un nivel**, además de sus efectos actuales en estadísticas. Los demás minerales activos aportan 25%; Diamond/Evolve siguen sin efecto. El nivel máximo es **100**. Al llenar la barra, las estadísticas crecen gradualmente dentro de los límites de la especie. Si una estadística ya está al máximo, el mineral puede seguir dando XP. Si no cambia ni XP ni estadísticas, no se consume.

Los Child obtienen XP mediante alimentación. Al completar una incursión, cada Adult/Evolved vivo del equipo de cada participante recibe una victoria y hasta 60 XP, reducidos si ese jugador murió. Los Child no reciben XP de batalla. Estos contadores preparan la futura evolución sin activarla.

## Compañeros y guardado

Selecciona un Child vivo y pulsa Compañero. Te sigue y ayuda a escanear. Durante los [encuentros de vórtice](vortices.md) permanece guardado y después regresa. Daños, entrenamiento y colección se conservan. Las entradas sin vida no aparecen. Cambiar de compañero actualiza su entidad. Haz una copia del mundo antes de actualizar.

La entrada seleccionada muestra su modelo 3D animado y su color real junto a las estadísticas. Las tarjetas de la colección conservan sus iconos.

## Referencia de evolución: Beyond the Portals

| Spectrobe | Minerales |
| --- | --- |
| Komainu | 5 |
| Inkana | 4 |
| Vilar | 7 |
| Segu | 4 |
| Spiko | 3 |
| Shakin | 4 |

Solo referencia; la evolución sigue sin estar disponible.


## Crafteo y colores

{{PRIZMOD_RECIPE}}

{{DYE_RECIPES}}

{{ITEM_CREDITS}}


Los compañeros Child no reciben daño de combate ni ambiental: flechas, fuego, lava, caídas, asfixia y ahogamiento incluidos. También se protegen sus PS guardados. Abrir el Prizmod recupera los Child heridos o a cero PS de partidas anteriores, conservando estadísticas, entrenamiento y medallas. Las formas Adult/Evolved mantienen sus reglas de daño. Los Child siguen guardándose durante las incursiones.
