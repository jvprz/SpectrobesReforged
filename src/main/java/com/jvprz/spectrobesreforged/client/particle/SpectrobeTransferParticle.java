package com.jvprz.spectrobesreforged.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class SpectrobeTransferParticle
        extends TextureSheetParticle {

    private static final int SMALL_ORB_SPRITE = 0;
    private static final int LARGE_ORB_SPRITE = 1;
    private static final int FRAGMENTS_SPRITE = 2;

    private final SpriteSet spriteSet;

    private SpectrobeTransferParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed,
            SpriteSet spriteSet
    ) {
        super(
                level,
                x,
                y,
                z,
                xSpeed,
                ySpeed,
                zSpeed
        );

        this.spriteSet = spriteSet;

        this.gravity = 0.0F;
        this.friction = 0.90F;
        this.hasPhysics = false;

        /*
         * Each spawned transfer particle lives briefly. Since the
         * incubator emits a new particle at the packet position every
         * tick, these short-lived particles create a compact glow
         * without drawing a permanent straight line.
         */
        this.lifetime =
                8 + this.random.nextInt(4);

        this.quadSize =
                0.20F
                        + this.random.nextFloat()
                        * 0.04F;

        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.setSprite(
                this.spriteSet.get(
                        SMALL_ORB_SPRITE,
                        FRAGMENTS_SPRITE
                )
        );

        this.setAlpha(1.0F);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.removed) {
            return;
        }

        float lifeProgress =
                (float) this.age
                        / (float) this.lifetime;

        /*
         * Pulse between the small and large orb during most of the
         * particle lifetime. Use the fragments sprite only near the
         * end, when the individual particle dissipates.
         */
        if (lifeProgress >= 0.72F) {
            this.setSprite(
                    this.spriteSet.get(
                            FRAGMENTS_SPRITE,
                            FRAGMENTS_SPRITE
                    )
            );

            this.quadSize *= 1.025F;
        } else {
            boolean useLargeOrb =
                    (this.age / 2) % 2 != 0;

            int spriteIndex =
                    useLargeOrb
                            ? LARGE_ORB_SPRITE
                            : SMALL_ORB_SPRITE;

            this.setSprite(
                    this.spriteSet.get(
                            spriteIndex,
                            FRAGMENTS_SPRITE
                    )
            );

            /*
             * Apply a very subtle pulse to the orb size.
             */
            if (useLargeOrb) {
                this.quadSize *= 1.015F;
            } else {
                this.quadSize *= 0.985F;
            }
        }

        /*
         * Fade during the final portion of the lifetime.
         */
        if (lifeProgress > 0.60F) {
            float fadeProgress =
                    (lifeProgress - 0.60F)
                            / 0.40F;

            this.setAlpha(
                    Math.max(
                            0.0F,
                            1.0F - fadeProgress
                    )
            );
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider
            implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet spriteSet;

        public Provider(
                SpriteSet spriteSet
        ) {
            this.spriteSet = spriteSet;
        }

        @Override
        @Nullable
        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed
        ) {
            return new SpectrobeTransferParticle(
                    level,
                    x,
                    y,
                    z,
                    xSpeed,
                    ySpeed,
                    zSpeed,
                    this.spriteSet
            );
        }
    }
}