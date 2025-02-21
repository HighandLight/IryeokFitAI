function isTokenExpired(token) {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const exp = payload.exp * 1000;
    return Date.now() > exp;
}

function checkTokenExpiration() {
    const token = localStorage.getItem('token');
    if (token && isTokenExpired(token)) {
        alert('세션이 만료되었습니다. 다시 로그인해주세요.');
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        window.location.href = 'signIn.html';
    }
}

window.onload = checkTokenExpiration;
