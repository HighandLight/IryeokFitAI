async function signUp() {
    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirm-password").value;
    const phone = document.getElementById("phone").value;

    if (password !== confirmPassword) {
        alert("비밀번호가 일치하지 않습니다.");
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

        console.log("ooooooooo", requestData)

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
