package render_engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import resource_loaders.Loader;
import terrains.Terrain;

public class MasterRenderer {
	
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	
	private Matrix4f projectionMatrix;
	
	private EntityRenderer entityRenderer;
	private TerrainRenderer terrainRenderer;
	private SkyboxRenderer skyboxRenderer;
	
//	private HashMap<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
//	private List<Terrain> terrains = new ArrayList<Terrain>();
	
	private List<Renderer> renderers = new ArrayList<Renderer>();
	
	public MasterRenderer(Loader loader) {
		enableCulling();
		createProjectionMatrix();
		this.entityRenderer = new EntityRenderer(this.projectionMatrix);
		this.terrainRenderer = new TerrainRenderer(this.projectionMatrix);
		this.skyboxRenderer = new SkyboxRenderer(loader, this.projectionMatrix);
		this.renderers.add(this.entityRenderer);
		this.renderers.add(this.terrainRenderer);
		this.renderers.add(this.skyboxRenderer);
	}
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	
	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void renderScene(List<Entity> entities, List<Terrain> terrains, List<Light> lights, Camera camera) {
		processTerrains(terrains);
		
		processEntities(entities);
		
		render(lights, camera);
	}
	
	public void render(List<Light> lights, Camera camera) {
		prepare();
//		this.entityRenderer.render(this.entities);
//		this.entityRenderer.render(lights, camera);
//		this.terrainRenderer.render(this.terrains);
//		this.terrainRenderer.render(lights, camera);
//		this.skyboxRenderer.render(camera, SKY_RED, SKY_GREEN, SKY_BLUE);
//		this.skyboxRenderer.render(lights, camera);
		for (Renderer renderer : renderers) {
			renderer.render(lights, camera);
		}

		this.entityRenderer.getEntities().clear();
		this.terrainRenderer.getTerrains().clear();
	}
	
	public void processTerrain(Terrain terrain) {
//		this.terrains.add(terrain);
		this.terrainRenderer.getTerrains().add(terrain);
	}
	
	public void processTerrains(List<Terrain> terrains) {
		this.terrainRenderer.getTerrains().addAll(terrains);
//		for (Terrain terrain : terrains) {
//		processTerrain(terrain);
//		this.terrainRenderer.getTerrains().add(terrain);
//	}
	}
	
	public void processEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		Map<TexturedModel, List<Entity>> entities = this.entityRenderer.getEntities();
//		List<Entity> batch = this.entities.get(entityModel);
		List<Entity> batch = entities.get(entityModel);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
//			this.entities.put(entityModel, newBatch);
			entities.put(entityModel, newBatch);
		}
	}
	
	public void processEntities(List<Entity> entities) {
		for (Entity entity : entities) {
			processEntity(entity);
		}
	}
	
	public void cleanUp() {
		this.entityRenderer.cleanUp();
		this.terrainRenderer.cleanUp();
		this.skyboxRenderer.cleanUp();
	}
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(Camera.SKY_RED, Camera.SKY_GREEN, Camera.SKY_BLUE, 1);
	}
	
	private void createProjectionMatrix() {
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustrum_length = FAR_PLANE - NEAR_PLANE;
		
		this.projectionMatrix = new Matrix4f();
		this.projectionMatrix.m00 = x_scale;
		this.projectionMatrix.m11 = y_scale;
		this.projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustrum_length);
		this.projectionMatrix.m23 = -1;
		this.projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustrum_length);
		this.projectionMatrix.m33 = 0;
	}

}
