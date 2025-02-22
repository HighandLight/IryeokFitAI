function showLoadingModal() {
    document.getElementById('loadingModal').style.display = 'flex';
    setTimeout(() => {
        document.getElementById('loadingModal').style.display = 'none';
        const jobUrl = document.getElementById('jobUrl').value.trim();
        if (jobUrl === "") {
            showErrorModal();
        } else {
            showJobModal();
        }
    }, 2000);
}
function showJobModal() {
    document.getElementById('jobModal').style.display = 'flex';
}
function showErrorModal() {
    document.getElementById('errorModal').style.display = 'flex';
}
function closeModal() {
    document.getElementById('jobModal').style.display = 'none';
}
function closeErrorModal() {
    document.getElementById('errorModal').style.display = 'none';
}
function proceedToFeedback() {
    window.location.href = 'resumeFeedback.html';
}


document.getElementById("analyzeButton").addEventListener("click", async () => {
    const jobUrl = document.getElementById("jobUrl").value.trim();
    if (!jobUrl) {
        alert("채용 공고 URL을 입력해주세요.");
        return;
    }

    showLoadingModal();
    try {
        const response = await fetch(lambdaFunctionUrl, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ jobUrl: jobUrl })
        });

        const responseText = await response.text();
        let result;
        try {
            result = JSON.parse(responseText);
            console.log("Parsed result:", result);
        } catch (error) {
            console.error("JSON 파싱 오류:", error);
        }
        if (result ) {
            document.getElementById("jobTitle").innerHTML = result.title ? `<strong>${result.title}</strong>` : "<strong>제목 없음</strong>";
            // document.getElementById("jobResponsibilities").innerText = result.responsibilities ? "주요 업무: " + result.responsibilities : "";
            closeLoadingModal();
            openJobModal();
        } else {
            closeLoadingModal();
            openErrorModal();
        }
    } catch (error) {
        console.error("Lambda 호출 오류:", error);
        closeLoadingModal();
        openErrorModal();
    }
});
