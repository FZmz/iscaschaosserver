package com.iscas.lndicatormonitor.domain.topo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Topo {
    List<Node> nodes;
    List<Edge> edges;
    // Topo类的构造方法
    public Topo() {
        this.nodes = new ArrayList<>(); // 初始化节点列表
        this.edges = new ArrayList<>(); // 初始化边列表
    }

    public void addNode(Node node) {
        this.nodes.add(node);
    }

    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }
}
