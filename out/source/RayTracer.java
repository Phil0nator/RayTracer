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

ArrayList<Object3D> objects = new ArrayList<Object3D>(0);

abstract class Object3D{

    PVector location;
    int c;

    Object3D(PVector l, int C){
        location=l;
        println(location);
        c=C;
        objects.add(this);
    }

    public PVector collides(PVector origin, PVector direction){
        println("Something has gone very very wrong here");
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

    return closestObject.c;



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

    for(int x = -width/2;x < width/2;x++){

        for(int y = -height/2; y< height/2; y++){

            PVector dir = canvasToViewport(x,y);
            int dc = trace(camaraPosition, dir,0,Infinity);
            stroke(dc);
            placeRayPixel(x,y);
        }

    }

}
Sphere sphereA;
Sphere sphereB;
public void setupScene(){
    sphereA = new Sphere(new PVector(-1,-1,3),color(255,0,0),2);
    sphereB = new Sphere(new PVector(1,1,5),color(0,255,0),1.5f);
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
