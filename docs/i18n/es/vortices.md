# Vórtices Krawl pequeños

El vórtice persigue lentamente al jugador vivo no espectador más cercano a un máximo de **24 bloques**. Tocarlo inicia el encuentro, independientemente del objeto que lleves. Se contrae durante unos **0,8 segundos** y aparecen **tres Krawl** alrededor del jugador. El escáner se guarda temporalmente en el Prizmod.

## Aparición natural

En el Overworld, en Supervivencia y dificultad Fácil, Normal o Difícil, hay una comprobación por minuto con **25 % de probabilidad de intentar** generarlo a **40–64 bloques**. El sitio debe estar cargado, al aire libre, seco, despejado y sobre tierra, arena o piedra natural aceptada. Evita jugadores a menos de 32 bloques y vórtices existentes a 128. No se fuerza la carga de chunks ni se garantiza la aparición. No aparece naturalmente en Creativo; los inactivos caducan tras 20 minutos cargados.

## Krawl por bioma {#krawl-by-biome}

| Categoría | Tres enemigos |
| --- | --- |
| Cálido: etiqueta `hot` | Blovant · Corona |
| Templado: etiqueta `temperate` | Viblova · Aurora verde |
| Resto | Blova · Flash azul |

Las etiquetas pertenecen a `spectrobesreforged`. Si coinciden ambas, cálido tiene prioridad. El grupo se elige una vez al atraparte: cruzar de bioma durante el combate no lo cambia.

## Victoria y cancelación

Derrota a los tres y permanece a **32 bloques**: recibes **dos minerales de nivel C**, elegidos entre Power, Defense y Health, y **15 puntos de experiencia en orbes**. Puede repetirse el tipo de mineral. El propietario recibe la recompensa una vez; otros jugadores pueden ayudar. Se concede el logro de completar un vórtice y el compañero vuelve normalmente en aproximadamente un segundo.

Se cancela por dificultad Pacífico, **20 segundos consecutivos de ausencia** o **10 minutos de combate**. Se eliminan los enemigos cargados; los descargados se limpian cuando vuelven a procesarse. No hay recompensa de victoria al cancelar. Solo cuentan muertes confirmadas, no desapariciones. El progreso se guarda; una carga normal no repite la oleada ni la recompensa.

El modelo animado de 12 cubos tiene gas negro y morado, efectos ocasionales de muerte Krawl y polvo del suelo. Si aparece en el aire, desciende; sigue superficies y sube escalones de un bloque, sin atravesar muros altos ni pasos estrechos. No destruye bloques.

Para probar con permisos:

```text
/summon spectrobesreforged:krawl_vortex ~3 ~ ~
```
