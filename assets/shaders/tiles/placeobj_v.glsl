#version 150 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec2 levelOffset = vec2(0, 0);

in vec3 in_Position;
in vec3 in_Normal;
in vec2 in_TexCoord;

out vec4 pass_Position;
out vec3 pass_Normal;
out vec2 pass_TexCoord;
out vec4 pass_LevelPosition;
out vec2 pass_SkyCoord;

void main(void) {
	pass_LevelPosition = modelMatrix * vec4(in_Position, 1);
	pass_Position = viewMatrix * (pass_LevelPosition + vec4(levelOffset.x, 0, levelOffset.y, 0));
	gl_Position = projectionMatrix * pass_Position;
	pass_SkyCoord = 0.5+0.5*gl_Position.xy/gl_Position.w;
	
	pass_Normal = normalize(vec3(modelMatrix * vec4(in_Normal, 0)));
	pass_TexCoord = in_TexCoord;
}
