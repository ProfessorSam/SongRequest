@import de.professorsam.songrequest.VideoComponentCtx
@param VideoComponentCtx context

<div class="mb-4">
    <div class="ratio ratio-16x9">
        <iframe id="ytPlayer" src="https://www.youtube.com/embed/${context.videoId()}?enablejsapi=1"
                title="YouTube video" frameborder="0" allowfullscreen></iframe>
    </div>
</div>

<div id="errorMessage" class="alert alert-danger d-none" role="alert"></div>

<form id="timeSelectForm" hx-post="/api/done/" hx-headers='{"Content-Type": "application/json"}' hx-trigger="submit" hx-swap="none">
    <div class="mb-3">
        <label for="startTime" class="form-label">Startzeit auswählen (in Sekunden)</label>
        <input type="range" class="form-range" min="0" max="${context.duration()}" value="0" id="startTime" oninput="updateStartTime(this.value)">
        <div id="startPreview">0:00</div>
    </div>
    <div class="mb-3">
        <label for="manualStart" class="form-label">Oder manuell (Sekunden)</label>
        <input type="number" class="form-control" id="manualStart" min="0" max="${context.duration()}" value="0" oninput="syncSlider(this.value)">
    </div>

    <input type="hidden" name="videoId" value="${context.videoId()}">

    <button type="submit" class="btn btn-success mt-2"
            hx-vals='js:{ "videoId": "${context.videoId()}", "startTime": parseInt(document.getElementById("startTime").value) }'>
        Abschicken
    </button>
</form>

<script>
    @raw
    function formatTime(s) {
        const m = Math.floor(s / 60), sec = s % 60;
        return `${m}:${sec.toString().padStart(2, '0')}`;
    }

    function updateStartTime(value) {
        document.getElementById('startPreview').innerText = formatTime(value);
        document.getElementById('manualStart').value = value;
        seekVideo(parseInt(value));
    }

    function syncSlider(value) {
        document.getElementById('startTime').value = value;
        updateStartTime(value);
    }

    function seekVideo(seconds) {
        if (window.player && typeof window.player.seekTo === 'function') {
            window.player.seekTo(seconds, true);
        }
    }

    // Load YouTube IFrame API
    var tag = document.createElement('script');
    tag.src = "https://www.youtube.com/iframe_api";
    var firstScriptTag = document.getElementsByTagName('script')[0];
    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

    var player;
    function onYouTubeIframeAPIReady() {
        player = new YT.Player('ytPlayer', {
            events: {
                'onReady': function (event) {
                    window.player = event.target;
                }
            }
        });
    }

    document.body.addEventListener('htmx:responseError', function(evt) {
        const err = document.getElementById('errorMessage');
        err.classList.remove('d-none');
        err.innerText = 'Fehler beim Laden oder Absenden. Bitte überprüfe den YouTube-Link oder versuche es erneut.';
    });
    @endraw
</script>
