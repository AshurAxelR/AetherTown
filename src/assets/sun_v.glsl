#version 150 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform float fovFactor;
uniform float invAspectRatio;

uniform vec4 position;
uniform float size = 0.5;

in vec2 in_Position;
in vec2 in_TexCoord;

out vec3 pass_Position;
out vec2 pass_TexCoord;

void main(void) {
	gl_Position = projectionMatrix * viewMatrix * position;
	vec2 pos = -in_Position*size;//*fovFactor;
	pass_Position = position.xyz;
	pass_Position.xy += pos;
	
	pos *= fovFactor;
	pos.x *= invAspectRatio;
	float hdot = position.y;
	if(hdot<0)
		pos.x *= 10*(0.1-hdot);
	gl_Position.xy += pos;

	pass_TexCoord = in_TexCoord;
}
