#version 150 core

#define MIN_FOG_DIST 4
#define POINT_LIGHT_R 1

uniform mat4 viewMatrix;
uniform vec3 cameraPosition;

uniform sampler2D texSky;
uniform sampler2D dataPointLights;
uniform sampler2D texDiffuse;
uniform sampler2D texIllum;

uniform float levelSize;

uniform vec3 lightDirection = vec3(0.5, -0.65, -0.5);
uniform vec4 lightColor = vec4(1, 0.949, 0.850, 1);
uniform vec4 midColor = vec4(0.301, 0.450, 0.8, 1);
uniform vec4 shadowColor = vec4(0.250, 0.349, 0.6, 1);

uniform vec4 pointLightColor = vec4(1.0, 0.98, 0.9, 1);
uniform float illumTrigger = 2;

uniform float fogNear = 40;
uniform float fogFar = 160;

uniform float cloudTop = 0;
uniform float cloudBottom = -40;
uniform float cloudNear = 20;

in vec4 pass_Position;
in vec3 pass_Normal;
in vec2 pass_TexCoord;

in vec4 pass_WorldPosition;
in vec2 pass_SkyCoord;
in vec3 pass_illumMod;

out vec4 out_Color;

vec4 calcPointLight(vec2 index, vec3 normal) {
	vec4 lightPos = texture(dataPointLights, index/levelSize);
	float radius = lightPos.w;
	lightPos.w = 1;
	vec3 lightVec = (viewMatrix * (lightPos - pass_WorldPosition)).xyz;
	
	float dist = length(lightVec);
	float falloff = clamp((radius-dist)/(radius+dist), 0, 1);
	float angle = clamp(dot(normal, normalize(lightVec)), 0, 1);
	return pointLightColor*angle*falloff;
}

vec4 calcPointLights(vec3 normal) {
	vec4 light = vec4(0);
	vec2 center = vec2(floor(pass_WorldPosition.x/4+0.5), floor(pass_WorldPosition.z/4+0.5));
	for(int dx=-POINT_LIGHT_R; dx<=POINT_LIGHT_R; dx++)
		for(int dz=-POINT_LIGHT_R; dz<=POINT_LIGHT_R; dz++) {
			light += calcPointLight(center+vec2(dx, dz), normal);
		}
	return light;
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
	vec3 lightDir = normalize((viewMatrix * vec4(-lightDirection, 0)).xyz);
	float diffuse = dot(normal, lightDir);
	vec4 diffuseLight = diffuse>=0 ? mix(midColor, lightColor, diffuse) : mix(midColor, shadowColor, -diffuse);
	// float spec = pow(max(dot(viewDir, normalize(reflect(-lightDir, normal))), 0), specPower);
	
	if(illum<illumTrigger) {
		diffuseLight += calcPointLights(normal);
	}
	
	out_Color = diffuseColor * diffuseLight; // + specColor * lightColor * spec;
	if(illum<illumTrigger) {
		vec4 illumColor = texture(texIllum, pass_TexCoord) * vec4(pass_illumMod, 0);
		out_Color = max(out_Color, illumColor);
		float lightDist = viewDist * (1 - 0.5 * (length(illumColor.xyz) / sqrt(3.0)));
		viewDist = mix(lightDist, viewDist, clamp((viewDist-fogFar+12)/12, 0, 1));
	}
	
	vec4 fogColor = texture(texSky, pass_SkyCoord);
	float lowNear = clamp((viewDist - MIN_FOG_DIST) / (cloudNear - MIN_FOG_DIST), 0, 1);
	vec4 highColor = mix(out_Color, fogColor, clamp((viewDist - fogNear) / (fogFar - fogNear), 0, 1));
	highColor = mix(highColor, fogColor, clamp((pass_WorldPosition.y - cloudTop) / (cloudBottom - cloudTop), 0, 1) * lowNear);
	vec4 lowColor = mix(out_Color, fogColor, lowNear);
	out_Color = mix(highColor, lowColor, clamp((cameraPosition.y-cloudTop)/(cloudBottom-cloudTop), 0, 1));
	
	out_Color.a = diffuseColor.a;
}
