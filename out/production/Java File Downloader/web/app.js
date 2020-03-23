const downloadButton = document.querySelector(".button");
const downloadLink = document.querySelector(".hidden_link");
const eventSource = new EventSource("http://127.0.0.1:4040/events");

downloadButton.addEventListener("click", () => {
  downloadLink.click();
});

eventSource.onmessage = function(e) {
  const message = e.data.trim().split("/");
  const fileNumber = message[0].trim();
  const totalFiles = message[1].trim();

  if (fileNumber != totalFiles) {
    setTimeout(() => {
      downloadLink.click();
    }, 500);
  }
};
