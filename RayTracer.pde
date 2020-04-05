/////////
// @author: Philo Kaulkin
// (Inspiration and assistance) https://www.gabrielgambetta.com/computer-graphics-from-scratch/
////////

final double Infinity = Double.POSITIVE_INFINITY;
final double Epsilon = 0.00001;
final int MAX_BOUNCES = 5;


color bgColor = color(100,100,100);
PVector camaraPosition = new PVector(0,0,0);
RotationMatrix camaraRotation = new RotationMatrix();
int viewport_dim = 1;
int viewport_dist = 1;
PVector canvasToViewport(int x, int y){
    
    return new PVector((float)x *viewport_dim/width,(float) y*viewport_dim/height, viewport_dist);

}
float clampColor(double inp){
    if(inp<0){
        return 0.0;
    }
    if(inp>255){
        return 255.0;
    }
    return (float)inp;
}

color Multiply(double fac, color c){
    color out = color(clampColor(red(c)*fac),clampColor(green(c)*fac),clampColor(blue(c)*fac),alpha(c));
    return out;
}
color Add(color a, color b){
    return color(clampColor(red(a)+red(b)), clampColor(green(a)+green(b)), clampColor(blue(a)+blue(b)), clampColor(alpha(a)+alpha(b)));
}

Intersection findClosest(PVector origin, PVector direction, double min, double max){
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

color trace(PVector origin, PVector direction, double min, double max, int depth){

    
    Intersection closestObject = findClosest(origin,direction,min,max);
    if(closestObject.object == null){
            return bgColor;
    }
    double closest = closestObject.t;

    PVector spacePoint = origin.copy().add(direction.copy().mult((float)closest));
    PVector normal = spacePoint.copy().sub(closestObject.object.location);
    normal = normal.mult(1.0/normal.mag());
    color local_color = Multiply(computeLighting(spacePoint, normal, direction.copy().mult(-1), closestObject.object.specular), closestObject.object.c);

    if(depth > MAX_BOUNCES || closestObject.object.reflective == 0){
        return local_color;
    }

    PVector newRay = reflect(direction.mult(-1),normal);
    color reflectedColor = trace(spacePoint, newRay, Epsilon, Infinity, depth+1);

    return Add(Multiply(1-closestObject.object.reflective, local_color), Multiply(closestObject.object.reflective,reflectedColor));


}
void placeRayPixel(int x, int y){

    //x+=width/2;
    //y+=height/2;
    point(x+width/2,y+height/2);

}

void drawRayPixelToBuffer(int x, int y, color c,PImage b){
    while(isPlacing==true){}
    isPlacing=true;
    b.set(x,y,c);
    isPlacing=false;
}
int threads = 0;
int maxThreads = 10;
void setup(){

    size(1000,1000);
    setupScene();
    buffer = createImage(width,height,ARGB);
    thread("renderChunk");
    delay(50);
    thread("renderChunk");
    delay(50);
    thread("renderChunk");
    delay(50);
    thread("renderChunk");
    

}
void renderChunk(){
    
    while(true){

        for(int x = -width/2;x < width/2;x++){

            for(int y = -height/2; y< height/2; y++){

                //PVector dir = camaraRotation.applyTo(canvasToViewport(x,y));
                PVector dir = canvasToViewport(x,y);
                color dc = trace(camaraPosition, dir,0,Infinity,0);
                
                drawRayPixelToBuffer(x+width/2,y+height/2,dc,buffer);
            }
            
        }



    }

}
void reverseRender(){
    println("reverse");
    while(true){

        for(int x = width/2;x < -width/2;x--){

            for(int y = height/2; y< -height/2; y--){

                //PVector dir = camaraRotation.applyTo(canvasToViewport(x,y));
                PVector dir = canvasToViewport(x,y);
                color dc = trace(camaraPosition, dir,0,Infinity,0);
                drawRayPixelToBuffer(x+width/2,y+height/2,dc,buffer);
            }
            
        }



    }

}

PImage buffer;
boolean isPlacing =false;
void draw(){

    //background(0);

    



    /*
    for(int x = -width/2;x < width/2;x++){

        for(int y = -height/2; y< height/2; y++){

            //PVector dir = camaraRotation.applyTo(canvasToViewport(x,y));
            PVector dir = canvasToViewport(x,y);
            color dc = trace(camaraPosition, dir,0,Infinity,0);
            stroke(dc);
            placeRayPixel(x,y);
        }
    }

    camaraRotation.rotateY(.1);
    */
    image(buffer,0,0);
    camaraPosition.x+=.001;
    camaraPosition.y+=.001;
}