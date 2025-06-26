// modalHandler.js

// 모달 열기
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

const analyzeBtn = document.getElementById("modalAnalyzeButton");
const lambdaFunctionUrl = "/config/lambda-url";

analyzeBtn.addEventListener("click", async () => {
    const jobUrl = document.getElementById("modalJobUrl").value.trim();
    const resumeInput = document.getElementById("modalResumeUpload");

    if (!jobUrl) {
        alert("공고 URL을 입력해주세요");
        return;
    }

    if (!resumeInput.files || resumeInput.files.length === 0) {
        alert("이력서 파일을 업로드해주세요 (PDF)");
        return;
    }

    const resumeFile = resumeInput.files[0];
    if (resumeFile.type !== "application/pdf") {
        alert("PDF 파일만 지원됩니다.");
        return;
    }

    try {
        const lambdaRes = await fetch(lambdaFunctionUrl, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ jobUrl })
        });
        const parsed = await lambdaRes.json();
        if (!parsed.title) throw new Error("공고 분석 실패");

        const formData = new FormData();
        formData.append("userId", localStorage.getItem("userId"));
        formData.append("file", resumeFile);

        const uploadRes = await fetch("/resumes/upload", {
            method: "POST",
            headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`
            },
            body: formData
        });
        const uploaded = await uploadRes.json();

        const reportRes = await fetch("/reports", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${localStorage.getItem("token")}`
            },
            body: JSON.stringify({
                userId: localStorage.getItem("userId"),
                resumeId: uploaded.id,
                title: parsed.title,
                jobPostingUrl: jobUrl,
                responsibilities: parsed.responsibilities,
                requirements: parsed.requirements,
                preferred: parsed.preferred,
                skills: parsed.skills
            })
        });
        const report = await reportRes.json();

        await fetch(`/feedbacks/generate/${report.id}`, {
            method: "POST",
            headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
        });

        window.location.href = `/report?reportId=${report.id}`;
    } catch (err) {
        console.error("분석 실패:", err);
        alert("분석 중 오류가 발생했습니다. 다시 시도해주세요.");
    }
});
