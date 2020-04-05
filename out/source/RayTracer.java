import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class RayTracer extends PApplet {

public final double Infinity = Double.POSITIVE_INFINITY;

int bgColor = color(100,100,100);
PVector camaraPosition = new PVector(0,0,0);
int viewport_dim = 1;
int viewport_dist = 1;
public PVector canvasToViewport(int x, int y){
    
    return new PVector((float)x *viewport_dim/width,(float) y*viewport_dim/height, viewport_dist);

}
public float clampColor(double inp){
    if(inp<0){
        return 0.0f;
    }
    if(inp>255){
        return 255.0f;
    }
    return (float)inp;
}

public int Multiply(double fac, int c){
    int out = color(clampColor(red(c)*fac),clampColor(green(c)*fac),clampColor(blue(c)*fac),alpha(c));
    return out;
}

public int trace(PVector origin, PVector direction, int min, double max){

    double closest = Infinity;
    Object3D closestObject = null;

    for(Object3D o : objects){

        PVector ts = o.collides(origin,direction);
        
        if(ts.x < closest && min < ts.x){
            closest = ts.x;
            closestObject = o;
        }
        if(ts.y < closest && min < ts.y){
            closest =ts.y;
            closestObject = o;
        }
        
        

    }
    if(closestObject == null){
            return bgColor;
    }

    PVector spacePoint = origin.copy().add(direction.copy().mult((float)closest));
    PVector normal = spacePoint.copy().sub(closestObject.location);
    normal = normal.mult(1.0f/normal.mag());
    return Multiply(computeLighting(spacePoint, normal, direction.copy().mult(-1), closestObject.specular), closestObject.c);



}
public void placeRayPixel(int x, int y){

    //x+=width/2;
    //y+=height/2;
    point(x+width/2,y+height/2);

}


public void setup(){

    

    setupScene();
}



public void draw(){
    background(0);
    for(int x = -width/2;x < width/2;x++){

        for(int y = -height/2; y< height/2; y++){

            PVector dir = canvasToViewport(x,y);
            int dc = trace(camaraPosition, dir,0,Infinity);
            stroke(dc);
            placeRayPixel(x,y);
        }

    }

}

ArrayList<Object3D> objects = new ArrayList<Object3D>(0);
ArrayList<Light> lights = new ArrayList<Light>(0);

abstract class Object3D{

    PVector location;
    int c;
    double specular = 10;

    Object3D(PVector l, int C){
        location=l;
        println(location);
        c=C;
        objects.add(this);
    }

    public PVector collides(PVector origin, PVector direction){
        println("Something has gone very very wrong here --Object");
        return new PVector(0,0); 
    }

}

class Sphere extends Object3D{

    double r;

    Sphere(PVector l, int C, double rad){
        super(l,C);
        r=rad;
    }

    public @Override
    PVector collides(PVector origin, PVector direction){
        PVector oc = origin.copy().sub(location);

        double k1 = direction.dot(direction);
        double k2 = 2.0f*oc.dot(direction);
        double k3 = oc.dot(oc) - r*r;

        double discriminant = k2*k2 - 4.0f*k1*k3;
        if(discriminant<0){
            return new PVector((float)Infinity,(float)Infinity);
        }
        
        double t1 = (-k2+sqrt((float)discriminant))/(2.0f*k1);
        double t2 = (-k2 - sqrt((float)discriminant))/(2.0f*k1);

        return new PVector((float)t1,(float)t2);
    }

}
enum LightType{

    AMBIENT, POINT, DIRECTIONAL

}


class Light{

    PVector location;
    PVector direction;
    int c;
    double intensity;
    LightType type;

    Light(PVector l, PVector dir,int C, LightType type){
        location=l.copy();
        c=C;
        lights.add(this);
    }


}

public Light AmbientLight(PVector l, int c){
    return new Light(l,new PVector(0,0,0),c,LightType.AMBIENT);
}
public Light PointLight(PVector l, int c){
    return new Light(l, new PVector(0,0,0), c, LightType.POINT);
}
public Light DirectionlLight(PVector l, PVector dir, int c){
    return new Light(l, new PVector(0,0,0), c, LightType.DIRECTIONAL);
}


public double computeLighting(PVector point, PVector normal, PVector view, double spec){

    double intensity = 0;
    double normal_length = normal.mag();
    double view_length = view.mag();
    for(Light l : lights){

        if(l.type == LightType.AMBIENT){
            intensity+=l.intensity;
        }else{
            PVector v;
            if(l.type == LightType.POINT){
                v = l.location.copy().sub(point);
            }else{
                v = l.location.copy();
            }
            double normalDot = normal.dot(v);
            if(normalDot>0){
                intensity+=l.intensity*normalDot/(normal_length*v.mag());
            }
            if(spec!=-1){
                PVector reflec = normal.copy().mult((2*normal.copy().dot(v))).sub(v);
                double reflectionDotFactor = reflec.copy().dot(view);
                if(reflectionDotFactor > 0){
                    intensity += l.intensity * pow((float)(reflectionDotFactor / (reflec.mag()*view_length)),(float)spec);
                }

            }
        }
        

    }
    return intensity;

}
Sphere sphereA;
Sphere sphereB;
Light ambient;
Light pointer;

public void setupScene(){
    sphereA = new Sphere(new PVector(-1,-1,10),color(255,0,0),2);
    sphereA.specular = 1000;
    sphereB = new Sphere(new PVector(1,1,5),color(0,255,0),1.5f);
    sphereB.specular = -1;
    ambient = AmbientLight(new PVector(10,10,-10), color(255,255,255));
    ambient.intensity = 1;
    
    pointer = PointLight(new PVector(0,-10,0), color(255,255,255));
    pointer.intensity = 1;

}
  public void settings() {  size(1000,1000); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "RayTracer" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
