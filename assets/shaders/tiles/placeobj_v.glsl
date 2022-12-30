#version 150 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

in vec3 in_Position;
in vec3 in_Normal;
in vec2 in_TexCoord;

out vec4 pass_Position;
out vec3 pass_Normal;
out vec2 pass_TexCoord;
out vec4 pass_WorldPosition;
out vec2 pass_SkyCoord;

void main(void) {
	pass_WorldPosition = modelMatrix * vec4(in_Position, 1);
	pass_Position = viewMatrix * pass_WorldPosition;
	gl_Position = projectionMatrix * pass_Position;
	pass_SkyCoord = 0.5+0.5*gl_Position.xy/gl_Position.w;
	
	pass_Normal = normalize(vec3(viewMatrix * modelMatrix * vec4(in_Normal, 0)));
	pass_TexCoord = in_TexCoord;
}
