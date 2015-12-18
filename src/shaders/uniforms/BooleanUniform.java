package shaders.uniforms;

import org.lwjgl.opengl.GL20;

public class BooleanUniform extends Uniform {

	public BooleanUniform(String name, int handle) {
		super(name, handle);
	}
	
	@Override
	public void load(Object data) {
		if (data instanceof Boolean) {
			Boolean bool = (Boolean) data;
			float toLoad = (bool) ? 1 : 0;
			GL20.glUniform1f(this.handle, toLoad);
		} else {
			super.load(data);
		}
	}
		
}
