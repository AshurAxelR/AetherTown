#version 150 core

uniform vec3 cameraPosition;

uniform sampler2D dataBlockLighting;
uniform float levelSize;
uniform float illumTrigger = 2;
uniform vec4 lightColor = vec4(1, 0.949, 0.850, 1);

uniform float cloudTop = 0;
uniform float cloudBottom = -40;

uniform vec4 zenithColor = vec4(0.6, 0.65, 0.75, 1);
uniform vec4 bgColor = vec4(0.8, 0.819, 0.901, 1);
uniform vec4 cloudColor = vec4(0.95, 0.96, 0.975, 1);

uniform vec3 lightSkyDirection = vec3(-0.55f, -0.25f, -0.45f);
uniform vec4 lightSkyColor = vec4(1, 0.95, 0.85, 0);

in vec3 pass_Position;

out vec4 out_Color;

void main(void) {
	vec3 pos = normalize(pass_Position);
	float hdot = pos.y;
	float ldot = max(dot(pos, normalize(lightSkyDirection)), 0) * lightSkyColor.a;
	if(hdot>0) {
		out_Color = mix(bgColor, lightSkyColor, ldot*ldot);
		out_Color = mix(out_Color, cloudColor, clamp(5*hdot, 0, 1));
	}
	else {
		out_Color = mix(bgColor, zenithColor, -hdot);
		out_Color = mix(out_Color, lightSkyColor, ldot*ldot);
	}

	vec4 blockLight = vec4(0);
	float illum = lightColor.x+lightColor.y+lightColor.z;
	if(illum<illumTrigger && levelSize>0) {
		blockLight = texture(dataBlockLighting, (cameraPosition.xz/4+0.5)/levelSize)*(1-illum/illumTrigger);
	}
	vec4 fogColor = cloudColor + 0.15*blockLight;
	out_Color = mix(out_Color, fogColor, clamp((cameraPosition.y-cloudTop)/(cloudBottom-cloudTop), 0, 1));
}
