$(() => {
    let loginDoctor = $("#loginDoctor");
    let userInputDoctor = $(`#userInputDoctor`);
    let passwordDoctor = $(`#passwordDoctor`);

    let loginPharmacist = $("#loginPharmacist");
    let userInputPharmacist = $(`#userInputPharmacist`);
    let passwordPharmacist = $(`#passwordPharmacist`);


    // hardcoded values for tests:

    // userInputDoctor = "test@mailinator.com";
    // userInputDoctor = "uin";
    // passwordDoctor = "123456";

    function bodyConstructor(){
        let userInput = "";
        let userPassword;
        let userType;

        if(userInputDoctor.val().trim().length > 0 && passwordDoctor.val().trim().length > 0) {
            userType = "DOCTOR";
            userPassword = passwordDoctor.val();
        } else if (userInputPharmacist.val().trim().length > 0 && passwordPharmacist.val().trim().length > 0){
            userType = "PHARMACIST";
            userPassword = passwordPharmacist.val();
        }

        if (userType === "DOCTOR"){
            userInput = userInputDoctor.val();
        } else if(userType === "PHARMACIST"){
            userInput = userInputPharmacist.val();
        }

        if(userInput.toString().includes("@")){
            return JSON.stringify({ email: userInput, password: userPassword, userType: userType})
        } else {
            return JSON.stringify({ uin: userInput, password: userPassword, userType: userType})
        }
    }

    async function logUser(){
        const response = await fetch("/user/login", {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                'Cache-Control': 'no-cache'
            },
            body: bodyConstructor()
        });

        const {jwt_token} = await response.json();
        await login({jwt_token});
    }

    let inMemoryToken;

    function login ({ jwt_token, jwt_token_expiry }, noRedirect) {

        //todo

        // inMemoryToken = {
        //     token: jwt_token,
        //     expiry: jwt_token_expiry
        // };
        // if (!noRedirect) {
        //     Router.push('/app')
        // }
    }


    loginDoctor.on("click", async function (ev) {
        ev.preventDefault();
        await logUser();
    });

    loginPharmacist.on("click", async function (ev) {
        ev.preventDefault();
        await logUser();
    });
});
