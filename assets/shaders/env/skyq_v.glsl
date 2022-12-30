#version 150 core

uniform mat4 viewMatrix;

uniform float fov;
uniform float aspectRatio;

in vec2 in_Position;
in vec2 in_TexCoord;

out vec3 pass_Position;

void main(void) {
	gl_Position = vec4(in_Position, 0, 1);
	vec4 pos = inverse(viewMatrix) * normalize(vec4(tan(fov/2)*in_Position*vec2(-aspectRatio, -1), 1, 0));
	pass_Position = pos.xyz;
}
