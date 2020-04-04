public final float Infinity = 99999999.99;

color bgColor = color(100,100,100);
PVector camaraPosition = new PVector(0,0,0);
int viewport_dim = 1;
int viewport_dist = 1;
PVector canvasToViewport(int x, int y){
    
    return new PVector((float)x *viewport_dim/width,(float) y*viewport_dim/height, viewport_dist);

}

ArrayList<Object3D> objects = new ArrayList<Object3D>(0);

abstract class Object3D{

    PVector location;
    color c;

    Object3D(PVector l, color C){
        location=l;
        c=C;
        objects.add(this);
    }

    PVector collides(PVector origin, PVector direction){
        println("Something has gone very very wrong here");
        return new PVector(Infinity, Infinity); 
    }

}

class Sphere extends Object3D{

    float r;

    Sphere(PVector l, color C, float rad){
        super(l,C);
        r=rad;
    }

    @Override
    PVector collides(PVector origin, PVector direction){
        PVector oc = origin.sub(location);
        float k1 = direction.dot(direction);
        float k2 = 2.0*oc.dot(direction);
        float k3 = oc.dot(oc) - r*r;

        float discriminant = k2*k2 - 4.0*k1*k3;
        if(discriminant<0){
            return new PVector(Infinity,Infinity);
        }

        float t1 = (-k2+sqrt(discriminant))/(2.0*k1);
        float t2 = (-k2 - sqrt(discriminant))/(2.0*k1);
        println("SOMETHING DIFFERENT");
        return new PVector(t1,t2);
    }

}


color trace(PVector origin, PVector direction, int min, float max){

    float closest = Infinity;
    Object3D closestObject = null;

    for(Object3D o : objects){

        PVector ts = o.collides(origin,direction);
        
        if(ts.x < closest && min < ts.x && ts.x < max){
            closest = ts.x;
            closestObject = o;
        }
        if(ts.y < closest && min < ts.y && ts.y < max){
            closest =ts.y;
            closestObject = o;
        }
        

    }
    if(closestObject == null){
            return bgColor;
        }

    return closestObject.c;



}
void placeRayPixel(int x, int y){

    x+=width/2;
    y+=height/2;
    point(x,y);

}


void setup(){

    size(1000,800);

    setupScene();
}




void draw(){

    for(int x = -width/2;x < width/2;x++){

        for(int y = -height/2; y< height/2; y++){

            PVector dir = canvasToViewport(x,y);
            color dc = trace(camaraPosition, dir,1,Infinity);
            stroke(dc);
            placeRayPixel(x,y);
        }

    }
    sphereA.r*=2;
    println("FRAME");

}