package shaders.uniforms;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector2f;

public class Vector2fUniform extends Uniform {

	public Vector2fUniform(String name, int handle) {
		super(name, handle);
	}

	@Override
	public void load(Object data) {
		if (data instanceof Vector2f) {
			Vector2f vector = (Vector2f) data;
			GL20.glUniform2f(this.handle, vector.x, vector.y);
		} else {
			super.load(data);
		}
	}

}
