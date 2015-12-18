package shaders.uniforms;

import org.lwjgl.opengl.GL20;

public class FloatUniform extends Uniform {

	public FloatUniform(String name, int handle) {
		super(name, handle);
	}

	@Override
	public void load(Object data) {
		if (data instanceof Float) {
			Float f = (Float) data;
			GL20.glUniform1f(this.handle, f);
		} else {
			super.load(data);
		}
	}

}
