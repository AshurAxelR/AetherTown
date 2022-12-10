#version 150 core

uniform vec4 lightColor = vec4(1, 0.949, 0.850, 1);

in vec3 pass_Position;
in vec2 pass_TexCoord;

out vec4 out_Color;

vec4 pow4(vec4 v, float p) {
	return vec4(
		pow(v.x, p),
		pow(v.y, p),
		pow(v.z, p),
		1
	);
}

void main(void) {
	float d = distance(pass_TexCoord, vec2(0.5, 0.5)) * 2;
	
	float hdot = normalize(pass_Position).y;
	if(hdot<0)
		d -= hdot*10;
	
	if(d>=1) discard;
	out_Color = max(pow4(lightColor, 2.75) * 2.75, vec4(1, 0.2, 0, 1));
	out_Color.a = min(1, 10*(1-d)/(100*d+1));
}
