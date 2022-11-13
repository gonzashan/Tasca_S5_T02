function checkingSign(a, b, c, d) {

    let namePlayer = document.getElementById(a);
    let btn_form = document.getElementById(b);
    let response_text = document.getElementById(c);
    const form = document.getElementById(d);
    let formX = (d === "signup");

    $.get("/jocdedaus/players/" + namePlayer.value, function (data) {

        if (formX) {
            response_text.innerText = (data ? "Player registred " : "Valid name player");
            response_text.style.color = (data ? "red" : "green");
            form.action = (data ? '' : '/jocdedaus/' + d);
            data ? btn_form.setAttribute("disabled", "disabled") : btn_form.removeAttribute('disabled');

        } else {

            response_text.innerText = (!data ? "Player Not Registred " : "Player registred");
            response_text.style.color = (!data ? "red" : "green");
            form.action = (!data ? '' : '/jocdedaus/' + d);
            !data ? btn_form.setAttribute("disabled", "disabled") : btn_form.removeAttribute('disabled');
        }

    });


    namePlayer.onkeyup = function () {
        response_text.innerText = '';

    };

}
