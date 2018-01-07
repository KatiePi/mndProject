package sample;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GraphNode implements Comparable, Cloneable {

    private String name;
    private List<GraphNode> shortestPath = new LinkedList<>();
    private Integer distance = Integer.MAX_VALUE;

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    private boolean visited = false;

    private Map<GraphNode, Integer> neighbours = new HashMap<>();

    public GraphNode(String name) {
        this.name = name;
    }

    public void addNeighbour(GraphNode destination, int distance) {
        neighbours.put(destination, distance);
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setShortestPath(LinkedList<GraphNode> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public List<GraphNode> getShortestPath() {
        return shortestPath;
    }

    public void clearShortestPath() {
        this.shortestPath.clear();
    }



    public Map<GraphNode,Integer> getNeighbours() {
        return neighbours;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShortestPath(List<GraphNode> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public void setNeighbours(Map<GraphNode, Integer> neighbours) {
        this.neighbours = neighbours;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }


}