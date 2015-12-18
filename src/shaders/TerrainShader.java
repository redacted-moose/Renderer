package shaders;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Light;
import shaders.uniforms.FloatUniform;
import shaders.uniforms.IntUniform;
import shaders.uniforms.Matrix4fUniform;
import shaders.uniforms.Uniform;
import shaders.uniforms.Vector3fUniform;
import toolbox.Maths;

public class TerrainShader extends ShaderProgram {

	// private static final int MAX_LIGHTS = 4;

	private static final String VERTEX_FILE = SHADER_LOC + "terrain.vs";
	// private static final String FRAGMENT_FILE = SHADER_LOC +
	// "terrain_cel_shading.fs";
	private static final String FRAGMENT_FILE = SHADER_LOC + "terrain.fs";

	public static final String TRANSFORMATION_MATRIX = "transformationMatrix";
	public static final String PROJECTION_MATRIX = "projectionMatrix";
	public static final String VIEW_MATRIX = "viewMatrix";
	public static final String LIGHT_POSITION = "lightPosition";
	public static final String LIGHT_COLOR = "lightColor";
	public static final String ATTENUATION = "attenuation";
	public static final String SHINE_DAMPER = "shineDamper";
	public static final String REFLECTIVITY = "reflectivity";
	public static final String SKY_COLOR = "skyColor";
	public static final String BACKGROUND_TEXTURE = "backgroundTexture";
	public static final String R_TEXTURE = "rTexture";
	public static final String G_TEXTURE = "gTexture";
	public static final String B_TEXTURE = "bTexture";
	public static final String BLEND_MAP = "blendMap";

	private int location_lightPosition[];
	private int location_lightColor[];
	private int location_attenuation[];

	public TerrainShader() {
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
		// for (TerrainUniform var : TerrainUniform.values()) {
		// this.uniforms.put(var.name, new Uniform(var.name, var.type,
		// super.getUniformLocation(var.name)));
		// }

		this.uniforms.put(TRANSFORMATION_MATRIX,
				new Matrix4fUniform(TRANSFORMATION_MATRIX, super.getUniformLocation(TRANSFORMATION_MATRIX)));
		this.uniforms.put(PROJECTION_MATRIX,
				new Matrix4fUniform(PROJECTION_MATRIX, super.getUniformLocation(PROJECTION_MATRIX)));
		this.uniforms.put(VIEW_MATRIX, new Matrix4fUniform(VIEW_MATRIX, super.getUniformLocation(VIEW_MATRIX)));

		this.uniforms.put(SHINE_DAMPER, new FloatUniform(SHINE_DAMPER, super.getUniformLocation(SHINE_DAMPER)));
		this.uniforms.put(REFLECTIVITY, new FloatUniform(REFLECTIVITY, super.getUniformLocation(REFLECTIVITY)));

		this.uniforms.put(BACKGROUND_TEXTURE,
				new IntUniform(BACKGROUND_TEXTURE, super.getUniformLocation(BACKGROUND_TEXTURE)));
		this.uniforms.put(R_TEXTURE, new IntUniform(R_TEXTURE, super.getUniformLocation(R_TEXTURE)));
		this.uniforms.put(G_TEXTURE, new IntUniform(G_TEXTURE, super.getUniformLocation(G_TEXTURE)));
		this.uniforms.put(B_TEXTURE, new IntUniform(B_TEXTURE, super.getUniformLocation(B_TEXTURE)));
		this.uniforms.put(BLEND_MAP, new IntUniform(BLEND_MAP, super.getUniformLocation(BLEND_MAP)));

		this.uniforms.put(SKY_COLOR, new Vector3fUniform(SKY_COLOR, super.getUniformLocation(SKY_COLOR)));

		// location_lightPosition = new int[Light.MAX_LIGHTS];
		// location_lightColor = new int[Light.MAX_LIGHTS];
		// location_attenuation = new int[Light.MAX_LIGHTS];
		//
		// for (int i = 0; i < Light.MAX_LIGHTS; i++) {
		// location_lightPosition[i] = super.getUniformLocation("lightPosition["
		// + i + "]");
		// location_lightColor[i] = super.getUniformLocation("lightColor[" + i +
		// "]");
		// location_attenuation[i] = super.getUniformLocation("attenuation[" + i
		// + "]");
		// }

		ArrayList<Uniform> lightPosition = new ArrayList<Uniform>(Light.MAX_LIGHTS);
		ArrayList<Uniform> lightColor = new ArrayList<Uniform>(Light.MAX_LIGHTS);
		ArrayList<Uniform> attenuation = new ArrayList<Uniform>(Light.MAX_LIGHTS);

		for (int i = 0; i < Light.MAX_LIGHTS; i++) {
			lightPosition.add(new Vector3fUniform(LIGHT_POSITION, super.getUniformLocation(LIGHT_POSITION + "[" + i + "]")));
			lightColor.add(new Vector3fUniform(LIGHT_COLOR, super.getUniformLocation(LIGHT_COLOR + "[" + i + "]")));
			attenuation.add(new Vector3fUniform(ATTENUATION, super.getUniformLocation(ATTENUATION + "[" + i + "]")));
		}
		
		this.uniformArrays.put(LIGHT_POSITION, lightPosition);
		this.uniformArrays.put(LIGHT_COLOR, lightColor);
		this.uniformArrays.put(ATTENUATION, attenuation);
	}

	@Override
	public void connectTextureUnits() {
		super.loadInt(this.uniforms.get(BACKGROUND_TEXTURE).getHandle(), 0);
		super.loadInt(this.uniforms.get(R_TEXTURE).getHandle(), 1);
		super.loadInt(this.uniforms.get(G_TEXTURE).getHandle(), 2);
		super.loadInt(this.uniforms.get(B_TEXTURE).getHandle(), 3);
		super.loadInt(this.uniforms.get(BLEND_MAP).getHandle(), 4);
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(this.uniforms.get(TRANSFORMATION_MATRIX).getHandle(), matrix);
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(this.uniforms.get(PROJECTION_MATRIX).getHandle(), projection);
	}

	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(this.uniforms.get(VIEW_MATRIX).getHandle(), viewMatrix);
	}

	public void loadLights(List<Light> lights) {
		for (int i = 0; i < Light.MAX_LIGHTS; i++) {
			if (i < lights.size()) {
				super.loadVector3f(location_lightPosition[i], lights.get(i).getPosition());
				super.loadVector3f(location_lightColor[i], lights.get(i).getColor());
				super.loadVector3f(location_attenuation[i], lights.get(i).getAttenuation());
			} else {
				super.loadVector3f(location_lightPosition[i], new Vector3f(0, 0, 0));
				super.loadVector3f(location_lightColor[i], new Vector3f(0, 0, 0));
				super.loadVector3f(location_attenuation[i], new Vector3f(1, 0, 0));
			}
		}
	}

	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(this.uniforms.get(SHINE_DAMPER).getHandle(), damper);
		super.loadFloat(this.uniforms.get(REFLECTIVITY).getHandle(), reflectivity);
	}

	public void loadSkyColor(float r, float g, float b) {
		super.loadVector3f(this.uniforms.get(SKY_COLOR).getHandle(), new Vector3f(r, g, b));
	}
}
