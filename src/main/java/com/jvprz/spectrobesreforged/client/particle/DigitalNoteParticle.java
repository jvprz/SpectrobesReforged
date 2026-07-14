package com.jvprz.spectrobesreforged.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class DigitalNoteParticle
        extends TextureSheetParticle {

    private DigitalNoteParticle(
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

        this.gravity = 0.0F;
        this.friction = 0.92F;
        this.hasPhysics = false;

        this.lifetime =
                24 + this.random.nextInt(9);

        this.quadSize =
                0.16F
                        + this.random.nextFloat()
                        * 0.04F;

        /*
         * Give the note a subtle upward and sideways movement.
         */
        this.xd =
                xSpeed
                        + (
                        this.random.nextDouble()
                                - 0.5
                ) * 0.012;

        this.yd =
                0.012
                        + this.random.nextDouble()
                        * 0.008
                        + ySpeed;

        this.zd =
                zSpeed
                        + (
                        this.random.nextDouble()
                                - 0.5
                ) * 0.012;

        /*
         * Randomly choose one of the two musical-note sprites.
         *
         * The second SpriteSet#get argument is the maximum sprite
         * index, so a two-sprite set uses indices zero and one.
         */
        int spriteIndex =
                this.random.nextInt(2);

        this.setSprite(
                spriteSet.get(
                        spriteIndex,
                        1
                )
        );

        this.setAlpha(0.95F);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.removed) {
            return;
        }

        /*
         * Fade the note during the final part of its lifetime.
         */
        float lifeProgress =
                (float) this.age
                        / (float) this.lifetime;

        if (lifeProgress > 0.65F) {
            float fadeProgress =
                    (lifeProgress - 0.65F)
                            / 0.35F;

            this.setAlpha(
                    Math.max(
                            0.0F,
                            0.95F
                                    * (1.0F - fadeProgress)
                    )
            );
        }

        /*
         * Slowly reduce the size as the note disappears.
         */
        this.quadSize *= 0.992F;
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
            return new DigitalNoteParticle(
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