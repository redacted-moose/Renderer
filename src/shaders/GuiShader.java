package shaders;

import org.lwjgl.util.vector.Matrix4f;

import shaders.uniforms.Matrix4fUniform;

public class GuiShader extends ShaderProgram{
	
	private static final String VERTEX_FILE = SHADER_LOC + "gui.vs";
	private static final String FRAGMENT_FILE = SHADER_LOC + "gui.fs";
	
	public static final String TRANSFORMATION_MATRIX = "transformationMatrix";

	public GuiShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	public void loadTransformation(Matrix4f matrix){
//		super.loadMatrix(location_transformationMatrix, matrix);
		this.uniforms.get(TRANSFORMATION_MATRIX).load(matrix);
	}

	@Override
	protected void getAllUniformLocations() {
//		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		this.uniforms.put(TRANSFORMATION_MATRIX, new Matrix4fUniform(TRANSFORMATION_MATRIX, super.getUniformLocation(TRANSFORMATION_MATRIX)));
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	
	

}
