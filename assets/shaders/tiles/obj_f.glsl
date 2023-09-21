#version 150 core

#define MIN_FOG_DIST 4
#define POINT_LIGHT_R 1
#define EPSILON 0.001

uniform vec3 cameraPosition;

uniform sampler2D texSky;
uniform sampler2D dataPointLights;
uniform sampler2D dataBlockLighting;
uniform sampler2D texDiffuse;
#ifdef ILLUM_TILE
uniform sampler2D texIllum;
#endif

uniform float levelSize;

uniform vec3 lightDirection = vec3(0.5, -0.65, -0.5);
uniform vec4 lightColor = vec4(1, 0.949, 0.850, 1);
uniform vec4 midColor = vec4(0.301, 0.450, 0.8, 1);
uniform vec4 shadowColor = vec4(0.250, 0.349, 0.6, 1);

uniform vec4 pointLightColor = vec4(1.0, 0.98, 0.9, 1);
uniform float illumTrigger = 2;
#ifdef ILLUM_TILE
uniform int illumMask = 0;
#endif

uniform float fogNear = 40;
uniform float fogFar = 160;

uniform float cloudTop = 0;
uniform float cloudBottom = -40;
uniform float cloudNear = 20;

in vec4 pass_Position;
in vec3 pass_Normal;
in vec2 pass_TexCoord;

in vec4 pass_LevelPosition;
in vec2 pass_SkyCoord;
#ifdef ILLUM_TILE
flat in vec3 pass_illumMod;
flat in int pass_illumMask;
flat in float pass_illumTrigger;
#endif

out vec4 out_Color;

float calcPointLight(ivec2 index, vec3 normal) {
	vec4 lightPos = texelFetch(dataPointLights, index, 0);
	float radius = lightPos.w;
	lightPos.w = 1;
	vec3 lightVec = (lightPos - pass_LevelPosition).xyz;
	
	float dist = length(lightVec);
	float falloff = clamp((radius-dist)/(radius+dist), 0, 1);
	float angle = clamp(dot(normal, normalize(lightVec)), 0, 1);
	return angle*falloff;
}

vec4 calcPointLights(vec3 normal) {
	ivec2 center = ivec2(floor(pass_LevelPosition.x/4+(0.5+EPSILON)), floor(pass_LevelPosition.z/4+(0.5+EPSILON)));
	float light = 0;
	for(int dx=-POINT_LIGHT_R; dx<=POINT_LIGHT_R; dx++)
		for(int dz=-POINT_LIGHT_R; dz<=POINT_LIGHT_R; dz++) {
			light += calcPointLight(center+ivec2(dx, dz), normal);
		}
	return pointLightColor*light;
}

void main(void) {
	vec4 diffuseColor = texture(texDiffuse, pass_TexCoord);
	if(diffuseColor.a<0.5)
		discard;
	
	// vec4 specMask = texture(texSpecMask, pass_TexCoord);
	// float specPower = specMask.r*255+1;
	// vec4 specColor = mix(diffuseColor, vec4(1, 1, 1, 1), specMask.b) * specMask.g;
	
	vec3 normal = gl_FrontFacing ? pass_Normal : -pass_Normal;
	
	float viewDist = length(pass_Position.xyz);
	vec3 viewDir = normalize(-pass_Position.xyz);
	
	float illum = lightColor.x+lightColor.y+lightColor.z;
	vec3 lightDir = normalize(-lightDirection);
	float diffuse = dot(normal, lightDir);
	vec4 diffuseLight = diffuse>=0 ? mix(midColor, lightColor, diffuse) : mix(midColor, shadowColor, -diffuse);
	// float spec = pow(max(dot(viewDir, normalize(reflect(-lightDir, normal))), 0), specPower);
	
	vec4 blockLight = vec4(0);
	if(illum<illumTrigger) {
		diffuseLight += calcPointLights(normal);
		blockLight = texture(dataBlockLighting, (pass_LevelPosition.xz/4+0.5+0.5*normal.xz)/levelSize)*(1-illum/illumTrigger);
		diffuseLight += 0.4*blockLight;
	}
	
	out_Color = diffuseColor * diffuseLight; // + specColor * lightColor * spec;
	#ifdef ILLUM_TILE
	if(illum<pass_illumTrigger && (pass_illumMask & illumMask)!=0) {
		vec4 illumColor = texture(texIllum, pass_TexCoord) * vec4(pass_illumMod, 0);
		out_Color = max(out_Color, illumColor);
		float lightDist = viewDist * (1 - 0.5 * (length(illumColor.xyz) / sqrt(3.0)));
		viewDist = mix(lightDist, viewDist, clamp((viewDist-fogFar+12)/12, 0, 1));
	}
	#endif
	
	vec4 fogColor = texture(texSky, pass_SkyCoord);
	float lowNear = clamp((viewDist - MIN_FOG_DIST) / (cloudNear - MIN_FOG_DIST), 0, 1);
	vec4 highColor = mix(out_Color, fogColor, clamp((viewDist - fogNear) / (fogFar - fogNear), 0, 1));
	highColor = mix(highColor, fogColor+0.075*blockLight, clamp((pass_LevelPosition.y - cloudTop) / (cloudBottom - cloudTop), 0, 1) * lowNear);
	vec4 lowColor = mix(out_Color, fogColor, lowNear);
	out_Color = mix(highColor, lowColor, clamp((cameraPosition.y-cloudTop)/(cloudBottom-cloudTop), 0, 1));
	
	out_Color.a = diffuseColor.a;
}
