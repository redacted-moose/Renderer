package render_engine;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Light;
import models.RawModel;
import shaders.StaticShader;
import shaders.TerrainShader;
import terrains.Terrain;
import textures.TerrainTexturePack;
import toolbox.Maths;

public class TerrainRenderer extends Renderer {
	
//	private TerrainShader shader;
	
	private List<Terrain> terrains = new ArrayList<Terrain>();
	
	public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		this.shader.start();
		this.shader.start();
//		this.shader.loadProjectionMatrix(projectionMatrix);
		this.shader.loadUniform(TerrainShader.PROJECTION_MATRIX, projectionMatrix);
		this.shader.connectTextureUnits();
		this.shader.stop();
	}
	
	public TerrainRenderer(Matrix4f projectionMatrix) {
		this.shader = new TerrainShader();
		this.shader.start();
		this.shader.start();
		this.shader.loadUniform(TerrainShader.PROJECTION_MATRIX, projectionMatrix);
		this.shader.connectTextureUnits();
		this.shader.stop();
	}
	
	public void render(List<Terrain> terrains) {
		for (Terrain terrain : terrains) {
			prepareTerrain(terrain);
			loadModelMatrix(terrain);
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			unbindTexturedModel();
		}
	}

	private void prepareTerrain(Terrain terrain) {
		RawModel rawModel = terrain.getModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);	
		bindTextures(terrain);
//		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
//		shader.loadShineVariables(1, 0);
		shader.loadUniform(TerrainShader.SHINE_DAMPER, 1.0f);
		shader.loadUniform(TerrainShader.REFLECTIVITY, 0.0f);
	}
	
	private void bindTextures(Terrain terrain) {
		TerrainTexturePack texturePack = terrain.getTexturePack();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getrTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getgTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getbTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());
	}
	
	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);	
	}
	
	private void loadModelMatrix(Terrain terrain) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
		shader.loadUniform(TerrainShader.TRANSFORMATION_MATRIX, transformationMatrix);	
	}

	@Override
	public void cleanUp() {
		this.shader.cleanUp();
	}

	@Override
	public void render(List<Light> lights, Camera camera, Vector4f clipPlane) {
		this.shader.start();
		this.shader.loadUniform(TerrainShader.PLANE, clipPlane);
		this.shader.loadUniform(TerrainShader.SKY_COLOR, new Vector3f(Camera.SKY_RED, Camera.SKY_GREEN, Camera.SKY_BLUE));
//		this.shader.loadLights(lights);
		for (int i = 0; i < Light.MAX_LIGHTS; i++) {
			if (i < lights.size()) {
				Light light = lights.get(i);
				this.shader.loadUniformArray(StaticShader.LIGHT_POSITION, i, light.getPosition());
				this.shader.loadUniformArray(StaticShader.LIGHT_COLOR, i, light.getColor());
				this.shader.loadUniformArray(StaticShader.ATTENUATION, i, light.getAttenuation());
			} else {
				this.shader.loadUniformArray(StaticShader.LIGHT_POSITION, i, new Vector3f(0, 0, 0));
				this.shader.loadUniformArray(StaticShader.LIGHT_COLOR, i, new Vector3f(0, 0, 0));
				this.shader.loadUniformArray(StaticShader.ATTENUATION, i, new Vector3f(1, 0, 0));
			}
		}
		this.shader.loadUniform(TerrainShader.VIEW_MATRIX, Maths.createViewMatrix(camera));
		
		render(this.terrains);
		
		this.shader.stop();
	}
	
	public List<Terrain> getTerrains() {
		return terrains;
	}
}
