package photolog.api.dto.tour;


import photolog.api.domain.Tour;

public class TagDetailResponse {
    private String imageUrl;
    private String cat1;
    private String cat2;
    private String cat3;
    private String title;
    private Integer bookmarkCount;

    private String address;
    private String content;

    private String infoCall;
    private String restDate;
    private String useTime;

    public TagDetailResponse(Tour tour, String content, String infoCall, String restDate, String useTime) {
        this.imageUrl = tour.getFirstimage();
        this.cat1 = tour.getCat1();
        this.cat2 = tour.getCat2();
        this.cat3 = tour.getCat3();
        this.title = tour.getTitle();
        this.bookmarkCount = tour.getBookmarkCount();

        this.address = tour.getAddr();
        this.content = content;
        this.infoCall = infoCall;
        this.restDate = restDate;
        this.useTime = useTime;
    }

}
