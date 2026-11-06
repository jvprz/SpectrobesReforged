# Vivid mineral palettes

Color-only pass requested after the softened mineral palettes looked too muted. The source directory preserves all 64 current non-cube mineral textures before this change. The shared cube sprite is excluded.

Colored pixels gain saturation and a modest value boost, preserving hue. Neutral metal/white pixels and the darkest outlines are retained. No geometry, pixel placement, alpha, resolution, texture filtering or mineral behavior changes.

The generation script always reads these frozen sources to avoid compounding the color adjustment. The previous softening script is disabled as an executable entry point, so it does not inadvertently restore the duller palettes.

Each comparison cell shows before on the left and after on the right. Preview enlargements use nearest-neighbor only. To revert, copy a source PNG back to the matching item/minerals path.
