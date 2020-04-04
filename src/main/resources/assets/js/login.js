$(document).ready(function() {
	var loginDoctor = $("#loginDoctor");
	var userInputDoctor = $("#userInputDoctor");
	var passwordDoctor = $(""#passwordDoctor");
	
	var loginPharmacist = $("#loginPharmacist");
	var userInputPharmacist = $("#userInputPharmacist");
	var passwordPharmacist = $("#passwordPharmacist");
	
	function bodyConstructor(userType) {
	    let userInput = "";
	    let userPassword;
	    let userType;
	
	    if (userType == "DOCTOR") {
	        userPassword = passwordDoctor.val();
	        userInput = userInputDoctor.val();
	    } else if (userType == "PHARMACIST") {
	        userPassword = passwordPharmacist.val();
	        userInput = userInputPharmacist.val();
	    }
	
	    if (userInput.toString().includes("@")) {
	        return JSON.stringify({email: userInput, password: userPassword, userType: userType});
	    } else {
	        return JSON.stringify({uin: userInput, password: userPassword, userType: userType});
	    }
	}
	
	async function logUser() {
	
	    try {
	        const response = await fetch("/user/login", {
	            method: 'POST',
	            credentials: 'include',
	            headers: {
	                'Content-Type': 'application/json',
	                'Cache-Control': 'no-cache'
	            },
	            body: bodyConstructor()
	        });
	
	        if (response.status == 200) {
	            clearForm();
	
	            window.location = "/";
	        } else {
	            console.log('Login failed.');
	            let error = new Error(response.statusText);
	            error.response = response;
	            throw error;
	        }
	    } catch (error) {
	        // console.error(
	        //     'Logging error.',
	        //     error
	        // );
	
	        // const { response } = error;
	        // setUserData(
	        //     Object.assign({}, userData, {
	        //         error: response ? response.statusText : error.message
	        //     })
	        // )
	    }
	}
	
	loginDoctor.on('click', logUser("DOCTOR"));
	loginPharmacist.on('click', logUser("PHARMACIST"));
})