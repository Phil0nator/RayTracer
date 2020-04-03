class Ray{

    PVector location;
    PVector velocity;
    PVector drawCoord;
    int maxBounces;
    int maxLife;
    color c;

    Ray(int x, int y){
        this.drawCoord=new PVector(x,y);
        this.velocity = new PVector(x-cam.x,y-cam.y,1).normalize();
        rays.add(this);
    }



}