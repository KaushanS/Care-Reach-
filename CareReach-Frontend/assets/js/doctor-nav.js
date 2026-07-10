(function () {
    const sidebar = document.getElementById('sidebar') || document.querySelector('.sidebar');
    if (!sidebar) return;

    const currentPage = window.location.pathname.split('/').pop() || 'dashboard.html';

    const navGroups = [
        {
            title: 'Main',
            links: [
                { href: 'dashboard.html', icon: 'bi-grid-1x2-fill', label: 'Dashboard' }
            ]
        },
        {
            title: 'Clinical Care',
            links: [
                { href: 'appointments.html', icon: 'bi-calendar-event', label: 'Appointments' },
                { href: 'patients.html', icon: 'bi-person-lines-fill', label: 'Patients' },
                { href: 'home-visits.html', icon: 'bi-house-heart', label: 'Home Visits' },
                { href: 'prescriptions.html', icon: 'bi-prescription2', label: 'Treatments' }
            ]
        },
        {
            title: 'Management',
            links: [
                { href: 'reports.html', icon: 'bi-file-earmark-text', label: 'Reports' },
                { href: 'profile.html', icon: 'bi-person-badge', label: 'Profile' }
            ]
        }
    ];

    const navHtml = navGroups.map((group) => {
        const links = group.links.map((link) => {
            const active = currentPage === link.href ? ' active' : '';
            return `
                <a href="${link.href}" class="menu-item${active}">
                    <i class="bi ${link.icon}"></i> ${link.label}
                </a>
            `;
        }).join('');

        return `
            <div class="menu-title">${group.title}</div>
            ${links}
        `;
    }).join('');

    sidebar.innerHTML = `
        <div class="brand">
            <div class="brand-icon"><i class="bi bi-heart-pulse-fill"></i></div>
            CARE<span>REACH</span>
        </div>
        <nav class="sidebar-menu">
            ${navHtml}
        </nav>
    `;
})();
