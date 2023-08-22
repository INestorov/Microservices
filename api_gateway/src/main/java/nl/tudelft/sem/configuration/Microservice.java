package nl.tudelft.sem.configuration;

public enum Microservice {
    AUTHENTICATION("http://localhost:8001/application/authentication/"),
    FOOD_MANAGEMENT("http://localhost:8002/application/foodmanagement/"),
    HOUSE_MANAGEMENT("http://localhost:8003/application/housemanagement/");

    public final String url;

    Microservice(String url) {
        this.url = url;
    }
}
