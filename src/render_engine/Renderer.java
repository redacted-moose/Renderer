package render_engine;

import java.util.List;

import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Light;
import shaders.ShaderProgram;

public abstract class Renderer {
	
	protected ShaderProgram shader;
	
	public abstract void render(List<Light> lights, Camera camera, Vector4f clipPlane);
	
	public void cleanUp() {
		this.shader.cleanUp();
	}

}
