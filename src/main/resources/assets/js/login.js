const userInputDoctor = $("#userInputDoctor");
const userInputPharmacist = $("#userInputPharmacist");

$(document).ready(function () {
    let loginDoctor = $("#loginDoctor");
    let loginPharmacist = $("#loginPharmacist");

    loginDoctor.on('click', function (e) {
        e.preventDefault();
        checkInputData();
        logUser("DOCTOR");
    });

    loginPharmacist.on('click', function (e) {
        e.preventDefault();
        checkInputData();
        logUser("PHARMACIST");
    });


    $(document).keypress(function (event) {

        if (event.keyCode === 13) {
            event.preventDefault();

            if (userInputDoctor.val().toString().length > 0) {
                logUser("DOCTOR");
            } else if (userInputPharmacist.val().toString().length > 0) {
                logUser("PHARMACIST");
            } else {
                checkInputData();
            }
        }
    });
});

function buildBody(userType) {
    let userInput = "";
    let userPassword;

    if (userType === "DOCTOR") {
        let userInputDoctor = $("#userInputDoctor");
        let passwordDoctor = $("#passwordDoctor");
        userPassword = passwordDoctor.val();
        userInput = userInputDoctor.val();
    } else if (userType === "PHARMACIST") {
        let userInputPharmacist = $("#userInputPharmacist");
        let passwordPharmacist = $("#passwordPharmacist");

        userPassword = passwordPharmacist.val();
        userInput = userInputPharmacist.val();
    }

    if (userInput.toString().includes("@")) {
        return JSON.stringify({email: userInput, password: userPassword, userType: userType});
    } else {
        return JSON.stringify({uin: userInput, password: userPassword, userType: userType});
    }
}

async function logUser(userType) {
    try {
        const response = await fetch("/user/login", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Cache-Control': 'no-cache'
            },
            body: buildBody(userType)
        });

        if (response.status === 200) {
            window.location = "/";
        } else {
            console.log('Login failed.');
            let error = new Error(response.statusText);
            error.response = response;
            throw error;
        }
    } catch (error) {
        console.error('Login error.', error);
    }
}

function customErrorAlert() {
    const close = document.getElementsByClassName("closeErrorWindow")[0];
    const box = document.getElementById("errorBox");

    box.style.display = "block";

    close.onclick = function () {
        box.style.display = "none";
    };

    window.onclick = function (event) {
        if (event.target === box) {
            box.style.display = "none";
        }
    };
}

function checkInputData() {
    if (userInputDoctor.val().toString().length === 0 && userInputPharmacist.val().toString().length === 0) {
        customErrorAlert();
    }
}