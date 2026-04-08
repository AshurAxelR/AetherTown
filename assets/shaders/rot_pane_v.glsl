 #version 150 core

uniform vec2 screenSize;
uniform vec2 panePosition;
uniform float paneRadius;
uniform float angle = 0;
uniform int ydown = 1;

in vec2 in_Position;
in vec2 in_TexCoord;

out vec2 pass_TexCoord;

vec2 rotate(vec2 v, float a) {
    float s = sin(a);
    float c = cos(a);
    return mat2(c, s, -s, c) * v;
}

void main(void) {
    vec2 pos = rotate(in_Position, angle);
	gl_Position = vec4(
		(pos.x * paneRadius + panePosition.x) * 2.0 / screenSize.x - 1.0,
		1.0 - (pos.y * paneRadius + panePosition.y) * 2.0 / screenSize.y,
		0.0, 1.0);
	
	pass_TexCoord.x = in_TexCoord.x;
	pass_TexCoord.y = ydown==0 ? 1.0 - in_TexCoord.y : in_TexCoord.y;
}
