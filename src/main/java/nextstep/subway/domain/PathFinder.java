package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {

    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public PathFinder(List<Line> lines) {
        this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        lines.forEach(line -> line.registerToPathFinder(this));
    }

    void registerSection(Station upStation, Station downStation, int distance) {
        if (!graph.containsVertex(upStation)) {
            graph.addVertex(upStation);
        }
        if (!graph.containsVertex(downStation)) {
            graph.addVertex(downStation);
        }

        graph.setEdgeWeight(graph.addEdge(upStation, downStation), distance);
    }

    private GraphPath<Station, DefaultWeightedEdge> getDijkstraShortestPath(Station source, Station target) {
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);

        try {
            return dijkstraShortestPath.getPath(source, target);
        } catch (IllegalArgumentException e) {
            throw e.getMessage().equals("graph must contain the sink vertex")
                    ? new IllegalArgumentException("연결이 되어 있지 않은 구간 입니다.")
                    : e;
        }
    }

    public List<Station> getShortestPathStations(Station source, Station target) {
        return getDijkstraShortestPath(source, target).getVertexList();
    }

    public Double getShortestPathDistance(Station source, Station target) {
        return getDijkstraShortestPath(source, target).getWeight();
    }
}
