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
            window.jobPostingData = result;

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
    const proceedButton = document.getElementById("proceedButton");

    proceedButton.disabled = true;
    proceedButton.innerHTML = `
        <div class="loading-bar"></div> 이력서 검토중...
    `;

    setTimeout(() => {
        proceedButton.innerHTML = `
            <div class="loading-bar"></div> 피드백 생성중...
        `;
    }, 3000);

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
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("token")}`
            },
            body: formData,
        });
        uploadedResume = await uploadResponse.json();
    } catch (error) {
        console.error("이력서 업로드 실패:", error);
        proceedButton.innerHTML = "✅ 네, 진행할게요";
        proceedButton.disabled = false;
        return;
    }

    const jobPostingData = window.jobPostingData || {};

    const reportData = {
        userId: userId,
        resumeId: uploadedResume.id,
        title: jobPostingData.title || "새로운 공고",
        jobPostingUrl: jobUrl,
        responsibilities: jobPostingData.responsibilities,
        requirements: jobPostingData.requirements,
        preferred: jobPostingData.preferred,
        skills: jobPostingData.skills
    };

    let report;
    try {
        const reportResponse = await fetchWithAuth("/reports", {
            method: "POST",
            body: JSON.stringify(reportData),
        });

        report = await reportResponse.json();
    } catch (error) {
        console.error("리포트 생성 실패:", error);
        proceedButton.innerHTML = "✅ 네, 진행할게요";
        proceedButton.disabled = false;
        return;
    }

    try {
        const feedbackResponse = await fetchWithAuth(`/feedbacks/generate/${report.id}`, {
            method: "POST",
        });

        if (!feedbackResponse.ok) {
            console.error("피드백 생성 실패");
            proceedButton.innerHTML = "✅ 네, 진행할게요";
            proceedButton.disabled = false;
            return;
        }
    } catch (error) {
        console.error("피드백 생성 요청 오류:", error);
        proceedButton.innerHTML = "✅ 네, 진행할게요";
        proceedButton.disabled = false;
        return;
    }

    window.location.href = `report?reportId=${report.id}`;
}

const openBtn = document.getElementById("openResumeModal");
const closeBtn = document.getElementById("modalCloseButton");
const modal = document.getElementById("newFeedbackModal");
const blurWrapper = document.getElementById("blurWrapper");

openBtn.addEventListener("click", () => {
    modal.classList.remove("hidden");
    blurWrapper.classList.add("modal-active");
});

closeBtn.addEventListener("click", () => {
    modal.classList.add("hidden");
    blurWrapper.classList.remove("modal-active");
});

document.addEventListener("DOMContentLoaded", () => {
    try {
        fetchReports();
    } catch (error) {
        console.error("리포트 목록을 불러오는 도중 에러가 발생했습니다.", error);
    }

    const reportId = getReportIdFromURL();
    if (reportId) {
        fetchResumeImage(reportId);
        fetchFeedbacks(reportId);
    } else {
        console.error("Report ID가 URL에 없습니다.");
    }
});

function getReportIdFromURL() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get("reportId");
}

document.addEventListener("DOMContentLoaded", () => {
    const toggleBtn = document.querySelector(".toggle-sidebar-btn");
    const sidebar = document.querySelector(".sidebar");

    toggleBtn.addEventListener("click", () => {
        sidebar.classList.toggle("active");
    });
});


function getUserIdFromToken() {
    const token = localStorage.getItem("token");

    if (!token || isTokenExpired(token)) {
        alert("세션이 만료되었습니다. 다시 로그인해주세요.");
        localStorage.removeItem("token");
        localStorage.removeItem("userId");
        localStorage.removeItem("username");
        window.location.href = "/signin";
        return null;
    }

    return localStorage.getItem("userId");
}

async function fetchReports() {
    const userId = getUserIdFromToken();
    if (!userId) return;

    try {
        const response = await fetchWithAuth(`/reports/user/${userId}`);
        if (!response.ok) throw new Error("리스트 로드 실패");

        const reports = await response.json();
        renderReportList(reports);
    } catch (error) {
        console.error("리포트 목록 로드 에러:", error);
    }
}

function renderReportList(reports) {
    const reportListContainer = document.getElementById("reportList");
    reportListContainer.innerHTML = "";

    reports.forEach((report) => {
        const reportItem = document.createElement("div");
        reportItem.classList.add("report-item");
        reportItem.textContent = report.title;

        reportItem.addEventListener("click", () => {
            document.querySelectorAll('.report-item').forEach(item => {
                item.classList.remove('selected');
            });

            reportItem.classList.add("selected");

            window.location.href = `/report?reportId=${report.id}`;
        });

        reportListContainer.appendChild(reportItem);
    });
}

async function fetchResumeImage(reportId) {
    try {
        const response = await fetchWithAuth(`/resumes/images/${reportId}`);

        if (response.status === 404) {
            console.error("해당 이력서를 찾을 수 없습니다.");
            return;
        }

        if (!response.ok) throw new Error("이력서 이미지를 불러오는 데 실패했습니다.");

        const data = await response.json();
        const imageUrl = data.convertedImageUrl;

        if (imageUrl) {
            displayResumeImage(imageUrl);
        } else {
            console.error("이미지 URL이 존재하지 않습니다.");
        }
    } catch (error) {
        console.error("이력서 이미지 로딩 에러:", error);
    }
}

function displayResumeImage(imageUrl) {
    const resumeImage = document.getElementById("resumeImage");
    resumeImage.src = imageUrl;
    resumeImage.alt = "변환된 이력서 이미지";
}

async function fetchFeedbacks(reportId) {
    try {
        const response = await fetchWithAuth(`/feedbacks/${reportId}`);
        if (!response.ok) throw new Error("피드백 리스트 로드 실패");

        const feedbacks = await response.json();
        renderFeedbackList(feedbacks);
    } catch (error) {
        console.error("피드백 로딩 에러:", error);
    }
}

function renderFeedbackList(feedbacks) {
    const feedbackListContainer = document.getElementById("feedbackSection");
    feedbackListContainer.innerHTML = "";

    feedbacks.forEach((feedback) => {
        const feedbackItem = document.createElement("div");
        feedbackItem.classList.add("feedback-item", getPriorityClass(feedback.priority));

        feedbackItem.innerHTML = `
            <strong>${getPriorityEmoji(feedback.priority)} [${feedback.priority}] ${feedback.suggestionText}</strong>
            <button class="toggle-detail">자세히 보기 ▾</button>
            <div class="feedback-detail hidden">${feedback.detailText}</div>
        `;

        const toggleButton = feedbackItem.querySelector(".toggle-detail");
        const detailText = feedbackItem.querySelector(".feedback-detail");

        toggleButton.addEventListener("click", () => {
            const isHidden = detailText.classList.toggle("hidden");
            toggleButton.textContent = isHidden ? "자세히 보기 ▾" : "간략히 보기 ▴";
        });

        feedbackListContainer.appendChild(feedbackItem);
    });
}

function getPriorityClass(priority) {
    switch (priority.toLowerCase()) {
        case "high":
            return "high";
        case "medium":
            return "medium";
        case "low":
            return "low";
        default:
            return "";
    }
}

function getPriorityEmoji(priority) {
    switch (priority.toLowerCase()) {
        case "high":
            return "🔴";
        case "medium":
            return "🟡";
        case "low":
            return "🟢";
        default:
            return "⚪";
    }
}
