<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Fertig!</title>
    <style>
        html, body {
            margin: 0;
            padding: 0;
            background: black;
            overflow: hidden;
        }

        canvas {
            display: block;
            position: absolute;
            top: 0;
            left: 0;
            z-index: 0;
        }

        .center-text {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            font-family: sans-serif;
            font-size: 5em;
            font-weight: bold;
            color: white;
            z-index: 1;
            pointer-events: none;
            text-shadow: 0 0 20px white;
        }
    </style>
</head>
<body>
<div class="center-text">Fertig!</div>
<canvas id="fireworks"></canvas>
@raw
<script>
    const canvas = document.getElementById('fireworks');
    const ctx = canvas.getContext('2d');
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;

    window.addEventListener('resize', () => {
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
    });

    class Particle {
        constructor(x, y, color, angle, speed, decay) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.angle = angle;
            this.speed = speed;
            this.alpha = 1;
            this.decay = decay;
        }

        update() {
            this.x += Math.cos(this.angle) * this.speed;
            this.y += Math.sin(this.angle) * this.speed + 0.5;
            this.speed *= 0.98;
            this.alpha -= this.decay;
        }

        draw(ctx) {
            ctx.save();
            ctx.globalAlpha = this.alpha;
            ctx.fillStyle = this.color;
            ctx.beginPath();
            ctx.arc(this.x, this.y, 2, 0, Math.PI * 2);
            ctx.fill();
            ctx.restore();
        }
    }

    class Rocket {
        constructor(x, y, targetY, color) {
            this.x = x;
            this.y = y;
            this.targetY = targetY;
            this.color = color;
            this.trail = [];
            this.exploded = false;
        }

        update() {
            this.trail.push({ x: this.x, y: this.y, alpha: 1 });
            this.y -= 5;

            // Fade old trail
            for (let t of this.trail) t.alpha -= 0.05;
            this.trail = this.trail.filter(t => t.alpha > 0);

            if (this.y <= this.targetY && !this.exploded) {
                this.explode();
                this.exploded = true;
            }
        }

        draw(ctx) {
            // Trail
            for (let t of this.trail) {
                ctx.save();
                ctx.globalAlpha = t.alpha;
                ctx.fillStyle = this.color;
                ctx.beginPath();
                ctx.arc(t.x, t.y, 2, 0, Math.PI * 2);
                ctx.fill();
                ctx.restore();
            }

            // Rocket head
            if (!this.exploded) {
                ctx.save();
                ctx.fillStyle = this.color;
                ctx.beginPath();
                ctx.arc(this.x, this.y, 3, 0, Math.PI * 2);
                ctx.fill();
                ctx.restore();
            }
        }

        explode() {
            const count = 50 + Math.random() * 30;
            for (let i = 0; i < count; i++) {
                const angle = Math.random() * 2 * Math.PI;
                const speed = Math.random() * 5 + 2;
                const decay = Math.random() * 0.015 + 0.007;
                particles.push(new Particle(this.x, this.y, this.color, angle, speed, decay));
            }
        }
    }

    const rockets = [];
    const particles = [];

    function launchRocket(x = Math.random() * canvas.width) {
        const y = canvas.height;
        const targetY = Math.random() * canvas.height * 0.5;
        const colors = ['#ff0043', '#14fc56', '#1e90ff', '#ffff00', '#ff8000'];
        const color = colors[Math.floor(Math.random() * colors.length)];
        rockets.push(new Rocket(x, y, targetY, color));
    }

    function animate() {
        ctx.fillStyle = "rgba(0, 0, 0, 0.2)";
        ctx.fillRect(0, 0, canvas.width, canvas.height);

        for (let i = rockets.length - 1; i >= 0; i--) {
            const r = rockets[i];
            r.update();
            r.draw(ctx);
            if (r.exploded) rockets.splice(i, 1);
        }

        for (let i = particles.length - 1; i >= 0; i--) {
            const p = particles[i];
            p.update();
            p.draw(ctx);
            if (p.alpha <= 0) particles.splice(i, 1);
        }

        requestAnimationFrame(animate);
    }

    // Launch rockets automatically
    setInterval(() => {
        launchRocket();
    }, 1000);

    // Click to launch
    canvas.addEventListener('click', (e) => {
        launchRocket(e.clientX);
    });

    animate();
</script>
@endraw
</body>
</html>
