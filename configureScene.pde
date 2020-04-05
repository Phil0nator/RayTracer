
void setupScene(){

    Sphere sphereA = new Sphere(new PVector(0,0,5),color(255,255,255),1);
    sphereA.specular = 0;
    sphereA.reflective = .3;
    Sphere sphereB = new Sphere(new PVector(-2,-1,3),color(255,0,255),1);
    sphereB.specular = 100;
    sphereB.reflective=0;

    Light ambient = AmbientLight(new PVector(0,0,0), color(255,255,255));
    ambient.intensity = .5;


    Light point = PointLight(new PVector(5,0,0),color(255,255,255));
    point.intensity = 1;
}