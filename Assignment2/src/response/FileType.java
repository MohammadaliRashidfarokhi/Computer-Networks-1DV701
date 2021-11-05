package response;


public enum FileType {

    htmlFile("text/html", "html, htm"),
    cssFile("text/css", "css"),
    javascriptFile("text/javascript", "js"),
    pngImageFile("image/png", "png"),
    jpegImageFile("image/jpeg", "jpg, jpeg");


    protected String value;
    protected String[] fileSuffix;


    private FileType(String value, String fileSuffix) {
        this.value = value;
        this.fileSuffix = fileSuffix.split(", ");
    }

}

