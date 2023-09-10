#version 150 core

#define MIN_FOG_DIST 4

uniform sampler2D texColor;
uniform vec4 illumColor = vec4(1.0, 0.98, 0.9, 1);
uniform float fogFar = 160;

uniform float cloudTop = 0;
uniform float cloudBottom = -40;
uniform float cloudNear = 20;

uniform vec4 lightColor = vec4(1, 0.949, 0.850, 1);
uniform float illumTrigger = 2;

in vec4 pass_Position;
in vec2 pass_TexCoord;
in vec4 pass_LevelPosition;

out vec4 out_Color;

void main(void) {
	float illum = lightColor.x+lightColor.y+lightColor.z;
	if(illum<illumTrigger) {
		float viewDist = length(pass_Position.xyz);
		float fadeDist = mix(fogFar, cloudNear*2, clamp((pass_LevelPosition.y-cloudTop)/(cloudBottom-cloudTop), 0, 1));
		float fade = 1 - clamp(viewDist, 0, fadeDist) / fadeDist;
		out_Color = texture(texColor, pass_TexCoord) * illumColor * pow(fade, 2) * (1-illum/illumTrigger);
	}
	else {
		out_Color = vec4(0);
	}
}
