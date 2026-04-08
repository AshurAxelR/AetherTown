 #version 150 core

uniform sampler2D tex;
uniform float alpha;
uniform int clip = 1;

in vec2 pass_TexCoord;

out vec4 out_Color;

void main(void) {
    float d = distance(pass_TexCoord, vec2(0.5, 0.5));
    if(clip!=0 && d>0.5)
        discard;
	out_Color = texture(tex, pass_TexCoord);
	out_Color.a *= alpha;
}
