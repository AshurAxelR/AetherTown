#version 150 core

uniform vec3 cameraPosition;

uniform vec4 lightColor = vec4(1, 0.949, 0.850, 1);

uniform float cloudTop = 0;
uniform float cloudBottom = -40;

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
	float a = min(1.5, 10*(1-d)/(100*d+1));
	float clear = 1 - clamp((cameraPosition.y-cloudTop)/(cloudBottom-cloudTop), 0, 1);
	// out_Color = pow4(lightColor, 2.75) * 2.75;
	float len = pow(length(lightColor.xyz), 0.35);
	out_Color = vec4(lightColor.xyz*1.2/len + pow(max(0, (a*clear-1)), 1.5), sqrt(a)*clear);
}
