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
    private List<DirectoryTreeResponse> children = new ArrayList<>();

    public DirectoryTreeResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addChild(DirectoryTreeResponse child) {
        this.children.add(child);
    }

}
