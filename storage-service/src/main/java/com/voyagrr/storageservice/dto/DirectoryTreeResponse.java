package com.voyagrr.storageservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DirectoryTreeResponse {

    private Long id;
    private String name;
    private short type;
    private List<DirectoryTreeResponse> children = new ArrayList<>();

    public DirectoryTreeResponse(Long id, String name, short type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public void addChild(DirectoryTreeResponse child) {
        this.children.add(child);
    }

}
