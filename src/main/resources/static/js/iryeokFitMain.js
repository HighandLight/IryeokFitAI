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