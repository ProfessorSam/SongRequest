package de.professorsam.songrequest.data;

import java.util.Objects;

public final class Student {
    private final String id;
    private final String name;
    private String videoID;
    private int startTime;

    public Student(String id, String name, String videoID, int startTime) {
        this.id = id;
        this.name = name;
        this.videoID = videoID;
        this.startTime = startTime;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String videoID() {
        return videoID;
    }

    public int startTime() {
        return startTime;
    }

    public void videoID(String videoID) {
        this.videoID = videoID;
    }

    public void startTime(int startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Student) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.videoID, that.videoID) &&
                this.startTime == that.startTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, videoID, startTime);
    }

    @Override
    public String toString() {
        return "Student[" +
                "id=" + id + ", " +
                "name=" + name + ", " +
                "videoID=" + videoID + ", " +
                "startTime=" + startTime + ']';
    }


}
