package shaders.uniforms;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;

public class Vector3fUniform extends Uniform {

	public Vector3fUniform(String name, int handle) {
		super(name, handle);
	}
	
	@Override
	public void load(Object data) {
		if (data instanceof Vector3f) {
			Vector3f vector = (Vector3f) data;
			GL20.glUniform3f(this.handle, vector.x, vector.y, vector.z);
		} else {
			super.load(data);
		}
	}

}
