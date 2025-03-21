async function signUp() {
    const name = document.getElementById("name").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();
    const confirmPassword = document.getElementById("confirm-password").value.trim();
    const phone = document.getElementById("phone").value.trim();

    let missingFields = [];

    if (!name) missingFields.push("이름");
    if (!email) missingFields.push("이메일");
    if (!password) missingFields.push("비밀번호");
    if (!confirmPassword) missingFields.push("비밀번호 확인");
    if (!phone) missingFields.push("전화번호");

    if (missingFields.length > 0) {
        alert(`${missingFields.join(", ")} 항목을 입력해주세요!`);
        return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        alert("올바른 이메일 형식을 입력해주세요!");
        return;
    }

    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    if (!passwordRegex.test(password)) {
        alert("비밀번호는 최소 8자 이상이며, 영문, 숫자, 특수문자를 포함해야 합니다!");
        return;
    }

    if (password !== confirmPassword) {
        alert("비밀번호가 일치하지 않습니다.");
        return;
    }

    const phoneRegex = /^01\d{9}$/;
    if (!phoneRegex.test(phone)) {
        alert("올바른 전화번호를 입력해주세요!");
        return;
    }

    const requestData = {
        name: name,
        email: email,
        password: password,
        phoneNumber: phone
    };

    try {
        const response = await fetch("/users", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestData)
        });

        if (response.ok) {
            alert("회원가입이 완료되었습니다!");
            window.location.href = "/signin";
        } else {
            const errorData = await response.json();
            alert(errorData.message || "회원가입에 실패했습니다.");
        }
    } catch (error) {
        console.error("회원가입 오류:", error);
        alert("회원가입 중 오류가 발생했습니다.");
    }
}
