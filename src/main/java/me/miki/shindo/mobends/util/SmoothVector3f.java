package me.miki.shindo.mobends.util;

import org.lwjgl.util.vector.Vector3f;

public class SmoothVector3f {
	
	public Vector3f vOld = new Vector3f(0,0,0);
	public Vector3f vSmooth = new Vector3f(0,0,0);
	public Vector3f vFinal = new Vector3f(0,0,0);
	public Vector3f smoothness = new Vector3f(1,1,1);
	public Vector3f completion = new Vector3f(0,0,0);
	
	public void setSmooth(Vector3f argRotation, float argSmooth){
		if(this.vFinal != argRotation){
			this.vFinal = argRotation;
			this.vOld = this.vSmooth;
			this.completion = new Vector3f(0,0,0);
			this.smoothness = new Vector3f(argSmooth,argSmooth,argSmooth);
		}
	}
	
	public void setSmoothZero(){
		this.setSmoothZero(1.0f);
	}
	
	public void setSmoothZero(float argSmooth){
		this.setSmooth(new Vector3f(0,0,0), argSmooth);
	}
	
	public void setSmooth(Vector3f argRotation){
		this.setSmooth(argRotation, 1.0f);
	}
	
	public void setSmooth(MoBendsEnumAxis argAxis,float argRot,float argSmooth){
		if((argAxis == MoBendsEnumAxis.X ? this.vFinal.x : argAxis == MoBendsEnumAxis.Y ? this.vFinal.y : this.vFinal.z) != argRot){
			if(argAxis == MoBendsEnumAxis.X) this.vFinal.x = argRot;
			if(argAxis == MoBendsEnumAxis.Y) this.vFinal.y = argRot;
			if(argAxis == MoBendsEnumAxis.Z) this.vFinal.z = argRot;
			if(argAxis == MoBendsEnumAxis.X) this.vOld.x = this.vSmooth.x;
			if(argAxis == MoBendsEnumAxis.Y) this.vOld.y = this.vSmooth.y;
			if(argAxis == MoBendsEnumAxis.Z) this.vOld.z = this.vSmooth.z;
			if(argAxis == MoBendsEnumAxis.X) this.completion.x = 0.0f;
			if(argAxis == MoBendsEnumAxis.Y) this.completion.y = 0.0f;
			if(argAxis == MoBendsEnumAxis.Z) this.completion.z = 0.0f;
		}
		if(argAxis == MoBendsEnumAxis.X) this.smoothness.x = argSmooth;
		if(argAxis == MoBendsEnumAxis.Y) this.smoothness.y = argSmooth;
		if(argAxis == MoBendsEnumAxis.Z) this.smoothness.z = argSmooth;
	}
	
	public void setSmoothX(float argX,float argSmooth){
		if(this.vFinal.x != argX){
			this.vFinal.x = argX;
			this.vOld.x = this.vSmooth.x;
			this.completion.x = 0.0f;
		}
		this.smoothness.x = argSmooth;
	}
	
	public void setSmoothY(float argY,float argSmooth){
		if(this.vFinal.y != argY){
			this.vFinal.y = argY;
			this.vOld.y = this.vSmooth.y;
			this.completion.y = 0.0f;
		}
		this.smoothness.y = argSmooth;
	}
	
	public void setSmoothZ(float argZ,float argSmooth){
		if(this.vFinal.z != argZ){
			this.vFinal.z = argZ;
			this.vOld.z = this.vSmooth.z;
			this.completion.z = 0.0f;
		}
		this.smoothness.z = argSmooth;
	}
	
	public void setSmoothX(float argX){
		this.setSmoothX(argX, 0.6f);
	}
	
	public void setSmoothY(float argY){
		this.setSmoothY(argY, 0.6f);
	}
	
	public void setSmoothZ(float argZ){
		this.setSmoothZ(argZ, 0.6f);
	}
	
	public void setX(float argX){
		this.vOld.x = argX;
		this.vSmooth.x = argX;
		this.vFinal.x = argX;
		this.completion.x = 1.0f;
	}
	
	public void setY(float argY){
		this.vOld.y = argY;
		this.vSmooth.y = argY;
		this.vFinal.y = argY;
		this.completion.y = 1.0f;
	}
	
	public void setZ(float argZ){
		this.vOld.z = argZ;
		this.vSmooth.z = argZ;
		this.vFinal.z = argZ;
		this.completion.z = 1.0f;
	}
	
	public void set(SmoothVector3f argV){
		this.completion = argV.completion;
		this.smoothness = argV.smoothness;
		this.vFinal = argV.vFinal;
		this.vOld = argV.vOld;
		this.vSmooth = argV.vSmooth;
	}
	
	public float getX(){
		return this.vSmooth.x;
	}
	
	public float getY(){
		return this.vSmooth.y;
	}
	
	public float getZ(){
		return this.vSmooth.z;
	}
	
	public void update(float argTicksPerFrame){
		this.completion.x += argTicksPerFrame*this.smoothness.x;
		this.completion.y += argTicksPerFrame*this.smoothness.y;
		this.completion.z += argTicksPerFrame*this.smoothness.z;
		this.completion = MoBendsGUtil.max(this.completion, 1.0f);
		
		if(completion.x >= 1.0f) this.vOld.x = this.vSmooth.x = this.vFinal.x;
		else this.vSmooth.x = this.vOld.x+(this.vFinal.x-this.vOld.x)*this.completion.x;
		
		if(completion.y >= 1.0f) this.vOld.y = this.vSmooth.y = this.vFinal.y;
		else this.vSmooth.y = this.vOld.y+(this.vFinal.y-this.vOld.y)*this.completion.y;
		
		if(completion.z >= 1.0f) this.vOld.z = this.vSmooth.z = this.vFinal.z;
		else this.vSmooth.z = this.vOld.z+(this.vFinal.z-this.vOld.z)*this.completion.z;
		
		this.setSmooth(new Vector3f(0.0f,0.0f,0.0f), 0.5f);
	}
}
