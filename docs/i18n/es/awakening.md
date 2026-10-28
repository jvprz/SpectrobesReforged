# Incubadora y despertar

{{INCUBATOR_RECIPE}}

**Experimental en 0.6.2-beta.3:** el mineral fósil ahora entrega una pieza en bruto. Excávala en una mesa antes de introducir el fósil identificado en la incubadora. Los fósiles antiguos siguen funcionando. Consulta [la prueba de excavación](getting-started.md#excavation). La tabla muestra los resultados identificados posibles, no el botín directo en bruto.

[Fabrica la incubadora](getting-started.md#incubator), deja dos espacios horizontales libres y alimenta cualquiera de sus mitades con **redstone**. No utiliza combustible ni energía FE.

Introduce un fósil compatible, pulsa iniciar y mantén la alimentación durante **200 ticks: unos 10 segundos a 20 TPS**. Si pierde alimentación, el progreso se pausa. El jugador que inició el proceso debe seguir conectado para recibir el resultado; si no está, la entrega espera. El fósil se consume cuando se crea la entrada. Si el hueco infantil está vacío, la entrada va allí; si está ocupado, va al almacén.

La frecuencia del **último tick** determina el color: **0–5 → variante 0**, **6–10 → variante 1**, **11–16 → variante 2**. El silencio permite completar el proceso. No se usa el micrófono real: se captan eventos del juego y tocadiscos reproduciendo música a un máximo de **16 bloques**. Mira el medidor al final. [Chroma](minerals.md#chroma) permite cambiar la variante después.


## Interfaz de señal y captura

El medidor muestra tres bandas de color, señal de 0–16, variante prevista, fuente (eventos cercanos, tocadiscos o ambos), segundos restantes y pausa por falta de redstone. Los tres últimos segundos muestran 3–2–1 con avisos sonoros locales. Detener cancela sin consumir el fósil; solo puede hacerlo quien inició el proceso, antes de completarse.

La captura sigue los sonidos nuevos más suaves en lugar de retener un pico fuerte anterior. Los eventos simultáneos usan el mayor valor, independientemente del orden de llegada. Un filtro breve suaviza las transiciones durante unos pocos ticks; la señal capturada final sigue fijando el color. Se ignoran eventos emitidos desde bloques de incubadora para que abrir su propia interfaz no alimente el medidor. Se mantienen la distancia del tocadiscos y la mezcla que usa la fuente más fuerte. El indicador representa intensidad de eventos de Minecraft, no volumen de micrófono ni hercios reales.

La presentación se inspira en el medidor y la cuenta atrás del manual de BTP. El mod conserva sus diez segundos y permite despertar en silencio; no exige mantener una banda concreta para tener éxito.
