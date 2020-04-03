class Camera{

    PVector location = new PVector(0,0,-10);
    Object3D viewPlane; 
    Camera(){
        viewPlane=Plane(location.x,location.y,location.z,width,height,color(255));

    }

    void send(){

        for(int i = 0; i < width;i++){
            for(int j = 0 ; j < height;j++){

                Ray r = new Ray(i,j);

            }
        }

    }


}