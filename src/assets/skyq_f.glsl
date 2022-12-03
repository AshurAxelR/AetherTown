#version 150 core

uniform float viewY = 0;
uniform float cloudTop = 0;
uniform float cloudBottom = -40;

uniform vec4 zenithColor = vec4(0.6, 0.65, 0.75, 1);
uniform vec4 bgColor = vec4(0.8, 0.819, 0.901, 1);
uniform vec4 cloudColor = vec4(0.95, 0.96, 0.975, 1);

uniform vec3 lightDirection = vec3(-0.55f, -0.25f, -0.45f);
uniform vec4 lightColor = vec4(1, 0.95, 0.85, 1);

in vec3 pass_Position;
in vec2 pass_TexCoord;

out vec4 out_Color;

void main(void) {
	vec3 pos = normalize(pass_Position);
	float hdot = dot(pos, vec3(0, 1, 0));
	if(hdot>0)
		out_Color = mix(bgColor, cloudColor, clamp(5*hdot, 0, 1));
	else
		out_Color = mix(bgColor, zenithColor, -hdot);

	float ldot = max(dot(pos, normalize(lightDirection)), 0);
	out_Color += lightColor * ((pow(ldot, 1500) + pow(ldot, 8)) * 0.2);
	out_Color = mix(out_Color, cloudColor, clamp((viewY-cloudTop)/(cloudBottom-cloudTop), 0, 1));
}
