#version 150 core

uniform float viewY = 0;
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

	// out_Color += lightColor * ((pow(ldot, 1500) + pow(ldot, 8)) * 0.2);
	out_Color = mix(out_Color, cloudColor, clamp((viewY-cloudTop)/(cloudBottom-cloudTop), 0, 1));
}
