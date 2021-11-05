function POST() {

    var theChosenFile = document.getElementById("uploadedFile").files;

    if (theChosenFile.length > 0) {

        var loadingTheFile = theChosenFile[0];

        var reader = new FileReader();

        reader.onload = function (fileLoadedEvent) {

            var nameOfTheFile = loadingTheFile.name;

            var content = document.getElementById("contents");

            content.value = fileLoadedEvent.target.result;

            content.name = nameOfTheFile;
        };

        reader.readAsDataURL(loadingTheFile);
    }
}


function PUT() {

    var theChosenFile = document.getElementById("updatedFile").files;

    if (theChosenFile.length > 0) {

        var loadingTheFile = theChosenFile[0];

        var reader = new FileReader();

        reader.onload = function (fileLoadedEvent) {

            var nameOfTheFile = loadingTheFile.name;

            var content = document.getElementById("updateContents");

            content.value = fileLoadedEvent.target.result;
            content.name = "Method-PUT" + nameOfTheFile;
        };
        reader.readAsDataURL(loadingTheFile);
    }
}