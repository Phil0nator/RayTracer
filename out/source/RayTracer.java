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

Camera cam;
ArrayList<Sphere> spheres;
ArrayList<Ray> rays;

public void setup(){

    
    spheres = new ArrayList<Sphere>(0);
    rays = new ArrayList<Ray>(0);

    cam = new Camera();
    Sphere tester = new Sphere(new PVector(-1,-1,1),new Material(color(255,0,0)), 2);



    cam.send();
}



public void draw(){


    background(0);
    for(Sphere s : spheres){
        for(Ray r: rays){
            if(s.intersects(r)){
                
            }
        }
    }

}
class Camera{

    PVector location = new PVector(0,0,-10);
    Object3D viewPlane; 
    Camera(){
        viewPlane=Plane(location.x,location.y,location.z,width,height,color(255));

    }

    public void send(){

        for(int i = 0; i < width;i++){
            for(int j = 0 ; j < height;j++){

                Ray r = new Ray(i,j);

            }
        }

    }


}
class Vert{
    PVector location;
    Vert(float x, float y, float z){
        location=new PVector(x,y,z);
    }
}
class Face{
    Vert[] vertices = new Vert[4];
    Material material;
    Face(Vert a, Vert b, Vert c, Vert d, Material mat){
        vertices[0]=a;
        vertices[1]=b;
        vertices[2]=c;
        vertices[3]=d;
        material = mat;
    }
}

class Material{
    int c;
    Material(int inp){
        c=inp;
    }
}


class Object3D{
    Vert[] vertices;
    Face[] faces;
    Material material;
    PVector location;


    Object3D(Vert[] verts, Face[] faces, Material mat, PVector loc){
        vertices=verts;
        this.faces = faces;
        material=mat;
        location=loc;
    }

    public void translate(int x, int y, int z){
        for(int i = 0 ; i < vertices.length;i++){
            vertices[i].location.add(x,y,z);
        }
        this.location.add(x,y,z);
    }




}


public Object3D Plane(float x, float y, float z, int width, int height, int c){

    PVector loc = new PVector(x,y,z);
    Vert[] verts = {new Vert(x,y,z),new Vert(x+width,y,z),new Vert(x,y+height,z),new Vert(x+width,y+height,z)};
    Material mat = new Material(c);
    Face[] faces= {new Face(verts[0],verts[1],verts[2],verts[3],mat)};
    Object3D output = new Object3D(verts,faces,mat,loc);
    return output;
}

class Sphere{

    PVector loc = new PVector(0,0,0);
    Material mat = new Material(0);
    float r = 0;
    Sphere(PVector location, Material material, float radius){
        loc=location;
        mat=material;
        r=radius;
        spheres.add(this);
    }

    public boolean intersects(Ray r){

        PVector sphere_to_ray = r.drawCoord.sub(loc.x,loc.y,loc.z);
        PVector b = r.velocity.mult(2.0f).dot(sphere_to_ray); 
        PVector c = sphere_to_ray.dot(sphere_to_ray)-r*r;
        PVector discriminant = b.mult(b).sub(c.mult(4));
        return discriminant >= 0;

    }


}
class Ray{

    PVector location;
    PVector velocity;
    PVector drawCoord;
    int maxBounces;
    int maxLife;
    int c;

    Ray(int x, int y){
        this.drawCoord=new PVector(x,y);
        this.velocity = new PVector(x-cam.x,y-cam.y,1).normalize();
        rays.add(this);
    }



}
  public void settings() {  size(500,500); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "RayTracer" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
