package render_engine;

import java.util.List;

import entities.Camera;
import entities.Light;
import shaders.ShaderProgram;

public abstract class Renderer {
	
	protected ShaderProgram shader;
	
	public abstract void render(List<Light> lights, Camera camera);
	
	public void cleanUp() {
		this.shader.cleanUp();
	}

}
