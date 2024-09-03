var appNames = document.querySelectorAll(".app-name");

appNames.forEach(function (appName) {
	appName.addEventListener("click", function () {
		location.reload();
	});
});

function loadingOn() {
	document.querySelector(".spinner").style.display = "block";
	document.querySelector(".container").style.filter = "blur(6px)";
	document.body.style.overflowY = "hidden";
	document.body.classList.add("disable-pointer-hover");
}

function loadingOff() {
	document.querySelector(".container").style.filter = "none";
	document.querySelector(".spinner").style.display = "none";
	document.body.style.overflowY = "scroll";
	document.body.classList.remove("disable-pointer-hover");
}

document.querySelector("form").addEventListener("submit", function () {
	loadingOn();
});
document.querySelectorAll(".nav-link").forEach(function (element) {
	element.addEventListener("click", function () {
		loadingOn();
	});
});

window.addEventListener("pageshow", function () {
	loadingOff();
});
loadingOff();

AOS.init();
