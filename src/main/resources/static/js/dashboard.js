/* ============================================================
   LedgerFlow — dashboard.js
   Dark mode toggle, charts, utilities
   ============================================================ */

// ── Dark Mode ──────────────────────────────────────────────

const THEME_KEY = 'lf-theme';

function applyTheme(theme) {
  document.documentElement.setAttribute('data-theme', theme);
  localStorage.setItem(THEME_KEY, theme);
  const icon = document.getElementById('themeIcon');
  if (icon) {
    icon.className = theme === 'dark' ? 'fas fa-sun' : 'fas fa-moon';
  }
}

function initTheme() {
  const saved = localStorage.getItem(THEME_KEY);
  const preferred = window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  applyTheme(saved || preferred);
}

function toggleTheme() {
  const current = document.documentElement.getAttribute('data-theme');
  applyTheme(current === 'dark' ? 'light' : 'dark');
  // Re-render charts if on dashboard
  if (typeof renderCharts === 'function') {
    setTimeout(renderCharts, 50);
  }
}

// ── Chart Helpers ──────────────────────────────────────────

function getChartColors() {
  const isDark = document.documentElement.getAttribute('data-theme') === 'dark';
  return {
    text: isDark ? '#8b949e' : '#6b7280',
    grid: isDark ? '#21262d' : '#f0f2f6',
    palette: [
      '#3b82f6', '#10b981', '#f59e0b', '#ef4444',
      '#8b5cf6', '#06b6d4', '#ec4899', '#84cc16'
    ]
  };
}

function makePieChart(canvasId, labels, data, symbol) {
  const canvas = document.getElementById(canvasId);
  if (!canvas || !labels || labels.length === 0) return null;

  const colors = getChartColors();

  // Destroy existing instance
  const existing = Chart.getChart(canvas);
  if (existing) existing.destroy();

  return new Chart(canvas, {
    type: 'doughnut',
    data: {
      labels: labels,
      datasets: [{
        data: data,
        backgroundColor: colors.palette.map(c => c + 'cc'),
        borderColor: colors.palette,
        borderWidth: 2,
        hoverOffset: 6
      }]
    },
    options: {
      responsive: true,
      cutout: '62%',
      plugins: {
        legend: {
          position: 'bottom',
          labels: {
            color: colors.text,
            font: { family: 'DM Sans', size: 12, weight: '500' },
            padding: 14,
            usePointStyle: true,
            pointStyleWidth: 8
          }
        },
        tooltip: {
          backgroundColor: document.documentElement.getAttribute('data-theme') === 'dark'
            ? '#1c2333' : '#ffffff',
          titleColor: document.documentElement.getAttribute('data-theme') === 'dark'
            ? '#e6edf3' : '#0f1923',
          bodyColor: document.documentElement.getAttribute('data-theme') === 'dark'
            ? '#8b949e' : '#4a5568',
          borderColor: document.documentElement.getAttribute('data-theme') === 'dark'
            ? '#30363d' : '#e2e8f0',
          borderWidth: 1,
          padding: 12,
          cornerRadius: 10,
          callbacks: {
            label: function(ctx) {
              const val = ctx.parsed;
              const total = ctx.dataset.data.reduce((a, b) => a + b, 0);
              const pct = ((val / total) * 100).toFixed(1);
              return ` ${ctx.label}: ${symbol}${val.toLocaleString('en', {minimumFractionDigits:2, maximumFractionDigits:2})} (${pct}%)`;
            }
          }
        }
      }
    }
  });
}

// ── Form Validation ────────────────────────────────────────

function validateExpenseForm() {
  const amount = document.getElementById('amount');
  if (amount && parseFloat(amount.value) <= 0) {
    showToast('Please enter an amount greater than 0', 'error');
    return false;
  }
  return true;
}

function confirmDelete(itemLabel) {
  return confirm(`Delete this ${itemLabel || 'record'}? This cannot be undone.`);
}

// ── Toast notifications ────────────────────────────────────

function showToast(message, type = 'info') {
  const existing = document.getElementById('lf-toast');
  if (existing) existing.remove();

  const toast = document.createElement('div');
  toast.id = 'lf-toast';
  const colors = { error: '#dc2626', success: '#059669', info: '#2563eb' };
  toast.style.cssText = `
    position: fixed; bottom: 24px; right: 24px; z-index: 9999;
    background: var(--bg-surface); color: var(--text-primary);
    border: 1px solid var(--border); border-left: 4px solid ${colors[type] || colors.info};
    border-radius: 10px; padding: 14px 18px; font-size: 0.875rem;
    box-shadow: var(--shadow-lg); max-width: 320px;
    animation: fadeUp 0.3s ease both; font-family: 'DM Sans', sans-serif;
  `;
  toast.textContent = message;
  document.body.appendChild(toast);
  setTimeout(() => { toast.style.opacity = '0'; toast.style.transition = 'opacity 0.4s'; setTimeout(() => toast.remove(), 400); }, 3500);
}

// ── Auto-dismiss alerts ────────────────────────────────────

function initAlerts() {
  setTimeout(() => {
    document.querySelectorAll('.alert.auto-dismiss').forEach(el => {
      el.style.transition = 'opacity 0.5s';
      el.style.opacity = '0';
      setTimeout(() => el.remove(), 500);
    });
  }, 4500);
}

// ── Number formatting helper ───────────────────────────────

function formatCurrency(val, symbol) {
  return symbol + parseFloat(val).toLocaleString('en', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

// ── Init ──────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', function () {
  initTheme();
  initAlerts();
});