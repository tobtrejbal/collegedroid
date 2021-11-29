package tobous.collegedroid.functions.graphs.encapsulation;

import tobous.collegedroid.functions.plans.encapsulation.Room;

/**
 * Created by Tob on 18. 12. 2015.
 */
public class Vertex {
    final private String id;
    final private int type;
    final private int coordX;
    final private int coordY;
    final private int graphId;


    public Vertex(String id, int type, int coordX, int coordY, int graphId) {
        this.id = id;
        this.type = type;
        this.coordX = coordX;
        this.coordY = coordY;
        this.graphId = graphId;
    }

    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vertex other = (Vertex) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public int getType() {
        return type;
    }

    public int getCoordX() {
        return coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    public int getGraphId() {
        return graphId;
    }
}