package com.iscas.lndicatormonitor.domain.topo;

import lombok.Data;

@Data
public class Node {
    private String id;
    private String label;
    private String imagetype;

    public Node(String s, String s1, String springBoot) {
        this.id=s;
        this.label=s1;
        this.imagetype=springBoot;
    }
}
