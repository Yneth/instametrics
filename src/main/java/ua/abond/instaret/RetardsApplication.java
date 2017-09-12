package ua.abond.instaret;


import ua.abond.instaret.service.InstagramService;

public class RetardsApplication {

    public static void main(String[] args) throws Exception {
        InstagramService instagramService = instagramService();
        instagramService.getFollowers().forEach(System.out::println);
    }

    public static InstagramService instagramService() {
        return new InstagramService();
    }

}
