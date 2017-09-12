package ua.abond.instaret;


import ua.abond.instaret.service.Authentication;
import ua.abond.instaret.service.InstagramService;

public class RetardsApplication {

    public static void main(String[] args) throws Exception {
        InstagramService instagramService = instagramService();
        Authentication login = instagramService.login("usrnm", "pwd");
        instagramService.getFollowers(login, "usrnm").forEach(System.out::println);
    }

    public static InstagramService instagramService() {
        return new InstagramService();
    }

}
