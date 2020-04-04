$(document).ready(function() {
	let loginDoctor = $("#loginDoctor");
	let loginPharmacist = $("#loginPharmacist");
	
	loginDoctor.on('click', function() {
		logUser("DOCTOR");
	});

	loginPharmacist.on('click', function() {
		logUser("PHARMACIST");
	});
});

function buildBody(userType) {
    let userInput = "";
    let userPassword;

    if (userType == "DOCTOR") {
    	let userInputDoctor = $("#userInputDoctor");
    	let passwordDoctor = $("#passwordDoctor");
        userPassword = passwordDoctor.val();
        userInput = userInputDoctor.val();
    } else if (userType == "PHARMACIST") {
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

        if (response.status == 200) {
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

