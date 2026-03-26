// static/js/dashboard.js

// Initialize charts when document is ready
document.addEventListener('DOMContentLoaded', function() {
    initializeExpenseChart();
});

function initializeExpenseChart() {
    const ctx = document.getElementById('expenseChart');
    if (!ctx) return;
    
    // Get data from data attributes
    const categories = JSON.parse(ctx.dataset.categories || '[]');
    const amounts = JSON.parse(ctx.dataset.amounts || '[]');
    
    new Chart(ctx, {
        type: 'pie',
        data: {
            labels: categories,
            datasets: [{
                data: amounts,
                backgroundColor: [
                    '#FF6384',
                    '#36A2EB',
                    '#FFCE56',
                    '#4BC0C0',
                    '#9966FF',
                    '#FF9F40'
                ],
                borderWidth: 2,
                borderColor: '#fff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        font: {
                            size: 12
                        }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = context.parsed;
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = ((value / total) * 100).toFixed(1);
                            return `${label}: $${value.toFixed(2)} (${percentage}%)`;
                        }
                    }
                }
            }
        }
    });
}

// Form validation
function validateExpenseForm() {
    const amount = document.getElementById('amount');
    if (amount && parseFloat(amount.value) <= 0) {
        alert('Please enter a valid amount greater than 0');
        return false;
    }
    return true;
}

// Delete confirmation
function confirmDelete(expenseId) {
    if (confirm('Are you sure you want to delete this expense? This action cannot be undone.')) {
        window.location.href = `/expenses/delete/${expenseId}`;
    }
}

// Auto-hide alerts after 5 seconds
setTimeout(function() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function(alert) {
        alert.style.transition = 'opacity 0.5s ease';
        alert.style.opacity = '0';
        setTimeout(function() {
            alert.remove();
        }, 500);
    });
}, 5000);