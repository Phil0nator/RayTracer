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

final double Infinity = Double.POSITIVE_INFINITY;
final double Epsilon = 0.00001f;
final int MAX_BOUNCES = 5;


int bgColor = color(100,100,100);
PVector camaraPosition = new PVector(0,0,0);
PVector camaraDirection = new PVector(1,1,1);
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
public int Add(int a, int b){
    return color(clampColor(red(a)+red(b)), clampColor(green(a)+green(b)), clampColor(blue(a)+blue(b)), clampColor(alpha(a)+alpha(b)));
}

public Intersection findClosest(PVector origin, PVector direction, double min, double max){
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
        
        
    return new Intersection(closestObject, closest);
}

public int trace(PVector origin, PVector direction, double min, double max, int depth){

    
    Intersection closestObject = findClosest(origin,direction,min,max);
    if(closestObject.object == null){
            return bgColor;
    }
    double closest = closestObject.t;

    PVector spacePoint = origin.copy().add(direction.copy().mult((float)closest));
    PVector normal = spacePoint.copy().sub(closestObject.object.location);
    normal = normal.mult(1.0f/normal.mag());
    int local_color = Multiply(computeLighting(spacePoint, normal, direction.copy().mult(-1), closestObject.object.specular), closestObject.object.c);

    if(depth > MAX_BOUNCES || closestObject.object.reflective == 0){
        return local_color;
    }

    PVector newRay = reflect(direction.mult(-1),normal);
    int reflectedColor = trace(spacePoint, newRay, Epsilon, Infinity, depth+1);

    return Add(Multiply(1-closestObject.object.reflective, local_color), Multiply(closestObject.object.reflective,reflectedColor));


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
            int dc = trace(camaraPosition, dir,0,Infinity,0);
            stroke(dc);
            placeRayPixel(x,y);
        }

    }

}

ArrayList<Object3D> objects = new ArrayList<Object3D>(0);
ArrayList<Light> lights = new ArrayList<Light>(0);
class Intersection{
    double t;
    Object3D object;
    Intersection(Object3D o, double T){
        t=T;
        object=o;
    }
}

abstract class Object3D{

    PVector location;
    int c;
    double specular = 10;
    double reflective = 0.01f;
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
        direction=dir;
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
public PVector reflect(PVector ray, PVector normal){
    return normal.mult(2).mult(normal.dot(ray)).sub(ray);
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
            double maxDist;
            if(l.type == LightType.POINT){
                v = l.location.copy().sub(point);
                maxDist = 1;
            }else{
                v = l.location.copy();
                maxDist = Infinity;
            }
            //check for shadows:
            Intersection shadow = findClosest(point, v, Epsilon, maxDist);
            if(shadow.object !=null){
                continue;
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

public void setupScene(){

    Sphere sphereA = new Sphere(new PVector(0,0,5),color(255,255,255),1);
    sphereA.specular = 0;
    sphereA.reflective = .3f;
    Sphere sphereB = new Sphere(new PVector(-2,-1,3),color(255,0,255),1);
    sphereB.specular = 100;
    sphereB.reflective=0;

    Light ambient = AmbientLight(new PVector(0,0,0), color(255,255,255));
    ambient.intensity = .5f;


    Light point = PointLight(new PVector(5,0,0),color(255,255,255));
    point.intensity = 1;
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
