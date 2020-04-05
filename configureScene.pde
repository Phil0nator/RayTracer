
void setupScene(){

    Sphere sphereA = new Sphere(new PVector(0,0,10),color(255,255,255),1);
    sphereA.specular = 0;
    sphereA.reflective = .3;
    Sphere sphereB = new Sphere(new PVector(-2,-1,5),color(255,0,255),1);
    sphereB.specular = 100;
    sphereB.reflective=0;

    Light ambient = AmbientLight(new PVector(0,0,0), color(255,255,255));
    ambient.intensity = .5;


    Light point = PointLight(new PVector(5,0,0),color(255,255,255));
    point.intensity = 1;


    for(int i = 1 ; i < 10; i++){
        new Sphere(new PVector(i,i,i+5),color(255,100,0),1);
    }


}