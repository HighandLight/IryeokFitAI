let lambdaFunctionUrl = "";
let reportCache = []; // 전역 캐시 추가

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
    await fetchReports(); // 초기 리포트 목록 로딩
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
        reportCache = reports; //  캐시에 저장
        renderReportList(reportCache);
    } catch (error) {
        console.error("리포트 목록 에러:", error);
    }
}

function renderReportList(reports) {
    const container = document.getElementById("reportList");
    container.innerHTML = "";

    reports.forEach((report) => {
        const item = document.createElement("div");
        item.classList.add("report-item");
        item.textContent = report.title;

        if (report.status === "WAITING") item.classList.add("loading");
        else if (report.status === "COMPLETED") item.classList.add("completed");

        item.addEventListener("click", () => {
            document.querySelectorAll('.report-item').forEach(i => i.classList.remove("selected"));
            item.classList.remove("completed", "loading");
            item.classList.add("selected");
            onReportClick(report.id); // 읽음 처리 - status COMPLETED -> SAVED 로 바꾼 후 배경 색 제거
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
    closeModal();
    document.getElementById("newFeedbackModal").classList.add("hidden");
    document.getElementById("blurWrapper").classList.remove("modal-active");

    const proceedButton = document.getElementById("proceedButton");

    const userId = localStorage.getItem("userId");
    const jobUrl = document.getElementById("jobUrl").value.trim();
    const resumeInput = document.getElementById("resumeUpload");
    const resumeFile = resumeInput.files[0];
    const jobPostingData = window.jobPostingData || {};

    // report 선 생성 (resume 없이 - polling 구현 위함)
    let report;
    try {
        const res = await fetchWithAuth("/reports", {
            method: "POST",
            body: JSON.stringify({
                userId: userId,
                title: "공고를 불러오는 중입니다..", // (TTT-1) TODO : 어떻게 바꾸맂?적절한 메세지?
                jobPostingUrl: jobUrl
            }),
        });
        report = await res.json();
        reportCache.unshift(report);
        renderReportList(reportCache);
    } catch (e) {
        console.error("report 선 생성 실패", e);
        return;
    }

    //  resume 업로드
    let uploadedResume;
    try {
        const formData = new FormData();
        formData.append("userId", userId);
        formData.append("file", resumeFile);

        const res = await fetch("/resumes/upload", {
            method: "POST",
            headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
            body: formData,
        });

        uploadedResume = await res.json();
    } catch (e) {
        console.error("이력서 업로드 실패", e);
        return;
    }

    // report 정보 PATCH로 업데이트
    try {
        const updateData = {
            resumeId: uploadedResume.id,
            title: jobPostingData.title || "제목 없음",
            jobPostingUrl: jobUrl,
            responsibilities: jobPostingData.responsibilities,
            requirements: jobPostingData.requirements,
            preferred: jobPostingData.preferred,
            skills: jobPostingData.skills
        };

        await fetchWithAuth(`/reports/${report.id}`, {
            method: "PATCH",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(updateData)
        });

        //캐시 내 report 내용 갱신 및 다시 렌더링 -> ((TTT-1) 문구 변경하도록)
        Object.assign(report, updateData);
        renderReportList(reportCache);
    } catch (e) {
        console.error("report 업데이트 실패", e);
        return;
    }


    // 피드백 생성
    try {
        const feedbackRes = await fetchWithAuth(`/feedbacks/generate/${report.id}`, {
            method: "POST"
        });

        if (!feedbackRes.ok) throw new Error("피드백 실패");
    } catch (e) {
        console.error("피드백 생성 실패", e);
        return;
    }

    // 상태 polling 및 시각적 반영
    const reportItems = document.querySelectorAll(".report-item");
    const newItem = reportItems[0];
    if (newItem) {
        newItem.classList.add("selected");
        pollReportStatus(report.id, newItem);
    }
}

function onReportClick(reportId) {
    fetch(`/reports/${reportId}/mark-as-read`, {
        method: "GET",
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
    }).then(() => {
        window.location.href = `/report.html?reportId=${reportId}`;
    });
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

function resetModalState() { // 새 이력서 첨삭 시, 기존 모달 내용 초기화
    // 입력값 초기화
    document.getElementById("jobUrl").value = "";
    document.getElementById("resumeUpload").value = "";
    // 전역 변수 초기화
    window.jobPostingData = null;
    // jobModal 내용 초기화
    document.getElementById("jobTitle").innerHTML = "";
    document.getElementById("jobResponsibilities").innerHTML = "";
    document.getElementById("jobSkills").innerHTML = "";
    document.getElementById("jobRequirements").innerHTML = "";
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
    resetModalState();  // IRYEOKFIT-022 관련, 기존 모달 초기화
    document.getElementById("newFeedbackModal").classList.remove("hidden");
    document.getElementById("blurWrapper").classList.add("modal-active");
});
document.getElementById("modalCloseButton").addEventListener("click", () => {
    document.getElementById("newFeedbackModal").classList.add("hidden");
    document.getElementById("blurWrapper").classList.remove("modal-active");
});
