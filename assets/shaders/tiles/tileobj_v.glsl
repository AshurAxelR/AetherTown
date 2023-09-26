#version 150 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec2 levelOffset = vec2(0, 0);

#ifdef ILLUM_TILE
	uniform vec4 lightColor = vec4(1, 0.949, 0.850, 1);
	uniform int illumMask = 0;
#endif

in vec3 in_Position;
in vec3 in_Normal;
in vec2 in_TexCoord;

in vec3 ins_Position;
in float ins_Rotation;
#ifdef SCALED_TILE
	in float ins_ScaleXZ;
	in float ins_ScaleY;
#endif
#ifdef ILLUM_TILE
	in vec3 ins_illumMod;
	in float ins_illumMask;
	in float ins_illumTrigger;
#endif

out vec4 pass_Position;
out vec3 pass_Normal;
out vec2 pass_TexCoord;
out vec4 pass_LevelPosition;
out vec2 pass_SkyCoord;
#ifdef ILLUM_TILE
	flat out vec3 pass_illumMod;
#endif

mat4 translationMatrix(vec3 t) {
	mat4 m = mat4(1);
	m[3] = vec4(t, 1);
	return m;
}

mat4 rotationYMatrix(float a) {
	mat4 m = mat4(1);
	m[0][0] = cos(a);
	m[0][2] = sin(a);
	m[2][0] = -m[0][2];
	m[2][2] = m[0][0];
	return m;
}

#ifdef SCALED_TILE
mat4 scaleMatrix(float xz, float y) {
	mat4 m = mat4(1);
	m[0][0] = xz;
	m[1][1] = y;
	m[2][2] = xz;
	return m;
}
#endif

void main(void) {
	#ifdef SCALED_TILE
		mat4 modelMatrix = translationMatrix(ins_Position) * rotationYMatrix(ins_Rotation) * scaleMatrix(ins_ScaleXZ, ins_ScaleY);
	#else
		mat4 modelMatrix = translationMatrix(ins_Position) * rotationYMatrix(ins_Rotation);
	#endif
	pass_LevelPosition = modelMatrix * vec4(in_Position, 1);
	pass_Position = viewMatrix * (pass_LevelPosition + vec4(levelOffset.x, 0, levelOffset.y, 0));
	gl_Position = projectionMatrix * pass_Position;
	pass_SkyCoord = 0.5+0.5*gl_Position.xy/gl_Position.w;
	
	pass_Normal = normalize(vec3(modelMatrix * vec4(in_Normal, 0)));
	pass_TexCoord = in_TexCoord;
	#ifdef ILLUM_TILE
		float illum = lightColor.x+lightColor.y+lightColor.z;
		pass_illumMod = ((int(ins_illumMask) & illumMask)!=0 && illum < ins_illumTrigger) ? ins_illumMod : vec3(0);
	#endif
}
