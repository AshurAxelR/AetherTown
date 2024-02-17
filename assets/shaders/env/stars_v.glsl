#version 150 core

#define MAX_SIZE 32

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec3 cameraPosition;

uniform float exposure = 1/3.0;
uniform float contrast = 1.4;
uniform float saturation = 1/2.0;

uniform float lightWashTop = 0;
uniform float lightWashBottom = 0;
uniform float cloudTop = 0;
uniform float cloudBottom = -40;

in vec3 in_Position;
in float in_Magnitude;
in vec3 in_Color;

out float pass_Size;
out float pass_Brightness;
out vec4 pass_Color;

void main(void) {
	vec4 pos = modelMatrix * vec4(in_Position, 1);
	gl_Position = projectionMatrix * viewMatrix * pos;
	
	float hdot = pos.y;
	float lightWash = 0;
	if(hdot>0)
		lightWash = mix(lightWashBottom, lightWashTop, hdot);
	else
		lightWash = mix(lightWashBottom, 1, -hdot*20);
	float yfog = clamp((cameraPosition.y-cloudTop)/(cloudBottom-cloudTop), 0, 1);
	float mag = in_Magnitude + 10*yfog*yfog + 10*lightWash*lightWash;
	float con = contrast * (1+2*yfog*lightWash);

	pass_Brightness = 100 * exposure * pow(10, -0.4 * con * mag);
	pass_Brightness *= (1-yfog)*(1-lightWash);
	if(pass_Brightness>1)
	 	pass_Brightness = pow(pass_Brightness, saturation);
	
	pass_Size = (pass_Brightness>1) ? min(4*sqrt(pass_Brightness), MAX_SIZE) : 1;
	gl_PointSize = pass_Size;
	
	pass_Color = vec4(mix(vec3(1, 1, 1), in_Color, clamp(pass_Brightness*0.5, 0, 1)), 1);
}
