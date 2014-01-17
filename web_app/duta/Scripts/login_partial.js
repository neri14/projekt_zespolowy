var loginId;
var loginName;
$(document).ready(function () {
    function redirectToHomepage() {
        window.location.href = "/Home/Index";
    }
    

    function onLogin() {
        var loginValue = $('#txt-login').val();
        var passValue = $('#txt-pass').val();

        $.ajax({
            type: "post",
            url: "/Service/Login",
            data: {
                login: loginValue,
                password: passValue
            },
            dataType: "json",
            success: function (response) {
                if (response.logged_in == 1) {                    
                    loginId = response.user_id;
                    loginName = loginValue;
                }
            },
            complete: redirectToHomepage
        });
    }

    function onLogout() {
        $.ajax({
            type: "post",
            url: "/Service/Logout",
            dataType: "json",
            complete: redirectToHomepage
        });
    }

    $('#button-login').on("click", onLogin);
    $('#button-logout').on("click", onLogout);
});