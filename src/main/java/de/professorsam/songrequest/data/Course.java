package de.professorsam.songrequest.data;

import java.util.List;

public record Course (String id, String name, List<Student> students) {
}
