function validateTeam(teamId) {

    var i = $("#validationStatus").text().indexOf("Validation complete")
    if (i == -1) {
        $("#validationStatus").load("<g:createLink controller="registration" action="validate"/>?teamId=" + teamId,
            function() {
                validateTeam(teamId);
            });
    }
    else {
        location.reload(true)
    }
}

function validateRegistrations() {

    var i = $("#validationStatus").text().indexOf("Validation complete")
    if (i == -1) {
        $("#validationStatus").load("<g:createLink controller="registration" action="validate"/>",
            function() {
                validateRegistrations();
            });
    }
    else {
        location.reload(true)
    }
}

function validateClub(clubId) {

    var i = $("#validationStatus").text().indexOf("Validation complete")
    if (i == -1) {
        $("#validationStatus").load("<g:createLink controller="registration" action="validate"/>?clubId=" + clubId,
            function() {
                validateClub(clubId);
            });
    }
    else {
        location.reload(true)
    }
}
