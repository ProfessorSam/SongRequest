@param de.professorsam.songrequest.AdminCtx ctx
<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <title>Kursverwaltung</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://unpkg.com/htmx.org@1.9.10"></script>
</head>
<body class="bg-light">
<div class="container-fluid py-4">
    <div class="d-flex justify-content-end mb-3">
        <button class="btn btn-success" data-bs-toggle="modal" data-bs-target="#addCourseModal">
            + Kurs hinzufügen
        </button>
    </div>

    <div class="row row-cols-1 row-cols-md-3 g-4" id="kanban-row">
        @if(ctx.courses().isEmpty())
            <div class="col">
                <div class="alert alert-info text-center w-100">
                    Noch keine Kurse vorhanden. Klicke auf "+ Kurs hinzufügen".
                </div>
            </div>
        @endif

        @for(var course : ctx.courses())
            <div class="col">
                <div class="card shadow-sm h-100">
                    <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">${course.name()}</h5>
                        <button class="btn btn-sm btn-light"
                                data-bs-toggle="modal"
                                data-bs-target="#addStudentModal"
                                data-course-id="${course.id()}">
                            +
                        </button>
                    </div>
                    <div class="card-body p-2" id="course-${course.id()}-students">
                        @for(var student : course.students())
                            <div class="card mb-2 draggable" id="student-${student.id()}">
                                <div class="card-body p-2 d-flex justify-content-between align-items-center">
                                    <span>${student.name()}</span>
                                    <form hx-post="/api/students/delete"
                                          hx-target="#student-${student.id()}"
                                          hx-swap="outerHTML"
                                          class="d-inline mb-0">
                                        <input type="hidden" name="id" value="${student.id()}">
                                        <button type="submit" class="btn btn-sm btn-danger">✕</button>
                                    </form>
                                </div>
                            </div>
                        @endfor
                    </div>
                </div>
            </div>
        @endfor

    </div>
</div>

<div class="modal fade" id="addCourseModal" tabindex="-1" aria-labelledby="addCourseModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form hx-post="/api/courses/add" hx-trigger="submit" hx-include="this" hx-target="body" hx-swap="outerHTML">
                <div class="modal-header">
                    <h5 class="modal-title" id="addCourseModalLabel">Neuen Kurs hinzufügen</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Schließen"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="course-name" class="form-label">Kursname</label>
                        <input type="text" class="form-control" id="course-name" name="name" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Abbrechen</button>
                    <button type="submit" class="btn btn-primary">Hinzufügen</button>
                </div>
            </form>
        </div>
    </div>
</div>

<div class="modal fade" id="addStudentModal" tabindex="-1" aria-labelledby="addStudentModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="addStudentForm"
                  hx-post="/api/students/add"
                  hx-trigger="submit"
                  hx-target="body"
                  hx-swap="outerHTML">
                <div class="modal-header">
                    <h5 class="modal-title" id="addStudentModalLabel">Neuen Schüler hinzufügen</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Schließen"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" id="student-course-id" name="courseId">
                    <div class="mb-3">
                        <label for="student-name" class="form-label">Name des Schülers</label>
                        <input type="text" class="form-control" id="student-name" name="name" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Abbrechen</button>
                    <button type="submit" class="btn btn-primary">Hinzufügen</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function registerStudentModalListener() {
        const studentModal = document.getElementById('addStudentModal');
        if (!studentModal) return;

        studentModal.removeEventListener('show.bs.modal', studentModal._listener);

        const listener = function (event) {
            const button = event.relatedTarget;
            const courseId = button?.getAttribute('data-course-id') || '';
            const input = studentModal.querySelector('#student-course-id');
            input.value = courseId;
        };

        studentModal._listener = listener;
        studentModal.addEventListener('show.bs.modal', listener);
    }

    document.addEventListener("DOMContentLoaded", registerStudentModalListener);

    document.body.addEventListener('htmx:afterSwap', function () {
        registerStudentModalListener();
    });
</script>
</body>
</html>
