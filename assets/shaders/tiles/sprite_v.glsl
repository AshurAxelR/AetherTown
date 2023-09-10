#version 150 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec2 levelOffset = vec2(0, 0);

uniform float fovFactor;
uniform float invAspectRatio;

in vec2 in_Position;
in vec2 in_TexCoord;

in vec3 ins_Position;
in float ins_Size;

out vec4 pass_Position;
out vec2 pass_TexCoord;
out vec4 pass_LevelPosition;

void main(void) {
	pass_LevelPosition = vec4(ins_Position, 1);
	pass_Position = viewMatrix * (pass_LevelPosition+ vec4(levelOffset.x, 0, levelOffset.y, 0));
	gl_Position = projectionMatrix * pass_Position;
	vec2 pos = -in_Position*ins_Size*fovFactor;
	pos.x *= invAspectRatio;
	gl_Position.xy += pos;

	pass_TexCoord = in_TexCoord;
}
