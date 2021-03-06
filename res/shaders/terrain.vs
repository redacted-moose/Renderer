// Vertex Shader
#version 330 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

const int MAX_LIGHTS = 4;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[MAX_LIGHTS];
out vec3 toCameraVector;
out float visibility;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;
uniform vec3 lightPosition[MAX_LIGHTS];

const float density = 0.0035;
const float gradient = 5.0;

uniform vec4 plane;

void main(void) {

	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	
	gl_ClipDistance[0] = dot(worldPosition, plane);
	
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;
	pass_textureCoords = textureCoords;
	
	surfaceNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;

	for (int i = 0; i < MAX_LIGHTS; i++) {
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}

	// Grab the camera's position from the view matrix and get a vector pointing to it from the world position
	toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
	
	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance * density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);
	
}