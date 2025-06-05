package de.professorsam.songrequest;

import de.professorsam.songrequest.data.Course;

import java.util.List;

public record AdminCtx (List<Course> courses) {
}
