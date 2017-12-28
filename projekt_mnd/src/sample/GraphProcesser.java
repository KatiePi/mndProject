import java.lang.reflect.Array;
import java.util.*;

public class GraphProcesser {

    private boolean isFound;

    public static void calculateShortestPathInDirectedGraph(GraphNode source) {
        source.setDistance(0);

        Set<GraphNode> settledNodes = new HashSet<>();
        Set<GraphNode> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            GraphNode currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Map.Entry<GraphNode, Integer> adjacencyPair:
                    currentNode.getNeighbours().entrySet()) {
                GraphNode adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
    }

    private static GraphNode getLowestDistanceNode(Set <GraphNode> unsettledNodes) {
        GraphNode lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (GraphNode node: unsettledNodes) {
            int nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private static void calculateMinimumDistance(GraphNode evaluationNode, int edgeWeight, GraphNode sourceNode) {
        int sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeight < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeight);
            LinkedList<GraphNode> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }

    public ArrayList<GraphNode> findCriticalNodes(GraphNode source, ArrayList<GraphNode> allNodes, GraphNode target) {
        ArrayList<GraphNode> criticalNodes = new ArrayList<>();
        try {
            for(GraphNode node: allNodes.subList(1,allNodes.size())) {
                System.out.println("IS NODE CRITICAL? :: " + node.getName());
                isFound = false;
                findLastNode(source,node,target);
                if(!isFound)criticalNodes.add(node);
            }
            return criticalNodes;
        } catch (StackOverflowError e) {
            return null;
        }

    }

    public void findLastNode(GraphNode source, GraphNode deletedNode, GraphNode target) {
        System.out.println("   node :: " + source.getName());
        for(Map.Entry<GraphNode, Integer> nodeIntegerEntry : source.getNeighbours().entrySet()) {
            System.out.println( "     " + source.getName() + " neighbour :: " + nodeIntegerEntry.getKey().getName());
            if(nodeIntegerEntry.getKey() != deletedNode) {
                if(nodeIntegerEntry.getKey() == target) {
                    isFound = true;
                    System.out.println("LAST NODE FOUND!");
                    break;
                } else {
                    findLastNode(nodeIntegerEntry.getKey(), deletedNode,target);
                }
            }
        }
    }

    public static GraphNode lastNode(ArrayList<GraphNode> allNodes) {
        for(GraphNode node: allNodes) {
            if(node.getNeighbours().entrySet().size() == 0) {
                return node;
            }
        }
        return null;
    }

}
