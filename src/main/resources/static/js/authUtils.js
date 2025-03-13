function isTokenExpired(token) {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const exp = payload.exp * 1000;
    return Date.now() > exp;
}

async function fetchWithAuth(url, options = {}) {
    const token = localStorage.getItem("token");
    if (!token) {
        window.location.href = "/signin";
        return;
    }

    options.headers = {
        ...options.headers,
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
    };

    const response = await fetch(url, options);

    if (response.status === 401) {
        alert("세션이 만료되었습니다. 다시 로그인해주세요.");
        localStorage.removeItem("token");
        window.location.href = "/signin";
    }

    return response;
}


window.onload = checkTokenExpiration;
