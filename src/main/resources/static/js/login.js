async function login() {
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        const result = await response.json();
        if (response.ok) {
            localStorage.setItem('token', result.token); // 일단은 local storage에 저장.
            localStorage.setItem('username', result.username);
            localStorage.setItem('userId', result.userId);

            const reportResponse = await fetch(`/reports/user/${result.userId}`, {
                headers: { 'Authorization': `Bearer ${result.token}` }
            });

            if (!reportResponse.ok) {
                throw new Error(`Error fetching reports: ${reportResponse.status}`);
            }

            const reports = await reportResponse.json();
            console.log(reports);

            if (reports.length > 0) {
                window.location.href = 'report';
            } else {
                window.location.href = 'index';
            }
        } else {
            alert(result.error);
        }
    } catch (error) {
        console.error('Login error:', error);
        alert('에러 발생..');
    }
}