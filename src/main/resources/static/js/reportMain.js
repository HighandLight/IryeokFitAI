let lambdaFunctionUrl = "";

async function fetchLambdaUrl() {
    try {
        const response = await fetch("/config/lambda-url");
        const data = await response.json();
        if (data.lambdaFunctionUrl) lambdaFunctionUrl = data.lambdaFunctionUrl;
        else throw new Error("Lambda URL이 비어 있습니다.");
    } catch (error) {
        console.error("lambda URL 불러오기 실패:", error);
    }
}

document.addEventListener("DOMContentLoaded", async () => {
    await fetchLambdaUrl();
    fetchReports();
    const reportId = getReportIdFromURL();
    if (reportId) {
        fetchResumeImage(reportId);
        fetchFeedbacks(reportId);
    }
    const toggleBtn = document.querySelector(".toggle-sidebar-btn");
    const sidebar = document.querySelector(".sidebar");
    toggleBtn?.addEventListener("click", () => sidebar.classList.toggle("active"));
});

function getReportIdFromURL() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get("reportId");
}

function getUserIdFromToken() {
    const token = localStorage.getItem("token");
    if (!token || isTokenExpired(token)) {
        alert("세션이 만료되었습니다. 다시 로그인해주세요.");
        localStorage.clear();
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
        const reports = await response.json();
        renderReportList(reports);
    } catch (error) {
        console.error("리포트 목록 에러:", error);
    }
}

function renderReportList(reports) {
    const container = document.getElementById("reportList");
    container.innerHTML = "";

    reports.forEach((report) => { // 함수로 빼두고, 이력서 제출 시 받아오기?
        // 호출 시
        // reports 관리 -> 팝업에서 REPORTs 추가
        // 데이터 Ui랜더링 분리

        const item = document.createElement("div");
        item.classList.add("report-item");
        item.textContent = report.title;

        if (report.status === "WAITING") item.classList.add("loading");
        else if (report.status === "COMPLETED") item.classList.add("completed");

        item.addEventListener("click", () => {
            document.querySelectorAll('.report-item').forEach(i => i.classList.remove("selected"));
            item.classList.remove("completed", "loading");
            item.classList.add("selected");
            window.location.href = `/report?reportId=${report.id}`;
        });

        container.appendChild(item);

        if (report.status === "WAITING") pollReportStatus(report.id, item);
    });
}

async function pollReportStatus(reportId, itemEl) {
    let attempts = 0;
    const max = 60;
    const interval = 3000;

    const poll = async () => {
        try {
            const res = await fetchWithAuth(`/reports/${reportId}`);
            const report = await res.json();
            if (report.status === "COMPLETED") {
                itemEl.classList.remove("loading");
                itemEl.classList.add("completed");
            } else if (attempts++ < max) {
                setTimeout(poll, interval);
            }
        } catch (e) {
            console.error("폴링 실패:", e);
        }
    };
    poll();
}

// 피드백 진행 처리
async function proceedToFeedback() {
    const proceedButton = document.getElementById("proceedButton");
    proceedButton.disabled = true;
    proceedButton.innerHTML = `
        <div class="loading-bar"></div> 피드백 생성중...
    `;

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
                Authorization: `Bearer ${localStorage.getItem("token")}`
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

    //  모든 모달 닫기
    closeModal();
    document.getElementById("newFeedbackModal").classList.add("hidden");
    document.getElementById("blurWrapper").classList.remove("modal-active");

    //  리스트에 즉시 반영
    reportCache.unshift(report);
    renderReportList(reportCache);

    //  방금 추가된 첫 번째 item에 polling 시작
    const newItem = document.querySelector(".report-item");
    if (newItem) {
        pollReportStatus(report.id, newItem);
    }
}


// 이력서 이미지 및 피드백
async function fetchResumeImage(reportId) {
    try {
        const res = await fetchWithAuth(`/resumes/images/${reportId}`);
        const data = await res.json();
        displayResumeImage(data.convertedImageUrl);
    } catch (e) {
        console.error("이력서 이미지 오류:", e);
    }
}

function displayResumeImage(url) {
    const img = document.getElementById("resumeImage");
    img.src = url;
    img.alt = "변환된 이력서";
}

async function fetchFeedbacks(reportId) {
    try {
        const res = await fetchWithAuth(`/feedbacks/${reportId}`);
        const feedbacks = await res.json();
        renderFeedbackList(feedbacks);
    } catch (e) {
        console.error("피드백 로딩 에러:", e);
    }
}

function renderFeedbackList(feedbacks) {
    const container = document.getElementById("feedbackSection");
    container.innerHTML = "";

    feedbacks.forEach((f) => {
        const item = document.createElement("div");
        item.classList.add("feedback-item", getPriorityClass(f.priority));
        item.innerHTML = `
            <strong>${getPriorityEmoji(f.priority)} [${f.priority}] ${f.suggestionText}</strong>
            <button class="toggle-detail">자세히 보기 ▾</button>
            <div class="feedback-detail hidden">${f.detailText}</div>
        `;
        item.querySelector(".toggle-detail").addEventListener("click", () => {
            const detail = item.querySelector(".feedback-detail");
            const hidden = detail.classList.toggle("hidden");
            item.querySelector(".toggle-detail").textContent = hidden ? "자세히 보기 ▾" : "간략히 보기 ▴";
        });
        container.appendChild(item);
    });
}

function getPriorityClass(priority) {
    switch (priority.toLowerCase()) {
        case "high": return "high";
        case "medium": return "medium";
        case "low": return "low";
        default: return "";
    }
}

function getPriorityEmoji(priority) {
    switch (priority.toLowerCase()) {
        case "high": return "🔴";
        case "medium": return "🟡";
        case "low": return "🟢";
        default: return "⚪";
    }
}

// 모달 관련
function showLoadingModal() { document.getElementById('loadingModal').style.display = 'flex'; }
function closeLoadingModal() { document.getElementById('loadingModal').style.display = 'none'; }
function showJobModal() { document.getElementById('jobModal').style.display = 'flex'; }
function closeModal() { document.getElementById('jobModal').style.display = 'none'; }
function showErrorModal() { document.getElementById('errorModal').style.display = 'flex'; }
function closeErrorModal() { document.getElementById('errorModal').style.display = 'none'; }
function showFileErrorModal() { document.getElementById('fileErrorModal').style.display = 'flex'; }
function closeFileErrorModal() { document.getElementById('fileErrorModal').style.display = 'none'; }
function showUrlErrorModal() { document.getElementById('urlErrorModal').style.display = 'flex'; }
function closeUrlErrorModal() { document.getElementById('urlErrorModal').style.display = 'none'; }

document.getElementById("analyzeButton").addEventListener("click", async () => {
    const jobUrl = document.getElementById("jobUrl").value.trim();
    const resumeInput = document.getElementById("resumeUpload");
    if (!jobUrl) return showUrlErrorModal();
    if (!resumeInput.files || resumeInput.files.length === 0) return showFileErrorModal();
    if (resumeInput.files[0].type !== "application/pdf") return alert("PDF 파일만 지원됩니다.");

    showLoadingModal();
    try {
        const response = await fetch(lambdaFunctionUrl, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ jobUrl })
        });
        const result = await response.json();
        window.jobPostingData = result;
        document.getElementById("jobTitle").innerHTML = result.title ? `<strong>${result.title}</strong>` : "<strong>제목 없음</strong>";
        closeLoadingModal();
        showJobModal();
    } catch (error) {
        console.error("공고 분석 실패:", error);
        closeLoadingModal();
        showErrorModal();
    }
});

document.getElementById("proceedButton").addEventListener("click", proceedToFeedback);
document.getElementById("openResumeModal").addEventListener("click", () => {
    document.getElementById("newFeedbackModal").classList.remove("hidden");
    document.getElementById("blurWrapper").classList.add("modal-active");
});
document.getElementById("modalCloseButton").addEventListener("click", () => {
    document.getElementById("newFeedbackModal").classList.add("hidden");
    document.getElementById("blurWrapper").classList.remove("modal-active");
});
