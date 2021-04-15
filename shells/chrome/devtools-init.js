chrome.devtools.panels.create(
  "Hello Console Panel",
  "",
  "hello-panel.html",
  function (panel) {
    console.log("panel initialized", panel);
  }
);