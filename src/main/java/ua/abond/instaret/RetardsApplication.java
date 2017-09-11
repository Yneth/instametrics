package ua.abond.instaret;


import ua.abond.instaret.service.InstagramService;

public class RetardsApplication {

    public static void main(String[] args) throws Exception {
        InstagramService instagramService = instagramService();
        instagramService.getFollowers();
    }

    public static InstagramService instagramService() {
        return new InstagramService();
    }

}
