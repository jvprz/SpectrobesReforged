# Revisión de mantenimiento — 2 de noviembre de 2026

## Alcance

Inspección estática de excavación y sincronización cooperativa, compañeros/recogida de minerales, renderizado de Komainu, vórtices y persistencia de raids, incubadora y validación de mensajes de red. Se conservan los cambios previos del proyecto. Esta revisión no equivale a un perfil de rendimiento ni garantiza ausencia de errores en todo el mod.

## Cambios aplicados

- **Mesas sin usuarios:** el tick termina inmediatamente. Antes, una mesa con tablero creado volvía a serializar el espécimen 20 veces por segundo incluso después de cerrar la interfaz. El cierre sigue guardando, y la retirada del último usuario desconectado guarda una vez.
- **Sincronización del tablero:** la consulta del progreso del jugador se limita a las tres cabeceras que lo necesitan; no se repite en cada una de las 1.024 casillas.
- **Porcentaje de excavación:** se calcula una vez y se reutiliza mientras no haya otra acción de herramienta. Las acciones invalidan la caché, incluidos cambios de capa y herramientas automáticas. Tiempo, integridad y requisitos de medalla siguen evaluándose como antes.
- **Cursores cooperativos:** se conserva la ventana de cuatro ticks, pero los datos iguales no vuelven a enviarse. La caché es por jugador y menú; se invalida al salir y al cargar. Entradas, salidas, movimiento y expiración de reservas generan actualizaciones.
- **Guardado de raids:** la instantánea de enemigos derrotados se copia una vez por encuentro en lugar de una vez por enemigo.
- **Barra de raid:** consulta directa del número de enemigos, sin crear una copia del conjunto para obtener su tamaño.

Se ha extraído el estado de entrega de cursores a `ExcavationCursorSync`, separado de las reglas del minijuego y comprobable sin cliente o servidor. No cambian identificadores, formatos NBT, protocolo de red, recetas, modelos, texturas, estadísticas ni recompensas.

## Validación

- 138 pruebas unitarias, incluidas regresiones del porcentaje después de acciones, restauración y cambios de material.
- Pruebas de cursores: nuevo usuario, nuevo menú, movimiento, salida y expiración de reserva.
- 27 GameTests con Curios, incluida una mesa cerrada que no reconstruye sus datos guardados cada tick.
- Servidor dedicado sin Curios comprobado por separado.
- JAR de prueba: `0.7.1-beta.7-maintenance-playtest`. La versión oficial de `gradle.properties` no se cambia.

## Puntos para futuras pasadas

- `IncubatorBlockEntity` y `SpectrobeEntity` concentran muchas responsabilidades. Conviene separar entrega/despertar y animaciones/comportamientos en cambios dedicados, con pruebas de guardado y carga, en vez de trasladar cientos de líneas a la vez.
- El registro persistente de encuentros conserva resultados y reclamaciones para impedir duplicaciones. Su crecimiento merece medición en un servidor de larga duración antes de diseñar compactación; eliminar entradas sin ese diseño puede reabrir exploits.
- La densidad de partículas de los vórtices y el renderizado de la colección necesitan mediciones en cliente real. No se afirma una mejora de FPS a partir de estas pruebas.
- Las búsquedas de minerales ya tienen intervalo y límite de candidatos; el escáner tiene búsqueda incremental y la incubadora sale pronto si está inactiva. Se mantienen esos límites y comportamiento.
- Los manejadores de excavación validan menú, distancia y usuario, y limitan peticiones. Se conserva esa autoridad del servidor.

## Prueba manual sugerida

Abrir una mesa entre dos jugadores, excavar con herramientas distintas, salir/entrar, comprobar el desbloqueo de herramientas y recoger el resultado. Dejar el fósil sin terminar, cerrar, recargar el mundo y continuar. Completar un vórtice y comprobar barra y recompensas. No se ha realizado esta comprobación visual en cliente durante esta revisión.
