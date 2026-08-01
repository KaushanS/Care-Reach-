function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    if (sidebar) sidebar.classList.toggle('open');
}

function filterTable(tableId, filters) {
    const rows = document.querySelectorAll(`#${tableId} tbody tr`);
    let visibleCount = 0;

    rows.forEach((row) => {
        const visible = filters.every((filter) => {
            const select = document.getElementById(filter.selectId);
            if (!select || select.value === 'all') return true;
            return row.dataset[filter.dataKey] === select.value;
        });

        row.style.display = visible ? '' : 'none';
        if (visible) visibleCount += 1;
    });

    const empty = document.querySelector(`[data-empty-for="${tableId}"]`);
    if (empty) empty.style.display = visibleCount === 0 ? 'block' : 'none';
}

function selectVisit(button) {
    const row = button.closest('tr');
    if (!row) return;

    const fields = {
        visitPatient: row.dataset.patient,
        visitId: row.dataset.id,
        visitRisk: row.dataset.risk,
        visitLocation: row.dataset.location,
        visitCondition: row.dataset.condition,
        visitGn: row.dataset.gn,
        visitVolunteer: row.dataset.volunteer
    };

    Object.entries(fields).forEach(([id, value]) => {
        const el = document.getElementById(id);
        if (el) el.textContent = value || '-';
    });

    document.querySelectorAll('[data-visit-row]').forEach((visitRow) => {
        visitRow.style.background = '';
    });
    row.style.background = '#f0fdfa';

    const formPatient = document.getElementById('reportPatientName');
    if (formPatient) formPatient.value = row.dataset.patient || '';
}

function showToast(message) {
    const toast = document.getElementById('pageToast');
    if (!toast) {
        alert(message);
        return;
    }

    toast.textContent = message;
    toast.style.display = 'inline-flex';
    window.setTimeout(() => {
        toast.style.display = 'none';
    }, 2600);
}

function handleDemoSubmit(event, message) {
    event.preventDefault();
    showToast(message);
}
