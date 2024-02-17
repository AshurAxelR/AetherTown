#version 150 core

#define MIN_FOG_DIST 4
#define POINT_LIGHT_R 1
#define EPSILON 0.001

#ifdef TUNNEL_TILE
	#define LIGHT_FALLOFF 3.0
	#define LIGHT_TRIGGER_FALLOFF 6.0
#endif

uniform vec3 cameraPosition;

uniform sampler2D texSky;
uniform sampler2D dataPointLights;
uniform sampler2D dataBlockLighting;
#ifdef TUNNEL_TILE
	uniform sampler2D dataTunnelDepth;
#endif

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

vec4 calcPointLights(vec3 normal, float brightness) {
	ivec2 center = ivec2(floor(pass_LevelPosition.x/4+(0.5+EPSILON)), floor(pass_LevelPosition.z/4+(0.5+EPSILON)));
	vec2 centerf = vec2(center) + vec2(0.5, 0.5);
	float light = 0;
	for(int dx=-POINT_LIGHT_R; dx<=POINT_LIGHT_R; dx++)
		for(int dz=-POINT_LIGHT_R; dz<=POINT_LIGHT_R; dz++) {
			float value = calcPointLight(center+ivec2(dx, dz), normal);
			#ifdef TUNNEL_TILE
				float tunnelFactor = LIGHT_TRIGGER_FALLOFF / (LIGHT_TRIGGER_FALLOFF + texture(dataTunnelDepth, (centerf+vec2(dx,dz))/levelSize).x);
				value *= (brightness*tunnelFactor < illumTrigger) ? 1 : 0;
			#endif
			light += value;
		}
	return pointLightColor*light;
}

void main(void) {
	vec4 diffuseColor = texture(texDiffuse, pass_TexCoord);
	if(diffuseColor.a<0.3)
		discard;
	
	// vec4 specMask = texture(texSpecMask, pass_TexCoord);
	// float specPower = specMask.r*255+1;
	// vec4 specColor = mix(diffuseColor, vec4(1, 1, 1, 1), specMask.b) * specMask.g;
	
	vec3 normal = gl_FrontFacing ? pass_Normal : -pass_Normal;
	vec2 blockTexCoord = (pass_LevelPosition.xz/4+0.5+0.5*normal.xz)/levelSize;
	
	float viewDist = length(pass_Position.xyz);
	vec3 viewDir = normalize(-pass_Position.xyz);
	
	float brightness = lightColor.x+lightColor.y+lightColor.z;
	vec3 lightDir = normalize(-lightDirection);
	float diffuse = dot(normal, lightDir);
	#ifdef TUNNEL_TILE
		float tunnelFactor = LIGHT_FALLOFF / (LIGHT_FALLOFF + texture(dataTunnelDepth, blockTexCoord).x);
		diffuse = (diffuse+1) * tunnelFactor - 1;
		vec4 diffuseLight = diffuse>=0 ? mix(midColor, lightColor, diffuse) : mix(midColor, shadowColor, -diffuse);
		diffuseLight *= tunnelFactor;
	#else
		vec4 diffuseLight = diffuse>=0 ? mix(midColor, lightColor, diffuse) : mix(midColor, shadowColor, -diffuse);
	#endif
	// float spec = pow(max(dot(viewDir, normalize(reflect(-lightDir, normal))), 0), specPower);
	
	vec4 blockLight = vec4(0);
	#ifdef TUNNEL_TILE
		diffuseLight += calcPointLights(normal, brightness);
		if(brightness<illumTrigger) {
			blockLight = texture(dataBlockLighting, blockTexCoord)*(1-brightness/illumTrigger);
			diffuseLight += 0.4*blockLight * tunnelFactor;
		}
	#else
		if(brightness<illumTrigger) {
			diffuseLight += calcPointLights(normal, brightness);
			blockLight = texture(dataBlockLighting, blockTexCoord)*(1-brightness/illumTrigger);
			diffuseLight += 0.4*blockLight;
		}
	#endif
	
	out_Color = diffuseColor * diffuseLight; // + specColor * lightColor * spec;
	#ifdef ILLUM_TILE
		vec4 illumColor = texture(texIllum, pass_TexCoord) * vec4(pass_illumMod, 0);
		float illumBrigtness = (illumColor.x + illumColor.y + illumColor.z) / 3.0;
		float colorBrightness = (out_Color.x + out_Color.y + out_Color.z) / 3.0;
		float illumFactor = illumBrigtness>0 ? max(0, (illumBrigtness - colorBrightness)) / illumBrigtness : 0;
		out_Color = out_Color + illumColor * illumFactor;
		float lightDist = viewDist * (1 - 0.5 * illumBrigtness);
		viewDist = mix(lightDist, viewDist, clamp((viewDist-fogFar+12)/12, 0, 1));
	#endif
	
	vec4 fogColor = texture(texSky, pass_SkyCoord);
	float lowNear = clamp((viewDist - MIN_FOG_DIST) / (cloudNear - MIN_FOG_DIST), 0, 1);
	vec4 highColor = mix(out_Color, fogColor, clamp((viewDist - fogNear) / (fogFar - fogNear), 0, 1));
	highColor = mix(highColor, fogColor+0.075*blockLight, clamp((pass_LevelPosition.y - cloudTop) / (cloudBottom - cloudTop), 0, 1) * lowNear);
	vec4 lowColor = mix(out_Color, fogColor, lowNear);
	out_Color = mix(highColor, lowColor, clamp((cameraPosition.y-cloudTop)/(cloudBottom-cloudTop), 0, 1));
	
	out_Color.a = diffuseColor.a;
}
