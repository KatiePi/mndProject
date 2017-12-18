package sample;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;

import java.awt.*;

public class ApplicationController {

    //TODO validate if weight is numeric
    @FXML private TextArea graphInput;
    @FXML private SwingNode graphNode;
    private Graph graph;

    @FXML
    protected void add() {
        this.graph = new SingleGraph("Network");
        this.graph.setStrict(false);
        this.graph.setAutoCreate(true);
        this.graph.addAttribute("ui.stylesheet",
            "node { " +
                        "text-alignment: at-right; " +
                        "text-padding: 3px, 2px; " +
                        "text-background-mode: rounded-box; " +
                        "text-background-color: #A7CC; " +
                        "text-color: white; " +
                        "text-style: bold-italic; " +
                        "text-offset: 5px, 0px;" +
                        "fill-color: red; " +
                        "size: 10px, 10px;" +
                    "}" +
                    "edge { " +
                        "fill-color: blue; " +
                        "text-alignment: along;" +
                        "text-style: bold-italic;" +
                        "text-padding: 3px, 2px; " +
                        "text-background-mode: rounded-box; " +
                        "text-background-color: grey; " +
                        "text-color: white; " +
                    "}");
        this.graph.addAttribute("ui.quality");
        this.graph.addAttribute("ui.antialias");

        System.out.println("The button was clicked!");
        String[] rows = splitInputIntoRows();
        createGraphFromSingleRows(rows);

        graphNode.setContent(null);
        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_SWING_THREAD);
        viewer.enableAutoLayout();
        View view = viewer.addDefaultView(false);
        view.setMinimumSize(new Dimension(249, 200));
        graphNode.setContent(view);

        //here create object which caltulate the shortest/longest paths etc - as an argument get graph
    }

    public String[] splitInputIntoRows() {
        String graphInputString = graphInput.getText();
        String[] rows = graphInputString.split("\n");
        return rows;
    }

    public void createGraphFromSingleRows(String[] rows) {
        for ( String row : rows) {
            String[] letters = row.split(" ");
            this.graph.addEdge( row, letters[0], letters[2], true);
            graph.getEdge(row).setAttribute("weight", Integer.parseInt(letters[1]));
            graph.getEdge(row).setAttribute("ui.label", Integer.parseInt(letters[1]));
        }

        for (Node node : graph.getNodeSet()) {
            node.setAttribute("ui.label", node.getId());
        }
    }
}
