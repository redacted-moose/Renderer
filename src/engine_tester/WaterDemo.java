package engine_tester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import render_engine.DisplayManager;
import render_engine.GuiRenderer;
import render_engine.MasterRenderer;
import render_engine.WaterRenderer;
import resource_loaders.Loader;
import resource_loaders.OBJLoader;
import shaders.WaterShader;
import terrains.Terrain;
import textures.GuiTexture;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterTile;

public class WaterDemo {

	public static void main(String[] args) {

		// TODO: Make preferences loader

		DisplayManager.createDisplay();
		Loader loader = new Loader();

		// ********TERRAIN TEXTURE STUFF***********

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		// ****************************************

		// ModelData data = OBJFileLoader.loadOBJ("tree");
		// RawModel treeModel = loader.loadToVAO(data.getVertices(),
		// data.getTextureCoords(), data.getNormals(),
		// data.getIndices());
		RawModel model = OBJLoader.loadObjModel("tree", loader);

		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("tree")));
		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),
				new ModelTexture(loader.loadTexture("grassTexture")));
		TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),
				new ModelTexture(loader.loadTexture("flower")));
		TexturedModel bobble = new TexturedModel(OBJLoader.loadObjModel("lowPolyTree", loader),
				new ModelTexture(loader.loadTexture("lowPolyTree")));
		TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp", loader),
				new ModelTexture(loader.loadTexture("lamp")));

		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		fernTextureAtlas.setNumberOfRows(2);

		TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader), fernTextureAtlas);

		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		flower.getTexture().setHasTransparency(true);
		flower.getTexture().setUseFakeLighting(true);
		fern.getTexture().setHasTransparency(true);
		lamp.getTexture().setUseFakeLighting(true);

		List<Terrain> terrains = new ArrayList<Terrain>();
		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");
		terrains.add(terrain);
		// Terrain terrain2 = new Terrain(-1, -1, loader, texturePack, blendMap,
		// "heightMap");
		// TODO: Add support for multiple terrains

		List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random();
		for (int i = 0; i < 500; i++) {
			float x, y, z;
			if (i % 2 == 0) {
				x = random.nextFloat() * 800 - 400;
				z = random.nextFloat() * -600;
				y = terrain.getHeightOfTerrain(x, z);

				entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0,
						0.9f));

			}
			if (i % 5 == 0) {

				x = random.nextFloat() * 800 - 400;
				z = random.nextFloat() * -600;
				y = terrain.getHeightOfTerrain(x, z);

				entities.add(new Entity(bobble, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0,
						random.nextFloat() * 0.1f + 0.6f));

				x = random.nextFloat() * 800 - 400;
				z = random.nextFloat() * -600;
				y = terrain.getHeightOfTerrain(x, z);

				entities.add(new Entity(staticModel, new Vector3f(x, y, z), 0, 0, 0, random.nextFloat() * 1 + 4));

//				x = random.nextFloat() * 800 - 400;
//				z = random.nextFloat() * -600;
//				y = terrain.getHeightOfTerrain(x, z);
//
//				entities.add(new Entity(grass, new Vector3f(x, y, z), 0, 0, 0, 1.8f));

//				x = random.nextFloat() * 800 - 400;
//				z = random.nextFloat() * -600;
//				y = terrain.getHeightOfTerrain(x, z);
//
//				entities.add(new Entity(flower, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 2.3f));
			}
		}
		
		List<Light> lights = new ArrayList<Light>();
		lights.add(new Light(new Vector3f(2000, 2000, 4000), new Vector3f(1.0f, 1.0f, 1.0f)));
		lights.add(new Light(new Vector3f(185, 10, -293), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
		lights.add(new Light(new Vector3f(370, 17, -300), new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f)));
		lights.add(new Light(new Vector3f(293, 7, -305), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f)));
		
		entities.add(new Entity(lamp, new Vector3f(185, -4.7f, -293), 0, 0, 0, 1));
		entities.add(new Entity(lamp, new Vector3f(370, 4.2f, -300), 0, 0, 0, 1));
		entities.add(new Entity(lamp, new Vector3f(293, -6.8f, -305), 0, 0, 0, 1));

		MasterRenderer renderer = new MasterRenderer(loader);

		RawModel bunnyModel = OBJLoader.loadObjModel("stanfordBunny", loader);
		TexturedModel stanfordBunny = new TexturedModel(bunnyModel, new ModelTexture(loader.loadTexture("white")));

		Player player = new Player(stanfordBunny, new Vector3f(100, 0, -50), 0, 0, 0, 1);
		entities.add(player);
		Camera camera = new Camera(player);
		
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
//		GuiTexture gui = new GuiTexture(loader.loadTexture("socuwan"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
//		guis.add(gui);
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);

		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);
		
		Entity lampEntity = (new Entity(lamp, new Vector3f(293, -6.8f, -305), 0, 0, 0, 1));
		entities.add(lampEntity);

		Light light = (new Light(new Vector3f(293, 7, -305), new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f)));
		lights.add(light);
		
		WaterShader waterShader = new WaterShader();
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		WaterTile water = new WaterTile(75, -75, 5);
		waters.add(water);
		
		
//		GuiTexture refraction =  new GuiTexture(fbos.getRefractionTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
//		GuiTexture reflection =  new GuiTexture(fbos.getReflectionTexture(), new Vector2f(-0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
//		guis.add(refraction);
//		guis.add(reflection);
		
		while (!Display.isCloseRequested()) {
			player.move(terrain);
			camera.move();
			picker.update();
			
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
			// Render reflection
			fbos.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, 1, 0, -water.getHeight() + 1f)); // Offset to minimize edge distortion glitch
			camera.getPosition().y += distance;
			camera.invertPitch();

			// Render refraction
			fbos.bindRefractionFrameBuffer();
			renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, -1, 0, water.getHeight() + 1f)); // Offset to minimize edge distortion glitch

			// Render to screen
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			fbos.unbindCurrentFrameBuffer();
			// If graphics driver ignores the glDisable call, clip plane is set really high so it doesn't clip anything
			renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, 1, 0, 1000000));
			waterRenderer.render(waters, camera, lights.get(0));
			guiRenderer.render(guis);

			DisplayManager.updateDisplay();
		}

		fbos.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

}
