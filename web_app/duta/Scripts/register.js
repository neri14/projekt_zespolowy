$(document).ready(function () {
    function redirectToHomepage() {
        window.location.href = "/Home/Index";
    }

    function onRegister() {
        var loginValue = $('#register-login').val();
        var pass1Value = $('#register-pass1').val();
        var pass2Value = $('#register-pass2').val();

        if (loginValue == "" || pass1Value == "" || pass2Value == "") {
            alert("Wszystkie pola są wymagane.");
            return;
        }

        if (pass1Value != pass2Value) {
            alert("Podane hasła muszą być takie same.");
            return;
        }

        $.ajax({
            type: "post",
            url: "/Service/Register",
            data: {
                login: loginValue,
                password: pass1Value
            },
            dataType: "json",
            success: function (data) {
                if (data.registered == 1) {
                    alert("Zarejestrowano");
                } else {
                    alert("Wystąpił błąd");
                }
                redirectToHomepage();
            },
            complete: redirectToHomepage
        });
    }

    $('#register-button').on("click", onRegister);
});