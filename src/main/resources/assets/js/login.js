let loginDoctor = $("#loginDoctor");
let userInputDoctor = $(`#userInputDoctor`);
let passwordDoctor = $(`#passwordDoctor`);

let loginPharmacist = $("#loginPharmacist");
let userInputPharmacist = $(`#userInputPharmacist`);
let passwordPharmacist = $(`#passwordPharmacist`);


// // hardcoded values for tests:

// userInputDoctor.val("test@mailinator.com");
userInputDoctor.val("uin");
passwordDoctor.val("123456");
// let userType = "DOCTOR";
let twoFactorAuthSecret = "666";



function bodyConstructor() {
    let userInput = "";
    let userPassword;
    let userType;

    if (userInputDoctor.val().trim().length > 0 && passwordDoctor.val().trim().length > 0) {
        // userType = "DOCTOR";
        userType = 0;
        userPassword = passwordDoctor.val();
    } else if (userInputPharmacist.val().trim().length > 0 && passwordPharmacist.val().trim().length > 0) {
        userType = "PHARMACIST";
        userPassword = passwordPharmacist.val();
    }

    // if (userType === "DOCTOR") {
    if (userType === 0) {
        userInput = userInputDoctor.val();
    } else if (userType === "PHARMACIST") {
        userInput = userInputPharmacist.val();
    }

    if (userInput.toString().includes("@")) {
        return JSON.stringify({email: userInput, password: userPassword, userType: userType, verificationCode: twoFactorAuthSecret});
    } else {
        return JSON.stringify({uin: userInput, password: userPassword, userType: userType, verificationCode: twoFactorAuthSecret});
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

        if (response.status === 200) {
            clearForm();

            const {jwt_token, jwt_token_expiry} = await response.json();
            // const { jwt_token } = await response.json();


            console.log('try to call login function');
            await login({jwt_token, jwt_token_expiry});

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

let inMemoryToken;

async function login({jwt_token, jwt_token_expiry}, noRedirect) {

    console.log('log in in');

    console.log(jwt_token.toString());
    console.log(jwt_token_expiry.toString());

    inMemoryToken = {
        token: jwt_token,
        expiry: jwt_token_expiry
    };
    if (!noRedirect) {

        // Router.push('/'); // как закачаме токена в хедъра?
        window.location('/');
    }
}

$('#logout').on("click", function () {
    inMemoryToken = null;
    window.location('/logout');
});

function isLoggedIn() {
    const jwt_token = inMemoryToken;
    if (!jwt_token) {
        // Router.push('/login')
        window.location('/login');
    }
    return jwt_token
}

function clearForm() {
    userInputDoctor.val('');
    passwordDoctor.val('');
    userInputPharmacist.val('');
    passwordPharmacist.val('');
}

loginDoctor.on('click', logUser);
loginPharmacist.on('click', logUser);