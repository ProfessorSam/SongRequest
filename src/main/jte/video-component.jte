@import de.professorsam.songrequest.VideoComponentCtx
@param VideoComponentCtx context

<div class="mb-4">
    <div class="ratio ratio-16x9 rounded shadow-sm overflow-hidden">
        <iframe id="ytPlayer"
                src="https://www.youtube.com/embed/${context.videoId()}?enablejsapi=1"
                title="YouTube video"
                allowfullscreen>
        </iframe>
    </div>
</div>

<div id="errorMessage" class="alert alert-danger d-none" role="alert"></div>

<form id="timeSelectForm"
      hx-post="/api/done/"
      hx-trigger="submit"
      hx-swap="none"
      onsubmit="return validateSubmission(${context.duration()})">

    <input type="hidden" name="videoId" value="${context.videoId()}">
    <input type="hidden" name="startTime" id="startTimeHidden" value="0">
    <input type="hidden" name="userId" value="${context.userid()}">

    <div class="mb-3">
        <label for="startTime" class="form-label fw-bold">Startzeit auswählen</label>
        <input type="range"
               class="form-range"
               min="0"
               max="${context.duration()}"
               value="0"
               id="startTime"
               oninput="onSliderChange(this.value)">
        <div class="d-flex justify-content-between small text-muted mt-1">
            <span>Start: <span id="startPreview">0:00</span></span>
            <span>Max: ${(int)Math.floor(context.duration() / 60F)}:${context.duration() % 60}</span>
        </div>
    </div>

    <div class="mb-3">
        <label for="manualStart" class="form-label">Oder manuell eingeben (mm:ss)</label>
        <input type="text"
               class="form-control"
               id="manualStart"
               placeholder="z. B. 1:23"
               oninput="onManualInput(this.value)">
    </div>

    <button type="submit" class="btn btn-primary w-100">
        🎵 Song einreichen
    </button>
</form>

<script>
    @raw
    function formatTime(s) {
        const m = Math.floor(s / 60);
        const sec = s % 60;
        return `${m}:${sec.toString().padStart(2, '0')}`;
    }

    function parseTime(str) {
        const match = str.match(/^(\d{1,2}):(\d{2})$/);
        if (!match) return null;
        const minutes = parseInt(match[1]);
        const seconds = parseInt(match[2]);
        if (seconds >= 60) return null;
        return minutes * 60 + seconds;
    }

    function onSliderChange(value) {
        const seconds = parseInt(value);
        document.getElementById('startPreview').innerText = formatTime(seconds);
        document.getElementById('manualStart').value = formatTime(seconds);
        document.getElementById('startTimeHidden').value = seconds;
        seekTo(seconds);
    }

    function onManualInput(value) {
        const seconds = parseTime(value);
        if (seconds != null) {
            document.getElementById('startTime').value = seconds;
            onSliderChange(seconds);
        }
    }

    function validateSubmission(duration) {
        const seconds = parseInt(document.getElementById('startTime').value);
        const err = document.getElementById('errorMessage');
        err.classList.add('d-none');

        if (duration - seconds < 30) {
            err.textContent = 'Bitte wähle eine Startzeit, die mindestens 30 Sekunden vom Ende entfernt ist.';
            err.classList.remove('d-none');
            return false;
        }

        document.getElementById('startTimeHidden').value = seconds;
        return true;
    }

    function seekTo(seconds) {
        if (window.player?.seekTo) {
            window.player.seekTo(seconds, true);
        }
    }

    // Load YouTube API
    const tag = document.createElement('script');
    tag.src = "https://www.youtube.com/iframe_api";
    document.head.appendChild(tag);

    let player;
    function onYouTubeIframeAPIReady() {
        player = new YT.Player('ytPlayer', {
            events: {
                onReady: (event) => window.player = event.target
            }
        });
    }

    document.body.addEventListener('htmx:responseError', () => {
        const err = document.getElementById('errorMessage');
        err.classList.remove('d-none');
        err.textContent = 'Fehler beim Absenden. Bitte überprüfe deine Eingabe.';
    });
    @endraw
</script>

<style>
    @media (max-width: 576px) {
        #startPreview {
            font-size: 1rem;
        }
    }

    .ratio iframe {
        border-radius: 0.5rem;
    }
</style>
