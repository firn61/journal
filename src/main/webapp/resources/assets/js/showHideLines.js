function showHideLines(varId) {
    var x = document.getElementById(varId);
    if (x.style.display === "none") {
        x.style.display = "block";
    } else {
        x.style.display = "none";
    }
}