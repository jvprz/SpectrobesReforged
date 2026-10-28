# Ajustes y servidores

**{{VERSION}}**

Abre **Mods → Spectrobes Reforged → Configurar** para tus preferencias. Reasigna **R** (escanear) y **G** (abrir Prizmod) en **Opciones → Controles → Asignación de teclas**. G funciona con el Prizmod equipado o con uno que lleves en el inventario o la mano secundaria; no da acceso sin tener el objeto. Los atajos no se activan al escribir en el chat ni dentro de otra pantalla.

## Preferencias personales

| Ajuste | Predeterminado | Efecto |
| --- | --- | --- |
| Partículas de los vórtices | Alta | Solo cambia la cantidad de partículas del vórtice. Se respeta el ajuste de partículas de Minecraft. |
| Volumen de efectos (%) | 100% | Regula efectos del mod: escáner, avisos de despertar, Krawl y sonidos de batalla. También se aplica el volumen de cada categoría de Minecraft. |
| Avisos del escáner | Visuales y sonido | Elige señales visuales, sonido o ambos. No cambia el radio de detección ni las reglas del servidor. |
| Explicaciones de excavación | Activado | Muestra explicaciones ampliadas de las herramientas. Conserva nombres, reparaciones, advertencias y estado de la excavación. |
| Reducir movimiento de interfaces | Desactivado | Reduce la decoración móvil de la incubadora, los fragmentos voladores y el movimiento del modelo del Prizmod. Conserva progreso y avisos. |

## Ajustes del mundo y servidor

Estos valores predeterminados coinciden con la guía de vórtices. El anfitrión o servidor controla las reglas para todos; las preferencias del cliente no pueden cambiarlas. En un jugador, el propio mundo actúa como servidor. La aparición natural sigue necesitando terreno adecuado, dificultad no pacífica y `doMobSpawning`.

| Ajuste | Predeterminado | Efecto |
| --- | --- | --- |
| Vórtices naturales | Activado | Controla las apariciones naturales de este mundo. Los vórtices invocados por comandos siguen funcionando. |
| Intervalo de aparición (segundos) | 20 | Segundos de juego entre comprobaciones a 20 TPS. Una comprobación no garantiza una aparición. |
| Probabilidad de intento (0–1) | 0.5 (50%) | 0 impide los intentos naturales con éxito; 0,5 equivale al 50 %; 1 intenta generar en cada comprobación válida. |
| Pausa personal (segundos) | 60 | Pausa tras una aparición correcta y tras terminar o cancelar el encuentro. Los vórtices cercanos existentes permanecen. |
| Velocidad del vórtice (bloques/segundo) | 5 | Velocidad en suelo llano despejado a 20 TPS. El servidor la controla para todos los jugadores. |
| Límite de vórtices cargados por dimensión | 32 | Al alcanzar este número de vórtices cargados se detienen los intentos naturales. Incluye combates activos y no elimina entidades existentes. |

## Archivos

Preferencias personales: `config/spectrobesreforged-client.toml`. Cada jugador tiene su archivo.

Mundo/servidor: `<mundo>/serverconfig/spectrobesreforged-server.toml`. Para un servidor dedicado edita el archivo de ese mundo. NeoForge sincroniza sus valores a los clientes. Reiniciar tras una edición manual es una forma segura de aplicarlos. Bajar el límite de vórtices no elimina los existentes; se pueden seguir invocando por comandos.

## Funcionamiento multijugador

Instala la **misma versión del mod en servidor y clientes**, con Minecraft 1.21.1, NeoForge, GeckoLib y Java 21. Beta.12 usa el protocolo de red 6 y no conecta con revisiones anteriores del protocolo. Se conservan los datos de los mundos existentes.

El servidor comprueba que lleves un Prizmod, tu propiedad y selección de criaturas, los ingredientes del inventario y la distancia a las máquinas. Limita las peticiones de abrir, mover, alimentar y escanear. Pulsar repetidamente el escáner no prolonga su duración sin límite. La colección pertenece a cada jugador; la mesa admite hasta 16 operadores cooperativos.

Los radios de escaneo y las definiciones Krawl se sincronizan al entrar y tras `/reload`. Los Krawl cargados se actualizan sin curarse. Desconectarse limpia la información visual local sin tocar la colección ni el registro del servidor. Los modelos y texturas siguen viniendo del mod y sus recursos; la sincronización de datapacks no descarga dibujos.

## Comprobaciones

Las pruebas de desarrollo cubren el arranque del servidor sin cliente, datos y configuración, creación de Krawl, descenso del vórtice, posesión del Prizmod y límites independientes por jugador. Una partida real con dos clientes humanos conectados queda como comprobación de juego adicional.


**Integración opcional:** [Curios API 9.5.1+1.21.1 para NeoForge](https://modrinth.com/mod/curios/version/yohfFbgD) añade la ranura extra. **Sin Curios**, lleva el Prizmod en el inventario o la mano secundaria y ábrelo con **G** o clic derecho, como antes. Para usar la ranura en multijugador, instala Curios en servidor y clientes. Antes de desinstalar Curios, pasa el objeto equipado al inventario normal.

Abre el inventario y pulsa el **botón de equipo de Curios**. Coloca cualquiera de los dieciséis colores en la ranura **Prizmod**, cierra el inventario y pulsa **G**. No ocupa una casilla normal ni una ranura de armadura. También se mantiene el uso desde el inventario, la mano secundaria y el clic derecho.

Curios también usa G por defecto. Si ambas acciones comparten tecla, G abre el equipo cuando no tienes Prizmod y la colección cuando lo llevas o lo tienes equipado. El botón del inventario siempre permite abrir el equipo; ambas teclas se pueden reasignar.


[Incursiones cooperativas](vortices.md#encounter-rules) · [Excavación cooperativa](getting-started.md#cooperative-excavation).


## Posición del HUD y feedback de la beta

Abre **Mods → Spectrobes Reforged → Configurar → Colocar HUD**. Arrastra la vista previa, usa la rueda para cambiar el tamaño (50–150%), activa u oculta el panel y los huecos del equipo, o restablece. Las preferencias también ofrecen posición horizontal/vertical en porcentaje y escala; son ajustes locales que se conservan al reiniciar. El tamaño inicial es 85%. **Dar feedback en Discord** abre la invitación tras confirmar. Durante la beta aparece un mensaje local al entrar al mundo/servidor con la invitación a Discord. No se envía a los demás ni se repite al morir o cambiar de dimensión. Activa HUD vertical para colocar el equipo en columna y ajusta el tamaño con − / + o la rueda.
