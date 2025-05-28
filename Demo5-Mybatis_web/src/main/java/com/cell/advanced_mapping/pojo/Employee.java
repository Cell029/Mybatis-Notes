package com.cell.advanced_mapping.pojo;

import java.util.List;

public class Employee {
    private Integer id;
    private String name;
    private List<Project> projects;

    public Employee() {

    }

    public Employee(Integer id, String name, List<Project> projects) {
        this.id = id;
        this.name = name;
        this.projects = projects;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", projects=" + projects +
                '}';
    }
}
