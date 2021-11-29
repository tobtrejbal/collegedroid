package tobous.collegedroid.utils;

import android.util.Log;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;


import tobous.collegedroid.core.AppCore;
import tobous.collegedroid.core.utils.AppState;
import tobous.collegedroid.functions.graphs.encapsulation.Edge;
import tobous.collegedroid.functions.graphs.encapsulation.Graph;
import tobous.collegedroid.functions.graphs.encapsulation.Vertex;
import tobous.collegedroid.functions.plans.encapsulation.Building;
import tobous.collegedroid.functions.plans.encapsulation.Room;
import tobous.collegedroid.functions.schedule.ScheduleUtils;
import tobous.collegedroid.functions.schedule.encapsulation.ScheduleAction;

/**
 * Created by Tobous on 7. 11. 2015.
 */
public class XmlManager {

    public static List<ScheduleAction> parseScheduleActions(String text, boolean addToRoomList, boolean filterBySemester) {

        AppCore mAppCore = AppCore.getInstance();
        AppState mAppState = AppState.getInstance();

        List<ScheduleAction> scheduleActions = new ArrayList<ScheduleAction>();

        try {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new InputSource( new StringReader( text )));

        NodeList nList = doc.getElementsByTagName("rozvrhovaAkce");

        //Log.v("----------------------------","");

        for (int temp = 0; temp < nList.getLength(); temp++) {

            ScheduleAction scheduleAction = new ScheduleAction();

            Node nNode = nList.item(temp);

            //Log.v("\nCurrent Element :" + nNode.getNodeName(),"");

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
                //Log.v("Předmět : " + eElement.getElementsByTagName("predmet").item(0).getTextContent(),"");
                String name = eElement.getElementsByTagName("predmet").item(0).getTextContent();

                String[] startDateStr = {"00","00"};
                String[] endDateStr = {"00","00"};
                String day = "Út";
                String capacity = "0";
                String occupancy = "0";
                String teacher = "neni";
                String actionType = "Cv";
                String startWeek = "0";
                String endWeek = "0";
                String weekType = "K";
                String place = "nikde";
                String semester = "ZS";
                try {
                    startDateStr = eElement.getElementsByTagName("hodinaSkutOd").item(0).getTextContent().split(":");
                } catch (NullPointerException ex) {

                }
                try {
                    endDateStr  = eElement.getElementsByTagName("hodinaSkutDo").item(0).getTextContent().split(":");
                } catch (NullPointerException ex) {

                }
                try {
                    day = eElement.getElementsByTagName("denZkr").item(0).getTextContent();
                } catch (NullPointerException ex) {

                }
                String fullName = eElement.getElementsByTagName("nazev").item(0).getTextContent();
                try {
                    capacity = eElement.getElementsByTagName("kapacitaMistnosti").item(0).getTextContent();
                } catch (NullPointerException ex) {

                }
                try {
                    occupancy = eElement.getElementsByTagName("obsazeni").item(0).getTextContent();
                } catch (NullPointerException ex) {

                }
                try {
                    teacher = eElement.getElementsByTagName("prijmeni").item(0).getTextContent();
                } catch (NullPointerException ex) {

                }
                try {
                    actionType = eElement.getElementsByTagName("typAkceZkr").item(0).getTextContent();
                } catch (NullPointerException ex) {

                }
                try {
                    startWeek = eElement.getElementsByTagName("tydenOd").item(0).getTextContent();
                } catch (NullPointerException ex) {

                }
                try {
                    endWeek = eElement.getElementsByTagName("tydenDo").item(0).getTextContent();
                } catch (NullPointerException ex) {

                }
                try {
                    weekType = eElement.getElementsByTagName("tydenZkr").item(0).getTextContent();
                } catch (NullPointerException ex) {

                }
                try {
                    place = eElement.getElementsByTagName("mistnost").item(0).getTextContent();
                } catch (NullPointerException ex) {

                }
                try {
                    semester = eElement.getElementsByTagName("semestr").item(0).getTextContent();
                } catch (NullPointerException ex) {

                }

                int weekTypeInt = 0;

                for(int l = 0; l < mAppCore.getWeekTypesShort().length;l++) {
                    if(mAppCore.getWeekTypesShort()[l].equals(weekType)) {
                        weekTypeInt = l;
                    }
                }

                int dayInt = DateUtils.getDayIntFromString(day);

                int actionTypeInt = 0;
                for(int l = 0; l < mAppCore.getActionTypesShort().length;l++) {
                    if(mAppCore.getActionTypesShort()[l].equals(actionType)) {
                        actionTypeInt = l;
                    }
                }

                scheduleAction.setPlace(place);
                scheduleAction.setCapacity(Integer.parseInt(capacity));
                scheduleAction.setOccupancy(Integer.parseInt(occupancy));
                scheduleAction.setTeacher(teacher);
                scheduleAction.setFullName(fullName);
                scheduleAction.setName(name);
                scheduleAction.setStartMinute(Integer.parseInt(startDateStr[1]));
                scheduleAction.setStartHour(Integer.parseInt(startDateStr[0]));
                scheduleAction.setEndMinute(Integer.parseInt(endDateStr[1]));
                scheduleAction.setEndHour(Integer.parseInt(endDateStr[0]));
                scheduleAction.setDayOfWeek(dayInt);
                scheduleAction.setActionType(actionTypeInt);
                scheduleAction.setId(ScheduleUtils.makeId(scheduleAction));
                scheduleAction.setUser(mAppState.getUserId());
                scheduleAction.setStartWeek(Integer.parseInt(startWeek));
                scheduleAction.setEndWeek(Integer.parseInt(endWeek));
                scheduleAction.setWeekType(weekTypeInt);

                if(addToRoomList) {
                    for(int i = 0; i < mAppCore.getBuildings().size();i++) {
                        List<Room> rooms = mAppCore.getBuildings().get(i).getRooms();
                        for(int j = 0; j < rooms.size(); j++ ) {
                            if(place.equals(rooms.get(j).getName())) {
                                rooms.get(j).getActionList().add(scheduleAction);
                            }
                        }
                    }
                }

                Log.v(scheduleAction.getName() + "", "lalala");

                if(filterBySemester) {
                    if(semester.equals(DateUtils.getCurrentSemester())) {
                     scheduleActions.add(scheduleAction);
                    }
                } else {
                    scheduleActions.add(scheduleAction);
                }

            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

        /*BufferedReader reader = new BufferedReader(new StringReader(text));
        try {
            String line;
            while((line = reader.readLine()) != null) {
                Log.v(line,"");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        return scheduleActions;

    }

    public static List<Building> loadBuildings(String text) {

        AppCore mAppCore = AppCore.getInstance();

        List<Building> buildings = new ArrayList<Building>();

        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource( new StringReader( text )));

            NodeList nList = doc.getElementsByTagName("building");

            //Log.v("----------------------------","");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Building building = new Building();

                Node nNode = nList.item(temp);

                //Log.v("\nCurrent Element :" + nNode.getNodeName(),"");

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    //Log.v("Budova : " + eElement.getElementsByTagName("name").item(0).getTextContent(),"");
                    String name = eElement.getElementsByTagName("name").item(0).getTextContent();
                    int floorCount = Integer.parseInt(eElement.getElementsByTagName("floorCount").item(0).getTextContent());
                    double lat = Double.parseDouble(eElement.getElementsByTagName("lat").item(0).getTextContent());
                    double lon = Double.parseDouble(eElement.getElementsByTagName("lon").item(0).getTextContent());

                    NodeList nListRooms = eElement.getElementsByTagName("room");

                    List<Room> rooms = new ArrayList<Room>();

                    for (int j = 0; j < nListRooms.getLength(); j++) {

                        Room room = new Room();

                        Node nNodeRoom = nListRooms.item(j);

                        Element eElementRoom = (Element) nNodeRoom;

                        String roomName = eElementRoom.getElementsByTagName("roomName").item(0).getTextContent();
                        String floorNumber = eElementRoom.getElementsByTagName("floorNumber").item(0).getTextContent();
                        /*String coordX = eElementRoom.getElementsByTagName("coordX").item(0).getTextContent();
                        String coordY = eElementRoom.getElementsByTagName("coordY").item(0).getTextContent();*/

                        List<ScheduleAction> scheduleActions = new ArrayList<ScheduleAction>();

                        room.setActionList(scheduleActions);
                        room.setName(roomName);
                        room.setFloor(Integer.parseInt(floorNumber)-1);
                       // room.setCoordinateX(Integer.parseInt(coordX));
                       // room.setCoordinateY(Integer.parseInt(coordY));

                        rooms.add(room);

                    }

                    int[] widths = new int[floorCount];
                    int[] heights = new int[floorCount];

                    NodeList nListFloorDimension = eElement.getElementsByTagName("floorDimension");

                    for (int j = 0; j < nListFloorDimension.getLength(); j++) {


                        Node nNodeDimension = nListFloorDimension.item(j);

                        //Log.v("\nCurrent Element :" + nNode.getNodeName(),"");

                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                            Element eElementDimension = (Element) nNodeDimension;

                            String width = eElementDimension.getElementsByTagName("width").item(0).getTextContent();
                            String height = eElementDimension.getElementsByTagName("height").item(0).getTextContent();

                            widths[j] = Integer.parseInt(width);
                            heights[j] = Integer.parseInt(height);

                        }
                    }

                    building.setWidths(widths);
                    building.setHeights(heights);
                    building.setPicture(mAppCore.getPATH_BUILDINGS()+name+".jpg");
                    building.setRooms(rooms);
                    building.setLat(lat);
                    building.setLon(lon);
                    building.setName(name);

                    building.setFloorCount(floorCount);

                    buildings.add(building);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return buildings;

    }

    public static Graph loadGraph(String text) {

        List<Vertex> vertices = new ArrayList<Vertex>();
        List<Edge> edges = new ArrayList<Edge>();

        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource( new StringReader( text )));

            NodeList nListVertices = doc.getElementsByTagName("vertex");

            //Log.v("----------------------------","");

            for (int i = 0; i < nListVertices.getLength(); i++) {

                Node nNodeVertex = nListVertices.item(i);

                Element eElementVertex = (Element) nNodeVertex;

                String graphId = eElementVertex.getElementsByTagName("idGraph").item(0).getTextContent();
                String name = eElementVertex.getElementsByTagName("name").item(0).getTextContent();
                String coordX = eElementVertex.getElementsByTagName("coordX").item(0).getTextContent();
                String coordY = eElementVertex.getElementsByTagName("coordY").item(0).getTextContent();
                String type = eElementVertex.getElementsByTagName("type").item(0).getTextContent();

                Vertex vertex = new Vertex(name, Integer.parseInt(type), Integer.parseInt(coordX), Integer.parseInt(coordY), Integer.parseInt(graphId));
                vertices.add(vertex);

            }

            NodeList nListEdges = doc.getElementsByTagName("edge");
            Log.v("buuuuuuuuuuuu","");
            //Log.v("----------------------------","");

            for (int i = 0; i < nListEdges.getLength(); i++) {

                Node nNodeEdge = nListEdges.item(i);

                Element eElementVertex = (Element) nNodeEdge;

                String id = eElementVertex.getElementsByTagName("idEdge").item(0).getTextContent();
                String sourceID = eElementVertex.getElementsByTagName("source").item(0).getTextContent();
                String destinationID = eElementVertex.getElementsByTagName("destination").item(0).getTextContent();

                Vertex source = null;
                Vertex destination = null;
                for(Vertex v : vertices) {
                    if(sourceID.equals(v.getId())) {
                        source = v;
                    }
                    if(destinationID.equals(v.getId())) {
                        destination = v;
                    }
                }

                Edge edge = new Edge(id, source, destination, calculateDistance(source, destination));
                edges.add(edge);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Graph graph = new Graph(vertices, edges);

        return graph;

    }

    private static int calculateDistance(Vertex vertex1, Vertex vertex2) {

        int a = Math.abs(vertex2.getCoordX()-vertex1.getCoordX());
        int b = Math.abs(vertex2.getCoordY()-vertex1.getCoordY());

        int distance = (int) Math.sqrt(a*a+b*b);

        return distance;


    }

}
