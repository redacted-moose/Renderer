#version 330 core

in vec2 textureCoords;
in vec4 clipSpace;
in vec3 toCameraVector;
in vec3 fromLightVector;

out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;
uniform sampler2D normalMap;
uniform sampler2D depthMap;
uniform vec3 lightColor;

uniform float moveFactor;

const float waveStrength = 0.04;
const float shineDamper = 20.0;
const float reflectivity = 0.5;

void main(void) {

	vec2 ndc = (clipSpace.xy / clipSpace.w) / 2.0 + 0.5; // Convert to normal device space coordinates (ndc)
	vec2 refractTexCoords = vec2(ndc.x, ndc.y);
	vec2 reflectTexCoords = vec2(ndc.x, -ndc.y);
	
	float near = 0.1; // Matches NEAR_PLANE in MasterRenderer
	float far = 1000; // Matches FAR_PLANE in MasterRenderer
	float depth = texture(depthMap, refractTexCoords).r;
	float floorDistance = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near)); // Calculate depth based on depth coordinate
	
	depth = gl_FragCoord.z;
	float waterDistance = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));
	float waterDepth = floorDistance - waterDistance;
	
	// vec2 distortion1 = (texture(dudvMap, vec2(textureCoords.x + moveFactor, textureCoords.y)).rg * 2.0 - 1.0) * waveStrength;
	// vec2 distortion2 = (texture(dudvMap, vec2(-textureCoords.x + moveFactor, textureCoords.y + moveFactor)).rg * 2.0 - 1.0) * waveStrength * clamp(waterDepth / 20.0, 0.0, 1.0); // See note below for info about 20.0
	// vec2 totalDistortion = distortion1 + distortion2;
	
	// Wave Distortion
	vec2 distortedTexCoords = texture(dudvMap, vec2(textureCoords.x + moveFactor, textureCoords.y)).rg*0.1;
	distortedTexCoords = textureCoords + vec2(distortedTexCoords.x, distortedTexCoords.y+moveFactor);
	vec2 totalDistortion = (texture(dudvMap, distortedTexCoords).rg * 2.0 - 1.0) * waveStrength;
	
	refractTexCoords += totalDistortion;
	refractTexCoords = clamp(refractTexCoords, 0.001, 0.999);

	reflectTexCoords += totalDistortion;
	reflectTexCoords.x = clamp(reflectTexCoords.x, 0.001, 0.999);
	reflectTexCoords.y = clamp(reflectTexCoords.y, -0.999, -0.001);

	// vec4 reflectColor = texture(reflectionTexture, textureCoords);
	vec4 reflectColor = texture(reflectionTexture, reflectTexCoords);
	// vec4 refractColor = texture(refractionTexture, textureCoords);
	vec4 refractColor = texture(refractionTexture, refractTexCoords);
	
	vec4 normalMapColor = texture(normalMap, distortedTexCoords);
	vec3 normal = vec3(normalMapColor.r * 2.0 - 1.0, normalMapColor.b + 3.0, normalMapColor.g * 2.0 - 1.0); // 3.0 is offset to make water look less "bumpy"
	normal = normalize(normal);
	
	vec3 viewVector = normalize(toCameraVector);
	// float refractiveFactor = dot(viewVector, vec3(0.0, 1.0, 0.0)); // viewVector <dot> water surface normal
	float refractiveFactor = dot(viewVector, normal); // viewVector <dot> water surface normal
	refractiveFactor = pow(refractiveFactor, 0.5); // Increase or decrease refractiveFactor
	
	// Specular lighting
	vec3 reflectedLight = reflect(normalize(fromLightVector), normal);
	float specular = max(dot(reflectedLight, viewVector), 0.0);
	specular = pow(specular, shineDamper);
	vec3 specularHighlights = lightColor * specular * reflectivity * clamp(waterDepth / 5.0, 0.0, 1.0);

	// out_Color = vec4(0.0, 0.0, 1.0, 1.0);
	out_Color = mix(reflectColor, refractColor, refractiveFactor);
	out_Color = mix(out_Color, vec4(0.0, 0.3, 0.5, 1.0), 0.2) + vec4(specularHighlights, 0.0); // Mix output color with a slightly blue tint
	out_Color.a = clamp(waterDepth / 5.0, 0.0, 1.0); // 5.0 represents depth of fully alpha blended water
	
	// out_Color = vec4(waterDepth/50.0); // Visualization of water depth

}