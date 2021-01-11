package co.kr.itforone.peertalk.contact_pkg;

public class itemModel {

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    String number;
    String name;
    String uri;

    itemModel(String number, String name, String uri){
        this.number = number;
        this.name = name;
        this.uri = uri;
    }

}
