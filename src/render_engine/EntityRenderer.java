package render_engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import shaders.StaticShader;
import textures.ModelTexture;
import toolbox.Maths;

public class EntityRenderer extends Renderer {

	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();

	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		this.shader.start();
		this.shader.loadUniform(StaticShader.PROJECTION_MATRIX, projectionMatrix);
		this.shader.stop();
	}
	
	public EntityRenderer(Matrix4f projectionMatrix) {
		this.shader = new StaticShader();
		this.shader.start();
		this.shader.loadUniform(StaticShader.PROJECTION_MATRIX, projectionMatrix);
		this.shader.stop();
	}

	public void render(Map<TexturedModel, List<Entity>> entities) {
		for (TexturedModel model : entities.keySet()) {
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for (Entity entity : batch) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
	}

	private void prepareTexturedModel(TexturedModel model) {
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		ModelTexture texture = model.getTexture();
		if (texture.isHasTransparency()) {
			MasterRenderer.disableCulling();
		}
		this.shader.loadUniform(StaticShader.NUMBER_OF_ROWS, new Float(texture.getNumberOfRows()));
		this.shader.loadUniform(StaticShader.USE_FAKE_LIGHTING, texture.isUseFakeLighting());
		this.shader.loadUniform(StaticShader.SHINE_DAMPER, texture.getShineDamper());
		this.shader.loadUniform(StaticShader.REFLECTIVITY, texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}

	private void unbindTexturedModel() {
		MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	private void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(),
				entity.getRotY(), entity.getRotZ(), entity.getScale());
		this.shader.loadUniform(StaticShader.TRANSFORMATION_MATRIX, transformationMatrix);
		this.shader.loadUniform(StaticShader.OFFSET, new Vector2f(entity.getTextureXOffset(), entity.getTextureYOffset()));
	}

	@Override
	public void cleanUp() {
		this.shader.cleanUp();
	}

	@Override
	public void render(List<Light> lights, Camera camera, Vector4f clipPlane) {
		this.shader.start();
		this.shader.loadUniform(StaticShader.PLANE, clipPlane);
		this.shader.loadUniform(StaticShader.SKY_COLOR, camera.getSkyColor());
//		this.shader.loadLights(lights); // If lights don't change, only load once
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
		this.shader.loadUniform(StaticShader.VIEW_MATRIX, Maths.createViewMatrix(camera));
		
		render(entities);
		
		this.shader.stop();
	}

	public Map<TexturedModel, List<Entity>> getEntities() {
		return entities;
	}

}
