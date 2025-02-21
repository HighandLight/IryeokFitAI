document.addEventListener("DOMContentLoaded", () => {
    try {
        fetchReports();
    } catch (error) {
        console.error("리포트 목록을 불러오는 도중 에러가 발생했습니다.", error);
    }
    const reportId = getReportIdFromURL();

    if (reportId) {
        fetchResumeImage(reportId);
    } else {
        console.error("Report ID가 URL에 없습니다.");
    }
});

function getReportIdFromURL() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get("reportId");
}

function getUserIdFromToken() {
    const token = localStorage.getItem("token");
    if (!token) {
        alert("로그인이 필요합니다.");
        window.location.href = "/signIn";
        return null;
    }
    const userId = localStorage.getItem("userId")

    return userId;
}



async function fetchReports() {
    const userId = getUserIdFromToken();
    if (!userId) return;

    try {
        const response = await fetch(`/reports/user/${userId}`, {
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("token")}`
            }
        });

        if (response.status === 401) {
            alert("세션이 만료되었습니다. 다시 로그인해주세요.");
            localStorage.removeItem("token");
            window.location.href = "/signIn";
            return;
        }

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

            window.location.href = `/report.html?reportId=${report.id}`;
        });

        reportListContainer.appendChild(reportItem);
    });

}
async function fetchResumeImage(reportId) {
    try {
        const response = await fetch(`/resumes/images/${reportId}`, {
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("token")}`
            }
        });

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