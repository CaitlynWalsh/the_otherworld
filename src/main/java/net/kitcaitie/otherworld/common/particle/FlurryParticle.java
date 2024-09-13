package net.kitcaitie.otherworld.common.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlurryParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final double xStart;
    private final double yStart;
    private final double zStart;

    protected FlurryParticle(ClientLevel p_108323_, double p_108324_, double p_108325_, double p_108326_, double p_107586_, double p_107587_, double p_107588_, SpriteSet spriteSet) {
        super(p_108323_, p_108324_, p_108325_, p_108326_, p_107586_, p_107587_, p_107588_);
        this.sprites = spriteSet;
        this.pickSprite(spriteSet);
        this.xd = p_107586_;
        this.yd = p_107587_;
        this.zd = p_107588_;
        this.x = p_108324_;
        this.y = p_108325_;
        this.z = p_108326_;
        this.xStart = this.x;
        this.yStart = this.y;
        this.zStart = this.z;
        this.quadSize = 0.15F * (this.random.nextFloat() * 0.2F + 0.5F);
        this.lifetime = (int)(Math.random() * 10.0D) + 40;
    }

    public void move(double p_107560_, double p_107561_, double p_107562_) {
        this.setBoundingBox(this.getBoundingBox().move(p_107560_, p_107561_, p_107562_));
        this.setLocationFromBoundingbox();
    }

    public float getQuadSize(float p_107567_) {
        float f = ((float)this.age + p_107567_) / (float)this.lifetime;
        f = 1.0F - f;
        f *= f;
        f = 1.0F - f;
        return this.quadSize * f;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            float f = (float)this.age / (float)this.lifetime;
            float f1 = -f + f * f * 2.0F;
            float f2 = 1.0F - f1;
            this.x = this.xStart + this.xd * (double)f2;
            this.y = this.yStart + this.yd * (double)f2 + (double)(1.0F - f);
            this.z = this.zStart + this.zd * (double)f2;
            this.setPos(this.x, this.y, this.z);
        }
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void setSpriteFromAge(SpriteSet p_108340_) {
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet p_107570_) {
            this.sprite = p_107570_;
        }

        public Particle createParticle(SimpleParticleType p_107581_, ClientLevel p_107582_, double p_107583_, double p_107584_, double p_107585_, double p_107586_, double p_107587_, double p_107588_) {
            FlurryParticle flurryParticle = new FlurryParticle(p_107582_, p_107583_, p_107584_, p_107585_, p_107586_, p_107587_, p_107588_, sprite);
            return flurryParticle;
        }

    }
}
