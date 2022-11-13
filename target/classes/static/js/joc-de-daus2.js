function checkingSign(a, b, c, d) {

    var namePlayer = document.getElementById(a);
    var btn_sign = document.getElementById(b);
    var response_sign = document.getElementById(c);
    const form_sign = document.getElementById(d);
    let formX = (d == "signup_form");

    let access = $.get( "jocdedaus/players/" + namePlayer, function( data ) {
      //  $( ".result" ).html( data );
        alert( "Load was performed."  + data);
    });

    user_name.onkeyup = function () {
        response_sign.innerText = ''
    };

    if (formX) {
        response_sign.innerText = (access ? "Player registred " : "Valid user");
        response_sign.style.color = (access ? "red" : "green");
        form_sign.action = (access ? '' : '/jocdedaus/signupNewPlayer');
        access ? btn_sign.setAttribute("disabled", "disabled") : btn_sign.removeAttribute('disabled');

    } else {

        response_sign.innerText = (!access ? "Player Not Registred " : "Player registred");
        response_sign.style.color = (!access ? "red" : "green");
        //form_sign.action = (!access ? '' : '/jocdedaus/signupNewPlayer');
        !access ? btn_sign.setAttribute("disabled", "disabled") : btn_sign.removeAttribute('disabled');
    }

}

function isInList(user_name) {
    const lowerList = list.map(element => {
        return element.toLowerCase();
    });
    return lowerList.includes((user_name.value).toLowerCase(), 0);
}