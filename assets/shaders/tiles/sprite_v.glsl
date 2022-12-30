#version 150 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform float fovFactor;
uniform float invAspectRatio;

in vec2 in_Position;
in vec2 in_TexCoord;

in vec3 ins_Position;
in float ins_Size;

out vec2 pass_TexCoord;

void main(void) {
	gl_Position = projectionMatrix * viewMatrix * vec4(ins_Position, 1);
	vec2 pos = -in_Position*ins_Size*fovFactor;
	pos.x *= invAspectRatio;
	gl_Position.xy += pos;

	pass_TexCoord = in_TexCoord;
}
