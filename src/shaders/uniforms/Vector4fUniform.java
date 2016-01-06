package shaders.uniforms;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Vector4fUniform extends Uniform {

	public Vector4fUniform(String name, int handle) {
		super(name, handle);
	}
	
	@Override
	public void load(Object data) {
		if (data instanceof Vector4f) {
			Vector4f vector = (Vector4f) data;
			GL20.glUniform4f(this.handle, vector.x, vector.y, vector.z, vector.w);
		} else {
			super.load(data);
		}
	}

}
