public final double Infinity = Double.POSITIVE_INFINITY;

color bgColor = color(100,100,100);
PVector camaraPosition = new PVector(0,0,0);
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

color trace(PVector origin, PVector direction, int min, double max){

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
    normal = normal.mult(1.0/normal.mag());
    return Multiply(computeLighting(spacePoint, normal, direction.copy().mult(-1), closestObject.specular), closestObject.c);



}
void placeRayPixel(int x, int y){

    //x+=width/2;
    //y+=height/2;
    point(x+width/2,y+height/2);

}


void setup(){

    size(1000,1000);

    setupScene();
}



void draw(){
    background(0);
    for(int x = -width/2;x < width/2;x++){

        for(int y = -height/2; y< height/2; y++){

            PVector dir = canvasToViewport(x,y);
            color dc = trace(camaraPosition, dir,0,Infinity);
            stroke(dc);
            placeRayPixel(x,y);
        }

    }

}