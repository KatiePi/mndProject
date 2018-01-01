package sample;
import javafx.scene.control.TextArea;

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
            if(source.getNeighbours().entrySet().size() == 1) {
                criticalNodes.add(source);
            }
            for(GraphNode node: allNodes) {
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
        for(Map.Entry<GraphNode, Integer> nodeIntegerEntry : source.getNeighbours().entrySet()) {
            if(nodeIntegerEntry.getKey() != deletedNode) {
                if(nodeIntegerEntry.getKey() == target) {
                    isFound = true;
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

    public static GraphNode firstNode(ArrayList<GraphNode> allNodes) {
        ArrayList<GraphNode> firstNodeInList = allNodes;
        ArrayList<GraphNode> nodesToRemove = new ArrayList<>();

        for(GraphNode node: allNodes) {
            for(GraphNode nodeNeighbouring: allNodes) {
                if(node != nodeNeighbouring) {
                    for(Map.Entry<GraphNode, Integer> nodeIntegerEntry : nodeNeighbouring.getNeighbours().entrySet()) {
                        if(nodeIntegerEntry.getKey() == node){
                            nodesToRemove.add(node);
                        }
                    }
                }
            }
        }
       return firstNodeInList.get(0);
    }

    public boolean graphAcyclic(ArrayList<GraphNode> allNodes) {
        isFound = false;
        try {
            for(GraphNode node : allNodes) {
                findLastNode(node, null, node);
                if(isFound) return false;
            }
        } catch (StackOverflowError soe) {
            return false;
        }

        return true;
    }

    public static boolean validateNecessaryActivitiesTextField(TextArea text, ArrayList<GraphNode> allNodes) {
        String[] rows = text.getText().split("\n");
        String[] letters;
        if(ApplicationController.areRowsValidated(rows)) {
             boolean nodesMatched = false;
             for(String row: rows) {
                  nodesMatched = false;
                  for(GraphNode node: allNodes) {
                      letters  = row.split(" ");
                      if(Objects.equals(letters[0], node.getName())) {
                          nodesMatched = true;
                          for(Map.Entry<GraphNode, Integer> nodeIntegerEntry: node.getNeighbours().entrySet()) {
                              if(!Objects.equals(nodeIntegerEntry.getKey().getName(), letters[2]) || nodeIntegerEntry.getValue() != Integer.parseInt(letters[1])) return false;
                          }
                      }
                  }
                  if(!nodesMatched) return false;
             }
        } else {
            return false;
        }
        return true;

    }

    public static LinkedHashMap<GraphNode, GraphNode> getListOfNecessaryNodes(TextArea text, ArrayList<GraphNode> allNodes) {
        LinkedHashMap<GraphNode, GraphNode> returnMap = new LinkedHashMap<>();
        String[] rows = text.getText().split("\n");
        String[] letters;
        for(String row: rows) {
            letters  = row.split(" ");
            for(GraphNode node: allNodes) {
                if(Objects.equals(letters[0], node.getName())){
                    for(Map.Entry<GraphNode, Integer> nodeIntegerEntry : node.getNeighbours().entrySet()) {
                        if(Objects.equals(letters[2], nodeIntegerEntry.getKey().getName())) {
                            returnMap.put(node, nodeIntegerEntry.getKey());
                        }
                    }
                }
            }
        }
        return returnMap;
    }


}
