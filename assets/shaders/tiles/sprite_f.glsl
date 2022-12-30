#version 150 core

uniform sampler2D texColor;

in vec2 pass_TexCoord;

out vec4 out_Color;

void main(void) {
	out_Color = texture(texColor, pass_TexCoord);
}
