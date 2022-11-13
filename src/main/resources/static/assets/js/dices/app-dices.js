/*****
 *
 * INIZIALIZACIÓN
 *
 *
 * */
let oldName = $('#namePlayer').val();
let token = $('#token').val();
$(document).ready(function () {


    let total, sum, leftDice, rightDice;
    let dice_pressed = true;
    const message = document.querySelector('.text-move');
    const el = document.querySelector('.even-roll');

    // Keep this for change the name
    const namePlayer = $('#namePlayer');
    const idPlayer = $('#idPlayer');


    const btn_throw = $('#roll-button');
    let myKeyVals = {scored: -1, idPlayer: -1, bet: -1, leftDice: -1, rightDice: -1};
    let value = -1;


    $(".col.s1.grid-example").click(function () {


        if (dice_pressed) {
            let elemDice = this.firstChild;
            this.style.background = 'tomato';
            dice_pressed = false;
            rollDice(elemDice.innerHTML);
            $("#total-dices").fadeOut( 600 );
        }
    });
// LOGOUT ACTION
    $("#btn-logout").click(function () {
        alert("Hola, manola");
        $.ajax({
            type: 'PUT',
            url: '/jocdedaus/logout' ,
            data: {
                idPlayer: idPlayer.val(),
                token: token
            },
            headers: {
                'Authorization': "Bearer " + token
            },
            cache: false,
            success: function (result) {
                alert("saliendo");

            },
            error: function (jqXHR) {
                $(document.body).text('Error: ' + jqXHR.status);
            }});

        });




// DELETE SCORE LIST
    $("#btn-delete").click(function () {

        $.ajax({
            type: 'DELETE',
            url: '/jocdedaus/players/' + idPlayer.val() + '/games/',
            headers: {
                'Authorization': "Bearer " + token
            },
            cache: false,
            success: function (result) {
                $('#table-throws').html('');
                Swal.fire({
                    icon: 'success',
                    title: 'Score List erased',
                    showConfirmButton: false,
                    timer: 1500
                });
                $.ajaxSetup({
                    headers: {
                        'Authorization': "Bearer " + token
                    }
                });
                $.get("/jocdedaus/player-update/" + idPlayer.val(), function (data) {
                    updateScores(data);
                    $('#table-throws').html('');

                });
            },
            error: function (jqXHR) {
                $(document.body).text('Error: ' + jqXHR.status);
            }
        });


    });


    // function to control the dices throws and update values to DB
    el.addEventListener('transitionend', () => {

        $.ajax({
            type: 'POST',
            url: '/jocdedaus/players/' + idPlayer.val() + '/games/',
            data: myKeyVals,
            headers: {
                'Authorization': "Bearer " + token
            },
            cache: false,
            success: function () {

                message.textContent = value;
                $("#total-dices").fadeIn(20);
                $('.message').toggleClass('animate flip');

                if(myKeyVals.bet == myKeyVals.scored){
                    party.sparkles(message);
                }
                setTimeout(function () {// wait for 1 secs(2)

                    dice_pressed = true;
                    $(".col.s1.grid-example").css("background-color", "");
                    $('.message').toggleClass('animate flip');
                }, 1000);

                // Updating values after throwing dice
                $.ajaxSetup({
                    headers: {
                        'Authorization': "Bearer " + token
                    }
                });
                $.get("/jocdedaus/player-update/" + idPlayer.val(), function (data) {

                    updateScores(data);
                    paintingScoreTable(data);

                });

            },
            error: function (jqXHR) {
                if (jqXHR.status == 401) {
                    window.location.href = 'http://localhost:9005/jocdedaus/login';
                }
            }
        });
    });

    /*** FUNCTIONS DICES MOVES *****************************/

    function rollDice(diceSelected) {

        let i = 0;
        total = 0;
        sum = 0;
        leftDice = 0;
        rightDice = 0;
        //message.textContent = '';
        const dice = [...document.querySelectorAll(".die-list")];
        dice.forEach(die => {
            toggleClasses(die);
            die.dataset.roll = getRandomNumber(1, 6);
            if (i === 0) leftDice = die.dataset.roll;
            else rightDice = die.dataset.roll;
            i++;
        });

        result(sum, diceSelected);
    }


    function result(result, diceSelected) {

        value = result;
        myKeyVals = {
            scored: result,
            idPlayer: idPlayer.val(),
            bet: diceSelected,
            leftDice: leftDice,
            rightDice: rightDice
        };
    }


    function toggleClasses(die) {
        die.classList.toggle("odd-roll");
        die.classList.toggle("even-roll");
    }


    function getRandomNumber(min, max) {

        min = Math.ceil(min);
        max = Math.floor(max);
        total = Math.floor(Math.random() * (max - min + 1)) + min;
        sum = sum + total;


        return total;
    }

    /******* END control when dices moves ********+****************************/


    /*👺👺👺👺👺👺👺👺👺👺👺👺👺👺 RENAME PLAYER 👺👺👺👺👺👺👺👺👺👺👺👺👺👺👺👺👺👺👺*/
    function renamePlayer(newNewPayer) {


        $.ajax({
            type: 'PUT',
            //accept: 'application/json',
            url: '/jocdedaus/players/',
            headers: {
                'Authorization': "Bearer " + token
            },
            data: JSON.stringify({
                id: idPlayer.val(),
                namePlayer: newNewPayer
            }),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            cache: false,
            success: function () {
                Swal.fire(
                    'Good job!',
                    'Name has been changed!',
                    'success'
                )
                oldName = newNewPayer;
            },
            error: function (jqXHR) {
                Swal.fire({
                    icon: 'error',
                    title: 'Oops...',
                    text: 'Something went wrong!',
                    footer: '<a href="">Why do I have this issue?</a>'
                })
                namePlayer.val(oldName);
            }
        });

    }


    /* Methods to handel edit name action
     * Activating input element by id="namePlayer" changing attributes and css style **/

    namePlayer.focusin(function () {
        $('#nameSpan').css("display", "inline").fadeIn(5000);
    });

    namePlayer.change(function () {
        restoreInputNamePlayer();
        renamePlayer(namePlayer.val());
    });

    $('#btn-name').click(function () {
        namePlayer.removeAttr('readonly');
        namePlayer.removeAttr('disabled');
        namePlayer.addClass('selected');
        namePlayer.select();
    });

    namePlayer.bind('keypress', function (e) {
        if (e.keyCode === 13) {
            renamePlayer(namePlayer.val());
            restoreInputNamePlayer();
            namePlayer.blur();
        }
    });

    function restoreInputNamePlayer() {
        namePlayer.removeClass('selected');
        namePlayer.attr('disabled', true);
        namePlayer.attr('readonly', true);
        $('#nameSpan').css("display", "inline").fadeOut(800);

    }//END RENAME PLAYER


    /* UPDATE DATA AFTER DICES THROWN
    *😎😎😎😎😎😎😎😎😎😎😎😎😎😎😎😎😎 */
    function updateScores(data) {
        console.log(data);
        $('#your-throws').text(data.scoreList.length);
        $('#your-wins').text(data.games);
        $('#your-ranking').text(data.ranking);
        $('#your-percentage').text(data.average + "%");

    }

    function paintingScoreTable(data) {
        let size = data.scoreList.length;
        let colorOption = (data.scoreList[size - 1].bet === value ? 'green' : 'azure');
        let html = "<tr><th class='text-center h6 text-color-" + colorOption + "\' style='padding: .619rem .25rem;'>" + size + "</th>" +
            "<th class='text-center h6 text-color-" + colorOption + "\' scope='row' style='padding: .619rem .25rem;'>" + value + "</th>" +
            "<th class='text-center h6 text-color-" + colorOption + "\' style='padding: .619rem .25rem;'>" + data.scoreList[size - 1].bet + "</th>" +
            "</tr>"

        $('#table-throws').prepend(html);
    }


});  // WHEN DOCUMENT IS READY


//document.getElementById("roll-button").addEventListener("click", rollDice(e));



