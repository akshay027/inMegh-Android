package com.exalogic.inmeghschool.Models.ParentModels.ParentTimeTable;

import com.exalogic.inmeghschool.Models.TeacherModels.database.TimeTable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Exalogic on 28-Aug-16.
 */
public class ParentTimeTableStructure {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("time_table")
    @Expose
    private List<TimeTable> timeTable = null;

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

    public List<TimeTable> getTimeTable() {
        return timeTable;
    }

    public void setTimeTable(List<TimeTable> timeTable) {
        this.timeTable = timeTable;
    }

    @Override
    public String toString() {
        return "ParentTimeTableStructure{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", timeTable=" + timeTable +
                '}';
    }
}
