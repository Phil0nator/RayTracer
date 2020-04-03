Camera cam;
ArrayList<Sphere> spheres;
ArrayList<Ray> rays;

void setup(){

    size(500,500);
    spheres = new ArrayList<Sphere>(0);
    rays = new ArrayList<Ray>(0);

    cam = new Camera();
    Sphere tester = new Sphere(new PVector(-1,-1,1),new Material(color(255,0,0)), 2);



    cam.send();
}



void draw(){


    background(0);
    for(Sphere s : spheres){
        for(Ray r: rays){
            if(s.intersects(r)){
                
            }
        }
    }

}