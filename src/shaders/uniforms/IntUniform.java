package shaders.uniforms;

import org.lwjgl.opengl.GL20;

public class IntUniform extends Uniform {

	public IntUniform(String name, int handle) {
		super(name, handle);
	}
	
	@Override
	public void load(Object data) {
		if (data instanceof Integer) {
			Integer i = (Integer) data;
			GL20.glUniform1i(handle, i);
		} else {
			super.load(data);
		}
	}

}
