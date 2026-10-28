# Vórtices Krawl pequeños

**{{VERSION}}**

Estos son los valores predeterminados del servidor; consulta [ajustes](settings.md) para cambiarlos.

El vórtice negro y violeta detecta jugadores vivos no espectadores a **64 bloques** y avanza a **5 bloques por segundo** en terreno llano despejado. Caminar ya no basta para escapar; correr permite ganar distancia. Tocarlo inicia una incursión con **tres Krawl por participante**, lleves lo que lleves. Al comenzar la persecución aparece un aviso sobre la barra de acceso rápido con su elemento.

## Aparición natural {#discovery-in-survival}

En el Overworld, los jugadores vivos fuera de Creativo y Espectador y con cielo abierto tienen una comprobación cada **20 segundos**, con **50 % de probabilidad de intentar** generar un vórtice. Cada intento prueba hasta **seis posiciones a 32–48 bloques**. No significa una aparición garantizada cada 20 segundos: el terreno, otros jugadores o los vórtices existentes pueden impedirla. Debe estar activado `doMobSpawning` y la dificultad debe ser Fácil, Normal o Difícil.

El lugar debe estar cargado, seco, dentro del borde del mundo y con tres bloques de aire sobre suelo natural adecuado. Admite tierra, arena, piedra, grava, terracota, nieve, hielo, barro y arenisca mediante la etiqueta de bloques `vortex_ground`. Evita jugadores a menos de 24 bloques del lugar y vórtices a 96 bloques del jugador que lo descubre. No carga chunks a la fuerza. Un jugador bajo tierra no inicia apariciones.

Tras generar uno, y de nuevo tras terminar o cancelar tu encuentro mientras estás conectado, tienes una pausa de **60 segundos** antes de nuevas apariciones naturales para ti. Durante tu combate tampoco se hacen intentos. El límite de vórtices cercanos sigue aplicándose a otros jugadores. Los inactivos desaparecen tras 20 minutos de ticks cargados.

## Krawl por bioma {#krawl-by-biome}

El **bioma de origen del vórtice** fija su oleada, también al invocarlo con comandos. Llevarlo después a otro bioma no cambia los Krawl. El tipo elegido se conserva al guardar.

| Clima | Enemigos | Ejemplos |
| --- | --- | --- |
| Cálido | Blovant · Corona | Desierto, sabana, badlands y junglas etiquetadas |
| Templado | Viblova · Aurora verde | Llanuras, bosques; pantanos y manglares sin etiqueta usan temperatura |
| Frío | Blova · Flash azul | Biomas nevados, picos helados y taiga etiquetada |

Las etiquetas existentes tienen prioridad sobre la temperatura base. Si un datapack las solapa, el orden es **hot → temperate → cold**. Sin etiqueta coincidente: **temperatura ≥ 1,0 → Corona; < 0,2 → Flash; el resto → Aurora**. También cubre biomas de otros mods. Se usa la temperatura base del bioma, no el clima del momento, la hora ni el cálculo de nieve por altura. Estas son las listas explícitas; la regla de temperatura cubre además los biomas que no aparecen:

{{BIOMES}}

## Persecución y terreno

Detecta a 64 bloques y mantiene el objetivo hasta **80 bloques** si no hay otro más cercano dentro del radio de detección. Se mueve 0,25 bloques por tick, aproximadamente 5 bloques por segundo a 20 TPS. Puede superar escalones de un bloque, losas y escaleras, y seguir sobre grava. Muros altos, agua, pasos estrechos y precipicios aún pueden bloquearlo. Si aparece en el aire baja al suelo. El lag reduce la velocidad real; `randomTickSpeed` no adelanta las apariciones.

## Incursiones cooperativas {#encounter-rules}

Una barra al estilo vanilla muestra los Krawl restantes y el número de participantes. Los jugadores vivos de Supervivencia/Aventura se incorporan a **32 bloques**: aparecen **tres Krawl por jugador**, hasta **16 jugadores / 48 enemigos**. Cada UUID se registra una sola vez, incluso al salir, regresar o recargar. Nadie puede estar en dos incursiones activas. Si no hay tres posiciones seguras, la incorporación espera. Todas las oleadas mantienen el tipo del bioma de origen. Los compañeros se ocultan para todos los participantes.

Morir en solitario cancela inmediatamente el encuentro y elimina los enemigos sin botín. En multijugador continúa mientras sobreviva un aliado. Volver vivo permite reincorporarse, conservando la penalización por muerte. Si todos los participantes registrados están caídos a la vez, se pierde el encuentro. También se cancela en Pacífico, tras diez minutos cargados o tras veinte segundos cargados sin ningún participante vivo a 32 bloques.

Hay que derrotar a **todos los enemigos de todas las incorporaciones**. El premio es personal y se entrega una sola vez al terminar; los Krawl de incursión no dan botín ni XP individuales. Quienes estén muertos o desconectados reciben el premio pendiente al volver vivos y conectados. Alejarse no crea premios adicionales; los participantes registrados conservan su parte si el grupo vence.

| Jugadores registrados | Minerales C por persona | Minerales B por persona | XP por persona |
| --- | --- | --- | --- |
| 1 | 2 | 0 | 15 |
| 2 | 3 | 0 | 20 |
| 3 | 3 | 1 | 25 |
| 4–16 | 3 | 1 | 30 |

Los minerales aleatorios son de poder, defensa o salud. Morir elimina los B y resta un C por muerte hasta dos (mínimo uno). La XP se divide entre muertes + 1, redondeando hacia abajo a múltiplos de cinco, mínimo cinco. La penalización se limita a tres muertes. El entrenamiento de Adult/Evolved elegibles da 60 XP sin morir y 60 / (muertes + 1) en caso contrario, mínimo diez. Si no cabe el botín se deja reservado para su destinatario. No se rompen bloques; se combate con armas de Minecraft. La evolución Child sigue sin estar disponible.

## Prueba rápida {#quick-test}

```text
/summon spectrobesreforged:krawl_vortex ~10 ~ ~
```

Busca una superficie abierta en Fácil, Normal o Difícil, invoca el vórtice con el comando y cambia a Supervivencia. Prueba caminar frente a correr, un escalón, grava, límites de bioma, guardar/cargar y derrotar los tres Krawl. Las apariciones naturales funcionan en chunks existentes; no necesitas regenerar el mundo.


El vórtice en persecución o en espera hace rebotar las flechas con el sonido y la respuesta de un escudo vanilla, incluidas las de Perforación. Dispararle no lo destruye ni inicia una incursión. Una vez comienza el encuentro, el controlador invisible deja de interceptar proyectiles.
