package io.shape.service;

import io.kakai.annotate.Service;

@Service
public class UxService {

    String businessName;
    String businessEmail;

    public String getBusinessName(){
        return this.businessName;
    }

    public String getBusinessEmail(){
        return this.businessEmail;
    }
}
