package ua.abond.instaret;


import ua.abond.instaret.service.InstagramService;
import ua.abond.instaret.service.PreAuthorizedInstagramService;

public class RetardsApplication {

    public static void main(String[] args) throws Exception {
        InstagramService instagramService = instagramService();

        PreAuthorizedInstagramService preAuthorizedInstagramService =
            new PreAuthorizedInstagramService(instagramService);

        preAuthorizedInstagramService.followerDifference("usrnm").forEach(System.out::println);
    }

    public static InstagramService instagramService() {
        return new InstagramService();
    }

}
