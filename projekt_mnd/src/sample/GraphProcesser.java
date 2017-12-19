import java.util.*;

public class GraphProcesser {

    private boolean isFound;

    public static void calculateShortestPathInDirectedGraph(Node source) {
        source.setDistance(0);

        Set<Node> settledNodes = new HashSet<>();
        Set<Node> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            Node currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Map.Entry< Node, Integer> adjacencyPair:
                    currentNode.getNeighbours().entrySet()) {
                Node adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
    }

    private static Node getLowestDistanceNode(Set <Node> unsettledNodes) {
        Node lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (Node node: unsettledNodes) {
            int nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private static void calculateMinimumDistance(Node evaluationNode, int edgeWeight, Node sourceNode) {
        int sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeight < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeight);
            LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }

    public ArrayList<Node> findCriticalNodes(Node source, ArrayList<Node> allNodes, Node target) {
        ArrayList<Node> criticalNodes = new ArrayList<>();
        try {
            for(Node node: allNodes.subList(1,allNodes.size()-1)) {
                isFound = false;
                findLastNode(source,node,target);
                if(!isFound)criticalNodes.add(node);
            }
            System.out.println("CRITICAL NODES IN GRAPH: ");
            return criticalNodes;
        } catch (StackOverflowError e) {
            return null;
        }

    }

    public void findLastNode(Node source, Node deletedNode, Node target) {
        for(Map.Entry<Node, Integer> nodeIntegerEntry : source.getNeighbours().entrySet()) {
            if(nodeIntegerEntry.getKey() != deletedNode) {
                if(nodeIntegerEntry.getKey() == target) {
                    isFound = true;
                } else {
                    findLastNode(nodeIntegerEntry.getKey(), deletedNode,target);
                }
            }
        }
    }
}
