package com.iscas.lndicatormonitor.domain.topo;

import lombok.Data;

@Data
public class Edge {
    private String source;
    private String target;

    public Edge(String s, String s1) {
        this.source=s;
        this.target=s1;
    }
}
