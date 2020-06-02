var lastLocation = '';

function onSuccess(data, textStatus, request) {
    let redirect = request.getResponseHeader("X-IdBusLocation");
    if (redirect) {
        if (redirect == '{USE-Location}')
            redirect = request.getResponseHeader("Location");
        window.location.replace(redirect);
    } else {
        $(data).appendTo('#forms-container');
        let form = $('form');
        let action = form.prop('action');
        let formdata = form.serialize();
        $('#forms-container').empty();

        lastLocation = action;

        $.post({
            url: lastLocation,
            headers: {
                'accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9',
                'X-IdBusProcessUI': 'true'
            },
            data: formdata,
            success: onSuccess,
            error: onError
        })
    }
}

function onError(jqXHR, textStatus, errorThrown){
    // Redirect to error page:

    let pathArray = window.location.pathname.split('/');
    let errorLocation = "/" + pathArray[1] + "/ERR/" + jqXHR.status + "?location=" + btoa(lastLocation);
    console.log(textStatus + ": " + jqXHR.status + " " + errorThrown + ":" + errorLocation + " lastLocation ["+lastLocation+"]");
    window.location.replace(errorLocation);
}

$(window).on('load', function() {

    const METHOD = '#METHOD#';
    lastLocation = window.location.pathname + window.location.search;

    $.ajax({
        url: lastLocation,
        method: METHOD,
        headers: {
            'accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9',
            'X-IdBusProcessUI': 'true'
        },
        success: onSuccess,
        error: onError
    });
});
