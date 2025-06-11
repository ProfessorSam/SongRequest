package de.professorsam.songrequest.data;

import de.professorsam.songrequest.SongRequest;

import java.util.Objects;

public class Student {
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

    public String generateEmail(){
        return String.format("""
                Hey %s,
                
                zur übergabe deines Zeugnisses erhältst du die Möglichkeit dir einen Song zu
                wünschen. Gehe dazu auf YouTube und suche deinen Song raus. Gehe auf den
                Link unten und füge dort den Link zu dem Song ein. Wenn du möchtest kannst
                du dann noch eine Startzeit für den Song wählen (z.B zur 2. Strophe).
                Gehe dann auf "Song einreichen" um ihn abzugeben.
                Bitte beachte, dass dieser Song nicht indiziert und zur Feierlichkeit
                passen sollte. Alle Songs werden vorher einzeln geprüft.
                
                Dein Link lautet: %s
                
                Liebe Grüße
                Samuel Rosenstein & Finn Grauwinkel
                """, name, SongRequest.getInstance().getAppHost() + "/s/" + id);
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
