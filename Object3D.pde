class Vert{
    PVector location;
    Vert(float x, float y, float z){
        location=new PVector(x,y,z);
    }
}
class Face{
    Vert[] vertices = new Vert[4];
    Material material;
    Face(Vert a, Vert b, Vert c, Vert d, Material mat){
        vertices[0]=a;
        vertices[1]=b;
        vertices[2]=c;
        vertices[3]=d;
        material = mat;
    }
}

class Material{
    int c;
    Material(color inp){
        c=inp;
    }
}


class Object3D{
    Vert[] vertices;
    Face[] faces;
    Material material;
    PVector location;


    Object3D(Vert[] verts, Face[] faces, Material mat, PVector loc){
        vertices=verts;
        this.faces = faces;
        material=mat;
        location=loc;
    }

    void translate(int x, int y, int z){
        for(int i = 0 ; i < vertices.length;i++){
            vertices[i].location.add(x,y,z);
        }
        this.location.add(x,y,z);
    }




}


Object3D Plane(float x, float y, float z, int width, int height, color c){

    PVector loc = new PVector(x,y,z);
    Vert[] verts = {new Vert(x,y,z),new Vert(x+width,y,z),new Vert(x,y+height,z),new Vert(x+width,y+height,z)};
    Material mat = new Material(c);
    Face[] faces= {new Face(verts[0],verts[1],verts[2],verts[3],mat)};
    Object3D output = new Object3D(verts,faces,mat,loc);
    return output;
}

class Sphere{

    PVector loc = new PVector(0,0,0);
    Material mat = new Material(0);
    float r = 0;
    Sphere(PVector location, Material material, float radius){
        loc=location;
        mat=material;
        r=radius;
        spheres.add(this);
    }

    boolean intersects(Ray r){

        PVector sphere_to_ray = r.drawCoord.sub(loc.x,loc.y,loc.z);
        PVector b = r.velocity.mult(2.0).dot(sphere_to_ray); 
        PVector c = sphere_to_ray.dot(sphere_to_ray)-r*r;
        PVector discriminant = b.mult(b).sub(c.mult(4));
        return discriminant >= 0;

    }


}