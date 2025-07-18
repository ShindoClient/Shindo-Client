package me.miki.shindo.mobends.client.renderer.entity.layers;

import me.miki.shindo.mobends.client.model.entity.ModelBendsPlayer;
import me.miki.shindo.mobends.client.renderer.BendsCapeRenderer;
import me.miki.shindo.mobends.client.renderer.entity.RenderBendsPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.MathHelper;

public class LayerBendsCape implements LayerRenderer<AbstractClientPlayer>
{
    private final RenderBendsPlayer playerRenderer;
    private final BendsCapeRenderer capeRenderer;
    
    public LayerBendsCape(RenderBendsPlayer playerRendererIn) {
        this.playerRenderer = playerRendererIn;
        this.capeRenderer = new BendsCapeRenderer();
    }

    public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
    	
        if (entitylivingbaseIn != Minecraft.getMinecraft().thePlayer) {
            return;
        }
 
        if(entitylivingbaseIn.getLocationCape() != null) {
        	
            this.playerRenderer.bindTexture(entitylivingbaseIn.getLocationCape());
            
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.pushMatrix();	
            double d0 = entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * (double)partialTicks - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * (double)partialTicks);
            double d2 = entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * (double)partialTicks - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * (double)partialTicks);
            float f = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
            double d3 = (double) MathHelper.sin(f * 0.017453292F);
            double d4 = (double)(-MathHelper.cos(f * 0.017453292F));
            float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
            float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;

            if (f2 < 0.0F) {
                f2 = 0.0F;
            }

            ((ModelBendsPlayer)this.playerRenderer.getMainModel()).bipedBody.postRender(0.0625F);
            GlStateManager.translate(0, -12*0.0625F, 3*0.0625F);
            GlStateManager.rotate(6.0F + f2 / 2.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            
            this.capeRenderer.animate(entitylivingbaseIn, partialTicks, ageInTicks);
            this.capeRenderer.render(0.0625F);
            
            GlStateManager.popMatrix();
            GlStateManager.enableTexture2D();
        }
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}