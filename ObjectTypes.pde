
ArrayList<Object3D> objects = new ArrayList<Object3D>(0);
ArrayList<Light> lights = new ArrayList<Light>(0);

abstract class Object3D{

    PVector location;
    color c;
    double specular = 10;

    Object3D(PVector l, color C){
        location=l;
        println(location);
        c=C;
        objects.add(this);
    }

    PVector collides(PVector origin, PVector direction){
        println("Something has gone very very wrong here --Object");
        return new PVector(0,0); 
    }

}

class Sphere extends Object3D{

    double r;

    Sphere(PVector l, color C, double rad){
        super(l,C);
        r=rad;
    }

    @Override
    PVector collides(PVector origin, PVector direction){
        PVector oc = origin.copy().sub(location);

        double k1 = direction.dot(direction);
        double k2 = 2.0*oc.dot(direction);
        double k3 = oc.dot(oc) - r*r;

        double discriminant = k2*k2 - 4.0*k1*k3;
        if(discriminant<0){
            return new PVector((float)Infinity,(float)Infinity);
        }
        
        double t1 = (-k2+sqrt((float)discriminant))/(2.0*k1);
        double t2 = (-k2 - sqrt((float)discriminant))/(2.0*k1);

        return new PVector((float)t1,(float)t2);
    }

}
enum LightType{

    AMBIENT, POINT, DIRECTIONAL

}


class Light{

    PVector location;
    PVector direction;
    color c;
    double intensity;
    LightType type;

    Light(PVector l, PVector dir,color C, LightType type){
        location=l.copy();
        c=C;
        lights.add(this);
    }


}

Light AmbientLight(PVector l, color c){
    return new Light(l,new PVector(0,0,0),c,LightType.AMBIENT);
}
Light PointLight(PVector l, color c){
    return new Light(l, new PVector(0,0,0), c, LightType.POINT);
}
Light DirectionlLight(PVector l, PVector dir, color c){
    return new Light(l, new PVector(0,0,0), c, LightType.DIRECTIONAL);
}


double computeLighting(PVector point, PVector normal, PVector view, double spec){

    double intensity = 0;
    double normal_length = normal.mag();
    double view_length = view.mag();
    for(Light l : lights){

        if(l.type == LightType.AMBIENT){
            intensity+=l.intensity;
        }else{
            PVector v;
            if(l.type == LightType.POINT){
                v = l.location.copy().sub(point);
            }else{
                v = l.location.copy();
            }
            double normalDot = normal.dot(v);
            if(normalDot>0){
                intensity+=l.intensity*normalDot/(normal_length*v.mag());
            }
            if(spec!=-1){
                PVector reflec = normal.copy().mult((2*normal.copy().dot(v))).sub(v);
                double reflectionDotFactor = reflec.copy().dot(view);
                if(reflectionDotFactor > 0){
                    intensity += l.intensity * pow((float)(reflectionDotFactor / (reflec.mag()*view_length)),(float)spec);
                }

            }
        }
        

    }
    return intensity;

}