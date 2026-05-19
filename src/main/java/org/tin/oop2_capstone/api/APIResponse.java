package org.tin.oop2_capstone.api;

public class APIResponse {
    /*
    This class is just a helper to return
    two things: json string & httpCode(either success or any error)
    *mainly just used for FoodAPI*
     */
    private String json;
    private int httpCode;

    public APIResponse(String json, int httpCode){
        this.json = json;
        this.httpCode = httpCode;
    }

    public String getJson() {
        return json;
    }

    public int getHttpCode(){
        return httpCode;
    }
}
