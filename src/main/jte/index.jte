@import de.professorsam.songrequest.IndexCtx
@param IndexCtx context

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Songwunsch für die Abiturfeier</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    <script src="https://unpkg.com/htmx.org@1.9.2"></script>
    <script src="https://unpkg.com/hyperscript.org@0.9.7"></script>
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background: #f4f4f9;
            padding: 2rem;
        }
        .container {
            max-width: 600px;
            background: white;
            padding: 2rem;
            border-radius: 1rem;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        h1 {
            font-size: 1.8rem;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Hallo ${context.student()}!</h1>
    <p>Du bist in: <strong>${context.course()}</strong></p>
    <p>Wähle deinen Song für die Übergabe deines Zeugnisses:</p>

    <div id="errorMessage" class="alert alert-danger d-none" role="alert"></div>

    <form id="linkForm" hx-post="/api/postlink/" hx-target="#videoComponent" hx-swap="innerHTML">
        <div class="mb-3">
            <label for="youtubeLink" class="form-label">YouTube-Link</label>
            <input type="url" class="form-control" id="youtubeLink" name="link" required>
        </div>
        <button type="submit" class="btn btn-primary">Video laden</button>
    </form>

    <div id="videoComponent" class="mt-4">
        <!-- Filled dynamically with video and time selection -->
    </div>
</div>

<script>
    @raw
    document.body.addEventListener('htmx:responseError', function(evt) {
        const err = document.getElementById('errorMessage');
        err.classList.remove('d-none');
        err.innerText = 'Ungültiger YouTube-Link oder ein Fehler ist aufgetreten. Bitte erneut versuchen.';
    });
    @endraw
</script>
</body>
</html>
