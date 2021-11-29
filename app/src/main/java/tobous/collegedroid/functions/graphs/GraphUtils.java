package tobous.collegedroid.functions.graphs;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.core.utils.AppState;
import tobous.collegedroid.functions.graphs.encapsulation.Graph;
import tobous.collegedroid.functions.graphs.encapsulation.Vertex;
import tobous.collegedroid.functions.plans.encapsulation.Building;
import tobous.collegedroid.functions.plans.encapsulation.Room;

/**
 * Created by Tob on 18. 12. 2015.
 */
public class GraphUtils {

   public static List<List<Vertex>> findPath(List<Graph> graphs, Vertex primaryOrigin, Vertex primaryTarget) {

       List<List<Vertex>> paths = new ArrayList<List<Vertex>>();

       List<DijskraAlgorithm> dijskraAlgorithms = new ArrayList<DijskraAlgorithm>();

       Log.v("pocet grafu"+graphs.size(),"");

       for(int i = 0; i < graphs.size(); i++) {

           dijskraAlgorithms.add(new DijskraAlgorithm(graphs.get(i)));

       }

       Vertex temp;

       boolean reverse = false;

       if(primaryTarget.getGraphId() < primaryOrigin.getGraphId()) {

           temp = primaryOrigin;
           primaryOrigin = primaryTarget;
           primaryTarget = temp;
           reverse = true;

       }

       Vertex lastConnector = null;

       for(int i = 0; i < graphs.size(); i++) {

           Log.v("id:"+i+" primaryOrigin:"+primaryOrigin.getGraphId()+" primaryTarget:"+primaryTarget.getGraphId(),"");

           if(i < primaryOrigin.getGraphId()) {
               paths.add(new ArrayList<Vertex>());
               Log.v("pridan prazdny","");
               continue;
           }

           if(i > primaryTarget.getGraphId()) {
               paths.add(new ArrayList<Vertex>());
               Log.v("pridan prazdny","");
               continue;
           }

           if((i == primaryOrigin.getGraphId()) && (i == primaryTarget.getGraphId())) {

               dijskraAlgorithms.get(i).execute(primaryOrigin);
               dijskraAlgorithms.get(i).getPath(primaryTarget,reverse);
               paths.add(dijskraAlgorithms.get(i).getPath(primaryTarget,reverse));
               Log.v("pridano stejne patro","");
               continue;
           }

           if(i == primaryOrigin.getGraphId()) {
               dijskraAlgorithms.get(i).execute(primaryOrigin);
               List<Vertex> path = dijskraAlgorithms.get(i).getPathToConnector(reverse);
               paths.add(path);
               if(reverse) {
                   lastConnector = path.get(0);
               } else {
                   lastConnector = path.get(path.size()-1);
               }
               Log.v("pridan pocatek","");
               continue;
           }

           if((i >  primaryOrigin.getGraphId()) &&  (i < primaryTarget.getGraphId())) {
               Log.v("pridan prostredek","");
               for(Vertex v : graphs.get(i).getVertexes()) {
                   if(lastConnector.getId().equals(v.getId())) {
                       List<Vertex> path = new ArrayList<Vertex>();
                       path.add(v);
                       path.add(v);
                       paths.add(path);
                       Log.v("Pridaaaano","");
                   }
               }
               continue;
           }

           if(i == primaryTarget.getGraphId()) {
               Log.v("lastConn:"+lastConnector.getId(),"");
               for(Vertex v : graphs.get(i).getVertexes()) {
                   if(lastConnector.getId().equals(v.getId())) {
                       dijskraAlgorithms.get(i).execute(v);
                       paths.add(dijskraAlgorithms.get(i).getPath(primaryTarget, reverse));
                       lastConnector = paths.get(i).get(paths.get(i).size()-1);
                       Log.v("pridan konec","");
                       continue;
                   }
               }
           };

       }
        //Log.v("cesta"+paths.size(),"");
       for(List<Vertex> path : paths) {
           //Log.v("floor","");
           for(Vertex vertex : path) {
               //Log.v("id:"+vertex.getId(),"");
           }
       }

       return paths;
   }

    public static Vertex getVertexFromRoom(Room room) {

        AppState mAppState = AppState.getInstance();
        Vertex vertex = null;

        for(Graph graph : mAppState.getSelectedBuilding().getFloorGraphs()) {

            for(Vertex v : graph.getVertexes()) {

                //Log.v("V Gid:" + v.getGraphId() + " Room floor:" + (room.getFloor()-1)  + " v id:"+ v.getId()+ " roomName:" + room.getName(),"");
                //Log.v(((v.getGraphId() == (room.getFloor()-1)) && (v.getId().equals(room.getName())))+"","");
                if((v.getGraphId() == (room.getFloor())) && (v.getId().equals(room.getName()))) {
                    vertex = v;
                }

            }

        }

        return vertex;

    }

    public static Room getRoomFromVertex(Vertex vertex) {

        AppState mAppState = AppState.getInstance();

        for(Room room : mAppState.getSelectedBuilding().getRooms()) {
            if((vertex.getGraphId() == (room.getFloor())) && (vertex.getId().equals(room.getName()))) {
                return room;
            }
        }

        return null;

    }

    public static Vertex getVertexByName(Building building, String name) {

        for(Graph graph : building.getFloorGraphs()) {

            for(Vertex vertex : graph.getVertexes()) {

                if(vertex.getId().equals(name)) {

                    return vertex;

                }

            }

        }

        return null;

    }

}
