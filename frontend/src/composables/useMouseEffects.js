/**
 * 全新极客星尘微光粒子与鼠标交互引擎 (Magic Dust / Sparkles Cursor Engine)
 * - 恢复操作系统原生清脆指针，彻底告别生硬跟随圆圈
 * - 鼠标划过产生天蓝 (#38bdf8)、星辉紫 (#818cf8)、微金 (#fbbf24) 四芒星微光与星尘拖尾
 * - 鼠标点击产生轻盈灵动的星辉爆散微粒
 * - 自适应休眠引擎：静止与无粒子时自动暂停 RAF 循环，零空转 CPU 开销
 */

export function initMouseEffects() {
  const isTouchDevice = ('ontouchstart' in window) || (navigator.maxTouchPoints > 0) || !window.matchMedia('(hover: hover)').matches;
  if (isTouchDevice && window.innerWidth < 768) {
    return () => {};
  }

  const canvas = document.getElementById('magic-dust-canvas');
  const ambient = document.getElementById('ambient-cursor');
  if (!canvas) return () => {};

  const ctx = canvas.getContext('2d');
  if (!ctx) return () => {};

  let dpr = Math.min(window.devicePixelRatio || 1, 2);
  let width = window.innerWidth;
  let height = window.innerHeight;

  function resizeCanvas() {
    dpr = Math.min(window.devicePixelRatio || 1, 2);
    width = window.innerWidth;
    height = window.innerHeight;
    canvas.width = width * dpr;
    canvas.height = height * dpr;
    canvas.style.width = `${width}px`;
    canvas.style.height = `${height}px`;
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
  }

  resizeCanvas();
  window.addEventListener('resize', resizeCanvas);

  const colors = ['#38bdf8', '#818cf8', '#fbbf24', '#60a5fa', '#a78bfa'];

  // 四芒星绘制辅助函数
  function drawStar(context, cx, cy, size, rotation, color, alpha) {
    context.save();
    context.translate(cx, cy);
    context.rotate(rotation);
    context.beginPath();

    const spikes = 4;
    const outerRadius = size;
    const innerRadius = size * 0.28;
    let rot = (Math.PI / 2) * 3;
    const step = Math.PI / spikes;

    context.moveTo(0, -outerRadius);
    for (let i = 0; i < spikes; i++) {
      let x = Math.cos(rot) * outerRadius;
      let y = Math.sin(rot) * outerRadius;
      context.lineTo(x, y);
      rot += step;

      x = Math.cos(rot) * innerRadius;
      y = Math.sin(rot) * innerRadius;
      context.lineTo(x, y);
      rot += step;
    }
    context.lineTo(0, -outerRadius);
    context.closePath();

    context.fillStyle = color;
    context.globalAlpha = Math.max(0, Math.min(1, alpha));
    context.shadowColor = color;
    context.shadowBlur = size * 1.2;
    context.fill();
    context.restore();
  }

  // 柔和微光光子圆点
  function drawGlowDot(context, cx, cy, radius, color, alpha) {
    context.save();
    context.beginPath();
    context.arc(cx, cy, radius, 0, Math.PI * 2);
    context.fillStyle = color;
    context.globalAlpha = Math.max(0, Math.min(1, alpha));
    context.shadowColor = color;
    context.shadowBlur = radius * 2;
    context.fill();
    context.restore();
  }

  // 粒子数据模型
  class SparkleParticle {
    constructor(x, y, isBurst = false) {
      this.x = x;
      this.y = y;
      this.color = colors[Math.floor(Math.random() * colors.length)];
      this.isStar = Math.random() > 0.45; // 55% 概率为四芒星，45% 为微光粒子

      if (isBurst) {
        // 点击爆散：360度发射，具有初速度与弹性
        const angle = Math.random() * Math.PI * 2;
        const speed = Math.random() * 3.8 + 1.2;
        this.vx = Math.cos(angle) * speed;
        this.vy = Math.sin(angle) * speed;
        this.maxLife = Math.floor(Math.random() * 18) + 24;
        this.size = Math.random() * 4 + 3.5;
      } else {
        // 移动拖尾：柔和微扰
        this.vx = (Math.random() - 0.5) * 1.1;
        this.vy = (Math.random() - 0.5) * 1.1 + 0.35;
        this.maxLife = Math.floor(Math.random() * 14) + 18;
        this.size = Math.random() * 3.2 + 2.4;
      }

      this.life = this.maxLife;
      this.rotation = Math.random() * Math.PI * 2;
      this.vRot = (Math.random() - 0.5) * 0.15;
    }

    update() {
      this.x += this.vx;
      this.y += this.vy;
      this.vx *= 0.95;
      this.vy *= 0.95;
      this.vy += 0.035; // 细微重力下沉感
      this.rotation += this.vRot;
      this.life--;
    }

    draw(context) {
      const progress = this.life / this.maxLife;
      const currentSize = Math.max(0.5, this.size * progress);
      const alpha = progress * 0.9;
      if (this.isStar) {
        drawStar(context, this.x, this.y, currentSize, this.rotation, this.color, alpha);
      } else {
        drawGlowDot(context, this.x, this.y, currentSize * 0.85, this.color, alpha);
      }
    }
  }

  const particles = [];
  let mouseX = width / 2;
  let mouseY = height / 2;
  let lastSpawnX = mouseX;
  let lastSpawnY = mouseY;
  let ambX = mouseX;
  let ambY = mouseY;

  let animationFrameId = null;
  let isLoopRunning = false;

  function addParticle(x, y, isBurst = false) {
    if (particles.length < 120) {
      particles.push(new SparkleParticle(x, y, isBurst));
    }
  }

  function startLoopIfNeeded() {
    if (!isLoopRunning) {
      isLoopRunning = true;
      animationFrameId = requestAnimationFrame(renderLoop);
    }
  }

  function onMouseMove(e) {
    mouseX = e.clientX;
    mouseY = e.clientY;

    // 连续移动星尘产生算法：根据移动距离插值
    const dist = Math.hypot(mouseX - lastSpawnX, mouseY - lastSpawnY);
    if (dist >= 10) {
      const steps = Math.min(Math.floor(dist / 14), 3);
      for (let i = 0; i <= steps; i++) {
        const ratio = steps === 0 ? 1 : i / steps;
        const px = lastSpawnX + (mouseX - lastSpawnX) * ratio + (Math.random() - 0.5) * 4;
        const py = lastSpawnY + (mouseY - lastSpawnY) * ratio + (Math.random() - 0.5) * 4;
        addParticle(px, py, false);
      }
      lastSpawnX = mouseX;
      lastSpawnY = mouseY;
    }

    startLoopIfNeeded();

    // 现有卡片聚光灯与倾斜效果保持
    const cards = document.querySelectorAll('.spotlight-card');
    cards.forEach(card => {
      const rect = card.getBoundingClientRect();
      if (mouseX >= rect.left - 100 && mouseX <= rect.right + 100 &&
          mouseY >= rect.top - 100 && mouseY <= rect.bottom + 100) {
        card.style.setProperty('--mouse-x', `${mouseX - rect.left}px`);
        card.style.setProperty('--mouse-y', `${mouseY - rect.top}px`);
      }
    });

    const tiltCards = document.querySelectorAll('.tilt-card');
    tiltCards.forEach(card => {
      const rect = card.getBoundingClientRect();
      if (mouseX >= rect.left && mouseX <= rect.right && mouseY >= rect.top && mouseY <= rect.bottom) {
        const normX = (mouseX - rect.left) / rect.width - 0.5;
        const normY = (mouseY - rect.top) / rect.height - 0.5;
        card.style.transform = `perspective(1000px) rotateY(${normX * 6}deg) rotateX(${-normY * 6}deg) translateY(-2px)`;
      } else {
        card.style.transform = 'perspective(1000px) rotateY(0deg) rotateX(0deg) translateY(0px)';
      }
    });
  }

  function onPointerDown(e) {
    // 点击爆散特效：产生一圈向外飞溅的星辉
    const clickX = e.clientX;
    const clickY = e.clientY;
    const burstCount = 12;
    for (let i = 0; i < burstCount; i++) {
      addParticle(clickX, clickY, true);
    }
    startLoopIfNeeded();
  }

  function renderLoop() {
    ctx.clearRect(0, 0, width, height);

    // 平滑驱动深层极光背景光斑
    ambX += (mouseX - ambX) * 0.05;
    ambY += (mouseY - ambY) * 0.05;
    if (ambient) {
      ambient.style.left = `${ambX}px`;
      ambient.style.top = `${ambY}px`;
    }

    // 渲染并更新粒子
    for (let i = particles.length - 1; i >= 0; i--) {
      const p = particles[i];
      p.update();
      p.draw(ctx);
      if (p.life <= 0) {
        particles.splice(i, 1);
      }
    }

    // 智能休眠引擎：若无粒子且鼠标光晕基本已跟上，自动休眠节省电量
    const ambDist = Math.hypot(mouseX - ambX, mouseY - ambY);
    if (particles.length === 0 && ambDist < 1) {
      isLoopRunning = false;
      animationFrameId = null;
      return;
    }

    animationFrameId = requestAnimationFrame(renderLoop);
  }

  window.addEventListener('mousemove', onMouseMove, { passive: true });
  window.addEventListener('pointerdown', onPointerDown, { passive: true });

  // 启动初始渲染
  startLoopIfNeeded();

  return () => {
    window.removeEventListener('resize', resizeCanvas);
    window.removeEventListener('mousemove', onMouseMove);
    window.removeEventListener('pointerdown', onPointerDown);
    if (animationFrameId) {
      cancelAnimationFrame(animationFrameId);
    }
    particles.length = 0;
    ctx.clearRect(0, 0, width, height);
  };
}
