Sphere sphereA;
Sphere sphereB;
Light ambient;
Light pointer;

void setupScene(){
    sphereA = new Sphere(new PVector(-1,-1,10),color(255,0,0),2);
    sphereA.specular = 1000;
    sphereB = new Sphere(new PVector(1,1,5),color(0,255,0),1.5);
    sphereB.specular = -1;
    ambient = AmbientLight(new PVector(10,10,-10), color(255,255,255));
    ambient.intensity = 1;
    
    pointer = PointLight(new PVector(0,-10,0), color(255,255,255));
    pointer.intensity = 1;

}