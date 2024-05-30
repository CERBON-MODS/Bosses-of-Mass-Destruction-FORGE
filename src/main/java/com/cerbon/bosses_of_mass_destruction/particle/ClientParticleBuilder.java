package com.cerbon.bosses_of_mass_destruction.particle;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.vector.Vector3d;

import java.util.function.Function;
import java.util.function.Supplier;

public class ClientParticleBuilder {
    private final IParticleData options;
    private Function<SimpleParticle, Vector3d> getVel;
    private Function<SimpleParticle, Vector3d> continuousPos;
    private Function<Float, Vector3d> color;
    private Function<Float, Integer> brightness;
    private Function<Float, Float> scale;
    private Supplier<Integer> age;
    private Function<SimpleParticle, Float> getRotation;
    private Double colorVariation;

    public ClientParticleBuilder(IParticleData options){
        this.options = options;
    }

    public ClientParticleBuilder continuousRotation(Function<SimpleParticle, Float> rotation) {
        this.getRotation = rotation;
        return this;
    }

    public ClientParticleBuilder continuousVelocity(Function<SimpleParticle, Vector3d> velocity) {
        this.getVel = velocity;
        return this;
    }

    public ClientParticleBuilder continuousPosition(Function<SimpleParticle, Vector3d> positionProvider) {
        this.continuousPos = positionProvider;
        return this;
    }

    public ClientParticleBuilder color(Function<Float, Vector3d> color) {
        this.color = color;
        return this;
    }

    public ClientParticleBuilder color(Vector3d color) {
        this.color = f -> color;
        return this;
    }

    public ClientParticleBuilder rotation(Float rotation) {
        this.getRotation = p -> rotation;
        return this;
    }

    public ClientParticleBuilder brightness(Integer brightness) {
        this.brightness = f -> brightness;
        return this;
    }

    public ClientParticleBuilder scale(Function<Float, Float> scale) {
        this.scale = scale;
        return this;
    }

    public ClientParticleBuilder scale(Float scale) {
        this.scale = f -> scale;
        return this;
    }

    public ClientParticleBuilder age(Supplier<Integer> age) {
        this.age = age;
        return this;
    }

    public ClientParticleBuilder age(Integer age) {
        this.age = () -> age;
        return this;
    }

    public ClientParticleBuilder age(Integer min, Integer max) {
        this.age = () -> RandomUtils.range(min, max);
        return this;
    }

    public ClientParticleBuilder colorVariation(Double variation) {
        this.colorVariation = variation;
        return this;
    }

    public void build(Vector3d pos, Vector3d vel) {
        Minecraft client = Minecraft.getInstance();
        ActiveRenderInfo camera = client.gameRenderer.getMainCamera();

        if (camera.isInitialized()) {
            Particle particle = client.particleEngine.createParticle(options, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
            if (particle == null) return;

            if (scale != null)
                particle.scale(scale.apply(0f));

            if (color != null) {
                Vector3d clr = color.apply(0f);
                particle.setColor((float) clr.x, (float) clr.y, (float) clr.z);
            }

            if (age != null)
                particle.setLifetime(age.get());

            if (particle instanceof SimpleParticle) {
                SimpleParticle simpleParticle = (SimpleParticle) particle;

                if (brightness != null)
                    simpleParticle.setBrightnessOverride(brightness);

                if (color != null)
                    simpleParticle.setColorOverride(color);

                if (scale != null)
                    simpleParticle.setScaleOverride(scale);

                if (getVel != null)
                    simpleParticle.setVelocityOverride(getVel);

                if (continuousPos != null)
                    simpleParticle.setPositionOverride(continuousPos);

                if (colorVariation != null)
                    simpleParticle.setColorVariation(colorVariation);

                if (getRotation != null)
                    simpleParticle.setRotationOverride(getRotation);
            }
        }
    }
}
