function validateTeam(teamId) {

    var i = $("#validationStatus").text().indexOf("Validation complete")
    if (i == -1) {
        $("#validationStatus").load("/bwpl-registration/registration/validate?teamId=" + teamId,
            function() {
                validateTeam(teamId);
            });
    }
    else {
        location.reload(true)
    }
}

function validateClub(clubId) {

    var i = $("#validationStatus").text().indexOf("Validation complete")
    if (i == -1) {
        $("#validationStatus").load("/bwpl-registration/registration/validate?clubId=" + clubId,
            function() {
                validateClub(clubId);
            });
    }
    else {
        location.reload(true)
    }
}

function validateRegistrations() {

    var i = $("#validationStatus").text().indexOf("Validation complete")
    if (i == -1) {
        $("#validationStatus").load("/bwpl-registration/registration/validate",
            function() {
                validateRegistrations();
            });
    }
    else {
        location.reload(true)
    }
}
