let lambdaFunctionUrl = "";
let reportCache = []; // 전역 캐시 추가
let menuClickHandlerInitialized = false; // 메뉴 외부 클릭 초기화flag


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

document.addEventListener("DOMContentLoaded", async () => {await fetchReports(); // 초기 리포트 목록 로딩
    await fetchLambdaUrl();
    await fetchReports(); // 초기 리포트 목록 로딩
    const reportId = getReportIdFromURL();
    if (reportId) {
        fetchResumeImage(reportId);
        fetchFeedbacks(reportId);
    }

    const toggleBtn = document.getElementById("toggleSidebar");
    const sidebar = document.getElementById("sidebar");
    const overlay = document.getElementById("sidebarOverlay");

    if (toggleBtn && sidebar && overlay) {
        toggleBtn.addEventListener("click", () => {
            sidebar.classList.toggle("active");
            overlay.style.display = sidebar.classList.contains("active") ? "block" : "none";
        });

        // 오버레이 클릭 시 사이드바 닫기
        overlay.addEventListener("click", () => {
            sidebar.classList.remove("active");
            overlay.style.display = "none";
        });

        // 사이드바 외부 클릭 시 닫기
        document.addEventListener("click", (e) => {
            if (!sidebar.contains(e.target) && !toggleBtn.contains(e.target)) {
                sidebar.classList.remove("active");
                overlay.style.display = "none";
            }
        });
    }
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

        // 제목을 안전하게 처리하고 중복 제거
        const title = report.title || "제목 없음";
        const cleanTitle = title.replace(/\s+/g, ' ').trim(); // 연속된 공백 제거

        item.innerHTML = `
            <div class="report-item-content">
                <span class="report-status-icon"></span>
                <span class="report-title">${cleanTitle}</span>
            </div>
            <button class="report-menu-btn" data-report-id="${report.id}" type="button">
                <span class="menu-icon">&#8942;</span>
            </button>
            <div class="report-menu-dropdown hidden" data-report-id="${report.id}">
                <button class="menu-item edit-item" data-report-id="${report.id}" type="button">
                    <span class="menu-item-icon"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/></svg></span>
                    <span class="menu-item-text">제목 수정</span>
                </button>
                <button class="menu-item delete-item" data-report-id="${report.id}" type="button">
                    <span class="menu-item-icon"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 6h18M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2m3 0-1 14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2L4 6"/></svg></span>
                    <span class="menu-item-text">삭제</span>
                </button>
            </div>
        `;

        if (report.status === "WAITING") {
            item.classList.add("loading");
            item.style.cursor = "not-allowed";
            // WAITING 상태일 때 메뉴 버튼 숨기기
            const menuBtn = item.querySelector(".report-menu-btn");
            if (menuBtn) {
                menuBtn.style.display = "none";
            }
        } else if (report.status === "COMPLETED") {
            item.classList.add("completed");
        }

        // 메뉴 버튼과 드롭다운 참조
        const menuBtn = item.querySelector(".report-menu-btn");
        const menuDropdown = item.querySelector(".report-menu-dropdown");

        // 메뉴 버튼 클릭 이벤트
        if (menuBtn && menuDropdown) {
            menuBtn.addEventListener("click", (e) => {
                e.stopPropagation(); // report-item 클릭 이벤트 방지

                // 다른 메뉴 닫기
                document.querySelectorAll(".report-menu-dropdown").forEach(menu => {
                    if (menu !== menuDropdown && !menu.classList.contains("hidden")) {
                        menu.classList.add("hidden");
                        // 다른 항목의 menu-open 클래스 제거
                        const otherItem = menu.closest(".report-item");
                        if (otherItem) {
                            otherItem.classList.remove("menu-open");
                        }
                    }
                });

                // 현재 메뉴 토글
                const isHidden = menuDropdown.classList.toggle("hidden");
                if (isHidden) {
                    item.classList.remove("menu-open");
                } else {
                    item.classList.add("menu-open");
                }
            });

            // 메뉴 아이템 클릭 이벤트 (기능은 나중에 구현)
            const editBtn = item.querySelector(".edit-item");
            const deleteBtn = item.querySelector(".delete-item");

            if (editBtn) {
                editBtn.addEventListener("click", (e) => {
                    e.stopPropagation();
                    menuDropdown.classList.add("hidden");
                    item.classList.remove("menu-open");
                    // TODO: 제목 수정 기능 구현
                    console.log("제목 수정:", report.id);
                });
            }

            if (deleteBtn) {
                deleteBtn.addEventListener("click", (e) => {
                    e.stopPropagation();
                    menuDropdown.classList.add("hidden");
                    item.classList.remove("menu-open");
                    // TODO: 삭제 기능 구현
                    console.log("삭제:", report.id);
                });
            }
        }

        // report-item 클릭 이벤트
        item.addEventListener("click", (e) => {
            // 메뉴 관련 요소 클릭 시 무시
            if (e.target.closest(".report-menu-btn") || e.target.closest(".report-menu-dropdown")) {
                return;
            }

            // WAITING 상태인 경우 클릭을 막음
            if (report.status === "WAITING") {
                return;
            }

            document.querySelectorAll('.report-item').forEach(i => i.classList.remove("selected"));
            item.classList.remove("completed", "loading");
            item.classList.add("selected");
            onReportClick(report.id);
            window.location.href = `/report?reportId=${report.id}`;
        });

        container.appendChild(item);

        if (report.status === "WAITING") {
            connectWebSocket(report.id, item);
        }
    });

    // 외부 클릭 시 모든 메뉴 닫기 (한 번만 초기화)
    if (!menuClickHandlerInitialized) {
        document.addEventListener("click", (e) => {
            if (!e.target.closest(".report-item")) {
                document.querySelectorAll(".report-menu-dropdown").forEach(menu => {
                    menu.classList.add("hidden");
                    const item = menu.closest(".report-item");
                    if (item) {
                        item.classList.remove("menu-open");
                    }
                });
            }
        });
        menuClickHandlerInitialized = true;
    }
}

const pollingSet = new Set(); // 중복 방지를 위한 Set
async function longPollReportStatus(reportId, itemEl) {
    if (pollingSet.has(reportId)) return; // 중복 방지
    pollingSet.add(reportId);// 중복방지

    try {
        const response = await fetchWithAuth(`/reports/${reportId}/wait-complete`);
        const report = await response.json();
        if (report.status === "COMPLETED") {
            itemEl.classList.remove("loading");
            itemEl.classList.add("completed");
        } else {
            console.warn("잘못된 상태:", report.status);
        }
    } catch (e) {
        console.error("롱폴링 실패:", e);
        // 실패 시 몇 초 후 재시도
        setTimeout(() => longPollReportStatus(reportId, itemEl), 3000);
    }
}

// async function pollReportStatus(reportId, itemEl) {
//     let attempts = 0;
//     const max = 60;
//     const interval = 3000;
//
//     const poll = async () => {
//         try {
//             const res = await fetchWithAuth(`/reports/${reportId}`);
//             const report = await res.json();
//             if (report.status === "COMPLETED") {
//                 itemEl.classList.remove("loading");
//                 itemEl.classList.add("completed");
//             } else if (attempts++ < max) {
//                 setTimeout(poll, interval);
//             }
//         } catch (e) {
//             console.error("폴링 실패:", e);
//         }
//     };
//     poll();
// }

//IRYEOKFIT-024
const connectedReports = new Set();

function connectWebSocket(reportId, itemEl) {
    if (connectedReports.has(reportId)) return; // 이미 연결된 report는 스킵
    connectedReports.add(reportId);

    const socket = new SockJS("/ws/reports");
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        stompClient.subscribe(`/topic/reports/${reportId}`, (message) => {
            console.log("WebSocket published 메시지 수신:", message.body);
            const body = JSON.parse(message.body);
            if (body.status === "COMPLETED") {
                itemEl.classList.remove("loading");
                itemEl.classList.add("completed");
                itemEl.style.cursor = "pointer";
                itemEl.style.opacity = "1";
                itemEl.style.pointerEvents = "auto";

                // 캐시의 상태도 업데이트
                const reportIndex = reportCache.findIndex(r => r.id === reportId);
                if (reportIndex !== -1) {
                    reportCache[reportIndex].status = "COMPLETED";
                }
            }
        });
    });
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

    // report 선 생성 (resume 없이 - 동적 할당)
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
        report.status = "WAITING"; // 상태를 WAITING으로 설정
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
        report.status = "WAITING"; // 상태를 WAITING으로 유지
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

    // 상태 변경 및 시각적 반영
    const reportItems = document.querySelectorAll(".report-item");
    const newItem = reportItems[0];
    if (newItem) {
        newItem.classList.add("selected");
        newItem.classList.remove("loading");
        newItem.style.cursor = "pointer";
        newItem.style.opacity = "1";
        newItem.style.pointerEvents = "auto";

        // WebSocket 연결로 상태 모니터링
        connectWebSocket(report.id, newItem);
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

        // 텍스트 내용을 안전하게 처리
        const suggestionText = f.suggestionText || "";
        const detailText = f.detailText || "";

        item.innerHTML = `
            <div class="feedback-header">
                <strong><span class="priority-dot"></span>${f.priority.toUpperCase()}</strong>
                <div class="feedback-suggestion">${suggestionText}</div>
            </div>
            <button class="toggle-detail">자세히 보기 ▾</button>
            <div class="feedback-detail hidden">${detailText}</div>
        `;

        item.querySelector(".toggle-detail").addEventListener("click", () => {
            const detail = item.querySelector(".feedback-detail");
            const button = item.querySelector(".toggle-detail");
            const hidden = detail.classList.toggle("hidden");
            button.textContent = hidden ? "자세히 보기 ▾" : "간략히 보기 ▴";
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
