package com.hackorama.mcore.service.group;


import com.hackorama.mcore.service.Component;

public class Group extends Component {

    private String name;
    private String email;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}
