package com.jvprz.spectrobesreforged.client.particle;

import com.jvprz.spectrobesreforged.common.content.entity.krawl.KrawlVortexEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

/** Short-lived gas follows its owner while spiralling upward, never colliding with terrain. */
public final class VortexGasParticle extends TextureSheetParticle {
    private final KrawlVortexEntity owner;
    private final boolean inner;
    private final float initialSize;
    private final double initialHeight;
    private final double initialAngle;
    private final double orbitSpeed;
    private final double radialDepth;

    private VortexGasParticle(ClientLevel level, double x, double y, double z,
                              KrawlVortexEntity owner, double height, double angle, SpriteSet sprites, boolean inner) {
        super(level, x, y, z, 0, 0, 0);
        this.owner = owner;
        this.inner = inner;
        radialDepth = random.nextDouble();
        initialHeight = height;
        initialAngle = angle;
        orbitSpeed = 0.09 + random.nextDouble() * 0.045;
        initialSize = inner ? 0.34F + random.nextFloat() * 0.18F : 0.23F + random.nextFloat() * 0.17F;
        quadSize = initialSize;
        lifetime = 30 + random.nextInt(15);
        hasPhysics = false;
        gravity = 0;
        pickSprite(sprites);
        float tint = random.nextFloat();
        if (inner) setColor(0.018F + tint * 0.025F, 0.008F + tint * 0.012F, 0.028F + tint * 0.035F);
        else setColor(0.18F + tint * 0.27F, 0.07F + tint * 0.10F, 0.27F + tint * 0.34F);
        alpha = 0;
    }

    @Override public void tick() {
        xo = x; yo = y; zo = z;
        if (++age >= lifetime || owner.isRemoved()) { remove(); return; }
        double dissolve = owner.dissolveProgress(0);
        if (dissolve >= 1) { remove(); return; }
        double scale = 1 - dissolve;
        double height = Math.min(1.08, initialHeight + age * 0.003);
        double angle = initialAngle + age * orbitSpeed;
        double funnel = 0.06 + Math.pow(Math.min(height, 1), 1.4) * 1.15;
        // Keep the dense gas close to the model; the sprite edges cover the crown.
        double crown = height > 0.88 ? 1.10 : 0;
        double envelope = Math.max(funnel, crown);
        // A thick turbulent volume instead of one thin orbital surface.
        double depth = inner ? 0.86 + radialDepth * 0.12 : 0.96 + radialDepth * 0.12;
        double radius = envelope * depth * scale;
        radius += Math.sin(age * 0.18 + initialAngle * 2) * 0.015 * scale;
        setPos(owner.getX() + Math.cos(angle) * radius,
                owner.getY() + height * 2.62 * scale + dissolve * 0.6,
                owner.getZ() + Math.sin(angle) * radius);
        float life = age / (float) lifetime;
        // Almost opaque through most of its life; only the edges and final ticks fade.
        float fadeOut = Math.min(1.0F, (1 - life) / 0.25F);
        alpha = (float) (Math.min(1, age / 2.0) * fadeOut * 0.96 * scale);
        quadSize = initialSize * (0.8F + life * 0.6F) * (float) scale;
    }

    @Override public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites, boolean inner) implements ParticleProvider<SimpleParticleType> {
        public Provider(SpriteSet sprites) { this(sprites, false); }
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level,
                double x, double y, double z, double ownerId, double height, double angle) {
            return level.getEntity((int) ownerId) instanceof KrawlVortexEntity owner
                    ? new VortexGasParticle(level, x, y, z, owner, height, angle, sprites, inner) : null;
        }
    }
}
