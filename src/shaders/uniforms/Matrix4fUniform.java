package shaders.uniforms;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

public class Matrix4fUniform extends Uniform {
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

	public Matrix4fUniform(String name, int handle) {
		super(name, handle);
	}
	
	@Override
	public void load(Object data) {
		if (data instanceof Matrix4f) {
			Matrix4f matrix = (Matrix4f) data;
			matrix.store(matrixBuffer);
			matrixBuffer.flip();
			GL20.glUniformMatrix4(this.handle, false, matrixBuffer);
		} else {
			super.load(data);
		}

	}

}
