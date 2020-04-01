$(() => {

        let loginButton = $("#login");
//      let userInputField = $(`#userInput`);
//      let password = $(`#password`);
//      let userType = $(`#userType`);
//      let verificationCode = $(`#verificationCode`);

        let baseUrl = "http://localhost:8080";
        let path = "/login";
        let url = baseUrl + path;


        // hardcoded values

        let email = "test@mailinator.com";
        let uin = "uin";
        let password = "123456";
        let userType = "DOCTOR";
        let verificationCode = 666;


        //function

        loginButton.on("click", async function (ev) {
            ev.preventDefault();

            const response = await fetch(url, {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json',
                    'Cache-Control': 'no-cache',
                    body: JSON.stringify({
                        email
                        , password
                        // , userType
                        // , verificationCode
                    })
                }
            });

            const {jwt_token} = await response.json();
            await login({jwt_token})
        });
});
