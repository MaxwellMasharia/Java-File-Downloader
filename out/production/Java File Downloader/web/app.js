const downloadButton = document.querySelector(".button");
const downloadLink = document.createElement("a");
downloadLink.setAttribute("href", "/downloads");

downloadButton.addEventListener("click", () => {
  downloadLink.click();
});
