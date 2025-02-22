function showLoadingModal() {
    document.getElementById('loadingModal').style.display = 'flex';
}

function closeLoadingModal() {
    document.getElementById('loadingModal').style.display = 'none';
}

function showJobModal() {
    document.getElementById('jobModal').style.display = 'flex';
}

function showErrorModal() {
    document.getElementById('errorModal').style.display = 'flex';
}

function showUrlErrorModal() {
    document.getElementById('urlErrorModal').style.display = 'flex';
}

function showFileErrorModal() {
    document.getElementById('fileErrorModal').style.display = 'flex';
}

function closeFileErrorModal() {
    document.getElementById('fileErrorModal').style.display = 'none';
}

function closeUrlErrorModal() {
    document.getElementById('urlErrorModal').style.display = 'none';
}

function closeModal() {
    document.getElementById('jobModal').style.display = 'none';
}

function closeErrorModal() {
    document.getElementById('errorModal').style.display = 'none';
}

let lambdaFunctionUrl = "";

async function fetchLambdaUrl() {
    try {
        const response = await fetch("/config/lambda-url");
        const data = await response.json();
        if (data.lambdaFunctionUrl) {
            lambdaFunctionUrl = data.lambdaFunctionUrl;
        } else {
            throw new Error("Lambda URL이 비어 있습니다.");
        }
    } catch(error) {
        console.error("lambda URL 불러오기 실패:", error)
    }
}

document.addEventListener("DOMContentLoaded", fetchLambdaUrl);


document.getElementById("analyzeButton").addEventListener("click", async () => {
    const jobUrl = document.getElementById("jobUrl").value.trim();
    if (!jobUrl) {
        showUrlErrorModal();
        return;
    }

    const resumeInput = document.getElementById("resumeUpload");
    if (!resumeInput.files || resumeInput.files.length === 0) {
        showFileErrorModal();
        return;
    }
    const resumeFile = resumeInput.files[0];
    if (resumeFile.type !== "application/pdf") {
        alert("PDF 파일만 지원됩니다.");
        return;
    }

    showLoadingModal();
    try {
        const response = await fetch(lambdaFunctionUrl, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
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

        if (result) {
            document.getElementById("jobTitle").innerHTML = result.title ? `<strong>${result.title}</strong>` : "<strong>제목 없음</strong>";

            closeLoadingModal();
            showJobModal();
        } else {
            closeLoadingModal();
            showErrorModal();
        }
    } catch (error) {
        console.error("Lambda 호출 오류:", error);
        closeLoadingModal();
        showErrorModal();
    }
});

async function proceedToFeedback() {
    const jobUrl = document.getElementById("jobUrl").value.trim();
    const resumeInput = document.getElementById("resumeUpload");
    const resumeFile = resumeInput.files[0];
    const userId = localStorage.getItem("userId");

    const formData = new FormData();
    formData.append("userId", userId);
    formData.append("file", resumeFile);

    let uploadedResume;
    try {
        const uploadResponse = await fetch("/resumes/upload", {
            method: "POST",
            body: formData,
        });

        uploadedResume = await uploadResponse.json();
    } catch (error) {
        console.error("이력서 업로드 실패:", error);
        return;
    }
    const jobTitle = document.getElementById("jobTitle").innerText || "새로운 공고";
    const reportData = {
        userId: userId,
        resumeId: uploadedResume.id,
        title: jobTitle,
        jobPostingUrl: jobUrl,
    };

    try {
        const reportResponse = await fetch("/reports", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(reportData),
        });

        const report = await reportResponse.json();
        window.location.href = `report.html?reportId=${report.id}`;
    } catch (error) {
        console.error("리포트 생성 실패:", error);
    }
}

