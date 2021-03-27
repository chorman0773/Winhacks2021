function get(url) {
    return new Promise((resolve, reject) => {
        var request = new XMLHttpRequest(url);
        request.onload = () => {
            resolve(request.response);
        };
        request.open("GET", url);
        request.send();
    });
}