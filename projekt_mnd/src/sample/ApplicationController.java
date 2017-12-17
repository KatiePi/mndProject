package sample;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

public class ApplicationController {

    //TODO validate if weight is numeric
    @FXML private TextArea graphInput;
    private Graph graph;

    @FXML
    protected void add() {
        this.graph = new SingleGraph("Network");
        this.graph.setStrict(false);
        this.graph.setAutoCreate( true );

        System.out.println("The button was clicked!");
        String[] rows = splitInputIntoRows();
        createGraphFromSingleRows(rows);
        //here create object which caltulate the shortest/longest paths etc - as an argument get graph
    }

    public String[] splitInputIntoRows() {
        String graphInputString = graphInput.getText();
        String[] rows = graphInputString.split("\n");
        return rows;
    }

    public void createGraphFromSingleRows(String[] rows) {
        for ( String row : rows) {
            String[] letters = row.split("");
            this.graph.addEdge( row, letters[0], letters[2] );
            graph.getEdge(row).setAttribute("weight", Integer.parseInt(letters[1]));
        }
    }
}
