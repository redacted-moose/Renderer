package shaders;

import java.util.ArrayList;

import entities.Light;
import shaders.uniforms.BooleanUniform;
import shaders.uniforms.FloatUniform;
import shaders.uniforms.Matrix4fUniform;
import shaders.uniforms.Uniform;
import shaders.uniforms.Vector2fUniform;
import shaders.uniforms.Vector3fUniform;
import shaders.uniforms.Vector4fUniform;

public class StaticShader extends ShaderProgram {

	private static final String VERTEX_FILE = SHADER_LOC + "static.vs";
	// private static final String FRAGMENT_FILE = SHADER_LOC +
	// "static_cel_shading.fs";
	private static final String FRAGMENT_FILE = SHADER_LOC + "static.fs";

	// TODO: More lights

	public static final String TRANSFORMATION_MATRIX = "transformationMatrix";
	public static final String PROJECTION_MATRIX = "projectionMatrix";
	public static final String VIEW_MATRIX = "viewMatrix";
	public static final String LIGHT_POSITION = "lightPosition";
	public static final String LIGHT_COLOR = "lightColor";
	public static final String ATTENUATION = "attenuation";
	public static final String SHINE_DAMPER = "shineDamper";
	public static final String REFLECTIVITY = "reflectivity";
	public static final String USE_FAKE_LIGHTING = "useFakeLighting";
	public static final String SKY_COLOR = "skyColor";
	public static final String NUMBER_OF_ROWS = "numberOfRows";
	public static final String OFFSET = "offset";
	public static final String PLANE = "plane";

	// private int location_lightPosition[];
	// private int location_lightColor[];
	// private int location_attenuation[];

	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		// for (StaticUniform var : StaticUniform.values()) {
		// this.uniforms.put(var.name, new Uniform(var.name,
		// super.getUniformLocation(var.name)));
		// }

		this.uniforms.put(TRANSFORMATION_MATRIX,
				new Matrix4fUniform(TRANSFORMATION_MATRIX, super.getUniformLocation(TRANSFORMATION_MATRIX)));
		this.uniforms.put(PROJECTION_MATRIX,
				new Matrix4fUniform(PROJECTION_MATRIX, super.getUniformLocation(PROJECTION_MATRIX)));
		this.uniforms.put(VIEW_MATRIX, new Matrix4fUniform(VIEW_MATRIX, super.getUniformLocation(VIEW_MATRIX)));

		this.uniforms.put(SHINE_DAMPER, new FloatUniform(SHINE_DAMPER, super.getUniformLocation(SHINE_DAMPER)));
		this.uniforms.put(REFLECTIVITY, new FloatUniform(REFLECTIVITY, super.getUniformLocation(REFLECTIVITY)));
		this.uniforms.put(NUMBER_OF_ROWS, new FloatUniform(NUMBER_OF_ROWS, super.getUniformLocation(NUMBER_OF_ROWS)));

		this.uniforms.put(USE_FAKE_LIGHTING,
				new BooleanUniform(USE_FAKE_LIGHTING, super.getUniformLocation(USE_FAKE_LIGHTING)));

		this.uniforms.put(OFFSET, new Vector2fUniform(OFFSET, super.getUniformLocation(OFFSET)));

		this.uniforms.put(SKY_COLOR, new Vector3fUniform(SKY_COLOR, super.getUniformLocation(SKY_COLOR)));
		
		this.uniforms.put(PLANE, new Vector4fUniform(PLANE, super.getUniformLocation(PLANE)));

		// location_lightPosition = new [MAX_LIGHTS];
		ArrayList<Uniform> lightPosition = new ArrayList<Uniform>(Light.MAX_LIGHTS);
		ArrayList<Uniform> lightColor = new ArrayList<Uniform>(Light.MAX_LIGHTS);
		ArrayList<Uniform> attenuation = new ArrayList<Uniform>(Light.MAX_LIGHTS);
		// location_lightColor = new int[MAX_LIGHTS];
		// location_attenuation = new int[MAX_LIGHTS];

		// If more than four lights, maybe only grab four nearest to camera?
		for (int i = 0; i < Light.MAX_LIGHTS; i++) {
			// location_lightPosition[i] =
			// super.getUniformLocation("lightPosition[" + i + "]");
			lightPosition
					.add(new Vector3fUniform(LIGHT_POSITION, super.getUniformLocation(LIGHT_POSITION + "[" + i + "]")));
			lightColor.add(new Vector3fUniform(LIGHT_COLOR, super.getUniformLocation(LIGHT_COLOR + "[" + i + "]")));
			attenuation.add(new Vector3fUniform(ATTENUATION, super.getUniformLocation(ATTENUATION + "[" + i + "]")));
			// location_lightColor[i] = super.getUniformLocation("lightColor[" +
			// i + "]");
			// location_attenuation[i] = super.getUniformLocation("attenuation["
			// + i + "]");
		}

		this.uniformArrays.put(LIGHT_POSITION, lightPosition);
		this.uniformArrays.put(LIGHT_COLOR, lightColor);
		this.uniformArrays.put(ATTENUATION, attenuation);
	}

//	public void loadNumberOfRows(float numberOfRows) {
//		// super.loadFloat(this.uniforms.get(StaticUniform.NUMBER_OF_ROWS.name).getHandle(),
//		// numberOfRows);
//		this.uniforms.get(NUMBER_OF_ROWS).load(numberOfRows);
//	}
//
//	public void loadOffset(float x, float y) {
//		// super.loadVector2f(this.uniforms.get(StaticUniform.OFFSET.name).getHandle(),
//		// new Vector2f(x, y));
//		this.uniforms.get(OFFSET).load(new Vector2f(x, y));
//	}
//
//	public void loadTransformationMatrix(Matrix4f matrix) {
//		// super.loadMatrix(this.uniforms.get(StaticUniform.TRANSFORMATION_MATRIX.name).getHandle(),
//		// matrix);
//		this.uniforms.get(TRANSFORMATION_MATRIX).load(matrix);
//	}
//
//	public void loadProjectionMatrix(Matrix4f projection) {
//		// super.loadMatrix(this.uniforms.get(StaticUniform.PROJECTION_MATRIX.name).getHandle(),
//		// projection);
//		this.uniforms.get(PROJECTION_MATRIX).load(projection);
//	}
//
//	public void loadViewMatrix(Camera camera) {
//		// super.loadMatrix(this.uniforms.get(StaticUniform.VIEW_MATRIX.name).getHandle(),
//		// Maths.createViewMatrix(camera));
//		this.uniforms.get(VIEW_MATRIX).load(Maths.createViewMatrix(camera));
//	}
//
//	public void loadLights(List<Light> lights) {
//		for (int i = 0; i < Light.MAX_LIGHTS; i++) {
//			if (i < lights.size()) {
//				Light light = lights.get(i);
//				this.uniformArrays.get(LIGHT_POSITION).get(i).load(light.getPosition());
//				this.uniformArrays.get(LIGHT_COLOR).get(i).load(light.getColor());
//				this.uniformArrays.get(ATTENUATION).get(i).load(light.getAttenuation());
//				// super.loadVector3f(location_lightColor[i],
//				// lights.get(i).getColor());
//				// super.loadVector3f(location_attenuation[i],
//				// lights.get(i).getAttenuation());
//			} else {
//				// super.loadVector3f(location_lightPosition[i], new Vector3f(0,
//				// 0, 0));
//				// super.loadVector3f(location_lightColor[i], new Vector3f(0, 0,
//				// 0));
//				// super.loadVector3f(location_attenuation[i], new Vector3f(1,
//				// 0, 0));
//				this.uniformArrays.get(LIGHT_POSITION).get(i).load(new Vector3f(0, 0, 0));
//				this.uniformArrays.get(LIGHT_COLOR).get(i).load(new Vector3f(0, 0, 0));
//				this.uniformArrays.get(ATTENUATION).get(i).load(new Vector3f(1, 0, 0));
//			}
//		}
//	}
//
//	public void loadShineVariables(float damper, float reflectivity) {
//		// super.loadFloat(this.uniforms.get(StaticUniform.SHINE_DAMPER.name).getHandle(),
//		// damper);
//		this.uniforms.get(SHINE_DAMPER).load(damper);
//		// super.loadFloat(this.uniforms.get(StaticUniform.REFLECTIVITY.name).getHandle(),
//		// reflectivity);
//		this.uniforms.get(REFLECTIVITY).load(reflectivity);
//	}
//
//	public void loadFakeLightingVariable(boolean useFake) {
//		// super.loadBoolean(this.uniforms.get(StaticUniform.USE_FAKE_LIGHTING.name).getHandle(),
//		// useFake);
//		this.uniforms.get(USE_FAKE_LIGHTING).load(useFake);
//	}
//
//	public void loadSkyColor(float r, float g, float b) {
//		// super.loadVector3f(this.uniforms.get(StaticUniform.SKY_COLOR.name).getHandle(),
//		// new Vector3f(r, g, b));
//		this.uniforms.get(SKY_COLOR).load(new Vector3f(r, g, b));
//	}
//
//	public void loadSkyColor(Vector3f color) {
//		// super.loadVector3f(this.uniforms.get(StaticUniform.SKY_COLOR.name).getHandle(),
//		// new Vector3f(r, g, b));
//		this.uniforms.get(SKY_COLOR).load(color);
//	}
}
